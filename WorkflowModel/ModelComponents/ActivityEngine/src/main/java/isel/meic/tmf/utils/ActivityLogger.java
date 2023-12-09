package isel.meic.tmf.utils;

import isel.meic.tmf.configuration.ActivityConfiguration;
import isel.meic.tmf.engine.IterationController;
import isel.meic.tmf.ports.BrokerInteraction;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;

public class ActivityLogger {
    public static final String LOGS_EXCHANGE_NAME = "logs";
    public static final String FANOUT = "fanout";
    private static ActivityLogger instance;

    private ActivityConfiguration configuration;
    private BrokerInteraction brokerConnection;
    private IterationController iterationController;

    private ActivityLogger() {
        // Private constructor to prevent instantiation from outside the class
    }

    public static synchronized ActivityLogger getInstance() {
        if (instance == null) {
            instance = new ActivityLogger();
        }
        return instance;
    }

    public ActivityLogger setIterationController(IterationController iterationController) {
        this.iterationController = iterationController;
        return this;
    }

    public ActivityLogger setConfiguration(ActivityConfiguration configuration) {
        this.configuration = configuration;
        return this;
    }

    public ActivityLogger init() throws IOException, TimeoutException {
        brokerConnection.createExchange(LOGS_EXCHANGE_NAME, FANOUT);
        return this;
    }

    public ActivityLogger setBroker(BrokerInteraction brokerConnection) {
        this.brokerConnection = brokerConnection;
        return this;
    }

    public void info(String logMessage) {
        var suffixedLogMessage = getLogMessageWithSufix(logMessage);
        logLocal(suffixedLogMessage);
        logToBroker(suffixedLogMessage);
    }

    public void debug(String logMessage) {

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String timestamp = currentDateTime.format(formatter);

        String result =
                "[DEBUG LOG | " + timestamp + "]" + " - " + logMessage;

        logLocal(result);
    }

    private void logToBroker(String logMessage) {
        try {
            brokerConnection.publishToExchange("logs", logMessage);
        } catch (IOException e) {
            logLocal("Fail to publish logs to broker");
        }
    }

    private void logLocal(String logMessage) {
        System.out.println(logMessage);
    }

    private String getLogMessageWithSufix(String logMessage) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String timestamp = currentDateTime.format(formatter);

        String result =
                "[" + timestamp + " | workflowName: " + configuration.getWorkflowName() + " | activityName: " + configuration.getActivityName();

        if (iterationController != null) {
            result = result + " | iteration: " + iterationController.getCurrent();
        }

        return result + "]" + " - " + logMessage;
    }
}
