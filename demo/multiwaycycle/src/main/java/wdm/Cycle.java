package wdm;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.util.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * Created by maarten on 30-5-15.
 */
public class Cycle {
    public static class CycleMapper extends
            Mapper<Object, Text, Text, Text> {

        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {

            Scanner line = new Scanner(value.toString());
            line.useDelimiter("\t");

            LinkedList<String> nodes = new LinkedList<String>();
            LinkedList<String> nodesR = new LinkedList<String>();

            while(line.hasNext()) {
                String node = line.next();
                nodes.addFirst(node);
                nodesR.addLast(node);
            }
            Text key1 = new Text(nodes.pop());
            Text v1 = new Text(String.join("\t", nodes));
            context.write(key1, v1);

            Text key2 = new Text(nodesR.pop());
            Text v2 = new Text(String.join("\t", nodesR));
            context.write(key2, v2);
        }
    }

    public static class CycleReducer extends
            Reducer<Text, Text, Text, Text> {

        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
//            System.out.println(key);
            LinkedList<String> nodes = new LinkedList<String>();
            for(Text val: values) {
                nodes.add(val.toString());
            }

            LinkedList<String> result = new LinkedList<String>();
            for(String val: nodes) {
//                result.add(val);
                for(String val2: nodes) {
//                    result.add(val.split("\t")[0] + "\t" + val2);
                    context.write(new Text(val.split("\t")[0]), new Text(key + "\t" + val2));
                }
            }
        }
    }


    public static class CycleDupMapper extends
            Mapper<Object, Text, Text, Text> {

        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {
            context.write(value, value);
        }
    }
    public static class CycleDupReducer extends
            Reducer<Text, Text, Text, Text> {

        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            context.write(key, new Text());
//            context.write(key, null);
        }
    }

    public static class CycleDetectMapper extends
            Mapper<Object, Text, Text, Text> {

        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {

//            System.out.println("mapping: " + value.toString());

            String[] vs = value.toString().trim().split("\t");
            if(!vs[0].equals(vs[vs.length - 1])) {
                return;
            }

            Arrays.sort(vs);

            String prev = "";
            boolean hasDuplicates = false;
            for(int i=1; i<vs.length; i++) {
                hasDuplicates |= prev.equals(vs[i]);
                prev = vs[i];
            }

            if(!hasDuplicates) {
                context.write(new Text(StringUtils.join("\t", vs)), new Text());
            }
        }
    }
    public static class CycleDetectReducer extends
            Reducer<Text, Text, Text, Text> {

        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            context.write(key, new Text());
        }
    }
}
