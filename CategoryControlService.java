
package com.sample.dg.service.impl;

import com.sample.dg.model.ServiceCategoryEntity;
import com.sample.dg.service.CategoryControlService;
import com.sample.dg.util.DgConstants;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public class CategoryControlService implements CategoryControlService {

    private static Logger log = Logger.getLogger(CategoryControlService.class);
    private EntityManager em;

    @PersistenceContext
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    private EntityManager getEntityManager() {
        return em;
    }

    
    public ServiceCategoryEntity find(long id) {
        ServiceCategoryEntity serviceCategoryEntity = new ServiceCategoryEntity();
        serviceCategoryEntity = getEntityManager().find(ServiceCategoryEntity.class, id);
        return serviceCategoryEntity;
    }

    
    public List<ServiceCategoryEntity> findAll() {
        Query query = getEntityManager().createNamedQuery(
                "ServiceCategoryEntity.findAllNotDeleted");
        return (List<ServiceCategoryEntity>) query.getResultList();
    }

    
    public ServiceCategoryEntity validate(String name, String code) {
        ServiceCategoryEntity serviceCategoryEntity = null;
        try {
            Query query = getEntityManager().createQuery("select category from " +
                    "ServiceCategoryEntity as category "
                    + "where upper(category.name) like :name and category.code like :code");
            query.setParameter("name", name.toUpperCase());
            query.setParameter("code", code);
            query.setMaxResults(1);
            if (!query.getResultList().isEmpty()) {
                serviceCategoryEntity = (ServiceCategoryEntity) query.getResultList().get(0);
            }
        } catch (Exception ex) {
            log.error("Exception in CategoryControlService validate()", ex);
        }
        return serviceCategoryEntity;
    }

    
    public void save(ServiceCategoryEntity serviceCategoryEntity) {
        em.persist(serviceCategoryEntity);
    }

    
    public List<ServiceCategoryEntity> search(String searchBy, String keyword) {
        Query query = null;
        List<ServiceCategoryEntity> resultList = new ArrayList<ServiceCategoryEntity>();
        if ("NAME".equals(searchBy) && !"".equals(keyword)) {
            query = getEntityManager().createQuery("select category from ServiceCategoryEntity as category "
                    + "where (upper(category.name) like :name) and category.deleted = 'N' ");
            query.setParameter("name", "%" + keyword.toUpperCase() + "%");
            resultList = (List<ServiceCategoryEntity>) query.getResultList();
        } else if ("CODE".equals(searchBy) && !"".equals(keyword)) {
            query = getEntityManager().createQuery("select category from ServiceCategoryEntity as category "
                    + "where category.code like :code and category.deleted = 'N' ");
            query.setParameter("code", "%" + keyword);
            resultList = (List<ServiceCategoryEntity>) query.getResultList();
        } else {
            resultList = findAll();
        }
        return resultList;
    }

    
    public void update(ServiceCategoryEntity serviceCategoryEntity) {
        getEntityManager().merge(serviceCategoryEntity);
    }

    
    public void delete(long id) {
        Query query = getEntityManager().createNamedQuery("ServiceCategoryEntity.deleteByServiceId");
        query.setParameter("deleteFlag", "Y");
        query.setParameter("serviceId", id);
        query.executeUpdate();

    }

    
    public List<ServiceCategoryEntity> findAllByParentId(String parentId) {
        log.debug("inside CategoryControlService validateCode()....1.");
        Query query = getEntityManager().createNamedQuery("ServiceCategoryEntity.findByParentId");
        query.setParameter("parentId", Long.valueOf(parentId));
        return (List<ServiceCategoryEntity>) query.getResultList();
    }

    
    public List<ServiceCategoryEntity> findAllByType(String type) {
        log.debug("inside CategoryControlService validateCode()....1.");
        Query query = getEntityManager().createNamedQuery("ServiceCategoryEntity.findByType");
        query.setParameter("type", type);
        return (List<ServiceCategoryEntity>) query.getResultList();
    }

    
    public ServiceCategoryEntity validateCode(String name, String code, Long parentId) {
        ServiceCategoryEntity serviceCategoryEntity = null;
        try {
            Query query = getEntityManager().createQuery("select category from ServiceCategoryEntity as category "
                    + "where upper(category.name) like :name and category.code like :code");
            query.setParameter("name", name.toUpperCase());
            query.setParameter("code", code);

            query.setMaxResults(1);
            if (!query.getResultList().isEmpty()) {
                serviceCategoryEntity = (ServiceCategoryEntity) query.getResultList().get(0);
            }
        } catch (Exception ex) {
            log.error("Exception in CategoryControlService validateCode()", ex);
        }
        return serviceCategoryEntity;
    }

    
    public void saveCode(ServiceCategoryEntity serviceCategoryEntity) {
        em.persist(serviceCategoryEntity);
    }

    
    public List<ServiceCategoryEntity> searchcode(String searchBy, String keyword, String service) {
        Query query = null;
        List<ServiceCategoryEntity> resultList = new ArrayList<ServiceCategoryEntity>();
        if (service == null || "".equals(service)) {
            if ("Name".equals(searchBy) && !"".equals(keyword)) {
                query = getEntityManager().createQuery("select category from ServiceCategoryEntity as category "
                        + "where (upper(category.name) like :name) and category.deleted = 'N' and category.type like :type");
                query.setParameter("name", "%" + keyword.toUpperCase() + "%");
                query.setParameter("type", DgConstants.CODE);
                resultList = (List<ServiceCategoryEntity>) query.getResultList();
            } else if ("Code".equals(searchBy) && !"".equals(keyword)) {
                query = getEntityManager().createQuery("select category from ServiceCategoryEntity as category "
                        + "where category.code like :code and category.deleted = 'N' and category.type like :type ");
                query.setParameter("code", "%" + keyword + "%");
                query.setParameter("type", DgConstants.CODE);
                resultList = (List<ServiceCategoryEntity>) query.getResultList();
            } else {
                resultList = findAllByType(DgConstants.CODE);
            }
        } else {
            if ("Name".equals(searchBy) && !"".equals(keyword)) {
                query = getEntityManager().createQuery("select category from ServiceCategoryEntity as category "
                        + "where (upper(category.name) like :name) and category.deleted = 'N' and category.parentId like :parentId");
                query.setParameter("name", "%" + keyword.toUpperCase() + "%");
                query.setParameter("parentId", Long.valueOf(service));
                resultList = (List<ServiceCategoryEntity>) query.getResultList();
            } else if ("Code".equals(searchBy) && !"".equals(keyword)) {
                query = getEntityManager().createQuery("select category from ServiceCategoryEntity as category "
                        + "where category.code like :code and category.deleted = 'N' and category.parentId like :parentId ");
                query.setParameter("code", "%" + keyword + "%");
                query.setParameter("parentId", Long.valueOf(service));
                resultList = (List<ServiceCategoryEntity>) query.getResultList();
            } else {
                resultList = findAllByParentId(service);
            }
        }
        return resultList;
    }
}
