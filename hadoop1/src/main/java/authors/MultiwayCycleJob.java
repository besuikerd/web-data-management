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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * Created by maarten on 9-6-15.
 */
public class MultiwayCycleJob {


    public static void main(String[] args) throws Exception {

        // m = # lines per file
        // k <= m ^ 2
        // abc = k
        // in our case: r = s = t = |R|
        // ideal: a=(krt/s^2 ) ^ 1/3 = k ^ 1/3
        //Meaning b should be equal to k = m^2 ?!
        //TODO ask

        int b = 2;  //Number of buckets per dimension
        int m = 0;  //Number of edges


        Configuration conf = new Configuration();
        conf.setInt("b", b);

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
        job.setNumReduceTasks((int)Math.pow(b, 3));

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

        ArrayList<String> result = new ArrayList<>();

        System.out.println();
        for(int i=0; i<(int)Math.pow(b, 3); i++) {
            try (Stream<String> lines = Files.lines(Paths.get(".", outputDir, String.format("part-r-%05d", i)), Charset.defaultCharset())) {
                lines.forEachOrdered(l -> result.add(l));
            }
        }

        System.out.println(String.format("Found %d triangles:", result.size()));
        for(String l : result) {
            System.out.println(l);
        }
    }
}
