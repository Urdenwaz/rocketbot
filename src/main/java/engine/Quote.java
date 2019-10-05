package engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Quote {

     private List<String> quotes;
     private String name;

     public Quote(String name, String filename) throws IOException {
         String line;
         BufferedReader rdr = new BufferedReader(new FileReader(filename));

         this.name = name;
         quotes = new ArrayList<>();

         while ((line = rdr.readLine()) != null) {
             quotes.add(line.trim());
         }

         System.out.println("Read in " + quotes.size() + " quotes (" + name + ")");
     }

     public String getName() {
         return name;
     }

     public boolean isMe(String name) {
         return this.name.equalsIgnoreCase(name.trim());
     }

     public String getQuote() {
         Random random = new Random();
         return quotes.get(random.nextInt(quotes.size()));
     }

}
