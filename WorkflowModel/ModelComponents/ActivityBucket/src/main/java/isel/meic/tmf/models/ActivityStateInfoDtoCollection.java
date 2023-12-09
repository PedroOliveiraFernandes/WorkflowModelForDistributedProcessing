package isel.meic.tmf.models;

import com.google.gson.Gson;

public class ActivityStateInfoDtoCollection {

    public final ActivityStateInfoDto[] activityStates;

    public ActivityStateInfoDtoCollection(ActivityStateInfoDto[] activityStates) {
        this.activityStates = activityStates;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
