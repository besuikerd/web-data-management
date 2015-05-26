package authors;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper;

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
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            System.out.println(value.toString());
            System.out.println("\n\n====================================================");

            context.write(value, value);
        }
    }

    public static class MoviesReducer extends Reducer<Text, Text, Text, Text>{
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            context.write(key, values.iterator().next());
        }
    }
}
