package isel.meic.tmf.configuration.models;

public class ActivityArgumentsModel {
    //    public String workingDirectory;
    public String workflowName;
    public String activityName;
    public IterationsDto iterations;
    public TaskDto task;
    public PortDto[] ports;
    public MappingDto[] mappingsTaskArguments;
    public MappingDto[] mappingsTaskResults;
    public BrokerInfoDto brokerInfo;
}

