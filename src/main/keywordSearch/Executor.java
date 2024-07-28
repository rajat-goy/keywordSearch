package keywordSearch;

import utils.Pair;
import utils.StaticVarManger;

import java.io.Serializable;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Executor {
    private static final int NEW_QUERY = 1;
    private static final int CHECK_RESULT = 2;
    private static final int EXIT_CONSOLE = 3;
    private static ConcurrentLinkedQueue<Pair<Integer, String>> tokens;
    private static String filePath;
    private static ConcurrentHashMap<Integer, String> resultMap;

    public static void inputQuery(String keyword) {

        Integer queryId = StaticVarManger.getQueryId();
        tokens.add(new Pair(queryId,keyword));
        System.out.println("Query Id for "+keyword+" is : "+queryId.toString());
        tokens.notifyAll();
    }

    public static void outputResult(int queryId) {

    }

    public static void main(String[] args) {

        tokens = new ConcurrentLinkedQueue<>();
        resultMap = new ConcurrentHashMap<>();
        Crawler crawler = new Crawler();
        Finder finder = new Finder(tokens, filePath, resultMap);


        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 1; i <= 5; i++) {
            executor.submit(finder);
        }
        crawler.start();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            int type = scanner.nextInt();

            switch (type) {
                case NEW_QUERY:
                    String keywordToSearch = scanner.next();
                    inputQuery(keywordToSearch);
                    break;
                case CHECK_RESULT:
                    int queryId = scanner.nextInt();
                    outputResult(queryId);
                    break;

                case EXIT_CONSOLE:
                    System.out.println("Exiting Program...");
                    executor.shutdown();
                    System.exit(0);
                    break;

                default:
                    break;
            }
        }

    }
}
