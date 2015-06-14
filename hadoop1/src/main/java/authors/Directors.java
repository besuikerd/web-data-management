package authors;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by nick on 26-5-15.
 */
public class Directors {

    public static class DirectorsMapper extends Mapper<LongWritable, Text, Text, Text>{

        private Text director = new Text();
        private Text movie = new Text();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = dbFactory.newDocumentBuilder();
                Document d = builder.parse(new ByteArrayInputStream(value.toString().getBytes()));

                String title = d.getElementsByTagName("title").item(0).getTextContent().trim();
                String year = d.getElementsByTagName("year").item(0).getTextContent().trim();

                NodeList directors = d.getElementsByTagName("director");
                for(int i = 0 ; i < directors.getLength() ; i++)
                {
                    String lastName = directors.item(i).getChildNodes().item(1).getTextContent().trim();
                    String firstName = directors.item(i).getChildNodes().item(3).getTextContent().trim();

                    director.set(firstName + ' ' + lastName);
                    movie.set(title + '\t' + year);

                    context.write(director, movie);
                }
            } catch(Exception e){

            }
        }
    }
}
