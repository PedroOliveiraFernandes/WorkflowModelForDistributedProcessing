package isel.meic.tmf.models;

public class ActivityStateInfoDto {
    public final String workflowName;
    public final String activityName;
    public final long creationTime;
    public final String state;

    public ActivityStateInfoDto(String workflowName, String activityId, long creationTime, String state) {
        this.workflowName = workflowName;
        this.activityName = activityId;
        this.creationTime = creationTime;
        this.state = state;
    }
}
