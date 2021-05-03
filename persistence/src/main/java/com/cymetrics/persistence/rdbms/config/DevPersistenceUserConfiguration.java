package com.cymetrics.persistence.rdbms.config;


import com.zaxxer.hikari.HikariConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.cymetrics.persistence.rdbms.dao.dev",
        entityManagerFactoryRef = "devEntityManager",
        transactionManagerRef = "devTransactionManager"
)
public class DevPersistenceUserConfiguration extends HikariConfig {
    @Autowired
    private Environment env;

    @Bean
    public LocalContainerEntityManagerFactoryBean devEntityManager() {
        LocalContainerEntityManagerFactoryBean emf
                = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(devDataSource());
        emf.setPackagesToScan(
                new String[] { "com.cymetrics.persistence.rdbms.entities" });

        HibernateJpaVendorAdapter vendorAdapter
                = new HibernateJpaVendorAdapter();
        emf.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto",
                env.getProperty("spring.datasource.dev.hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect",
                env.getProperty("spring.datasource.dev.hibernate.dialect"));
        properties.put("hibernate.show_sql",
                env.getProperty("spring.datasource.dev.hibernate.show_sql"));
        emf.setJpaPropertyMap(properties);


        return emf;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.dev")
    public DataSource devDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public PlatformTransactionManager devTransactionManager() {

        JpaTransactionManager transactionManager
                = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
                devEntityManager().getObject());
        return transactionManager;
    }

}
