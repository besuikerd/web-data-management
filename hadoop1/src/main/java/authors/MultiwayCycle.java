package authors;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.*;

/**
 * Created by maarten on 9-6-15.
 */
public class MultiwayCycle {
    public static class MultiwayCycleMapper1 extends
            Mapper<Object, Text, Text, Text> {

        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {

            int b = context.getConfiguration().getInt("b", 0);
            System.out.println("Mapping1: " + value);
            String[] splits = value.toString().split("\t");

            for(int i=0; i<b; i++) {
                if(splits[0].hashCode() < splits[1].hashCode()) {
                    context.write(new Text(""), new Text(splits[0] + "\t" + splits[1] + "\t$"));
                }
                else {
                    context.write(new Text(""), new Text(splits[1] + "\t" + splits[0] + "\t$"));
                }
            }
        }
    }
    public static class MultiwayCycleMapper2 extends
            Mapper<Object, Text, Text, Text> {

        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {

            int b = context.getConfiguration().getInt("b", 0);
            System.out.println("Mapping2: " + value);
            String[] splits = value.toString().split("\t");

            for(int i=0; i<b; i++) {
                if(splits[0].hashCode() < splits[1].hashCode()) {
                    context.write(new Text(""), new Text(splits[0] + "\t$" + "\t" + splits[1]));
                }
                else {
                    context.write(new Text(""), new Text(splits[1] + "\t$" + "\t" + splits[0]));
                }
            }
        }
    }
    public static class MultiwayCycleMapper3 extends
            Mapper<Object, Text, Text, Text> {

        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {

            int b = context.getConfiguration().getInt("b", 0);
            System.out.println("Mapping3: " + value);
            String[] splits = value.toString().split("\t");

            for(int i=0; i<b; i++) {
                if(splits[0].hashCode() < splits[1].hashCode()) {
                    context.write(new Text(""), new Text("$" + "\t" + splits[0] + "\t" + splits[1]));
                }
                else {
                    context.write(new Text(""), new Text("$" + "\t" + splits[1] + "\t" + splits[0]));
                }
            }
        }
    }

    //TODO Dimensions always equal to eachother, allowed?
    public static class MultiwayCyclePartitioner extends Partitioner<Text, Text> {
        private Map<String, Integer> repetitions = new HashMap<>();
        public int getPartition(Text key, Text value, int numReduceTasks) {

            Integer val;
            if((val = repetitions.putIfAbsent(value.toString(), 0)) != null){
                repetitions.put(value.toString(), ++val);
            } else{
                val = 0;
            }
            String[] splits = value.toString().split("\t");

            int b = (int)(Math.pow(numReduceTasks, 1.0 / 3.0));
            String A = splits[0];
            int aHash = Math.abs(A.hashCode() % b);
            String B = splits[1];
            int bHash = Math.abs(B.hashCode() % b);
            String C = splits[2];
            int cHash = Math.abs(C.hashCode() % b);

            if(A.equals("$")) {
                aHash = val;
            }
            else if(B.equals("$")) {
                bHash = val;
            }
            else if(C.equals("$")) {
                cHash = val;
            }

            int result = aHash * b * b + bHash * b + cHash;
            System.out.println(value + " partition: " + result);
            return result;
        }
    }



    public static class MultiwayCycleReducer extends
            Reducer<Text, Text, Text, Text> {

        private boolean equalsWild(String a, String b, String c){
            return (a.equals(b) && c.equals("$")) || (b.equals(c) && a.equals("$")) || (c.equals(a) && b.equals("$"));
        }

        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            System.out.println("Reducing: " + key + " " + context.getTaskAttemptID().getTaskID().getId());

            ArrayList<String[]> vs0 = new ArrayList<>();
            ArrayList<String[]> vs1 = new ArrayList<>();
            ArrayList<String[]> vs2 = new ArrayList<>();

            for(Text t : values) {
                String[] splits = t.toString().split("\t");
                if(splits[0].equals("$"))
                    vs0.add(splits);
                else if(splits[1].equals("$"))
                    vs1.add(splits);
                else if(splits[2].equals("$"))
                    vs2.add(splits);
            }

            //Checking permutations
            ArrayList<String> result = new ArrayList<>();
            for(int i=0; i<vs0.size(); i++) {
                for(int j=0; j<vs1.size(); j++) {
                    for(int k=0; k<vs2.size(); k++) {
                        if(     equalsWild(vs0.get(i)[0], vs1.get(j)[0], vs2.get(k)[0]) &&
                                equalsWild(vs0.get(i)[1], vs1.get(j)[1], vs2.get(k)[1]) &&
                                equalsWild(vs0.get(i)[2], vs1.get(j)[2], vs2.get(k)[2])) {
                            String[] cycle = vs0.get(i).clone();
                            cycle[0] = vs1.get(j)[0];
                            result.add(String.join("\t", cycle));
                        }
                    }
                }
            }
            for(String s : result) {
                System.out.println(s);
                context.write(new Text(s), new Text(""));
            }
        }
    }
}