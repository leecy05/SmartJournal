package smartjournal;

import smartjournal.manager.UserManager;
import smartjournal.model.User;
import smartjournal.service.Weather;
import smartjournal.service.Sentiment;

import java.util.Scanner;
import java.io.File;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Main {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
    private static Scanner scanner;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        UserManager userManager = new UserManager();

        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║     Welcome to Smart Journal App     ║");
        System.out.println("╚══════════════════════════════════════╝");
        
        User loggedInUser = login(userManager);

        if (loggedInUser != null) {
            System.out.println("\n✅ Login successful!");
            System.out.println("Welcome, " + loggedInUser.getDisplayName());
            
            // Test weather service
            testWeatherService();
            
            showWelcome(loggedInUser);
            mainMenu(loggedInUser, userManager);
        } else {
            System.out.println("\n❗ Login failed!");
            System.out.println("Invalid email or password.");
        }

        scanner.close();
    }

    private static User login(UserManager userManager) {
        System.out.print("\nEnter email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        return userManager.authenticate(email, password);
    }

    private static void testWeatherService() {
        System.out.println("\n[Testing Weather Service...]");
        try {
            Weather weatherService = new Weather();
            String weather = weatherService.getCurrentWeather();
            System.out.println("✅ Weather Service: " + weather);
        } catch (Exception e) {
            System.out.println("⚠️ Weather Service Error: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void showWelcome(User user) {
        LocalTime now = LocalTime.now();
        String greeting;
        
        if (now.isBefore(LocalTime.NOON)) {
            greeting = "Good Morning ☀️";
        } else if (now.isBefore(LocalTime.of(17, 0))) {
            greeting = "Good Afternoon 🌤️";
        } else {
            greeting = "Good Evening 🌙";
        }

        System.out.println("\n" + greeting + ", " + user.getDisplayName() + "! 🌟");
        System.out.println("Today is " + LocalDate.now().format(DATE_FORMATTER));
    }

    private static void mainMenu(User user, UserManager userManager) {
        while (true) {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║          Main Menu                 ║");
            System.out.println("╠════════════════════════════════════╣");
            System.out.println("║ 1. Create, Edit & View Journals    ║");
            System.out.println("║ 2. View Weekly Mood Summary        ║");
            System.out.println("║ 3. Logout                          ║");
            System.out.println("╚════════════════════════════════════╝");
            System.out.print("Select option: ");

            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    journalMenu(user, userManager);
                    break;
                case "2":
                    weeklyMoodSummary(user, userManager);
                    break;
                case "3":
                    System.out.println("\n👋 Logged out. Goodbye, " + user.getDisplayName() + "!");
                    return;
                default:
                    System.out.println("⚠️ Invalid option. Please try again.");
            }
        }
    }

    private static void journalMenu(User user, UserManager userManager) {
        LocalDate today = LocalDate.now();
        ArrayList<LocalDate> dates = getJournalDates(user);

        if (!dates.contains(today)) {
            dates.add(today);
        }

        dates.sort((d1, d2) -> d2.compareTo(d1)); // Most recent first

        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║        Your Journal Dates          ║");
        System.out.println("╚════════════════════════════════════╝");

        for (int i = 0; i < dates.size(); i++) {
            LocalDate date = dates.get(i);
            String marker = date.equals(today) ? " (Today) 📝" : "";
            System.out.printf("%d. %s%s%n", (i + 1), date.format(DATE_FORMATTER), marker);
        }

        System.out.println("\nSelect a date to view/edit, or press Enter to go back:");
        System.out.print("> ");

        String input = scanner.nextLine().trim();
        
        if (input.isEmpty()) {
            return;
        }

        try {
            int choice = Integer.parseInt(input);
            if (choice >= 1 && choice <= dates.size()) {
                LocalDate selectedDate = dates.get(choice - 1);
                handleJournalForDate(user, userManager, selectedDate);
            } else {
                System.out.println("⚠️ Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("⚠️ Invalid input. Please enter a number.");
        }
    }

    private static void handleJournalForDate(User user, UserManager userManager, LocalDate date) {
        File journalFile = userManager.getJournalFile(user, date);
        LocalDate today = LocalDate.now();

        // Past dates are read-only
        if (date.isBefore(today)) {
            if (!journalFile.exists()) {
                System.out.println("\n⚠️ No journal entry for " + date.format(DATE_FORMATTER) + ".");
                return;
            }
            displayJournalEntry(userManager, user, date);
            return;
        }

        // Today's journal - create or edit
        if (!journalFile.exists()) {
            createNewJournal(user, userManager, date);
        } else {
            todayJournalMenu(user, userManager, date);
        }
    }

    private static void createNewJournal(User user, UserManager userManager, LocalDate date) {
        System.out.println("\n📝 No journal found for today.");
        System.out.println("Create a new journal entry for " + date.format(DATE_FORMATTER) + ":");
        System.out.println("(Write your thoughts below)");
        System.out.print("> ");

        String entry = scanner.nextLine().trim();

        if (entry.isEmpty()) {
            System.out.println("⚠️ Empty entry. Nothing saved.");
            return;
        }

        System.out.println("\n⏳ Fetching weather data and analyzing mood...");
        
        // Fetch weather
        Weather weatherService = new Weather();
        String weather = weatherService.getCurrentWeather();

        // Analyze sentiment
        Sentiment moodSentiment = new Sentiment();
        String mood = moodSentiment.analyze(entry);

        System.out.println("✅ Analysis complete!");
        System.out.println("  Weather: " + weather);
        System.out.println("  Mood: " + mood);

        // Format final entry
        String finalEntry = String.format(
            "Location Weather: %s%nMood Analysis: %s%n%s%n%s",
            weather, mood, "━".repeat(50), entry
        );

        userManager.writeJournalEntry(user, date, finalEntry);
        System.out.println("\n✅ Journal saved successfully! 🏆");
    }

    private static void todayJournalMenu(User user, UserManager userManager, LocalDate date) {
        while (true) {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║      Today's Journal Options       ║");
            System.out.println("╠════════════════════════════════════╣");
            System.out.println("║ 1. View Journal                    ║");
            System.out.println("║ 2. Edit Journal                    ║");
            System.out.println("║ 3. Back                            ║");
            System.out.println("╚════════════════════════════════════╝");
            System.out.print("Select option: ");

            String option = scanner.nextLine().trim();

            switch (option) {
                case "1":
                    displayJournalEntry(userManager, user, date);
                    break;
                case "2":
                    editJournal(user, userManager, date);
                    break;
                case "3":
                    return;
                default:
                    System.out.println("⚠️ Invalid option.");
            }
        }
    }

    private static void displayJournalEntry(UserManager userManager, User user, LocalDate date) {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  Journal Entry for " + date.format(DATE_FORMATTER));
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
        
        userManager.readJournalEntries(user, date);
        
        System.out.println("\n" + "─".repeat(60));
        System.out.println("Press Enter to go back.");
        scanner.nextLine();
    }

    private static void editJournal(User user, UserManager userManager, LocalDate date) {
        System.out.println("\n✎𓂃  Edit your journal entry for " + date.format(DATE_FORMATTER) + ":");
        System.out.println("(Enter your new content below)");
        System.out.print("> ");
        
        String newEntry = scanner.nextLine().trim();

        if (newEntry.isEmpty()) {
            System.out.println("⚠️ Empty entry. Nothing saved.");
            return;
        }

        System.out.println("\n⏳ Re-analyzing mood...");
        
        // Re-analyze sentiment for edited entry
        Sentiment moodSentiment = new Sentiment();
        String mood = moodSentiment.analyze(newEntry);
        
        // Keep original weather, update mood
        Weather weatherService = new Weather();
        String weather = weatherService.getCurrentWeather();

        String finalEntry = String.format(
            "Location Weather: %s%nMood Analysis: %s%n%s%n%s",
            weather, mood, "━".repeat(50), newEntry
        );

        userManager.overwriteJournal(user, date, finalEntry);
        System.out.println("✅ Journal updated successfully! ⭐");
    }

    private static void weeklyMoodSummary(User user, UserManager userManager) {
        LocalDate today = LocalDate.now();
        int positive = 0;
        int neutral = 0;
        int negative = 0;
        int totalEntries = 0;

        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║     Weekly Mood Summary            ║");
        System.out.println("║     (Last 7 Days)                  ║");
        System.out.println("╚════════════════════════════════════╝\n");

        for (int i = 0; i < 7; i++) {
            LocalDate date = today.minusDays(i);
            File journalFile = userManager.getJournalFile(user, date);

            if (!journalFile.exists()) {
                continue;
            }

            totalEntries++;
            String content = "";

            try (Scanner fileScanner = new Scanner(journalFile)) {
                while (fileScanner.hasNextLine()) {
                    content += fileScanner.nextLine().toLowerCase() + " ";
                }
            } catch (Exception e) {
                System.err.println("Error reading journal for " + date);
                continue;
            }

            // Simple sentiment analysis based on keywords
            int positiveCount = countKeywords(content, 
                new String[]{"happy", "good", "great", "excited", "fun", "joy", "wonderful", "love", "amazing"});
            int negativeCount = countKeywords(content, 
                new String[]{"sad", "tired", "angry", "stress", "bad", "hate", "terrible", "awful", "depressed"});

            if (positiveCount > negativeCount) {
                positive++;
            } else if (negativeCount > positiveCount) {
                negative++;
            } else {
                neutral++;
            }
        }

        // Display results
        if (totalEntries == 0) {
            System.out.println("No journal entries found in the last 7 days.");
        } else {
            System.out.println("📊 Mood Distribution:");
            System.out.println("━".repeat(40));
            printMoodBar("🤗 Positive days", positive, totalEntries);
            printMoodBar("🙂 Neutral days ", neutral, totalEntries);
            printMoodBar("😔 Negative days", negative, totalEntries);
            System.out.println("━".repeat(40));
            System.out.println("Total entries: " + totalEntries);
            
            // Insight
            if (positive > negative + neutral) {
                System.out.println("\n🎯 Great week! You've been mostly positive! 🌟");
            } else if (negative > positive) {
                System.out.println("\n🎯 Tough week? Remember, better days are ahead! 💪");
            } else {
                System.out.println("\n🎯 A balanced week with mixed emotions. 🌈");
            }
        }

        System.out.println("\nPress Enter to go back.");
        scanner.nextLine();
    }

    private static int countKeywords(String content, String[] keywords) {
        int count = 0;
        for (String keyword : keywords) {
            int index = 0;
            while ((index = content.indexOf(keyword, index)) != -1) {
                count++;
                index += keyword.length();
            }
        }
        return count;
    }

    private static void printMoodBar(String label, int count, int total) {
        int percentage = (total > 0) ? (count * 100 / total) : 0;
        int barLength = percentage / 5; // 20 chars max bar
        
        String bar = "▌".repeat(barLength) + "||".repeat(20 - barLength);
        System.out.printf("%s: %2d [%s] %3d%%%n", label, count, bar, percentage);
    }

    private static ArrayList<LocalDate> getJournalDates(User user) {
        ArrayList<LocalDate> dates = new ArrayList<>();
        File userFolder = new File("data/journals/" + user.getEmail());

        if (!userFolder.exists() || !userFolder.isDirectory()) {
            return dates;
        }

        File[] files = userFolder.listFiles();
        if (files == null) {
            return dates;
        }

        for (File file : files) {
            String name = file.getName();
            if (name.endsWith(".txt")) {
                String datePart = name.replace(".txt", "");
                try {
                    dates.add(LocalDate.parse(datePart));
                } catch (Exception e) {
                    // Ignore invalid filenames
                    System.err.println("⚠️ Invalid date format in filename: " + name);
                }
            }
        }

        return dates;
    }
}