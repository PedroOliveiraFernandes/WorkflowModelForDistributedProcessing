package isel.meic.tmf.api.mapper;

import com.google.gson.Gson;
import isel.meic.tmf.models.CreateActivityRequestDto;

public class ActivityConfigurationModelMapper {
    public static CreateActivityRequestDto map(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, CreateActivityRequestDto.class);
    }
}
