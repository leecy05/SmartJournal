package smartjournal.service;

import java.util.Arrays;
import java.util.List;

public class Sentiment {
    
    // Positive keywords
    private static final List<String> POSITIVE_WORDS = Arrays.asList(
        "happy", "joy", "excited", "great", "wonderful", "amazing", "love", "excellent",
        "fantastic", "good", "fun", "perfect", "best", "beautiful", "success", "proud",
        "grateful", "blessed", "awesome", "brilliant", "delighted", "enjoy", "pleased",
        "optimistic", "hopeful", "peaceful", "satisfied", "thrilled", "cheerful"
    );
    
    // Negative keywords
    private static final List<String> NEGATIVE_WORDS = Arrays.asList(
        "sad", "angry", "depressed", "terrible", "awful", "bad", "hate", "worry",
        "stress", "anxious", "upset", "frustrated", "disappointed", "hurt", "pain",
        "lonely", "tired", "exhausted", "miserable", "hopeless", "unhappy", "fear",
        "scared", "nervous", "irritated", "annoyed", "regret", "guilty", "shame"
    );
    
    // Mixed/Complex emotion keywords
    private static final List<String> MIXED_WORDS = Arrays.asList(
        "bittersweet", "conflicted", "mixed", "complicated", "confused", "uncertain",
        "ambivalent", "torn", "conflicted", "whiplash", "duality"
    );

    public String analyze(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "neutral";
        }
        
        String lowerText = text.toLowerCase();
        
        // Check for mixed emotions first
        int mixedCount = countKeywords(lowerText, MIXED_WORDS);
        if (mixedCount > 0) {
            return "mixed";
        }
        
        // Count positive and negative words
        int positiveCount = countKeywords(lowerText, POSITIVE_WORDS);
        int negativeCount = countKeywords(lowerText, NEGATIVE_WORDS);
        
        // Calculate sentiment score
        int totalEmotions = positiveCount + negativeCount;
        
        if (totalEmotions == 0) {
            return "neutral";
        }
        
        // Determine sentiment based on ratio
        double positiveRatio = (double) positiveCount / totalEmotions;
        
        if (positiveRatio >= 0.7) {
            return "very positive";
        } else if (positiveRatio >= 0.55) {
            return "positive";
        } else if (positiveRatio >= 0.45) {
            return "mixed";
        } else if (positiveRatio >= 0.3) {
            return "negative";
        } else {
            return "very negative";
        }
    }
    
    public String analyzeDetailed(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "Sentiment: Neutral (No content to analyze)";
        }
        
        String lowerText = text.toLowerCase();
        
        int positiveCount = countKeywords(lowerText, POSITIVE_WORDS);
        int negativeCount = countKeywords(lowerText, NEGATIVE_WORDS);
        int mixedCount = countKeywords(lowerText, MIXED_WORDS);
        
        String sentiment = analyze(text);
        
        StringBuilder result = new StringBuilder();
        result.append("Sentiment: ").append(sentiment);
        result.append(" (Positive: ").append(positiveCount);
        result.append(", Negative: ").append(negativeCount);
        
        if (mixedCount > 0) {
            result.append(", Mixed indicators: ").append(mixedCount);
        }
        
        result.append(")");
        
        return result.toString();
    }
    
    private int countKeywords(String text, List<String> keywords) {
        int count = 0;
        for (String keyword : keywords) {
            int index = 0;
            while ((index = text.indexOf(keyword, index)) != -1) {
                // Check if it's a whole word match
                boolean isWholeWord = true;
                
                // Check character before
                if (index > 0 && Character.isLetterOrDigit(text.charAt(index - 1))) {
                    isWholeWord = false;
                }
                
                // Check character after
                int endIndex = index + keyword.length();
                if (endIndex < text.length() && Character.isLetterOrDigit(text.charAt(endIndex))) {
                    isWholeWord = false;
                }
                
                if (isWholeWord) {
                    count++;
                }
                
                index += keyword.length();
            }
        }
        return count;
    }
    
    // Get emoji representation of sentiment
    public String getSentimentEmoji(String sentiment) {
        switch (sentiment.toLowerCase()) {
            case "very positive":
                return "😄";
            case "positive":
                return "😊";
            case "mixed":
                return "😐";
            case "negative":
                return "😔";
            case "very negative":
                return "😢";
            case "neutral":
            default:
                return "😶";
        }
    }
}