package isel.meic.tmf.service;

import com.google.gson.Gson;
import isel.meic.tmf.container.manager.models.ActivityArgumentDto;
import isel.meic.tmf.models.BrokerInfoDto;
import isel.meic.tmf.models.CreateActivityRequestDto;

public class CreateActivityRequest {
    private final CreateActivityRequestDto createActivityRequestDto;

    public CreateActivityRequest(CreateActivityRequestDto createActivityRequestDto) {
        this.createActivityRequestDto = createActivityRequestDto;
    }

    public String getContainerImage() {
        return createActivityRequestDto.containerImage;
    }

    public String getActivityName() {
        return createActivityRequestDto.activityName;
    }

    public String getActivityArgumentAsJson() {
        var activityArgument = new ActivityArgumentDto();

        activityArgument.workflowName = createActivityRequestDto.workflowName;
        activityArgument.activityName = createActivityRequestDto.activityName;
        activityArgument.iterations = createActivityRequestDto.iterations;
        activityArgument.task = createActivityRequestDto.task;
        activityArgument.ports = createActivityRequestDto.ports;
        activityArgument.constants = createActivityRequestDto.constants;
        activityArgument.mappingsTaskArguments = createActivityRequestDto.mappingsTaskArguments;
        activityArgument.mappingsTaskResults = createActivityRequestDto.mappingsTaskResults;
        activityArgument.brokerInfo = createActivityRequestDto.brokerInfo;

        return new Gson().toJson(activityArgument);
    }

    public String getWorkingDirectory() {
        return createActivityRequestDto.workingDirectory;
    }

    public void acceptBrokerInfo(BrokerInfoDto brokerInfoDto) {
        createActivityRequestDto.brokerInfo = brokerInfoDto;
    }

    public String getWorkflowName() {
        return createActivityRequestDto.workflowName;
    }
}
