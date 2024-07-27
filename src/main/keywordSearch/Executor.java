package keywordSearch;

import java.util.Scanner;

public class Executor {

    private static final int NEW_QUERY = 1;
    private static final int CHECK_RESULT = 2;
    private static final int EXIT_CONSOLE = 3;

    private static Finder finder;
    private static Crawler crawler;

    public static void main(String[] args) {
        crawler = new Crawler();
        finder = new Finder();
        crawler.start();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            int type = scanner.nextInt();

            switch (type) {
                case NEW_QUERY:
                    String keywordToSearch = scanner.next();
                    break;
                case CHECK_RESULT:
                    int queryId = scanner.nextInt();
                    break;

                case EXIT_CONSOLE:
                    System.exit(0);
                    break;

                default:
                    break;
            }
        }

    }
}
