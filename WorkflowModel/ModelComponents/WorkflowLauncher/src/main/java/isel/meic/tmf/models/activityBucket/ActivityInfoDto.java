package isel.meic.tmf.models.activityBucket;

import isel.meic.tmf.models.common.IterationsDto;
import isel.meic.tmf.models.common.MappingDto;
import isel.meic.tmf.models.common.PortDto;
import isel.meic.tmf.models.common.TaskDto;

public class ActivityInfoDto {
    public String activityName;
    public IterationsDto iterations;
    public String containerImage;
    public TaskDto task;
    public PortDto[] ports;
    public MappingDto[] mappingsTaskArguments;
    public MappingDto[] mappingsTaskResults;
}
