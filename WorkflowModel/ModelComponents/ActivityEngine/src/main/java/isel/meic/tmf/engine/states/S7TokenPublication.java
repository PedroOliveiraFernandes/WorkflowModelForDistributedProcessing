package isel.meic.tmf.engine.states;

import com.rabbitmq.client.Channel;
import isel.meic.tmf.configuration.ActivityConfiguration;
import isel.meic.tmf.configuration.models.PortDto;
import isel.meic.tmf.engine.IStateContext;
import isel.meic.tmf.engine.IterationController;
import isel.meic.tmf.ports.BrokerInteraction;
import isel.meic.tmf.ports.OutputPort;
import isel.meic.tmf.ports.OutputPortsCollection;
import isel.meic.tmf.task.TaskResultsModel;
import isel.meic.tmf.token.TaskResult;
import isel.meic.tmf.token.Token;
import isel.meic.tmf.utils.ActivityLogger;

import java.util.ArrayList;
import java.util.List;

public class S7TokenPublication extends EngineState {
    private final ActivityLogger logger = ActivityLogger.getInstance();

    private final TaskResultsModel taskResults;

    public S7TokenPublication(
            TaskResultsModel taskResults) {
        this.taskResults = taskResults;
    }

    @Override
    public void execute(IStateContext context) {
        logger.info("Emit task results...");
        var configuration = context.get(ActivityConfiguration.class);
        var brokerConnection = context.get(BrokerInteraction.class);
        var iterationController = context.get(IterationController.class);
        OutputPortsCollection outputPortsCollection = context.get(OutputPortsCollection.class);

        try {
            if (outputPortsCollection == null) {
                List<OutputPort> outputPorts = createOutputPorts(configuration.getOutputPorts(),
                                                                 brokerConnection.getChannel());
                outputPortsCollection = new OutputPortsCollection(outputPorts);
                context.register(OutputPortsCollection.class, outputPortsCollection);
            }

            for (OutputPort outputPort : outputPortsCollection.outputPorts) {
                var index = configuration.getTaskReultIndexByPortName(outputPort.getName());
                var result = taskResults.results[index];
                outputPort.emit(new Token(result, iterationController.getCurrent()));
            }
            setNextState(new S8IterationControlState(), context);
        } catch (Exception e) {
            logger.info("Failed to emit Tokens");
            setNextStateToFinalState(context);
        }
    }

    public List<OutputPort> createOutputPorts(List<PortDto> outputs, Channel channel) {

        List<OutputPort> outputPorts = new ArrayList<>();

        for (PortDto output : outputs) {
            var outputPorts1 = new OutputPort(output, channel);
            outputPorts.add(outputPorts1);
        }
        logger.info("created output ports " + outputs);
        return outputPorts;
    }
}
