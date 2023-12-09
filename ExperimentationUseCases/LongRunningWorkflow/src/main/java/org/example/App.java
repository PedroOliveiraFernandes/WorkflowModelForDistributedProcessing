package org.example;

import org.example.workflowEssentials.ArgumentsModel;
import org.example.workflowEssentials.ResultsModel;
import org.example.workflowEssentials.Utils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class App {
    public static void main(String[] args) throws IOException, InterruptedException {
        var arguments = ArgumentsModel.fromArgs(args);

        var timeToWait = Integer.parseInt(arguments.constants[0]);
        if (timeToWait > 0) {
            Thread.sleep(timeToWait * 1000L);
        }

        String result = "";
        if (arguments.arguments.length > 0 && !arguments.arguments[0].isEmpty()) {
            result = arguments.arguments[0] + "-";
        }
        result += arguments.activityName + "(" + arguments.currentIteration + ")";
        if (arguments.constants.length > 1) {
            writeToFile(arguments, result);
        }

        new ResultsModel(result).writeToOutputFile();
    }

    private static void writeToFile(ArgumentsModel arguments, String result) throws IOException {
        String fileName = Utils.PATH_TO_WORK_DIRECTORY + "/" + arguments.constants[1];
        System.out.println(fileName);
        Path pathToTaskResultsFile = Paths.get(fileName);
        Files.createDirectories(pathToTaskResultsFile.getParent());
        // Write JSON data to a.json file
        boolean shouldAppend = arguments.currentIteration > 0;
        try (FileWriter fileWriter = new FileWriter(fileName, shouldAppend)) {
            fileWriter.write(result + "\n");
        }
    }
}
