import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static BufferedReader reader;


    public static void main(String[] args) throws IOException {
        Map<String,Integer> subIdxMap = new HashMap<>();
        Map<String,Set<String>> authorSubMap = new HashMap<>();

        double threshHold = 0.2;

        Reader in = new FileReader("E:\\Reddit\\redditbucketaugust\\complete<.csv");
        Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);

        // Iterate file
        int i = -1;

        outer: for (CSVRecord record : records) {
            String subredditName = record.get(1);
            String authorName = record.get(0);
            // Create index map
            subIdxMap.put(subredditName, i++);

            // Create map of authors for each subreddit
            if (authorSubMap.containsKey(subredditName)){
                authorSubMap.get(subredditName).add(authorName);
            } else {
                Set<String> set = new HashSet<>();
                set.add(authorName);
                authorSubMap.put(subredditName,set);
            }

        }
        in.close();
        System.out.println("Maps created.");

        // Get array of subreddits only
        String[] subredditArray = subIdxMap.keySet().toArray(new String[subIdxMap.keySet().size()]);
        int batchLength = subredditArray.length / 8;
        String[][] batches = chunkArray(subredditArray,batchLength);
        System.out.println("Batches created.");

        // Setup tasks tasks
        int threadNo = 0;
        String fileNameBase = "ouput";
        ExecutorService threadPool = Executors.newFixedThreadPool(9);
        for (String[] batch : batches) {
            String fileName = fileNameBase + threadNo;
            threadPool.submit(new TaskProcessor(authorSubMap,subIdxMap, batch, subredditArray,threadNo, fileName));
            threadNo++;
        }
        threadPool.shutdown();
    }

    public static String[][] chunkArray(String[] array, int chunkSize) {
        int numOfChunks = (int)Math.ceil((double)array.length / chunkSize);
        String[][] output = new String[numOfChunks][];
        for(int i = 0; i < numOfChunks; ++i) {
            int start = i * chunkSize;
            int length = Math.min(array.length - start, chunkSize);
            String[] temp = new String[length];
            System.arraycopy(array, start, temp, 0, length);
            output[i] = temp;
        }
        return output;
    }

}
