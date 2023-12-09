package isel.meic.tmf.task;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static isel.meic.tmf.App.IS_RUNNING_IN_WINDOWS;

public class TaskArguments {
    private final List<String> arguments;
    private String[] constants;
    private int currentIteration;
    private String activityName;

    public TaskArguments() {
        arguments = new ArrayList<>();
    }

    public void acceptArgument(String content, int index) {
        arguments.add(index, content);
    }

    public void acceptConstants(String... constants) {
        this.constants = constants;
    }

    public void acceptCurrentIteration(int currentIteration) {
        this.currentIteration = currentIteration;
    }

    public void acceptActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String toJsonString() {
        var json = makeJsonString();
        if (IS_RUNNING_IN_WINDOWS) {
            json = json.replace("\"", "\\\"");
            json = json.replace("\\\\\"", "\\\\\\\"");
        }

        return json;
    }

    @Override
    public String toString() {
        return makeJsonString();
    }

    private String makeJsonString() {
        var argumentModel = new ArgumentsModel();
        argumentModel.arguments = arguments.toArray(new String[0]);
        argumentModel.constants = constants;
        argumentModel.currentIteration = currentIteration;
        argumentModel.activityName = activityName;

        return new Gson().toJson(argumentModel);
    }

    private class ArgumentsModel {
        public String[] arguments;
        public String[] constants;
        public int currentIteration;
        private String activityName;
    }
}
