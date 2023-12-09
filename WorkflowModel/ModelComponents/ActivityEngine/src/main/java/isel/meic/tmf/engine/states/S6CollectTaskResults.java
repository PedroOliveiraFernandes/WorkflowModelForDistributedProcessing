package isel.meic.tmf.engine.states;

import com.google.gson.Gson;
import isel.meic.tmf.configuration.ActivityConfiguration;
import isel.meic.tmf.engine.IStateContext;
import isel.meic.tmf.task.TaskResultsModel;
import isel.meic.tmf.utils.ActivityLogger;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class S6CollectTaskResults extends EngineState {
    private final ActivityLogger logger = ActivityLogger.getInstance();

    @Override
    public void execute(IStateContext context) {
        logger.info("Collecting task results...");
        var configuration = context.get(ActivityConfiguration.class);
        TaskResultsModel resultsModel;
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get(configuration.getPathToTaskResultsFile()));
            resultsModel = gson.fromJson(reader, TaskResultsModel.class);
            reader.close();
        } catch (IOException e) {
            resultsModel = new TaskResultsModel();
            resultsModel.results = new String[0];
        }

        logger.info("Task results collected: " + resultsModel);

        setNextState(new S7TokenPublication(resultsModel), context);
    }
}
