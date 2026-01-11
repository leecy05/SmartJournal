package smartjournal.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class Weather{
   
   private static final String API_URL = "https://api.data.gov.my/weather/forecast";

public String getCurrentWeather(){
   try{
      // Create Client
      HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
      
      // Create Request
      HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_URL)).GET().build();

      // Send & Request
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
         return parseMalaysianWeather(response.body());
      } 
      else {
         return "Weather Error (" + response.statusCode() + ")";
      }

   }
   catch (Exception e) {
      return "Weather unavailable (Offline)";
   }
}

private String parseMalaysianWeather(String json) {
   try {
      // Extract location
      String location = extractString(json, "location_name");

      // Extract Summary Forecast
      String summary = extractString(json, "summary_forecast");
   
      // Extract Temperatures
      String minTemp = extractNumber(json, "min_temp");
      String maxTemp = extractNumber(json, "max_temp");

      return String.format("%s: %s (%s°C - %s°C)", location, summary, minTemp, maxTemp);
   
   }
   catch (Exception e) {
      e.printStackTrace();
      return "Parse Error!";
   }
}

private String extractString(String json, String key){
   String searchKey = "\"" + key + "\":";
   int start = json.indexOf(searchKey);
   
   if(start == -1) return "Uknown";

   start = json.indexOf("\"", start + searchKey.length()) + 1;
   int end = json.indexOf("\"", start);

   return json.substring(start, end);
}
private String extractNumber(String json, String key) {
   String searchKey = "\"" + key + "\":";
   int start = json.indexOf(searchKey);
   if (start == -1) return "?";

   start += searchKey.length();
   
   int endComma = json.indexOf(",", start);
   int endBrace = json.indexOf("}", start);
   
   int end = (endComma == -1) ? endBrace : (endBrace == -1 ? endComma : Math.min(endComma, endBrace));
   
   return json.substring(start, end).trim();
}

}