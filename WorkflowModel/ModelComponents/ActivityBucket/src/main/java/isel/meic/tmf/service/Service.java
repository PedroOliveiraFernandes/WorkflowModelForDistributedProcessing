package isel.meic.tmf.service;

import isel.meic.tmf.container.manager.DockerApiRepository;
import isel.meic.tmf.models.BrokerInfoDto;
import isel.meic.tmf.models.ActivityStateInfoDtoCollection;
import isel.meic.tmf.models.CreateActivityRequestDto;

public class Service {

    private final DockerApiRepository dockerApiRepository;
    private final BrokerInfoDto brokerInfoDto;

    public Service(DockerApiRepository dockerApiRepository, BrokerInfoDto brokerInfoDto) {
        this.dockerApiRepository = dockerApiRepository;
        this.brokerInfoDto = brokerInfoDto;
    }

    public void createActivity(CreateActivityRequestDto createActivityRequestDto) throws InterruptedException {
        var createActivityRequest = new CreateActivityRequest(createActivityRequestDto);
        createActivityRequest.acceptBrokerInfo(brokerInfoDto);
        dockerApiRepository.createAndExecuteActivityContainer(createActivityRequest);
    }

    public ActivityStateInfoDtoCollection getRunningActivitiesForSpecificWorkflow(String workflowName) {
        return dockerApiRepository.getActivitiesForSpecificWorkflow(workflowName);
    }

    public void deleteAllFromWorkflow(String workflowName) {
        dockerApiRepository.deleteAllFromWorkflow(workflowName);
    }
}
