package isel.meic.tmf.container.manager;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import isel.meic.tmf.models.ActivityStateInfoDto;
import isel.meic.tmf.models.ActivityStateInfoDtoCollection;
import isel.meic.tmf.service.CreateActivityRequest;

import java.io.File;
import java.net.URI;
import java.util.Objects;

public class DockerApiRepository {

    public static final String WORKING_DIRECTORY_INSIDE_CONTAINER = "/var/workingDirectory";
    private DockerClient dockerClient;
    private final String sharedDirectory;

    public DockerApiRepository(String dockerHost, String sharedDirectory) {
        dockerClient = DockerClientBuilder.getInstance()
                                          .withDockerHttpClient(new ApacheDockerHttpClient.Builder().dockerHost(URI.create(dockerHost))
                                                                                                    .build())
                                          .build();
        this.sharedDirectory = sharedDirectory;
    }

    public void createAndExecuteActivityContainer(CreateActivityRequest createActivityRequest) {
        try {
            createImage(createActivityRequest);

            CreateContainerResponse containerResponse = dockerClient
                    .createContainerCmd(createActivityRequest.getContainerImage())
                    .withName(extractContainerName(createActivityRequest))
                    .withAttachStdout(true)
                    .withAttachStdin(true)
                    .withBinds(Bind.parse(
                            new File(sharedDirectory + createActivityRequest.getWorkingDirectory()).getAbsolutePath() + ":" + WORKING_DIRECTORY_INSIDE_CONTAINER))
                    .withCmd(createActivityRequest.getActivityArgumentAsJson())
                    .exec();

            dockerClient.startContainerCmd(containerResponse.getId())
                        .exec();

            System.out.println("Created Activity: " + createActivityRequest.getActivityName());
        } catch (Exception e) {
            System.out.println("Failed to create and execute the Activity with id: " + createActivityRequest.getActivityName() + "\n\tDetailed Error: " + e.getMessage());
            throw e;
        }
    }

    private String extractContainerName(CreateActivityRequest createActivityRequest) {
        return createActivityRequest.getWorkflowName() + "__" + createActivityRequest.getActivityName() + "__" + System.currentTimeMillis();
    }

    private void createImage(CreateActivityRequest createActivityRequest) {
        var splitted = createActivityRequest.getContainerImage()
                                            .split(":");

        var imageBuilder = dockerClient.pullImageCmd(splitted[0]);
        if (splitted.length > 1) {
            imageBuilder.withTag(splitted[1]);
        }
        try {
            imageBuilder
                    .start()
                    .awaitCompletion();
        } catch (Exception e) {

        }
    }

    public ActivityStateInfoDtoCollection getActivitiesForSpecificWorkflow(String workflowName) {

        ActivityStateInfoDto[] collect = dockerClient.listContainersCmd()
                                                     .withShowAll(true)
                                                     .exec()
                                                     .stream()
                                                     .filter(container -> container.getNames()[0].contains(workflowName))
                                                     .map(container -> {
                                                         var containerNameSplit = container.getNames()[0].replace("/", "")
                                                                                                 .split("__");
                                                         return new ActivityStateInfoDto(containerNameSplit[0],
                                                                                         containerNameSplit[1],
                                                                                         Long.parseLong(containerNameSplit[2]),
                                                                                         getMappedState(container));
                                                     })
                                                     .toArray(ActivityStateInfoDto[]::new);
        return new ActivityStateInfoDtoCollection(collect);
    }

    private static String getMappedState(Container container) {
        String state = container.getState();
        if (!Objects.equals(state, "running")) {
            state = "exited";
        }
        return state
                .toUpperCase();
    }

    public void deleteAllFromWorkflow(String workflowName) {
        try {
            dockerClient.listContainersCmd()
                        .withShowAll(true)
                        .exec()
                        .stream()
                        .filter(container -> container.getNames()[0].contains(workflowName))
                        .forEach(container -> {
                            if (container.getState()
                                         .equals("running")) {
                                dockerClient.stopContainerCmd(container.getId())
                                            .exec();
                            }
                            dockerClient.removeContainerCmd(container.getId())
                                        .exec();
                        });
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
