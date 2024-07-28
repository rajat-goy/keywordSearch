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


    Finder() {

    }

    @Override
    public void run() {
        while (true) {
            try {
                Pair<Integer, String> query = StaticVarManger.tokens.poll();
                if(query == null) {
                        Thread.sleep(500);
                        continue;
                }
                File file = new File(StaticVarManger.getResultPath());
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                String line;
                while ((line = br.readLine()) != null) {
                    String[] lineArr = line.split(":", 2);
                    if (lineArr[0].equalsIgnoreCase(query.getSecond())) {
                        StaticVarManger.resultMap.put(query.getFirst(), lineArr[1]);
                        break;
                    }
                }
                if (!StaticVarManger.resultMap.containsKey(query.getFirst()))
                    StaticVarManger.resultMap.put(query.getFirst(), "Not Found");
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}