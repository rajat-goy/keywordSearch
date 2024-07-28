package keywordSearch;

import java.io.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import utils.StaticVarManger;
import utils.Pair;


public class Crawler extends Thread {
    Set<String> crawledFiles;
    static ConcurrentHashMap<String, List<Pair<String, Integer>>> resultData = new ConcurrentHashMap<>();

    // keyword: pairs<file, line>
    // output: list of line, file with keyword using multithreading

    Crawler() {
        crawledFiles = new HashSet<>();
    }

    public void run() {
        while (true) {
            try {
                ExecutorService executorService = Executors.newCachedThreadPool();
                List<String> newFilesList = getNewFiles();

                newFilesList.forEach(filename -> {
                    executorService.submit(() -> {
                        crawlSingleFile(filename);
                    });
                });

                executorService.shutdown();
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                if (!resultData.isEmpty()) {
                    updateResult();
                    newFilesList.forEach(file -> crawledFiles.add(file));
                    StaticVarManger.swapResultPaths();

                    Thread.sleep(10_000);
                }


            } catch (Exception e) {
                ;
            }
        }
    }

    private List<String> getNewFiles() {
        File directory = new File(StaticVarManger.getCrawlPath());
        File[] files = directory.listFiles();
        List<String> newFilesList = new ArrayList<String>();


        if (files != null) {
            newFilesList = Arrays.stream(files)
                    .map(File::getName)
                    .filter(fileName -> !crawledFiles.contains((fileName)))
                    .collect(Collectors.toList());
        }

        return newFilesList;
    }

    private void addToHashmap(String keyword, String fileName, Integer lineno) {
        resultData.putIfAbsent(keyword, Collections.synchronizedList(new ArrayList<Pair<String, Integer>>()));
        resultData.get(keyword).add(new Pair(fileName, lineno));
    }

    private void crawlSingleFile(String fileName) {
        String targetPath = StaticVarManger.getCrawlPath() + "/" + fileName;
        try (BufferedReader reader = new BufferedReader(new FileReader(targetPath))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                final int currentLineNumber = ++lineNumber;
                String[] words = line.split("\\W+"); // Split line into words

                Arrays.stream(words).filter(word -> !word.isEmpty()).forEach(word -> addToHashmap(word, fileName, currentLineNumber));

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateResult() {
        File file = new File(StaticVarManger.getResultPath());
        File outputFile = new File(StaticVarManger.getResult2());

        try (BufferedReader reader = new BufferedReader(new FileReader(file));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                // Write the current line to results2.txt
                writer.write(line);

                // Extract the keyword (the first word before the colon)
                String keyword = line.split(":")[0].trim();

                // Check if the keyword exists in the hashmap
                if (resultData.containsKey(keyword)) {
                    writer.write(formatValue(resultData.get(keyword)));
                    resultData.remove(keyword);
                }
                writer.newLine();
            }

            for (Map.Entry<String, List<Pair<String, Integer>>> entry : resultData.entrySet()) {
                writer.write(entry.getKey() + ":" + formatValue(entry.getValue()));
                writer.newLine();
            }
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter bf = new BufferedWriter(new FileWriter(file))) {
            for (Map.Entry<String, List<Pair<String, Integer>>> entry : resultData.entrySet()) {
                bf.write(entry.getKey() + ":" + formatValue(entry.getValue()));
                bf.newLine();
            }
            bf.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData.clear();
    }

    private String formatValue(List<Pair<String, Integer>> pairs) {
        StringBuilder sb = new StringBuilder();
        for (Pair<String, Integer> pair : pairs) {
            sb.append("<").append(pair.getFirst()).append(", ").append(pair.getSecond()).append("> ");
        }
        return sb.toString().trim();
    }

}
