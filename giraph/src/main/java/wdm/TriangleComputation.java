package wdm;

import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

import java.io.IOException;


public class TriangleComputation extends BasicComputation<Text, IntWritable, FloatWritable, Text> {

    static final Logger log = Logger.getLogger(TriangleComputation.class);

    @Override
    public void compute(Vertex<Text, IntWritable, FloatWritable> vertex, Iterable<Text> messages) throws IOException {
        switch((int) getSuperstep()){
            case 0:
                for(Edge<Text, FloatWritable> e : vertex.getEdges()){
                    if(vertex.getId().compareTo(e.getTargetVertexId()) < 0){ //we have a smaller index
                        sendMessage(e.getTargetVertexId(), vertex.getId());
                    }
                }
                break;
            case 1:
                for(Text sourceId : messages){
                    for(Edge<Text, FloatWritable> e : vertex.getEdges()){
                        if(vertex.getId().compareTo(e.getTargetVertexId()) < 0){
                            sendMessage(e.getTargetVertexId(), sourceId);
                        }
                    }
                }
                break;
            case 2:
                int trianglesFound = 0;
                for(Text sourceId : messages){
                    for(Edge<Text, FloatWritable> e : vertex.getEdges()){
                        if(e.getTargetVertexId().compareTo(sourceId) == 0){
                            trianglesFound++;
                        }
                    }
                }
                log.debug("triangles found: " + trianglesFound);
                vertex.setValue(new IntWritable(trianglesFound));
                break;
        }
        vertex.voteToHalt();
    }
}
