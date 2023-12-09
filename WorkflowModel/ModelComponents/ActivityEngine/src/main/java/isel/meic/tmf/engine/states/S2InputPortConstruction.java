package isel.meic.tmf.engine.states;

import isel.meic.tmf.configuration.ActivityConfiguration;
import isel.meic.tmf.configuration.models.PortDto;
import isel.meic.tmf.engine.IStateContext;
import isel.meic.tmf.engine.IterationController;
import isel.meic.tmf.ports.BrokerInteraction;
import isel.meic.tmf.ports.InputPort;
import isel.meic.tmf.ports.InputPortsCollection;
import isel.meic.tmf.utils.ActivityLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class S2InputPortConstruction extends EngineState {
    private final ActivityLogger logger = ActivityLogger.getInstance();

    @Override
    public void execute(IStateContext context) {
        logger.info("Creating input ports...");
        var configuration = context.get(ActivityConfiguration.class);

        try {
            var iterationController = context.get(IterationController.class);
            var brokerInteraction = context.get(BrokerInteraction.class);
            List<InputPort> inputPorts = createInputPorts(configuration.getInputPorts(),
                                                          brokerInteraction, iterationController);

            context.register(InputPortsCollection.class, new InputPortsCollection(inputPorts));
            setNextState(new S3WaitForTokens(), context);
        } catch (Exception e) {
            logger.info("Failed to create input Ports, finalizing activity execution...");
            e.printStackTrace();
            setNextStateToFinalState(context);
        }
    }

    public List<InputPort> createInputPorts(List<PortDto> inputs, BrokerInteraction brokerInteraction,
                                            IterationController iterationController) throws IOException {

        List<InputPort> inputPorts = new ArrayList<>();
        if (inputs.size() == 0) {
            logger.info("No input ports defined");
            return inputPorts;
        }

        for (PortDto portDto : inputs) {
            InputPort inputPort = new InputPort(portDto, brokerInteraction, iterationController);
            inputPort.init();
            inputPorts.add(inputPort);
        }

        logger.info("Created input ports: " + inputPorts);
        return inputPorts;
    }
}