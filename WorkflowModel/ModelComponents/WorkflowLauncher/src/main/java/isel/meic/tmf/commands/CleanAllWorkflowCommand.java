package isel.meic.tmf.commands;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import isel.meic.tmf.mappers.configurationMapping.ConfigurationMapper;
import isel.meic.tmf.models.activityBucket.ActivityRequestBodyDto;
import isel.meic.tmf.models.infraestructure.InfraestructureSpecificationDto;
import isel.meic.tmf.models.workflowConfiguration.WorkflowSpecificationDto;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static isel.meic.tmf.mappers.ActivityRequestMapper.mapActivityRequests;

public class CleanAllWorkflowCommand implements ICommand {

    @Override
    public void execute(String[] args) {
        var infraestructureConfiguration = ConfigurationMapper.mapInfraestructureSpecification(args);
        var workflowConfiguration = ConfigurationMapper.mapWorkflowSpecification(args);

        new DeleteActivitiesCommand().execute(args);
        deleteQueue(infraestructureConfiguration,workflowConfiguration);
    }

    private void deleteQueue(InfraestructureSpecificationDto infraestructureConfiguration,
                             WorkflowSpecificationDto workflowConfiguration) {
        try {
            System.out.println("Deleting all broker channels for workflow: " + workflowConfiguration.workflowName);
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(infraestructureConfiguration.broker.ip);
            factory.setPort(infraestructureConfiguration.broker.port);
            Connection connection = factory.newConnection();
            var channel = connection.createChannel();

            List<ActivityRequestBodyDto> activityRequests = mapActivityRequests(workflowConfiguration);
            var channelNames = activityRequests.stream()
                                               .flatMap(activityRequestBodyDto -> Arrays.stream(activityRequestBodyDto.ports))
                                               .map(portDto -> portDto.channel)
                                               .distinct()
                                               .collect(Collectors.toList());

            for (var channelName : channelNames) {
                channel.queueDelete(channelName);
            }

            channel.close();
            connection.close();
            System.out.println("Deleted all broker channels for workflow: " + workflowConfiguration.workflowName);
        } catch (IOException | TimeoutException e) {
            System.out.println("Failed to delete all broker channels for workflow: " + workflowConfiguration.workflowName);
            System.out.println("Full error details:");
            e.printStackTrace();
        }
    }
}
