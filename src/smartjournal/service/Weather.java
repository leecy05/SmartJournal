package smartjournal.service;

import java.time.LocalDateTime;
import java.util.Random;

public class Weather {
   
   private static final String CITY = "Kuala Lumpur";
   private static final long CACHE_DURATION = 300_000; // 5 minutes
   
   private String cachedWeather;
   private long lastFetch;
   private Random random = new Random();

   public String getCurrentWeather() {
      System.out.println("[DEBUG] Weather.getCurrentWeather() called");
      
      // Return cached weather if still valid
      if (cachedWeather != null && 
          System.currentTimeMillis() - lastFetch < CACHE_DURATION) {
         System.out.println("[DEBUG] Returning cached weather");
         return cachedWeather;
      }
      
      System.out.println("[DEBUG] Generating new weather data...");
      
      // Generate realistic weather based on time and date
      cachedWeather = generateRealisticWeather();
      lastFetch = System.currentTimeMillis();
      
      System.out.println("[DEBUG] Weather generated: " + cachedWeather);
      
      return cachedWeather;
   }

   private String generateRealisticWeather() {
      LocalDateTime now = LocalDateTime.now();
      int hour = now.getHour();
      int dayOfYear = now.getDayOfYear();
      
      // Seed random with day so weather is consistent throughout the day
      Random dayRandom = new Random(dayOfYear);
      
      String condition;
      int baseTemp;
      int minTemp;
      int maxTemp;
      
      // Time-based conditions
      if (hour >= 6 && hour < 10) {
         // Morning
         condition = pickCondition(dayRandom, new String[]{
            "Partly cloudy", "Clear morning", "Light fog", "Sunny"
         });
         baseTemp = 25 + dayRandom.nextInt(3);
         minTemp = 24;
         maxTemp = 32;
         
      } else if (hour >= 10 && hour < 15) {
         // Midday - hottest
         condition = pickCondition(dayRandom, new String[]{
            "Sunny", "Hot and humid", "Partly cloudy", "Hazy"
         });
         baseTemp = 30 + dayRandom.nextInt(3);
         minTemp = 28;
         maxTemp = 34;
         
      } else if (hour >= 15 && hour < 18) {
         // Afternoon
         condition = pickCondition(dayRandom, new String[]{
            "Partly cloudy", "Scattered showers", "Thunderstorms possible", "Cloudy"
         });
         baseTemp = 29 + dayRandom.nextInt(2);
         minTemp = 27;
         maxTemp = 33;
         
      } else if (hour >= 18 && hour < 22) {
         // Evening
         condition = pickCondition(dayRandom, new String[]{
            "Clear evening", "Partly cloudy", "Light rain", "Breezy"
         });
         baseTemp = 26 + dayRandom.nextInt(2);
         minTemp = 25;
         maxTemp = 30;
         
      } else {
         // Night
         condition = pickCondition(dayRandom, new String[]{
            "Clear night", "Partly cloudy", "Cool and breezy", "Calm"
         });
         baseTemp = 24 + dayRandom.nextInt(2);
         minTemp = 23;
         maxTemp = 28;
      }
      
      int feelsLike = baseTemp + dayRandom.nextInt(3);
      
      return String.format("%s: %s, %d°C (feels like %d°C, min %d°C, max %d°C)", 
                          CITY, condition, baseTemp, feelsLike, minTemp, maxTemp);
   }

   private String pickCondition(Random rand, String[] conditions) {
      return conditions[rand.nextInt(conditions.length)];
   }
}