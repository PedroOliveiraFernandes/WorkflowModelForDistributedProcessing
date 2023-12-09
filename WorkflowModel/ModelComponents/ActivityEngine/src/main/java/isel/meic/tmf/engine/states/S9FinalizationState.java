package isel.meic.tmf.engine.states;

import isel.meic.tmf.engine.IStateContext;
import isel.meic.tmf.ports.BrokerInteraction;
import isel.meic.tmf.utils.ActivityLogger;

public class S9FinalizationState extends EngineState {
    private final ActivityLogger logger = ActivityLogger.getInstance();

    @Override
    public void execute(IStateContext context) {
        try {
            BrokerInteraction brokerInteraction = context.get(BrokerInteraction.class);
            if (brokerInteraction != null) {
                logger.info("Finished Activity execution");
                brokerInteraction
                        .close();
            }
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
        setNextState(null, context);
    }
}
