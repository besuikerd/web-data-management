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
 * Created by maarten on 29-5-15.
 */
public class InvertedJob {
    private static final String OUT1 = "datasets/inverted-first.txt";
    private static final String OUT2 = "datasets/inverted-second.txt";
    private static final String OUT3 = "datasets/inverted-third.txt";
    public static void main(String[] args) throws Exception {

	/*
	 * Load the Haddop configuration. IMPORTANT: the
	 * $HADOOP_HOME/conf directory must be in the CLASSPATH
	 */
        Configuration conf = new Configuration();

	/* We expect two arguments */

        if (args.length < 2) {
            System.err.println("Usage: AuthorsJob <in *> <out>");
            System.exit(2);
        }
        String outputDir = args[args.length - 1];
        File of1 = new File(OUT1);
        File of2 = new File(OUT2);
        File of3 = new File(OUT3);


        File f = new File(outputDir);
        if(f.exists()){
            FileUtils.forceDelete(f);
        }

	/* Allright, define and submit the job */
        Job job = new Job(conf, "Inverted first job");

	/* Define the Mapper and the Reducer */
        job.setMapperClass(Inverted.InvertedMapper1.class);
        job.setReducerClass(Inverted.InvertedReducer1.class);

	/* Define the output type */
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

	/* Set the input and the output */
        for(int i=0; i<args.length-1; i++) {
            FileInputFormat.addInputPath(job, new Path(args[i]));
        }
        FileOutputFormat.setOutputPath(job, new Path(outputDir));

	/* Do it! */
        job.waitForCompletion(true);
        FileUtils.copyFile(new File(outputDir + "/part-r-00000"), of1);


        FileUtils.forceDelete(f);
        Job job2 = new Job(conf, "Inverted second job");
        job2.setMapperClass(Inverted.InvertedMapper2.class);
        job2.setReducerClass(Inverted.InvertedReducer2.class);
        job2.setOutputKeyClass(Text.class);
        job2.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job2, new Path(of1.getPath()));
        FileOutputFormat.setOutputPath(job2, new Path(outputDir));
        job2.waitForCompletion(true);
        FileUtils.copyFile(new File(outputDir + "/part-r-00000"), of2);


        FileUtils.forceDelete(f);
        Job job3 = new Job(conf, "Inverted third job");
        job3.setMapperClass(Inverted.InvertedMapper3.class);
        job3.setReducerClass(Inverted.InvertedReducer3.class);
        job3.setOutputKeyClass(Text.class);
        job3.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job3, new Path(of2.getPath()));
        FileOutputFormat.setOutputPath(job3, new Path(outputDir));
        job3.waitForCompletion(true);
        FileUtils.copyFile(new File(outputDir + "/part-r-00000"), of3);








        System.exit(0);


    }
}