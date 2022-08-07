package com.example.oauth.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;

import javax.sql.DataSource;

@Slf4j
@Configuration
public class OAuthDatabasePopulator {

    private final DataSourceProperties dataSourceProperties;

    public OAuthDatabasePopulator(DataSourceProperties dataSourceProperties) {
        this.dataSourceProperties = dataSourceProperties;
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(dataSource);
        dataSourceInitializer.setDatabasePopulator(databasePopulator());
        return dataSourceInitializer;
    }

    /**
     * Define user and oauth schemas for sqlite or postgresql
     *
     * @return {@link DatabasePopulator}
     */
    @Bean
    public DatabasePopulator databasePopulator() {
        String driverClassName = dataSourceProperties.getDriverClassName();
        final ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        databasePopulator.setContinueOnError(true);
        if (org.postgresql.Driver.class.getName().equals(driverClassName)) {
            // NOTE: spring.session.jdbc.platform=postgresql
            databasePopulator.addScript(new ClassPathResource("db/postgresql/users.ddl"));
            databasePopulator.addScript(new ClassPathResource("db/postgresql/oauth2-authorization-schema.sql"));
            databasePopulator.addScript(new ClassPathResource("org/springframework/security/oauth2/server/authorization/oauth2-authorization-consent-schema.sql"));
            databasePopulator.addScript(new ClassPathResource("org/springframework/security/oauth2/server/authorization/client/oauth2-registered-client-schema.sql"));
        } else if (org.sqlite.JDBC.class.getName().equals(driverClassName)) {
            // NOTE: spring.session.jdbc.platform=sqlite
            databasePopulator.addScript(new ClassPathResource(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION));
            databasePopulator.addScript(new ClassPathResource("org/springframework/security/oauth2/server/authorization/oauth2-authorization-schema.sql"));
            databasePopulator.addScript(new ClassPathResource("org/springframework/security/oauth2/server/authorization/oauth2-authorization-consent-schema.sql"));
            databasePopulator.addScript(new ClassPathResource("org/springframework/security/oauth2/server/authorization/client/oauth2-registered-client-schema.sql"));
        }
        return databasePopulator;
    }
}
