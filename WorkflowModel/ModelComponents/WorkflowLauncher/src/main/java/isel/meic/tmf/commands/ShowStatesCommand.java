package isel.meic.tmf.commands;

import com.google.gson.Gson;
import isel.meic.tmf.mappers.configurationMapping.ConfigurationMapper;
import isel.meic.tmf.models.infraestructure.InfraestructureNodeDto;
import isel.meic.tmf.models.state.ActivityStateInfoDto;
import isel.meic.tmf.models.state.ActivityStateInfoDtoCollection;
import isel.meic.tmf.models.workflowConfiguration.WorkflowSpecificationDto;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static isel.meic.tmf.mappers.ActivityRequestMapper.mapActivityRequests;

public class ShowStatesCommand implements ICommand {
    @Override
    public void execute(String[] args) {
        var infraestructureConfiguration = ConfigurationMapper.mapInfraestructureSpecification(args);
        var workflowConfiguration = ConfigurationMapper.mapWorkflowSpecification(args);

        HttpClient client = HttpClient.newHttpClient();

        InfraestructureNodeDto[] nodes = infraestructureConfiguration.nodes;
        CompletableFuture<HttpResponse<String>>[] futures = new CompletableFuture[nodes.length];

        for (int i = 0; i < nodes.length; ++i) {

            var node = nodes[i];

            HttpRequest request = HttpRequest.newBuilder()
                                             .uri(URI.create("http://" + node.getUri() + "/activities?workflowName=" + workflowConfiguration.workflowName))
                                             .GET()
                                             .build();
            var response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            futures[i] = response;
        }
        CompletableFuture.allOf(futures)
                         .join();
        ActivityStateInfoDto[] mappedStates = mapToStates(futures);
        print(mappedStates, workflowConfiguration);
    }

    private ActivityStateInfoDto[] mapToStates(CompletableFuture<HttpResponse<String>>[] futures) {
        return Arrays.stream(futures)
                     .map(CompletableFuture::join)
                     .map(response -> {
                         if (response.statusCode() == 500) {
                             return null;
                         }

                         return new Gson().fromJson(response.body(),
                                                    ActivityStateInfoDtoCollection.class);
                     })
                     .filter(Objects::nonNull)
                     .flatMap(c -> Arrays.stream(c.activityStates))
                     .sorted((o1, o2) -> Math.toIntExact(o2.creationTime - o1.creationTime))
                     .toArray(ActivityStateInfoDto[]::new);
    }

    private void print(ActivityStateInfoDto[] states, WorkflowSpecificationDto workflowConfiguration) {
        System.out.println("State of all activities in workflow: " + workflowConfiguration.workflowName);
        var activityRequests = mapActivityRequests(workflowConfiguration);

        for (var activityRequest : activityRequests) {
            ActivityStateInfoDto stateInfo = find(states, activityRequest.activityName);

            String stateMessage;
            if (stateInfo == null) {
                stateMessage = "State could not be found";
            } else {
                stateMessage = stateInfo.state.toUpperCase();
            }
            System.out.println("Activity name: " + activityRequest.activityName + " | state: " + stateMessage);
        }
    }

    private ActivityStateInfoDto find(ActivityStateInfoDto[] states, String id) {
        for (ActivityStateInfoDto state : states) {
            if (Objects.equals(state.activityName, id)) return state;
        }
        return null;
    }
}
