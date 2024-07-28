package keywordSearch;

import utils.Pair;
import utils.StaticVarManger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Finder implements Runnable {
    private final ConcurrentLinkedQueue<Pair<Integer, String>> tokens;
    private final String filePath;
    private final ConcurrentHashMap<Integer, String> resultMap;

    Finder(ConcurrentLinkedQueue<Pair<Integer, String>> tokens, String filePath, ConcurrentHashMap<Integer, String> resultMap) {
        this.tokens = tokens;
        this.filePath = filePath;
        this.resultMap = resultMap;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Pair<Integer, String> query = tokens.poll();
                if(query == null) {
                        tokens.wait();
                        continue;
                }
                File file = new File(StaticVarManger.getResultPath());
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                String line;
                while ((line = br.readLine()) != null) {
                    String[] lineArr = line.split(":", 1);
                    if (lineArr[0].equalsIgnoreCase(query.getSecond())) {
                        resultMap.put(query.getFirst(), lineArr[1]);
                        break;
                    }
                }
                if (!resultMap.containsKey(query.getFirst()))
                    resultMap.put(query.getFirst(), "Not Found");
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}