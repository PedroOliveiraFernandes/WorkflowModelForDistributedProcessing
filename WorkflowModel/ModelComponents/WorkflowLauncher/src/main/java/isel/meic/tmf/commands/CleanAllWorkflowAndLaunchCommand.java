package isel.meic.tmf.commands;

import isel.meic.tmf.commands.launchWorkflow.LaunchWorkflowCommand;

public class CleanAllWorkflowAndLaunchCommand implements ICommand {
    @Override
    public void execute(String[] args) {
        new CleanAllWorkflowCommand().execute(args);
        new LaunchWorkflowCommand().execute(args);
    }
}
