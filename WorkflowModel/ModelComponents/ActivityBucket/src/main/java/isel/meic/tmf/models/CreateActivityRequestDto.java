package isel.meic.tmf.models;

public class CreateActivityRequestDto {
    public String workflowName;
    public String workingDirectory;
    public String activityName;
    public IterationsDto iterations;
    public String containerImage;
    public TaskDto task;
    public PortDto[] ports;
    public String[] constants;
    public MappingDto[] mappingsTaskArguments;
    public MappingDto[] mappingsTaskResults;

    public BrokerInfoDto brokerInfo;
}


