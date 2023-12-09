package org.example;

import java.io.*;

public class App {
    public static void main(String[] args) {
        var arguments = ArgumentsModel.fromArgs(args);
        var mapperTaskInputs = arguments.arguments[0].split(",");
        String inputFileName = mapperTaskInputs[0];
        String pathToInputFile = Utils.PATH_TO_WORK_DIRECTORY + "/" + inputFileName;
        int startLine = Integer.parseInt(mapperTaskInputs[1]);
        int endLine = Integer.parseInt(mapperTaskInputs[2]);
        String outputFileName = arguments.activityName + ".txt";
        String pathToOutputFile = Utils.PATH_TO_WORK_DIRECTORY + "/" + outputFileName;

        try {
            processFile(startLine, endLine, pathToInputFile, pathToOutputFile);
            System.out.println("Word count completed successfully.");
            new ResultsModel(outputFileName).writeToOutputFile();
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private static void processFile(int startLine, int endLine, String inputFile, String outputFile) throws IOException {

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile)); BufferedWriter writer =
                new BufferedWriter(new FileWriter(outputFile))) {
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                if (lineNumber >= startLine && lineNumber <= endLine) {
                    String[] words = line.replaceAll("([^a-zA-Z]{2,}+)"," ").toLowerCase().split("\\s");

//                    String[] words = line.split("\\s+");
                    for (String word : words) {
                        // Remove non-word characters and convert to lowercase
                        word = extractWords(word);
                        if (!word.isEmpty()) {
                            writer.write("(" + word + "," + 1 + ")\n");
                        }
                    }
                }
                lineNumber++;
                if (lineNumber > endLine) {
                    break;
                }
            }
        }
    }

    private static String extractWords(String word) {
        return word.replaceAll("(?<=\\\\W|^)[^a-zA-Z]+|[^a-zA-Z]+(?=\\\\W|$)", "")
                   .toLowerCase();
    }
}
