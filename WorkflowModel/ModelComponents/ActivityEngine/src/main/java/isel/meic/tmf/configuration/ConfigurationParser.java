package isel.meic.tmf.configuration;

import com.google.gson.Gson;
import isel.meic.tmf.configuration.models.ActivityArgumentsModel;
import isel.meic.tmf.utils.ActivityLogger;

public class ConfigurationParser {
    private static final ActivityLogger logger = ActivityLogger.getInstance();

    public static ActivityConfiguration parse(String[] args) {
        try {
            String taskExecutableDirectory = System.getenv("TASK_EXECUTABLE_DIRECTORY");
            logger.debug(taskExecutableDirectory);


            String pathToTaskResults = System.getenv("PATH_TO_TASK_RESULTS_FILE");
            logger.debug(pathToTaskResults);

            String jsonConfiguration = args[0];
            logger.debug(jsonConfiguration);

            ActivityArgumentsModel configurationModel =
                    mapFromString(jsonConfiguration);

            configurationModel.task.pathToTaskResultsFile = pathToTaskResults;
            configurationModel.task.pathToExecutablesDirectory = taskExecutableDirectory;

            return new ActivityConfiguration(configurationModel);
        } catch (Exception e) {
            logger.debug("Failed to parse arguments" + args);
            throw e;
        }
    }

    private static ActivityArgumentsModel mapFromString(String json) {
        Gson gson = new Gson();
        ActivityArgumentsModel configuration = gson.fromJson(json, ActivityArgumentsModel.class);
        return configuration;
    }
}
