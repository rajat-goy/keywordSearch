package utils;

public class StaticVarManger {
    static String resultPath = "results.txt";
    static String result2 = "results2.txt";
    static String crawlPath = "output";
    static final Object lock = new Object();

    static Integer queryId=0;

    public static void swapResultPaths() {
        synchronized (lock) {
            String temp = result2;
            result2 = resultPath;
            resultPath = temp;
        }
    }

    public static String getResultPath() {
        synchronized (lock) {
            return resultPath;
        }

    }
    public static String getResult2() {
        synchronized (lock) {
            return result2;
        }
    }
    public static String getCrawlPath(){
        return crawlPath;
    }

    public static Integer getQueryId(){
        synchronized (queryId){
            queryId++;
            return queryId;
        }
    }
}
