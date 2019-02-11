/*
 * Copyright © 2019 Logistimo.
 *
 * This file is part of Logistimo.
 *
 * Logistimo software is a mobile & web platform for supply chain management and remote temperature monitoring in
 * low-resource settings, made available under the terms of the GNU Affero General Public License (AGPL).
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing a commercial license. To know more about
 * the commercial license, please contact us at opensource@logistimo.com
 */

package com.logistimo.communication.application.configs;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.beans.PropertyVetoException;
import java.util.Properties;

import javax.annotation.Resource;

/**
 * Created by kumargaurav on 14/02/17.
 */
@Configuration
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
@EnableJpaRepositories(
    entityManagerFactoryRef = "cmEntityManagerFactory",
    transactionManagerRef = "cmTransactionManager",
    basePackages = {"com.logistimo.communication.repo.dao"})
public class DSConfig {

  @Resource
  Environment env;

  @Bean(name = "cmEntityManagerFactory")
  @Primary
  public LocalContainerEntityManagerFactoryBean entityManagerFactory()
      throws PropertyVetoException {
    LocalContainerEntityManagerFactoryBean
        entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
    entityManagerFactory.setDataSource(locationDataSource());
    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    entityManagerFactory.setJpaVendorAdapter(vendorAdapter);
    // Hibernate properties
    Properties additionalProperties = new Properties();
    additionalProperties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
    additionalProperties.put("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
    additionalProperties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
    //caching
    additionalProperties.put("hibernate.cache.use_second_level_cache", "true");
    additionalProperties.put("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.EhCacheRegionFactory");
    additionalProperties.put("hibernate.cache.use_query_cache", "true");
    additionalProperties.put("hibernate.generate_statistics", "true");
    additionalProperties.put("hibernate.enable_lazy_load_no_trans", "true");

    entityManagerFactory.setJpaProperties(additionalProperties);
    entityManagerFactory.setPackagesToScan("com.logistimo.communication.repo.entity");
    return  entityManagerFactory;
  }

  @Bean(name = "cmDataSource")
  @Primary
  public ComboPooledDataSource locationDataSource () throws PropertyVetoException {
    ComboPooledDataSource dataSource = new ComboPooledDataSource();
    dataSource.setMinPoolSize(Integer.parseInt(env.getProperty("hibernate.c3p0.min_size")));
    dataSource.setMaxPoolSize(Integer.parseInt(env.getProperty("hibernate.c3p0.max_size")));
//  dataSource.setAcquireIncrement(acquireIncrement);
//  dataSource.setIdleConnectionTestPeriod(idleTestPeriod);
//  dataSource.setMaxStatements(maxStatements);
    dataSource.setMaxIdleTime(Integer.parseInt(env.getProperty("hibernate.c3p0.idle_test_period")));
    dataSource.setJdbcUrl(env.getProperty("spring.communication.db.url"));
    dataSource.setPassword(env.getProperty("spring.communication.db.password"));
    dataSource.setUser(env.getProperty("spring.communication.db.username"));
    dataSource.setDriverClass(env.getProperty("spring.communication.db.driver"));
    return dataSource;
  }

  @Bean(name = "cmTransactionManager")
  @Primary
  public PlatformTransactionManager transactionManager() throws PropertyVetoException {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
    return transactionManager;
  }

}

