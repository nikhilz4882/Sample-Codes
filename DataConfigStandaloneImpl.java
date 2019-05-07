
package com.info.springconf.spring;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;

import com.info.sis.model.UserEntity;
import com.info.sis.model.UserMenuEntity;



@Configuration
@Profile({"junit","standalone"})
public class DataConfigStandaloneImpl implements DataConfig {
	
	@Autowired
    private Environment environment;
    public static final int MAX_AGE = 60;
    public static final int CONNECTION_PERIOD = 240;
    
    @Bean(name="sessionFactory")
    @Override
    public AnnotationSessionFactoryBean annotationSessionFactory(){
    AnnotationSessionFactoryBean as = new AnnotationSessionFactoryBean();	
    as.setDataSource(dataSource());
    Properties hibernateproperties = new Properties();
 	hibernateproperties.setProperty("hibernate.connection.username", "root");
 	hibernateproperties.setProperty("hibernate.connection.password", "root");
 	hibernateproperties.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/blog?useUnicode=true&amp;amp;characterEncoding=utf-8&amp;amp;autoReconnect=true");
 	hibernateproperties.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
 	hibernateproperties.setProperty("hibernate.dialect","org.hibernate.dialect.MySQL5InnoDBDialect");
 	hibernateproperties.setProperty("hibernate.hbm2ddl.auto", "update");
 	hibernateproperties.setProperty("hibernate.show_sql", "true"); 
 	as.setHibernateProperties(hibernateproperties);
 	as.setUseTransactionAwareDataSource(true);
 	
 	//as.setPackagesToScan(new String []{"com.info.sis.model"});
 	 Class<?>[] classes = {
             
             UserEntity.class,
             UserMenuEntity.class
             
     };
    as.setAnnotatedClasses(classes);
 	//as.setAnnotatedPackages(new String [] {"com.info.sis.model"});
 	return as;
 	   
    }
    

    
    @Bean(name="dataSource")
    @Override
    public DataSource dataSource()  {
        
    	DriverManagerDataSource ds = new DriverManagerDataSource();
        
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUrl("jdbc:mysql://localhost:3306/blog?useUnicode=true&amp;amp;characterEncoding=utf-8&amp;amp;autoReconnect=true");
        ds.setUsername("root");
        ds.setPassword("root");
        System.out.println("Datasource is: "+ds.toString());
        
        return ds;
    }
   
  

   
    @Bean(name="transactionAwareDataSource")
    @Override
    public DataSource transactionAwareDataSource(){
        return new TransactionAwareDataSourceProxy(dataSource());
    }
    
    
   
   
    /*<bean id="transactionManager" class="org.springframework.
    	orm.hibernate3.HibernateTransactionManager">
    	<propertyname="sessionFactory"ref="sessionFactory"/>
    	</bean>*/
    @Bean(name="transactionManager")
    @Override
    public HibernateTransactionManager transactionManager() {
    	HibernateTransactionManager txManager = new HibernateTransactionManager();
    	txManager.setSessionFactory( annotationSessionFactory().getObject());
    	return txManager;
    }
   
    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
       return new PersistenceExceptionTranslationPostProcessor();
    } 
}
