package authors;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;

import java.io.File;

/**
 * Created by maarten on 9-6-15.
 */
public class MultiwayCycleJob {


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

        File f = new File(outputDir);
        if (f.exists()) {
            FileUtils.forceDelete(f);
        }

        /* Allright, define and submit the job */
        Job job = new Job(conf, "Multiway Cycle job");
        job.setPartitionerClass(MultiwayCycle.MultiwayCyclePartitioner.class);
        job.setReducerClass(MultiwayCycle.MultiwayCycleReducer.class);
        job.setNumReduceTasks(27);


        /* Define the output type */
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        /* Set the input and the output */
        MultipleInputs.addInputPath(job, new Path("datasets/graph1.txt"), TextInputFormat.class, MultiwayCycle.MultiwayCycleMapper1.class);
        MultipleInputs.addInputPath(job, new Path("datasets/graph2.txt"), TextInputFormat.class, MultiwayCycle.MultiwayCycleMapper2.class);
        MultipleInputs.addInputPath(job, new Path("datasets/graph3.txt"), TextInputFormat.class, MultiwayCycle.MultiwayCycleMapper3.class);
        FileOutputFormat.setOutputPath(job, new Path(outputDir));

        /* Do it! */
        job.waitForCompletion(true);
    }
}
