package isel.meic.tmf.configuration;

import isel.meic.tmf.configuration.models.*;
import isel.meic.tmf.utils.ActivityLogger;

import java.util.*;
import java.util.stream.Collectors;

public class ActivityConfiguration {
    private final ActivityLogger logger = ActivityLogger.getInstance();

    public static final String PORT_TYPE_IN = "IN";
    public static final String PORT_TYPE_OUT = "OUT";
    private final ActivityArgumentsModel configurationModel;

    public ActivityConfiguration(ActivityArgumentsModel configurationModel) {
        this.configurationModel = configurationModel;
    }

    public List<PortDto> getInputPorts() {
        return filterPortsByType(PORT_TYPE_IN);
    }

    public List<PortDto> getOutputPorts() {
        return filterPortsByType(PORT_TYPE_OUT);
    }

    private List<PortDto> filterPortsByType(String portType) {
        var ports = configurationModel.ports;
        if (ports == null || ports.length == 0) return List.of();

        return Arrays.stream(ports)
                     .filter(p -> p.type != null && p.type.equals(portType))
                     .collect(Collectors.toList());
    }

    public BrokerInfoDto getBrokerInfo() {
        return configurationModel.brokerInfo;
    }

    public int getTaskReultIndexByPortName(String portName) {
        try {
            return Arrays.stream(configurationModel.mappingsTaskResults)
                         .filter(mapping -> mapping.portName.equals(portName))
                         .findFirst()
                         .get().index;
        } catch (NoSuchElementException e) {
            logger.debug("Mapping for port with name: " + portName + " not defined");
            throw e;
        }
    }

    public int getTaskArgumentIndexByPortName(String portName) {
        Optional<MappingDto> portMappingDto = Arrays.stream(configurationModel.mappingsTaskArguments)
                                                    .filter(mapping -> mapping.portName.equals(portName))
                                                    .findFirst();
        if (portMappingDto.isEmpty()) {
            String message = "No \"mappingsTaskArguments\" defined for port with name: " + portName;
            logger.info(message);
            throw new RuntimeException(message);
        }
        return portMappingDto
                .get().index;
    }

    public String[] getConstantArguments() {
        if (configurationModel.task.constants == null) return new String[0];

        return configurationModel.task.constants;
    }

    public String[] getTaskExecutionCommandArray() {
        if (configurationModel.task.executionCommand == null) {
            throw new RuntimeException("No \"executionCommand\" defined for the task");
        }
        if (configurationModel.task.pathToExecutablesDirectory == null) {
            throw new RuntimeException("\"TASK_EXECUTABLE_DIRECTORY\" environment variable not defined");
        }
        if (configurationModel.task.executable == null) {
            throw new RuntimeException("No \"executable\" defined for the task");
        }

        List<String> commandList = new ArrayList<>();
        commandList.addAll(splitBySpace(configurationModel.task.executionCommand));

        String pathToExecutable = configurationModel.task.pathToExecutablesDirectory.trim();
        if (pathToExecutable.charAt(pathToExecutable.length() - 1) != '/') {
            pathToExecutable += '/';
        }
        pathToExecutable += configurationModel.task.executable;

        commandList.add(pathToExecutable.trim());
        return commandList.toArray(String[]::new);
    }

    private List<String> splitBySpace(String value) {
        return Arrays.stream(value.split(" "))
                     .collect(Collectors.toList());
    }

    public String getPathToTaskResultsFile() {
        return configurationModel.task.pathToTaskResultsFile;
    }

    public int getTotalNumberOfIterations() {
        return configurationModel.iterations.total;
    }

    public String getWorkflowName() {
        return configurationModel.workflowName;
    }

    public String getActivityName() {
        return configurationModel.activityName;
    }

    public IterationsDto getIterationConfig() {
        return configurationModel.iterations;
    }
}
