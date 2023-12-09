package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class App {

    public static void main(String[] args) throws IOException {
        var arguments = ArgumentsModel.fromArgs(args);
        String inputFileName = arguments.constants[0];
        String pathToInputFile = Utils.PATH_TO_WORK_DIRECTORY + "/" + inputFileName;
        int numberOfPartsToSplit = Integer.parseInt(arguments.constants[1]);

        try {
            int totalLines = countLinesInFile(pathToInputFile);
            System.out.println("Total lines are: " + totalLines);
            String[] lineRanges = calculateLineRanges(arguments, numberOfPartsToSplit, totalLines);
            new ResultsModel(lineRanges).writeToOutputFile();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }



    private static int countLinesInFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        int totalLines = 0;

        while (reader.readLine() != null) {
            totalLines++;
        }

        reader.close();
        return totalLines;
    }

    private static String[] calculateLineRanges(ArgumentsModel arguments, int numberOfPartsToSplit, int totalLines) {
        int linesPerPart = totalLines / numberOfPartsToSplit;
        String[] lineRanges = new String[numberOfPartsToSplit];
        int currentLine = 1;
        for (int i = 0; i < numberOfPartsToSplit; i++) {
            var startLine = currentLine;

            int finishLine;
            if (i == numberOfPartsToSplit - 1) finishLine = totalLines;
            else {
                finishLine = currentLine + linesPerPart;
                currentLine += linesPerPart + 1;
            }
            lineRanges[i] = "" + arguments.constants[0] + "," + startLine + "," + finishLine;
            System.out.println("Part " + (i + 1) + ": Start Line = " + startLine + ", Finish Line = " + finishLine);
        }
        return lineRanges;
    }
}
