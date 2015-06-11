package wdm;

import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Computation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.FloatWritable;

import java.io.IOException;

public class TriangleComputation extends BasicComputation<DoubleWritable, DoubleWritable, FloatWritable, DoubleWritable> {
    @Override
    public void compute(Vertex<DoubleWritable, DoubleWritable, FloatWritable> vertex, Iterable<DoubleWritable> messages) throws IOException {
        
    }
}
