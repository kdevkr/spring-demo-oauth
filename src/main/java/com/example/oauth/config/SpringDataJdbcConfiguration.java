package com.example.oauth.config;

import com.example.oauth.sqlite.SQLiteDialectResolver;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.DialectResolver;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.util.Optional;

@Configuration
public class SpringDataJdbcConfiguration extends AbstractJdbcConfiguration {

    private final DataSourceProperties dataSourceProperties;

    public SpringDataJdbcConfiguration(DataSourceProperties dataSourceProperties) {
        this.dataSourceProperties = dataSourceProperties;
    }

    @Override
    public Dialect jdbcDialect(NamedParameterJdbcOperations operations) {
        DialectResolver.DefaultDialectProvider defaultDialectProvider = new DialectResolver.DefaultDialectProvider();
        Optional<Dialect> dialect = defaultDialectProvider.getDialect(operations.getJdbcOperations());
        if (dialect.isEmpty()) {
            String driverClassName = dataSourceProperties.getDriverClassName();
            if (org.sqlite.JDBC.class.getName().equals(driverClassName)) {
                // NOTE: org.springframework.data.jdbc.repository.config.DialectResolver$JdbcDialectProvider=com.example.oauth.sqlite.SQLiteDialectResolver in META-INF/spring.factories
                SQLiteDialectResolver sqLiteDialectResolver = new SQLiteDialectResolver();
                return sqLiteDialectResolver.getDialect(operations.getJdbcOperations()).orElse(null);
            }
        }
        return dialect.orElse(null);
    }
}