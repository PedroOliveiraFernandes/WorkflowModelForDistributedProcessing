package isel.meic.tmf.mappers;

import isel.meic.tmf.models.activityBucket.ActivityRequestBodyDto;
import isel.meic.tmf.models.common.IterationsDto;
import isel.meic.tmf.models.common.PortDto;
import isel.meic.tmf.models.workflowConfiguration.ActivityInfoConfigurationDto;
import isel.meic.tmf.models.workflowConfiguration.WorkflowSpecificationDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ActivityRequestMapper {
    public static List<ActivityRequestBodyDto> mapActivityRequests(WorkflowSpecificationDto workflowConfiguration) {

        List<ActivityRequestBodyDto> result = new ArrayList<>();

        for (var activity : workflowConfiguration.activities) {
            List<ActivityRequestBodyDto> mappedActivityRequests = mapActivityToRequests(activity,
                                                                                        workflowConfiguration);
            result.addAll(mappedActivityRequests);
        }
        return result;
    }

    private static List<ActivityRequestBodyDto> mapActivityToRequests(ActivityInfoConfigurationDto activityInfoConfigurationDto, WorkflowSpecificationDto workflowConfiguration) {
        List<ActivityRequestBodyDto> result = new ArrayList<>();

        for (int i = 0; i < activityInfoConfigurationDto.replicas; i++) {
            var request = new ActivityRequestBodyDto();
            request.workingDirectory = workflowConfiguration.workingDirectory;
            request.workflowName = workflowConfiguration.workflowName;

            var iteration = new IterationsDto();
            iteration.total = workflowConfiguration.iterations;
            iteration.step = activityInfoConfigurationDto.replicas;
            iteration.start = i;

            request.activityName = activityInfoConfigurationDto.activityName;
            if (activityInfoConfigurationDto.replicas > 1) {
                request.activityName += "_replica_" + (i + 1) + "_of_" + activityInfoConfigurationDto.replicas;
            }
            request.iterations = iteration;
            request.task = activityInfoConfigurationDto.task;
            request.ports = mapPorts(workflowConfiguration.workflowName,activityInfoConfigurationDto.ports);

            request.containerImage= activityInfoConfigurationDto.containerImage;
            request.mappingsTaskArguments = activityInfoConfigurationDto.mappingsTaskArguments;
            request.mappingsTaskResults = activityInfoConfigurationDto.mappingsTaskResults;

            result.add(request);
        }
        return result;
    }

    private static PortDto[] mapPorts(String workflowName, PortDto[] ports) {
        var result = new PortDto[ports.length];
        for (int i = 0; i < ports.length; i++) {
            PortDto portToMap = ports[i];

            String channel=workflowName;
            if (Objects.equals(portToMap.type, "IN")) {
                channel += portToMap.name + portToMap.channel;
            } else {
                channel += portToMap.channel + portToMap.name;
            }

            var mappedPort = new PortDto();
            mappedPort.channel = channel;
            mappedPort.name = portToMap.name;
            mappedPort.type = portToMap.type;

            result[i] = mappedPort;
        }
        return result;
    }
}
