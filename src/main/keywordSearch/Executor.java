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

    public static void inputQuery(String keyword) {

        Integer queryId = StaticVarManger.getQueryId();
        StaticVarManger.tokens.add(new Pair(queryId,keyword));
        System.out.println("Query Id for "+keyword+" is : "+queryId.toString());
    }

    public static void outputResult(Integer queryId) {
        if(StaticVarManger.resultMap.containsKey(queryId)){
            System.out.println("Result for "+queryId.toString()+" is: "+StaticVarManger.resultMap.get(queryId));
        }
        else{
            System.out.println("Result for "+queryId.toString()+" is not computed yet. Check again! ");
        }
    }

    public static void main(String[] args) {

        StaticVarManger.tokens = new ConcurrentLinkedQueue<>();
        StaticVarManger.resultMap = new ConcurrentHashMap<>();
        Crawler crawler = new Crawler();
        Finder finder = new Finder();


        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 1; i <= 5; i++) {
            executor.submit(finder);
        }
        crawler.start();

        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.println("Enter 1 for New Query, 2 for checking Query Results & 3 to Exit the console");
            int type = scanner.nextInt();
            switch (type) {
                case NEW_QUERY:
                    System.out.println("Enter a Keyword to Query");
                    String keywordToSearch = scanner.next();
                    inputQuery(keywordToSearch);
                    break;
                case CHECK_RESULT:
                    System.out.println("Enter Query Id to check Result");
                    int queryId = scanner.nextInt();
                    outputResult(queryId);
                    break;

                case EXIT_CONSOLE:
                    System.out.println("Exiting Program...");
                    executor.shutdownNow();
                    System.exit(0);
                    break;

                default:
                    break;
            }
        }

    }
}
