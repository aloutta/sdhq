package com.github.aloutta.backend.config;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import com.github.loki4j.logback.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@RequiredArgsConstructor
public class LoggingConfig {

  @Value("${loki.url:http://localhost:3100/loki/api/v1/push}")
  private String lokiUrl;

  @Value("${loki.level:INFO}")
  private String lokiLevel;

  @Value("${spring.application.name}")
  private String appName;

  private final Environment environment;

  @PostConstruct
  public void setupLogging() {
    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
    context.reset();

    ConsoleAppender<ILoggingEvent> consoleAppender = setupConsoleAppender(context);
    Loki4jAppender lokiAppender = setupLoki4jAppender(context);
    AsyncAppender asyncLokiAppender = setupAsyncAppender(context, lokiAppender);

    Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
    rootLogger.detachAndStopAllAppenders();
    rootLogger.addAppender(consoleAppender);
    rootLogger.addAppender(asyncLokiAppender);
    rootLogger.setLevel(Level.toLevel(lokiLevel));
  }

  private ConsoleAppender<ILoggingEvent> setupConsoleAppender(LoggerContext context) {

    PatternLayoutEncoder consoleEncoder = new PatternLayoutEncoder();
    consoleEncoder.setContext(context);
    consoleEncoder.setPattern(
        "%d{yyyy-MM-dd'T'HH:mm:ss} [%thread] %magenta(%-5level) %green(%logger{36}) - %msg%n");
    consoleEncoder.start();

    ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
    consoleAppender.setContext(context);
    consoleAppender.setName("CONSOLE");
    consoleAppender.setEncoder(consoleEncoder);
    consoleAppender.start();

    return consoleAppender;
  }

  private Loki4jAppender setupLoki4jAppender(LoggerContext context) {

    JavaHttpSender httpSender = new JavaHttpSender();
    httpSender.setUrl(lokiUrl);

    String hostname = environment.getProperty("hostname");
    String pattern = String.format("app=%s,host=%s", appName, hostname);
    String structuredPattern =
        "level=%level,thread=%thread,class=%logger,traceId=%X{traceId:-none}";

    JsonEncoder.LabelCfg labelCfg = new JsonEncoder.LabelCfg();
    labelCfg.setPattern(pattern);
    labelCfg.setStructuredMetadataPattern(structuredPattern);

    JsonEncoder lokiEncoder = new JsonEncoder();
    lokiEncoder.setLabel(labelCfg);
    lokiEncoder.setMessage(new JsonLayout());

    Loki4jAppender lokiAppender = new Loki4jAppender();
    lokiAppender.setContext(context);
    lokiAppender.setName("LOKI");
    lokiAppender.setHttp(httpSender);
    lokiAppender.setMetricsEnabled(true);
    lokiAppender.setFormat(lokiEncoder);
    lokiAppender.start();
    return lokiAppender;
  }

  private AsyncAppender setupAsyncAppender(LoggerContext context, Loki4jAppender lokiAppender) {
    AsyncAppender asyncLokiAppender = new AsyncAppender();
    asyncLokiAppender.setContext(context);
    asyncLokiAppender.setName("ASYNC_LOKI");
    asyncLokiAppender.setQueueSize(5000);
    asyncLokiAppender.setDiscardingThreshold(0);
    asyncLokiAppender.setNeverBlock(true);
    asyncLokiAppender.addAppender(lokiAppender);
    asyncLokiAppender.start();
    return asyncLokiAppender;
  }
}
