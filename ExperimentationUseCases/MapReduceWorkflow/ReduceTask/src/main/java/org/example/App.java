package org.example;

import java.io.*;
import java.util.*;

public class App {
    public static void main(String[] args) throws IOException {
        var arguments = ArgumentsModel.fromArgs(args);
        var inargs = arguments.arguments[0].split(",");
        var inputFilePath = Utils.PATH_TO_WORK_DIRECTORY + "/" + inargs[0];
        char charInit = inargs[1].charAt(0);
        char charEnd = inargs[2].charAt(0);

        String outputFile = arguments.activityName+".txt";

        String outputFilePath = Utils.PATH_TO_WORK_DIRECTORY + "/" + outputFile;
        String tempOutputFilePath =  "./temp" + outputFile;

        var map = countWordsInLetterRange(inputFilePath, charInit, charEnd);
        printResultMap(tempOutputFilePath, map);
        sortFile(tempOutputFilePath,outputFilePath,1000);
        new ResultsModel(outputFile).writeToOutputFile();
        new File(tempOutputFilePath).delete();

    }

    private static Map<String, Integer> countWordsInLetterRange(String filename, char initChar, char endChar) throws IOException {
        Map<String, Integer> wordCountMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) continue;

                var values = line.replace("(", "")
                                 .replace(")", "")
                                 .split(",");
                String key = values[0];
                Integer value = Integer.parseInt(values[1]);

                if (key.charAt(0) < initChar || key.charAt(0) > endChar) continue;

                if (!wordCountMap.containsKey(key)) {
                    wordCountMap.put(key, value);
                } else {
                    var currValue = wordCountMap.get(key);
                    wordCountMap.replace(key, currValue + value);
                }
            }
        }
        return wordCountMap;
    }

    private static void printResultMap(String outputFile, Map<String, Integer> words) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (Map.Entry<String, Integer> entry : words.entrySet()) {
                writer.write("(" + entry.getKey() + "," + entry.getValue() + ")\n");
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
                    Collections.sort(lines);
                    File chunkFile = writeChunk(lines);
                    chunks.add(chunkFile);
                    lines.clear();
                }
            }

            if (!lines.isEmpty()) {
                Collections.sort(lines);
                File chunkFile = writeChunk(lines);
                chunks.add(chunkFile);
            }
        }

        return chunks;
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
        PriorityQueue<ChunkReader> pq = new PriorityQueue<>(Comparator.comparing(ChunkReader::peek));

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
