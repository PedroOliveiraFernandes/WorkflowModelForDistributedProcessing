package isel.meic.tmf.engine.states;

import isel.meic.tmf.configuration.ActivityConfiguration;
import isel.meic.tmf.engine.IStateContext;
import isel.meic.tmf.engine.IterationController;
import isel.meic.tmf.task.TaskArguments;
import isel.meic.tmf.token.InputToken;
import isel.meic.tmf.utils.ActivityLogger;

import java.util.List;

public class S4MapTokensToTaskArguments extends EngineState {
    private final ActivityLogger logger = ActivityLogger.getInstance();

    private final List<InputToken> tokens;

    public S4MapTokensToTaskArguments(List<InputToken> tokens) {
        this.tokens = tokens;
    }

    @Override
    public void execute(IStateContext context) {
        logger.info("Mapping task arguments...");
        var configuration = context.get(ActivityConfiguration.class);
        var iterationController = context.get(IterationController.class);

        TaskArguments arguments = new TaskArguments();

        if (!tokens.isEmpty()) {
            tokens.forEach(token -> arguments.acceptArgument(token.getContent(),
                                                             configuration.getTaskArgumentIndexByPortName(
                                                                     token.getPortNameThatReceivedThisToken()
                                                             )));
        }

        arguments.acceptConstants(configuration.getConstantArguments());
        arguments.acceptCurrentIteration(iterationController.getCurrent());
        arguments.acceptActivityName(configuration.getActivityName());
        logger.info("Task will be executed with the following arguments: " + arguments.toJsonString());
        setNextState(new S5ExecuteTask(arguments.toString()), context);
    }
}
