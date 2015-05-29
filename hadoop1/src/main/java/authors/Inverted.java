package authors;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by maarten on 29-5-15.
 */
public class Inverted {

    private static final float D = 3; //TODO set these from job based on input args

    public static class InvertedMapper1 extends Mapper<LongWritable, Text, Text, IntWritable> {

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
//            System.out.println("mapping: " + value.toString());

            Scanner line = new Scanner(value.toString());
            line.useDelimiter(" ");

            FileSplit fileSplit = (FileSplit)context.getInputSplit();
            String filename = fileSplit.getPath().getName();

            while(line.hasNext()) {
                Text k = new Text();
                k.set(line.next() + '\t' + filename);
                context.write(k, new IntWritable(1));
            }
        }
    }

    public static class InvertedReducer1 extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable();
        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int count = 0;
            for (IntWritable val : values) {
                count += val.get();
            }
            result.set(count);
            context.write(key, result);
        }
    }


    public static class InvertedMapper2 extends Mapper<LongWritable, Text, Text, Text> {

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
//            System.out.println("mapping: " + value.toString());

            Scanner line = new Scanner(value.toString());
            line.useDelimiter("\t");

            String term = line.next();
            String doc = line.next();
            String n = line.next();

            context.write(new Text(doc), new Text(term + '\t' + n));
        }
    }

    public static class InvertedReducer2 extends Reducer<Text, Text, Text, Text> {
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            List<String> vs = new ArrayList<String>();
            int N = 0;
            for(Text val : values) {
                vs.add(val.toString());
                N += 1;
            }

            for(String val : vs) {
                String[] va = val.split("\t");
                context.write(new Text(va[0] + '\t' + key.toString()), new Text(va[1] + '\t' + N));
            }
        }
    }

    public static class InvertedMapper3 extends Mapper<LongWritable, Text, Text, Text> {

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            Scanner line = new Scanner(value.toString());
            line.useDelimiter("\t");

            String term = line.next();
            String doc = line.next();
            String n = line.next();
            String N = line.next();

            context.write(new Text(term), new Text(doc + '\t' + n + '\t' + N));
        }
    }
    public static class InvertedReducer3 extends Reducer<Text, Text, Text, Text> {
        private IntWritable result = new IntWritable();
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            List<String> vs = new ArrayList<String>();
            int d = 0;
            for(Text val : values){
                d++;
                vs.add(val.toString());
            }

            for(String val : vs) {
                String[] va = val.split("\t");
                float tf = Float.parseFloat(va[1]) / Float.parseFloat(va[2]);
                float idf = d / D;
                context.write(new Text(key.toString() + '\t' + va[0]), new Text(String.format("%.2f\t%.2f", tf, idf)));
            }
        }
    }
}
