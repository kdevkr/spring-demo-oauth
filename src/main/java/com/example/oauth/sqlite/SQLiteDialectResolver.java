package com.example.oauth.sqlite;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jdbc.core.dialect.*;
import org.springframework.data.jdbc.repository.config.DialectResolver;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.dialect.HsqlDbDialect;
import org.springframework.data.relational.core.dialect.MariaDbDialect;
import org.springframework.data.relational.core.dialect.OracleDialect;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcOperations;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Optional;

@Slf4j
public class SQLiteDialectResolver extends DialectResolver.DefaultDialectProvider {

    @Override
    public Optional<Dialect> getDialect(JdbcOperations operations) {
        return Optional.ofNullable(
                operations.execute((ConnectionCallback<Dialect>) SQLiteDialectResolver::getDialect));
    }

    private static Dialect getDialect(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        String name = metaData.getDatabaseProductName().toLowerCase(Locale.ROOT);
        if (name.contains("sqlite")) {
            return SQLiteDialect.INSTANCE;
        }
        if (name.contains("postgresql")) {
            return JdbcPostgresDialect.INSTANCE;
        }

        log.info(String.format("Couldn't determine Dialect for \"%s\"", name));
        return null;
    }
}