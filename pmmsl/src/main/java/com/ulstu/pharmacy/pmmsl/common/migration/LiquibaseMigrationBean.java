package com.ulstu.pharmacy.pmmsl.common.migration;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Startup
@Singleton
@TransactionManagement(TransactionManagementType.BEAN)
/** Бин, создающийся при запуске приложения, для накатывания скриптов миграций. */
public class LiquibaseMigrationBean {

    private static final String CHANGE_LOG_FILE_NAME = "db/changelog/master-changelog.xml";

    @Resource(lookup = "java:/jdbc/pharmacy")
    private DataSource dataSource;

    @PostConstruct
    protected void bootstrap() throws SQLException, LiquibaseException {
        ResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader());

        Connection connection = dataSource.getConnection();
        JdbcConnection jdbcConnection = new JdbcConnection(connection);

        Database db = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(jdbcConnection);
        Liquibase liquiBase = new Liquibase(CHANGE_LOG_FILE_NAME, resourceAccessor, db);
        liquiBase.update("init");

        connection.close();
    }
}