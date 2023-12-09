package isel.meic.tmf;

import isel.meic.tmf.commands.*;
import isel.meic.tmf.commands.launchWorkflow.LaunchWorkflowCommand;

public class App {
    public static void main(String[] args) {

        if(args.length<3){
            System.out.println("Not enough armuments have been provided");
            return;
        }

        ICommand command;
        switch (args[2]) {
            case "launch":
                command = new LaunchWorkflowCommand();
                break;
            case "states":
                command = new ShowStatesCommand();
                break;
            case "deleteActivities":
                command = new DeleteActivitiesCommand();
                break;
            case "cleanEverything":
                command = new CleanAllWorkflowCommand();
                break;
            case "cleanEverythingAndLaunch":
                command = new CleanAllWorkflowAndLaunchCommand();
                break;
            default:
                System.out.println("Command " + args[2]+ " does not exist");
                command = new StubCommand();
        }
        command.execute(args);
    }
}
