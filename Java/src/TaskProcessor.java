import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class TaskProcessor implements Runnable {
    private Map<String,Set<String>> subAuthorMap;
    private Map<String,Integer> subIdxMap;
    private Map<Integer,Integer> edgeMap;
    private String[] partSubList;
    private String[] fullSubList;
    private double threshHold = 0.05;
    private int threadNo;
    private String fileName;


    public TaskProcessor(Map<String, Set<String>> subAuthorMap, Map<String, Integer> subIdxMap, String[] partSubList, String[] fullSubList, int threadNo, String fileName) {
        this.subAuthorMap = subAuthorMap;
        this.subIdxMap = subIdxMap;
        this.partSubList = partSubList;
        this.fullSubList = fullSubList;
        this.threadNo = threadNo;
        this.fileName = fileName;
        this.edgeMap = new HashMap<>();
    }


    @Override
    public void run() {
        int len = partSubList.length;
        int status = 0;
        for (String sub1 : partSubList){
            Set<String> authors1 = subAuthorMap.get(sub1);
            for (String sub2 : fullSubList){
                if (!sub1.equals(sub2)){
                    Set<String> authors2 = subAuthorMap.get(sub2);
                    int commonSize = intersection(authors1, authors2);
                    double common1 = commonSize / authors1.size();
                    double common2 = commonSize / authors2.size();
                    if (common1 >= threshHold && common2 >= threshHold && commonSize > 1){
                        edgeMap.put(subIdxMap.get(sub1),subIdxMap.get(sub2));
                    }

                }
            }
            status++;
            //System.out.println("Thread " + threadNo + " : " + status + "/" + len );
        }
        System.out.println("Thread " + threadNo + " generated edgelist. Starting file writing...");
        try {
            fileWrite(edgeMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T> int intersection(Set<T> list1, Set<T> list2) {
        int size = 0;

        for (T t : list1) {
            if(list2.contains(t)) size++;
        }

        return size;
    }

    private void fileWrite(Map<Integer, Integer> map) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter("C:\\Users\\August\\iCloudDrive\\DTU filer\\02805 - Social Graphs and Interactions\\Project\\output\\" + fileName + ".txt"));
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            out.write(entry.getKey() + " " + entry.getValue() + "\n");
        }
        out.close();
        System.out.println("Thread " + threadNo +  "Completed writing to file ");
    }
}
