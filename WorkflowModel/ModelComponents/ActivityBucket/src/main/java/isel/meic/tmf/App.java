package isel.meic.tmf;

import isel.meic.tmf.api.rest.RestApi;
import isel.meic.tmf.container.manager.DockerApiRepository;
import isel.meic.tmf.models.BrokerInfoDto;
import isel.meic.tmf.service.Service;

public class App {
    private static final int DEFAULT_API_PORT = 8081;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Not enough arguments passed to ActivityBucket.\nPass, as the first argument, the path" +
                                       " to the working directory of the workflow.\nPass, as the second argument, the" +
                                       " path to the local \"docker.sock\", like \"unix:///var/run/docker.sock\"");
            return;
        }
        String sharedDirectory = args[0];
        var brokerInfo = extractBrokerInfo(args);
        String dockerHost = getDockerHost(args);
        int apiPort = extractApiPort(args);

        DockerApiRepository dockerApiRepository = new DockerApiRepository(dockerHost, sharedDirectory);
        var service = new Service(dockerApiRepository, brokerInfo);
        new RestApi(service, apiPort).start();

        System.out.println("Started ActivityBucket service in port:" + apiPort);
    }

    private static int extractApiPort(String[] args) {
        if (args.length < 3) {
            return DEFAULT_API_PORT;
        }
        try {
            return Integer.parseInt(args[2]);
        }catch (NumberFormatException e){
            return DEFAULT_API_PORT;
        }
    }

    private static BrokerInfoDto extractBrokerInfo(String[] args) {
        var splittedInputBrokerInfo = args[1].split(":");
        var result = new BrokerInfoDto();
        result.host = splittedInputBrokerInfo[0];
        result.port = Integer.parseInt(splittedInputBrokerInfo[1]);
        return result;
    }

    private static String getDockerHost(String[] args) {
        var isRunningEnvironmentWindows = System.getProperty("os.name")
                                                .toLowerCase()
                                                .contains("windows");
        String dockerHost;

        if (isRunningEnvironmentWindows) {
            dockerHost = "tcp://localhost:2375";
        } else {
            dockerHost = "unix:///var/run/docker.sock";
        }
        return dockerHost;
    }
}