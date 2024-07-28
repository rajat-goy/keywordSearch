package keywordSearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Finder implements Runnable {
    private final ConcurrentLinkedQueue<String> tokens;
    public final Object monitor;
    private final String filePath;
    private final ConcurrentHashMap<Integer, String> resultMap;
    private final Integer qId;

    Finder(ConcurrentLinkedQueue<String> tokens, Object monitor, String filePath, Integer qId, ConcurrentHashMap<Integer, String> resultMap) {
        this.tokens = tokens;
        this.monitor = monitor;
        this.filePath = filePath;
        this.qId = qId;
        this.resultMap = resultMap;
    }

    @Override
    public void run() {
        while(true) {
            try{
                String tokenToSearch = tokens.poll();
                if(tokenToSearch == null) {
                    synchronized(this.monitor) {
                            this.monitor.wait();
                            continue;
                    }
                }
                File file=new File(this.filePath);
                FileReader fr=new FileReader(file);
                BufferedReader br=new BufferedReader(fr);
                String line;
                while((line=br.readLine())!=null)
                {
                    String[] lineArr = line.split(":", 1);
                    if(lineArr[0].equalsIgnoreCase(tokenToSearch)) {
                        resultMap.put(qId, lineArr[1]);
                        break;
                    }
                }
                if(!resultMap.containsKey(qId))
                    resultMap.put(qId, "Not Found");
            } catch(InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}