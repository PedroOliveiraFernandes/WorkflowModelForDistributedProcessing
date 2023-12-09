package isel.meic.tmf.engine.states;

import isel.meic.tmf.engine.IStateContext;
import isel.meic.tmf.ports.InputPort;
import isel.meic.tmf.ports.InputPortsCollection;
import isel.meic.tmf.token.InputToken;
import isel.meic.tmf.utils.ActivityLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class S3WaitForTokens extends EngineState {
    private final ActivityLogger logger = ActivityLogger.getInstance();

    @Override
    public void execute(IStateContext context) {
        InputPortsCollection inputPortsCollection = context.get(InputPortsCollection.class);
        if (!inputPortsCollection.hasInputPorts()) {
            setNextState(new S4MapTokensToTaskArguments(new ArrayList<>()), context);
            return;
        }
        logger.info("Waiting for tokens...");
        List<InputToken> tokens = inputPortsCollection.inputPorts
                .stream()
                .map(InputPort::getTokenFuture)
                .map(S3WaitForTokens::getObject)
                .collect(Collectors.toList());
        logger.info("Received tokens: " + tokens);

        setNextState(new S4MapTokensToTaskArguments(tokens), context);
    }

    private static InputToken getObject(CompletableFuture<InputToken> inputTokenCompletableFuture) {
        try {
            return inputTokenCompletableFuture.get();
        } catch (Exception e) {
            return new InputToken(null, null);
        }
    }
}
