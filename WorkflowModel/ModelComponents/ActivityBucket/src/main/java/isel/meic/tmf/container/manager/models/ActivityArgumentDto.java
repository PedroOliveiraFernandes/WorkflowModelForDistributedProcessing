package isel.meic.tmf.container.manager.models;

import isel.meic.tmf.models.*;

public class ActivityArgumentDto {
    public String workflowName;
    public String activityName;
    public IterationsDto iterations;
    public TaskDto task;
    public PortDto[] ports;
    public String[] constants;
    public MappingDto[] mappingsTaskArguments;
    public MappingDto[] mappingsTaskResults;
    public BrokerInfoDto brokerInfo;
}


