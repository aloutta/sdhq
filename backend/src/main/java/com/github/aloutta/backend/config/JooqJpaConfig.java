package com.github.aloutta.backend.config;

import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.jooq.ExecuteContext;
import org.jooq.SQLDialect;
import org.jooq.impl.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

@Configuration
@RequiredArgsConstructor
public class JooqJpaConfig {

  private final DataSource dataSource;
  private final Environment env;

  @Bean
  public DataSourceConnectionProvider connectionProvider() {
    return new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy(dataSource));
  }

  @Bean
  public DefaultDSLContext dsl() {
    return new DefaultDSLContext(configuration());
  }

  @Bean
  public DefaultConfiguration configuration() {
    DefaultConfiguration configuration = new DefaultConfiguration();
    configuration.set(connectionProvider());
    configuration.set(new DefaultExecuteListenerProvider(exceptionTranslator()));
    configuration.setSQLDialect(dialect());
    return configuration;
  }

  private SQLDialect dialect() {
    String sqlDialectName = env.getProperty("spring.jooq.sql-dialect");
    return SQLDialect.valueOf(sqlDialectName);
  }

  @Bean
  public ExceptionTranslator exceptionTranslator() {
    return new ExceptionTranslator();
  }

  public static class ExceptionTranslator extends DefaultExecuteListener {
    public void exception(ExecuteContext context) {
      SQLDialect dialect = context.configuration().dialect();
      SQLExceptionTranslator translator = new SQLErrorCodeSQLExceptionTranslator(dialect.name());
      context.exception(
          translator.translate(
              "Access database using Jooq", context.sql(), context.sqlException()));
    }
  }
}
