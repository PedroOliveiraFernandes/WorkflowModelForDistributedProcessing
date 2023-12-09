package isel.meic.tmf.commands;

import isel.meic.tmf.mappers.configurationMapping.ConfigurationMapper;
import isel.meic.tmf.models.infraestructure.InfraestructureNodeDto;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class DeleteActivitiesCommand implements ICommand {

    @Override
    public void execute(String[] args) {
        var workflowConfiguration = ConfigurationMapper.mapWorkflowSpecification(args);
        var infraestructureConfiguration = ConfigurationMapper.mapInfraestructureSpecification(args);
        HttpClient client = HttpClient.newHttpClient();

        InfraestructureNodeDto[] nodes = infraestructureConfiguration.nodes;
        CompletableFuture<HttpResponse<String>>[] futures = new CompletableFuture[nodes.length];

        for (int i = 0; i < nodes.length; ++i) {
            var node = nodes[i];
            System.out.println("Deleting all activities in workflow: " + workflowConfiguration.workflowName + " in " +
                                       "ActivityBucket hosted in: " + node.getUri());
            HttpRequest request = HttpRequest.newBuilder()
                                             .uri(URI.create("http://" + node.getUri() + "/activities?workflowName=" + workflowConfiguration.workflowName))
                                             .DELETE()
                                             .build();
            var response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                                 .handle((httpResponse, err) -> {
                                     if (err == null) {
                                         System.out.println("Server: "+node.getUri()+", response: " +httpResponse.body());
                                         return httpResponse;
                                     }

                                     System.out.println("Failed to delete activities of the workflow with the name: " + workflowConfiguration.workflowName +
                                                                " in node: " + node.nodeName + " hosted in: " + node.ip +
                                                                ":" + node.port + "\nFull error details:\n" + err.getMessage() + "\n");
                                     return null;
                                 });
            futures[i] = response;
        }
        CompletableFuture.allOf(futures)
                         .join();

        if (futures.length != 0 && Arrays.stream(futures)
                                         .map(CompletableFuture::join)
                                         .allMatch(e -> e != null && e.statusCode() == 200)) {
            System.out.println("Deleted all activities in workflow: " + workflowConfiguration.workflowName);
        }
    }
}
