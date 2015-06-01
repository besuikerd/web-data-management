package authors;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.File;

/**
 * Created by maarten on 30-5-15.
 */
public class CycleJob {
    private static final String OUT1 = "datasets/cycle-1.txt";
    private static final String OUT2 = "datasets/cycle-2.txt";
    private static final String OUT3 = "datasets/cycle-3.txt";
    private static final String OUT4 = "datasets/cycle-4.txt";


    public static void main(String[] args) throws Exception {
        /*
         * Load the Haddop configuration. IMPORTANT: the
         * $HADOOP_HOME/conf directory must be in the CLASSPATH
         */
        Configuration conf = new Configuration();

        /* We expect two arguments */

        if (args.length != 2) {
            System.err.println("Usage: AuthorsJob <in> <out>");
            System.exit(2);
        }
        String outputDir = args[args.length - 1];
        File of1 = new File(OUT1);
        File of2 = new File(OUT2);
        File of3 = new File(OUT3);
        File of4 = new File(OUT4);

        File f = new File(outputDir);
        if (f.exists()) {
            FileUtils.forceDelete(f);
        }

        /* Allright, define and submit the job */
        Job job = new Job(conf, "Cycle first job");
        job.setMapperClass(Cycle.CycleMapper.class);
        job.setReducerClass(Cycle.CycleReducer.class);

        /* Define the output type */
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        /* Set the input and the output */
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(outputDir));

        /* Do it! */
        job.waitForCompletion(true);
        FileUtils.copyFile(new File(outputDir + "/part-r-00000"), of1);


        FileUtils.forceDelete(f);
        Job job2 = new Job(conf, "Cycle second job");
        job2.setMapperClass(Cycle.CycleMapper.class);
        job2.setReducerClass(Cycle.CycleReducer.class);;
        job2.setOutputKeyClass(Text.class);
        job2.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job2, new Path(of1.getPath()));
        FileOutputFormat.setOutputPath(job2, new Path(outputDir));
        job2.waitForCompletion(true);
        FileUtils.copyFile(new File(outputDir + "/part-r-00000"), of2);


        //TODO This job is optional
        FileUtils.forceDelete(f);
        Job job3 = new Job(conf, "Cycle third job");
        job3.setMapperClass(Cycle.CycleDupMapper.class);
        job3.setReducerClass(Cycle.CycleDupReducer.class);;
        job3.setOutputKeyClass(Text.class);
        job3.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job3, new Path(of2.getPath()));
        FileOutputFormat.setOutputPath(job3, new Path(outputDir));
        job3.waitForCompletion(true);
        FileUtils.copyFile(new File(outputDir + "/part-r-00000"), of3);


        FileUtils.forceDelete(f);
        Job job4 = new Job(conf, "Cycle fourth job");
        job4.setMapperClass(Cycle.CycleDetectMapper.class);
        job4.setReducerClass(Cycle.CycleDetectReducer.class);;
        job4.setOutputKeyClass(Text.class);
        job4.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job4, new Path(of3.getPath()));
        FileOutputFormat.setOutputPath(job4, new Path(outputDir));
        job4.waitForCompletion(true);
        FileUtils.copyFile(new File(outputDir + "/part-r-00000"), of4);
    }
}
