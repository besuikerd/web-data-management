package authors;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by nick on 26-5-15.
 */
public class Movies {

//
//    public static class Actor implements Writable {
//
//        private String firstName;
//        private String lastName;
//        private int birthDate;
//
//        public Actor(String firstName, String lastName, int birthDate) {
//            this.firstName = firstName;
//            this.lastName = lastName;
//            this.birthDate = birthDate;
//        }
//
//        public Actor() {
//        }
//
//        public String getFirstName() {
//            return firstName;
//        }
//
//        public String getLastName() {
//            return lastName;
//        }
//
//        public int getBirthDate() {
//            return birthDate;
//        }
//
//        @Override
//        public void write(DataOutput out) throws IOException {
//            out.writeUTF(firstName);
//            out.writeUTF(lastName);
//            out.writeInt(birthDate);
//        }
//
//        @Override
//        public void readFields(DataInput in) throws IOException {
//            this.firstName = in.readUTF();
//            this.lastName = in.readUTF();
//            this.birthDate = in.readInt();
//        }
//    }


    public static class MoviesMapper extends Mapper<LongWritable, Text, Text, Text>{

        private Text movie = new Text();
        private Text actor = new Text();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = dbFactory.newDocumentBuilder();
                Document d = builder.parse(new ByteArrayInputStream(value.toString().getBytes()));
                movie.set(d.getElementsByTagName("title").item(0).getTextContent());

                NodeList actors = d.getElementsByTagName("actor");
                for(int i = 0 ; i < actors.getLength() ; i++)
                {
                    String firstName = actors.item(i).getChildNodes().item(1).getTextContent().trim();
                    String lastName = actors.item(i).getChildNodes().item(3).getTextContent().trim();
                    String year = actors.item(i).getChildNodes().item(5).getTextContent().trim();
                    String role = actors.item(i).getChildNodes().item(7).getTextContent().trim();

                    actor.set(firstName + ' ' + lastName + '\t' + year + '\t' + role);
                    System.out.println(movie + " " + actor);
                    context.write(movie, actor);
                }
            } catch(Exception e){

            }
        }
    }

    public static class MoviesReducer extends Reducer<Text, Text, Text, Text>{
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            for(Text t : values) {
                context.write(key, t);
            }
        }
    }
}
