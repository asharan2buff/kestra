package org.floworc.cli;

import ch.qos.logback.classic.LoggerContext;
import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.server.EmbeddedServer;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import javax.inject.Inject;

@CommandLine.Command(
    mixinStandardHelpOptions = true
)
@Slf4j
abstract public class AbstractCommand implements Runnable {
    private final boolean withServer;

    @Inject
    private ApplicationContext applicationContext;

    @CommandLine.Option(names = {"-v", "--verbose"}, description = "Change log level. Multiple -v options increase the verbosity.")
    private boolean[] verbose = new boolean[0];

    @CommandLine.Option(names = {"-l", "--log-level"}, description = "Change log level (values: ${COMPLETION-CANDIDATES}; default: ${DEFAULT-VALUE})")
    private LogLevel logLevel = LogLevel.INFO;

    public enum LogLevel {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR
    }

    public AbstractCommand(boolean withServer) {
        this.withServer = withServer;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(this.getClass().getDeclaredAnnotation(CommandLine.Command.class).name());
        startLogger();
        startWebserver();
    }

    private void startLogger() {
        if (this.verbose.length == 1) {
            this.logLevel = LogLevel.DEBUG;
        } else if (this.verbose.length > 1) {
            this.logLevel = LogLevel.TRACE;
        }

        ((LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory())
            .getLoggerList()
            .stream()
            .filter(logger -> logger.getName().indexOf("org.floworc") == 0 || logger.getName().indexOf("flow") == 0)
            .forEach(
                logger -> logger.setLevel(ch.qos.logback.classic.Level.valueOf(this.logLevel.name()))
            );
    }

    private void startWebserver() {
        if (!this.withServer) {
            return;
        }

        applicationContext
            .findBean(EmbeddedServer.class)
            .ifPresent(server -> {
                server.start();

                log.info(
                    "Server Running: {}",
                    server.getURL()
                );
            });
    }
}
