package smartjournal.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.time.Duration;

public class Sentiment {

   private static final String API_URL = "https://api.mood-tracker.ninjagiraffes.co.uk/";
   private static final String API_TOKEN = "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";

   public String analyze(String text) {
      try {
         String safeText = text.replace("\"", "\"");
         String jsonBody = String.format("{\"inputs\": \"%s\"}", safeText);

         HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

         HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_URL)).header("authorization", "Bearer" + API_TOKEN).header("Content-Type", "application/json"). POST(BodyPublishers.ofString(jsonBody)).build();
         
         HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
         
         if (response.statusCode() == 200) {
               return parseHuggingFaceResponse(response.body());
            } else {
               // Useful for debugging if you forgot the token
               System.out.println("API Error: " + response.statusCode() + " " + response.body());
               return "unknown";
            }

      } 
      catch (Exception e) {
         e.printStackTrace();
         return "offline";
      }
   }

   private String parseHuggingFaceResponse(String json) {

   if (json.contains("\"label\":\"POSITIVE\"")) {
      int posIndex = json.indexOf("\"label\":\"POSITIVE\"");
      int negIndex = json.indexOf("\"label\":\"NEGATIVE\"");
      
      if (negIndex == -1 || posIndex < negIndex) {
         return "positive";
      } else {
         return "negative";
      }
   } 
   else if (json.contains("\"label\":\"NEGATIVE\"")) {
      return "negative";
   }
   
   return "neutral";
   }
}

