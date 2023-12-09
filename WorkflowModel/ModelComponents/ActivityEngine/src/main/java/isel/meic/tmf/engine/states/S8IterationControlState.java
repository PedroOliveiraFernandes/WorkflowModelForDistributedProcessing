package isel.meic.tmf.engine.states;

import isel.meic.tmf.engine.IStateContext;
import isel.meic.tmf.engine.IterationController;
import isel.meic.tmf.ports.InputPortsCollection;
import isel.meic.tmf.utils.ActivityLogger;

public class S8IterationControlState extends EngineState {
    private final ActivityLogger logger = ActivityLogger.getInstance();

    @Override
    public void execute(IStateContext context) {
        var iterationController = context.get(IterationController.class);
        var inputPortsCollection = context.get(InputPortsCollection.class);

        inputPortsCollection.confirmAllTokens();

        if (iterationController.canContinue()) {
            logger.info("Incrementing workflow iteration...");
            iterationController.stepIteration();
            setNextState(new S3WaitForTokens(), context);
            return;
        }
        setNextStateToFinalState(context);
    }
}
