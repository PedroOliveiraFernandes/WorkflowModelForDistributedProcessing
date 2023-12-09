package isel.meic.tmf.api.rest;

import isel.meic.tmf.api.mapper.ActivityConfigurationModelMapper;
import isel.meic.tmf.service.Service;
import isel.meic.tmf.models.CreateActivityRequestDto;
import spark.Request;
import spark.Response;
import spark.Spark;

public class RestApi {
    private final Service service;
    private final int port;

    public RestApi(Service service, int port) {
        this.service = service;
        this.port = port;
    }

    public void start() {
        Spark.port(port);
        Spark.post("/activity", "application/json", this::postActivity);
        Spark.get("/activities", "application/json", this::getAllFromWorkflow);
        Spark.delete("/activities", "application/json", this::deleteAllFromWorkflow);
    }

    public String postActivity(Request request, Response response) {
        CreateActivityRequestDto configuration = ActivityConfigurationModelMapper.map(request.body());
        try {
            System.out.println("REQUEST - Create Activity, with body: " + request.body());
            service.createActivity(configuration);
        } catch (Exception e) {
            response.status(500);
            return "Could not create Activity: " + configuration.activityName;
        }
        return "Successfully created Activity: " + configuration.activityName;
    }

    public String getAllFromWorkflow(Request request, Response response) {
        response.type("application/json");
        String workflowName = request.queryParams("workflowName");
        System.out.println("REQUEST - Get Activity states, for the workflow with the name: " + workflowName);
        try {
            var result = service.getRunningActivitiesForSpecificWorkflow(workflowName);
            if (result.activityStates.length == 0) {
                response.status(404);
                System.out.println("No states for the workflow with the name: " + workflowName + " could be fetched");
                return "";
            }

            String resultJson = result.toJson();
            System.out.println("Fetched states for the workflow with the name: " + workflowName + " are: \n" + resultJson);

            System.out.println(resultJson);
            return resultJson;
        } catch (Exception e) {
            response.status(500);
            System.out.println("Internal error fetching states for workflow with the name: " + workflowName + ". " +
                                       "Stack strace below:\n" + e.getMessage());
            return "Internal error";
        }
    }

    public String deleteAllFromWorkflow(Request request, Response response) {
        String workflowName = request.queryParams("workflowName");
        System.out.println("REQUEST - Delete activities, for workflow with the name: " + workflowName);
        try {
            service.deleteAllFromWorkflow(workflowName);
            return "Deleted all activities for the workflow with the name: " + workflowName;
        } catch (Exception e) {
            response.status(500);
            System.out.println("Internal error deleting Activities for workflow with the name: " + workflowName + ". " +
                                       "Stack strace below:\n" + e.getMessage());
            return "Internal error";
        }
    }
}

