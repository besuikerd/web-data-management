package authors;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.LinkedList;
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
            Scanner line = new Scanner(value.toString());
            line.useDelimiter("\t");

            Text nKey = new Text(line.next() + "\t" + line.next() + "\t$" );

            context.write(nKey, value);
        }
    }
    public static class MultiwayCycleMapper2 extends
            Mapper<Object, Text, Text, Text> {

        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {

            System.out.println("Mapping2: " + value);
            Scanner line = new Scanner(value.toString());
            line.useDelimiter("\t");

            Text nKey = new Text(line.next() + "\t$" + "\t" + line.next());

            context.write(nKey, value);
        }
    }
    public static class MultiwayCycleMapper3 extends
            Mapper<Object, Text, Text, Text> {

        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {

            System.out.println("Mapping2: " + value);
            Scanner line = new Scanner(value.toString());
            line.useDelimiter("\t");

            Text nKey = new Text("$" + "\t" + line.next() + "\t" + line.next());

            context.write(nKey, value);
        }
    }

    public class MultiwayCyclePartitioner extends Partitioner<Text, Text> {
        int balbl = 0;

        public int getPartition(Text key, Text value, int numReduceTasks) {
//
//            System.out.println(balbl++);
//            String sKey = key.toString();
//            String[] splits=sKey.split("\t");  //Split the key on tab
//
//            int m = (int)(Math.pow(numReduceTasks, 1.0 / 3.0));
//            System.out.println("m: " + m);
//            String A = splits[0];
//            int aHash = Math.abs(A.hashCode() % m);
//            String B = splits[1];
//            int bHash = Math.abs(B.hashCode() % m);
//            String C = splits[2];
//            int cHash = Math.abs(C.hashCode() % m);
//
//            if(A.equals("$")) {
//                for(aHash=0; aHash<m; aHash++) {
//
//                }
//            }
            return (int)(Math.random() * numReduceTasks); //Math.abs(value.toString().hashCode() % numReduceTasks);
//            return 0;
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
