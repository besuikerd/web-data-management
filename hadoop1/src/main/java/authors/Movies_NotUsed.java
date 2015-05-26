package authors;

import jdk.internal.org.xml.sax.Attributes;
import jdk.internal.org.xml.sax.SAXException;
import jdk.internal.org.xml.sax.helpers.DefaultHandler;

import java.util.List;

/**
 * Created by nick on 26-5-15.
 */
public class Movies_NotUsed {

    public static class Movie{
        public final String title;
        public final int year;
        public final String country;
        public final String genre;
        public final String summary;
        public final Director director;
        public final List<Actor> actors;

        public Movie(String title, int year, String country, String genre, String summary, Director director, List<Actor> actors) {
            this.title = title;
            this.year = year;
            this.country = country;
            this.genre = genre;
            this.summary = summary;
            this.director = director;
            this.actors = actors;
        }
    }

    public static class Person{
        public final String firstName;
        public final String lastName;
        public final int birthYear;

        public Person(String firstName, String lastName, int birthYear) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.birthYear = birthYear;
        }
    }

    public static class Actor extends Person{
        public final String role;

        public Actor(String firstName, String lastName, int birthYear, String role) {
            super(firstName, lastName, birthYear);
            this.role = role;
        }
    }

    public static class Director extends Person{
        public Director(String firstName, String lastName, int birthYear) {
            super(firstName, lastName, birthYear);
        }
    }


    public static class MoviesHandler extends DefaultHandler{


        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
        }
    }

}
