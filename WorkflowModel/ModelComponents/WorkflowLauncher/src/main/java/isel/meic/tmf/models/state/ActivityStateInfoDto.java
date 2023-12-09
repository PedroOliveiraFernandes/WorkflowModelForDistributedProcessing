package isel.meic.tmf.models.state;

public class ActivityStateInfoDto {
    public final String workflowName;
    public final String activityName;
    public final long creationTime;
    public final String state;

    public ActivityStateInfoDto(String workflowName, String activityName, long creationTime, String state) {
        this.workflowName = workflowName;
        this.activityName = activityName;
        this.creationTime = creationTime;
        this.state = state;
    }
}
