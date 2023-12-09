package isel.meic.tmf.commands.launchWorkflow;

import com.google.gson.Gson;
import isel.meic.tmf.commands.ICommand;
import isel.meic.tmf.commands.launchWorkflow.nodeSuppliers.INodeSupplier;
import isel.meic.tmf.commands.launchWorkflow.nodeSuppliers.NodeSupplierManualConfiguration;
import isel.meic.tmf.commands.launchWorkflow.nodeSuppliers.NodeSupplierRoundRobin;
import isel.meic.tmf.mappers.configurationMapping.ConfigurationMapper;
import isel.meic.tmf.models.activityBucket.ActivityRequestBodyDto;
import isel.meic.tmf.models.infraestructure.InfraestructureSpecificationDto;
import isel.meic.tmf.models.infraestructure.InfraestructureNodeDto;
import isel.meic.tmf.models.workflowConfiguration.MappingActivitiesToNodesSpecification;
import isel.meic.tmf.models.workflowConfiguration.WorkflowSpecificationDto;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static isel.meic.tmf.mappers.ActivityRequestMapper.mapActivityRequests;

public class LaunchWorkflowCommand implements ICommand {

    @Override
    public void execute(String[] args) {
        var infraestructureConfiguration = ConfigurationMapper.mapInfraestructureSpecification(args);
        var workflowConfiguration = ConfigurationMapper.mapWorkflowSpecification(args);
        var activityToNodesMapping = ConfigurationMapper.mapAtivityToNodesMappingSpecification(args);

        List<ActivityRequestBodyDto> activityRequests = mapActivityRequests(workflowConfiguration);
        INodeSupplier nodeSupplierForActivities;
        if (isActivityToNodeMappingPossible(infraestructureConfiguration, workflowConfiguration,
                                            activityToNodesMapping)) {
            nodeSupplierForActivities = new NodeSupplierManualConfiguration(infraestructureConfiguration,
                                                                            activityToNodesMapping);
        } else {
            nodeSupplierForActivities = new NodeSupplierRoundRobin(infraestructureConfiguration, activityRequests);
        }

        HttpClient client = HttpClient.newHttpClient();

        CompletableFuture<?>[] futures = new CompletableFuture[activityRequests.size()];
        for (int i = 0; i < activityRequests.size(); ++i) {
            final ActivityRequestBodyDto activitybody = activityRequests.get(i);
            var node = nodeSupplierForActivities.getNodeForActivityWithId(activitybody.activityName);

            String activityBucketUri = node.getUri();
            HttpRequest request = HttpRequest.newBuilder()
                                             .uri(URI.create("http://" + activityBucketUri + "/activity"))
                                             .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(activitybody)))
                                             .build();
            var response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                                 .handle((httpResponse, err) -> {
                                     String activityName =
                                             activitybody.activityName;
                                     if (err == null) {
                                         System.out.println("Activity with name: " + activityName + ", bellonging to " +
                                                                    "the workflow wih name: " + activitybody.workflowName +
                                                                    ", was successfully created in node: " + node.nodeName + " hosted in: " + node.ip + ":" + node.port);
                                         return httpResponse;
                                     }

                                     System.out.println("Failed to launch activity: " + activityName +
                                                                " in node: " + node.nodeName + " hosted in: " + node.ip + ":" + node.port + "\nFull error details:" + err.getMessage() + "\n");
                                     return null;
                                 });
            futures[i] = response;
        }

        CompletableFuture.allOf(futures)
                         .join();
    }

    private static boolean isActivityToNodeMappingPossible(InfraestructureSpecificationDto infraestructureConfiguration, WorkflowSpecificationDto workflowConfiguration, MappingActivitiesToNodesSpecification activityToNodesMapping) {
        if (activityToNodesMapping == null || workflowConfiguration == null || infraestructureConfiguration == null) {

            return false;
        }

        return allNodesHaveID(infraestructureConfiguration) && mappingIsValid(infraestructureConfiguration,
                                                                              workflowConfiguration,
                                                                              activityToNodesMapping);
    }

    private static boolean mappingIsValid(InfraestructureSpecificationDto infraestructureConfiguration,
                                          WorkflowSpecificationDto workflowConfiguration,
                                          MappingActivitiesToNodesSpecification activityToNodesMapping) {
        var nodeIds = Arrays.stream(infraestructureConfiguration.nodes)
                            .map(e -> e.nodeName)
                            .collect(Collectors.toList());
        var activitiesIds = Arrays.stream(workflowConfiguration.activities)
                                  .map(e -> e.activityName)
                                  .collect(Collectors.toList());

        var mappedNodeIds = Arrays.stream(activityToNodesMapping.mappingActivitiesToNodes)
                                  .map(e -> e.nodeNames)
                                  .flatMap(Arrays::stream)
                                  .distinct()
                                  .collect(Collectors.toList());
        if (mappedNodeIds.size() != nodeIds.size()) return false;
        if (mappedNodeIds.stream()
                         .anyMatch(e -> nodeIds.stream()
                                               .noneMatch(y -> Objects.equals(y, e)))) return false;

        var mappedActivitiesIds = Arrays.stream(activityToNodesMapping.mappingActivitiesToNodes)
                                        .map(e -> e.activityName)
                                        .distinct()
                                        .collect(Collectors.toList());

        if (mappedActivitiesIds.size() != activitiesIds.size()) return false;
        if (mappedActivitiesIds.stream()
                               .anyMatch(e -> activitiesIds.stream()
                                                           .noneMatch(y -> Objects.equals(y, e)))) return false;
        return true;
    }

    private static boolean allNodesHaveID(InfraestructureSpecificationDto infraestructureConfiguration) {

        for (InfraestructureNodeDto node : infraestructureConfiguration.nodes) {
            if (node.nodeName == null || node.nodeName.isEmpty()) return false;
        }
        return true;
    }
}
