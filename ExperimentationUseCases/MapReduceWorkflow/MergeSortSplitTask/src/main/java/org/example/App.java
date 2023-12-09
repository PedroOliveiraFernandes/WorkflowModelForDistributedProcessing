package org.example;

import java.io.*;
import java.util.*;

public class App {

    static SortedSet<Character> firstLettersInAWord = new TreeSet<>();

    public static void main(String[] args) {
        var arguments = ArgumentsModel.fromArgs(args);
        String outputFile = arguments.activityName + ".txt";
        String mergeOutputTempFile = "./temp" + outputFile;
        String outputFileAbsolut = Utils.PATH_TO_WORK_DIRECTORY + "/" + outputFile;
        int numParts = 0;
        if (arguments.constants.length > 0 && !arguments.constants[0].isEmpty()) {
            numParts = Integer.parseInt(arguments.constants[0]);
        }

        List<String> fileNames = new ArrayList<>();
        for (int i = 0; i < arguments.arguments.length; i++) {
            fileNames.add(Utils.PATH_TO_WORK_DIRECTORY + "/" + arguments.arguments[i]);
        }

        try {
            mergeFiles(mergeOutputTempFile, fileNames);
            sortFile(mergeOutputTempFile, outputFileAbsolut, 1000);
            String[] result = divideAlphabetLettersInParts(numParts, outputFile);
            new ResultsModel(result).writeToOutputFile();
            new File(mergeOutputTempFile).delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String[] divideAlphabetLettersInParts(int numParts, String outputFile) {
        var firstLettersInAWordArray = firstLettersInAWord.toArray(Character[]::new);
        List<String> result = new ArrayList<>();
        if (numParts > 0 && firstLettersInAWordArray.length > 0) {
            if (firstLettersInAWordArray.length == 1 || numParts == 1 || firstLettersInAWordArray.length < numParts) {
                for (Character character : firstLettersInAWordArray) {
                    result.add(outputFile + ',' + character + "," + character);
                }
                if (result.size() < numParts) {
                    for (int i = 0; i < numParts - result.size(); i++) {
                        result.add("");
                    }
                }
                return result.toArray(String[]::new);
            }
            int letterRange = firstLettersInAWordArray.length / numParts;
            char currentStart = firstLettersInAWordArray[0];
            for (int i = 1; i <= numParts; ++i) {
                char endRange;
                if (i == numParts) {
                    endRange = firstLettersInAWordArray[firstLettersInAWordArray.length - 1];
                    result.add(outputFile + ',' + currentStart + "," + endRange);
                    break;
                } else {
                    endRange = firstLettersInAWordArray[i * letterRange];
                    result.add(outputFile + ',' + currentStart + "," + endRange);
                    currentStart = (firstLettersInAWordArray[i * letterRange + 1]);
                }
            }
        }
        return result.toArray(String[]::new);
    }


    private static void mergeFiles(String outputFile, List<String> fileNames) throws IOException {
        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(outputFile))) {

            for (String filename : fileNames) {
                try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                    String line;

                    while ((line = reader.readLine()) != null) {
                        if (!line.isEmpty()) {
                            firstLettersInAWord.add(line.charAt(1));
                            writer.write(line + "\n");
                        }
                    }
                }
            }
        }
    }

    //SORT
    public static void sortFile(String inputFilename, String outputFilename, int chunkSize) throws IOException {
        List<File> chunks = createSortedChunks(inputFilename, chunkSize);
        mergeChunks(chunks, outputFilename);
    }

    public static List<File> createSortedChunks(String inputFilename, int chunkSize) throws IOException {
        List<File> chunks = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(inputFilename))) {
            String line;
            List<String> lines = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                lines.add(line);

                if (lines.size() == chunkSize) {
                    Collections.sort(lines, App::lineComparator);
                    File chunkFile = writeChunk(lines);
                    chunks.add(chunkFile);
                    lines.clear();
                }
            }

            if (!lines.isEmpty()) {
                Collections.sort(lines, App::lineComparator);
                File chunkFile = writeChunk(lines);
                chunks.add(chunkFile);
            }
        }

        return chunks;
    }

    private static int lineComparator(String in1, String in2) {
        var auxin1 = in1.replace("(", "")
                        .replace(")", "")
                        .split(",");
        var auxin2 = in2.replace("(", "")
                        .replace(")", "")
                        .split(",");
        if (Integer.parseInt(auxin1[1]) == Integer.parseInt(auxin2[1])) {
            return auxin1[0].compareTo(auxin2[0]);
        }
        return Integer.parseInt(auxin2[1]) - Integer.parseInt(auxin1[1]);
    }

    public static File writeChunk(List<String> lines) throws IOException {
        File chunkFile = File.createTempFile("chunk", ".tmp");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(chunkFile))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        }
        return chunkFile;
    }

    public static void mergeChunks(List<File> chunks, String outputFilename) throws IOException {
        PriorityQueue<ChunkReader> pq = new PriorityQueue<>((c1, c2) -> lineComparator(c1.currentLine, c2.currentLine));

        for (File chunk : chunks) {
            pq.add(new ChunkReader(chunk));
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilename))) {
            while (!pq.isEmpty()) {
                ChunkReader reader = pq.poll();
                String line = reader.pop();

                bw.write(line);
                bw.newLine();

                if (reader.hasNext()) {
                    pq.add(reader);
                } else {
                    reader.close();
                }
            }
        }

        for (File chunk : chunks) {
            chunk.delete();
        }
    }

    static class ChunkReader {
        private BufferedReader br;
        private String currentLine;

        public ChunkReader(File file) throws IOException {
            this.br = new BufferedReader(new FileReader(file));
            this.currentLine = br.readLine();
        }

        public String peek() {
            return currentLine;
        }

        public String pop() throws IOException {
            String line = currentLine;
            currentLine = br.readLine();
            return line;
        }

        public boolean hasNext() {
            return currentLine != null;
        }

        public void close() throws IOException {
            br.close();
        }
    }
}

