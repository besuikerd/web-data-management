package authors;

/**
 * Example of a simple MapReduce job: it reads
 * file containing authors and publications, and
 * produce each author with her publication count.
 */

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import authors.Authors;

import java.io.File;

/**
 * The follozing class implements the Job submission, based on
 * the Mapper (AuthorsMapper) and the Reducer (CountReducer)
 */
public class AuthorsJob {

    public static void main(String[] args) throws Exception {

	/*
	 * Load the Haddop configuration. IMPORTANT: the
	 * $HADOOP_HOME/conf directory must be in the CLASSPATH
	 */
        Configuration conf = new Configuration();

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
        Job job = new Job(conf, "Authors count");

	/* Define the Mapper and the Reducer */
        job.setMapperClass(Authors.AuthorsMapper.class);
        job.setCombinerClass(Authors.AuthorsCombiner.class);
        job.setReducerClass(Authors.CountReducer.class);

	/* Define the output type */
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

	/* Set the input and the output */
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

	/* Do it! */
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}