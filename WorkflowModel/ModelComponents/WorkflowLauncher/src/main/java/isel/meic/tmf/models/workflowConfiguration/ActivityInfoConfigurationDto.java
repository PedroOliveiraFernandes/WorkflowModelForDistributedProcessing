package isel.meic.tmf.models.workflowConfiguration;

import isel.meic.tmf.models.common.MappingDto;
import isel.meic.tmf.models.common.PortDto;
import isel.meic.tmf.models.common.TaskDto;

public class ActivityInfoConfigurationDto {
    public String activityName;
    public String containerImage;
    public int replicas = 1;
    public TaskDto task;
    public PortDto[] ports;
    public MappingDto[] mappingsTaskArguments;
    public MappingDto[] mappingsTaskResults;
}
