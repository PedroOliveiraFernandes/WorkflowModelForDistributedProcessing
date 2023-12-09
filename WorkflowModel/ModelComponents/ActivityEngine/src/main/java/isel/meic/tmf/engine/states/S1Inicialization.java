package isel.meic.tmf.engine.states;

import isel.meic.tmf.configuration.ActivityConfiguration;
import isel.meic.tmf.engine.IStateContext;
import isel.meic.tmf.engine.IterationController;
import isel.meic.tmf.ports.BrokerInteraction;
import isel.meic.tmf.utils.ActivityLogger;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class S1Inicialization extends EngineState {
    public S1Inicialization() {
        super();
    }

    @Override
    public void execute(IStateContext context) {
        ActivityConfiguration configuration = context.get(ActivityConfiguration.class);
        ActivityLogger logger = null;
        try {
            BrokerInteraction brokerInteraction = new BrokerInteraction(configuration.getBrokerInfo());
            brokerInteraction.init();
            context.register(BrokerInteraction.class, brokerInteraction);
            IterationController iterationController = new IterationController(configuration.getIterationConfig());
            logger = ActivityLogger.getInstance()
                                   .setConfiguration(configuration)
                                   .setIterationController(iterationController)
                                   .setBroker(brokerInteraction)
                                   .init();
            logger.info("Activity started");
            context.register(IterationController.class, iterationController);
            setNextState(new S2InputPortConstruction(), context);
        } catch (IOException | TimeoutException e) {
            if (logger != null) {
                logger.info("Failed to initialize Activity...");
            }
            e.printStackTrace();
            setNextStateToFinalState(context);
        }
    }
}
