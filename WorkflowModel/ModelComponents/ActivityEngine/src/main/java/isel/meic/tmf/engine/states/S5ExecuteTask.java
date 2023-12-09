package isel.meic.tmf.engine.states;

import com.google.gson.Gson;
import isel.meic.tmf.configuration.ActivityConfiguration;
import isel.meic.tmf.engine.IStateContext;
import isel.meic.tmf.utils.ActivityLogger;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class S5ExecuteTask extends EngineState {
    private final ActivityLogger logger = ActivityLogger.getInstance();

    private final String taskArgumentAsJson;

    public S5ExecuteTask(String taskArgumentAsJson) {
        this.taskArgumentAsJson = taskArgumentAsJson;
    }

    @Override
    public void execute(IStateContext context) {
        logger.info("Executing task");
        var configuration = context.get(ActivityConfiguration.class);
        try {
            logger.info("Setup results file before task execution");
            cleanTaskResultsFile(configuration);
            logger.info("Executing task");
            var executedTime = executeTask(configuration);
            logger.info("Task execution finished with a running time of: " + executedTime + "ms");
            setNextState(new S6CollectTaskResults(), context);
        } catch (Exception e) {
            e.printStackTrace();
            setNextStateToFinalState(context);
        }
    }

    private void cleanTaskResultsFile(ActivityConfiguration configuration) {
        try {
            Path pathToTaskResultsFile = Paths.get(configuration.getPathToTaskResultsFile());
            Files.createDirectories(pathToTaskResultsFile.getParent());
            FileWriter fileWriter = new FileWriter(configuration.getPathToTaskResultsFile());
            fileWriter.write("");
            System.out.println("JSON data written to file successfully!");
            fileWriter.close();
        } catch (
                IOException e) {
            System.out.println("An error occurred while writing JSON data to file.");
            e.printStackTrace();
        }
    }

    public long executeTask(ActivityConfiguration configuration) throws Exception {
        String[] cmdArray = getCmdArray(configuration);
        long startTime = System.currentTimeMillis(); // Store the start time
        Process p = Runtime.getRuntime()
                           .exec(cmdArray);

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

        // read the output from the command
        logger.debug("Task process output:");
        String s;
        while ((s = stdInput.readLine()) != null) {
            logger.debug("|\t " + s);
        }
        // read any errors from the attempted command
        while ((s = stdError.readLine()) != null) {
            logger.debug("|\t " + s);
        }
        p.waitFor();
        long totalTime = System.currentTimeMillis() - startTime; // Store the start time

        if (p.exitValue() != 0) {
            logger.info("Task execution failed...");
            throw new Exception("Task process exit status: " + p.exitValue());
        }

        return totalTime;
    }

    private String[] getCmdArray(ActivityConfiguration configuration) {
        String[] strCommand = configuration.getTaskExecutionCommandArray();

        String[] result = new String[strCommand.length + 1];
        System.arraycopy(strCommand, 0, result, 0, strCommand.length);
        result[result.length - 1] = taskArgumentAsJson;
        return result;
    }
}

