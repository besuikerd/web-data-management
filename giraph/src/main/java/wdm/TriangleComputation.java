package wdm;

import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.*;
import org.apache.log4j.Logger;
import java.io.IOException;


public class TriangleComputation extends BasicComputation<IntWritable, IntWritable, NullWritable, IntWritable> {

    static final Logger log = Logger.getLogger(TriangleComputation.class);


    @Override
    public void compute(Vertex<IntWritable, IntWritable, NullWritable> vertex, Iterable<IntWritable> messages) throws IOException {
        switch((int) getSuperstep()){
            case 0:
                for(Edge<IntWritable, NullWritable> e : vertex.getEdges()){
                    if(vertex.getId().get() < e.getTargetVertexId().get()) {
                        sendMessage(e.getTargetVertexId(), vertex.getId());
                    }
                }
                vertex.setValue(new IntWritable(0));
                break;
            case 1:
                for(IntWritable sourceId : messages) {
                    for(Edge<IntWritable, NullWritable> e : vertex.getEdges()){
                        if(vertex.getId().get() < e.getTargetVertexId().get()) {
                            sendMessage(e.getTargetVertexId(), sourceId);
                        }
                    }
                }
                break;
            case 2:
                int trianglesFound = 0;
                for(IntWritable sourceId : messages){
                    for(Edge<IntWritable, NullWritable> e : vertex.getEdges()){
                        if(e.getTargetVertexId().get() == sourceId.get()){
                            trianglesFound++;
                        }
                    }
                }
                vertex.setValue(new IntWritable(trianglesFound));
                break;
        }
        vertex.voteToHalt();
    }
}
