package keywordSearch;
import java.io.BufferedReader;
import java.io.File;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;
import utils.Pair;

public class Crawler extends Thread{
    static String filePath = "../../results.txt";
    static String crawlPath = "../../output";
    Set<String> crawledFiles;
    HashMap<String, List<Pair<String, Integer>>> resultData = new HashMap<>();
    // keyword: pairs<file, line>
    // output: list of line, file with keyword using multithreading

    Crawler() {
        crawledFiles = new HashSet<>();
    }
    public void run()
    {
        ExecutorService executorService = Executors.newCachedThreadPool();
        while(true) {
            List<String> newFilesList = getNewFiles();

            newFilesList.forEach(filename -> {
                executorService.submit(() -> {
                    crawlSingleFile(filename);
                });
            });


        }
    }

    private List<String> getNewFiles() {
        File directory = new File(crawlPath);
        File[] files = directory.listFiles();
        List<String> newFilesList = new ArrayList<String>();

        if(files != null) {
            newFilesList = Arrays.stream(files)
                    .map(File::getName)
                    .filter(fileName -> !crawledFiles.contains((fileName)))
                    .collect(Collectors.toList());
        }

        return newFilesList;
    }
    private void addToHashmap(String keyword, String fileName, Integer lineno) {

    }
    private void crawlSingleFile(String fileName) {
        String targetPath = crawlPath + "/" + fileName;
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

}
