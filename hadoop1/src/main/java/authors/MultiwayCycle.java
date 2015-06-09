package authors;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by maarten on 9-6-15.
 */
public class MultiwayCycle {
    public static class MultiwayCycleMapper1 extends
            Mapper<Object, Text, Text, Text> {

        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {

            System.out.println("Mapping1: " + value);
            String[] splits = value.toString().split("\t");

            for(int i=0; i<3; i++) {
                Text nKey = new Text(splits[0] + "\t" + splits[1] + "\t$");
                context.write(nKey, value);
            }
        }
    }
    public static class MultiwayCycleMapper2 extends
            Mapper<Object, Text, Text, Text> {

        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {

            System.out.println("Mapping2: " + value);
            String[] splits = value.toString().split("\t");

            for(int i=0; i<3; i++) {
                Text nKey = new Text(splits[0] + "\t$" + "\t" + splits[1]);
                context.write(nKey, value);
            }
        }
    }
    public static class MultiwayCycleMapper3 extends
            Mapper<Object, Text, Text, Text> {

        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {

            System.out.println("Mapping2: " + value);
            String[] splits = value.toString().split("\t");

            for(int i=0; i<3; i++) {
                Text nKey = new Text("$" + "\t" + splits[0] + "\t" + splits[1]);
                context.write(nKey, value);
            }
        }
    }

    public static class MultiwayCyclePartitioner extends Partitioner<Text, Text> {
        private Map<String, Integer> repetitions = new HashMap<>();
        public int getPartition(Text key, Text value, int numReduceTasks) {

            Integer val;
            if((val = repetitions.putIfAbsent(key.toString(), 0)) != null){
                repetitions.put(key.toString(), ++val);
            } else{
                val = 0;
            }
            String[] splits = key.toString().split("\t");  //Split the key on tab

            int m = (int)(Math.pow(numReduceTasks, 1.0 / 3.0));
            String A = splits[0];
            int aHash = Math.abs(A.hashCode() % m);
            String B = splits[1];
            int bHash = Math.abs(B.hashCode() % m);
            String C = splits[2];
            int cHash = Math.abs(C.hashCode() % m);

            int result = 0;
            if(A.equals("$")) {
                result =  val * 3 * m + bHash * m + cHash;
            }
            else if(B.equals("$")) {
                result = aHash * 3 * m + val * m + cHash;
            }
            else if(C.equals("$")) {
                result = aHash * 3 * m + bHash * m + val;
            }
            else {
                System.out.println("ERRORORORORORORORORORORRO");
            }
            System.out.println(key + " partition: " + result);
            return result;
        }
    }



    public static class MultiwayCycleReducer extends
            Reducer<Text, Text, Text, Text> {

        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            System.out.println("Reducing: " + key);
            for(Text t : values) {
                System.out.println(t);
            }
        }
    }
}
