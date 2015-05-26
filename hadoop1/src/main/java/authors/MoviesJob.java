package authors;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * Created by nick on 26-5-15.
 */
public class MoviesJob {
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

	/* Allright, define and submit the job */
        Job job = new Job(conf, "Movies");

//        job.setInputFormatClass(TextInputFormat.class);

	/* Define the Mapper and the Reducer */
        job.setMapperClass(Movies.MoviesMapper.class);
        //job.setCombinerClass(Authors.AuthorsCombiner.class);
        job.setReducerClass(Movies.MoviesReducer.class);

	/* Define the output type */
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

	/* Set the input and the output */
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

	/* Do it! */
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
