/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package smartjournal;

/**
 *
 * @author leecy
 */

import smartjournal.manager.UserManager;
import smartjournal.model.User;
import java.util.Scanner;
import java.io.File;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        UserManager userManager = new UserManager();
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        User loggedInUser = userManager.authenticate(email, password);

        if (loggedInUser != null) {
            System.out.println("\nLogin successful!");
            System.out.println("Welcome, " + loggedInUser.getDisplayName());
            showWelcome(loggedInUser);
            while(true){
                System.out.println("\n=== Main Menu ===");
                System.out.println("1. Create, Edit & View Journals");
                System.out.println("2. View Weekly Mood Summary");
                System.out.println("3. Logout");
                System.out.print("> ");

                String choice = scanner.nextLine();
                if (choice.equals("1")){
                    journalMenu(loggedInUser, userManager, scanner);
                }else if(choice.equals("2")){
                    weeklyMoodSummary(loggedInUser, userManager);
                }else if(choice.equals("3")){
                    System.out.println("Logged out. Goodbye!");
                    break;
                }
                else{
                      System.out.println("Invalid option. Please try again.");
                }
            }
            
            

            
            
        } else {
            System.out.println("\nLogin failed!");
            System.out.println("Invalid email or password.");
        }

        scanner.close();
    }
    
    private static void showWelcome(User user) {
      LocalTime now = LocalTime.now();

      String greeting;
      if (now.isBefore(LocalTime.NOON)) {
          greeting = "Good Morning";
      } else if (now.isBefore(LocalTime.of(17, 0))) {
          greeting = "Good Afternoon";
      } else {
          greeting = "Good Evening";
      }

      System.out.println("\n" + greeting + ", " + user.getDisplayName());
    }
    
    private static void journalMenu(
        User user,
        UserManager userManager,
        Scanner scanner) {

    LocalDate today = LocalDate.now();
    ArrayList<LocalDate> dates = getJournalDates(user);

    if (!dates.contains(today)) {
        dates.add(today);
    }

    dates.sort(LocalDate::compareTo);

    System.out.println("\n=== Journal Dates ===");

    for (int i = 0; i < dates.size(); i++) {
        LocalDate date = dates.get(i);
        if (date.equals(today)) {
            System.out.println((i + 1) + ". " + date + " (Today)");
        } else {
            System.out.println((i + 1) + ". " + date);
        }
    }

    System.out.println("Select a date to view journal, or create/edit today's journal:");
    System.out.print("> ");

    try {
        int choice = Integer.parseInt(scanner.nextLine());
        if (choice >= 1 && choice <= dates.size()) {
            LocalDate selectedDate = dates.get(choice - 1);
            handleJournalForDate(user, userManager, scanner, selectedDate);
        } else {
            System.out.println("Invalid selection.");
        }
    } catch (NumberFormatException e) {
        System.out.println("Invalid input.");
    }
}

    
    
    private static void handleJournalForDate(
        User user,
        UserManager userManager,
        Scanner scanner,
        LocalDate date) {

    File journalFile = userManager.getJournalFile(user, date);
    LocalDate today = LocalDate.now();

    // ===== PAST DATE → VIEW ONLY =====
    if (date.isBefore(today)) {

        if (!journalFile.exists()) {
            System.out.println("No journal entry for " + date + ".");
            return;
        }

        userManager.readJournalEntries(user, date);
        return;
    }

    // ===== TODAY =====
    if (!journalFile.exists()) {

        System.out.println("No journal found for today.");
        System.out.println("Create journal for " + date + ":");
        System.out.print("> ");

        String entry = scanner.nextLine().trim();

        if (!entry.isEmpty()) {
            userManager.writeJournalEntry(user, date, entry);
            System.out.println("Journal saved successfully!");
        } else {
            System.out.println("Empty entry. Nothing saved.");
        }
        return;
    }

    // ===== TODAY: VIEW / EDIT =====
    while (true) {
        System.out.println("\n1. View Journal");
        System.out.println("2. Edit Journal");
        System.out.println("3. Back");
        System.out.print("> ");

        String option = scanner.nextLine();

        if (option.equals("1")) {
            userManager.readJournalEntries(user, date);
        }
        else if (option.equals("2")) {
            System.out.println("Edit your journal entry for " + date + ":");
            System.out.print("> ");
            String newEntry = scanner.nextLine().trim();

            if (!newEntry.isEmpty()) {
                userManager.overwriteJournal(user, date, newEntry);
                System.out.println("Journal updated successfully!");
            } else {
                System.out.println("Empty entry. Nothing saved.");
            }
        }
        else if (option.equals("3")) {
            break;
        }
        else {
            System.out.println("Invalid option.");
        }
    }
    
}
    
    private static void weeklyMoodSummary(
        User user,
        UserManager userManager) {

    LocalDate today = LocalDate.now();
    int positive = 0;
    int neutral = 0;
    int negative = 0;

    System.out.println("\n=== Weekly Mood Summary ===");

    for (int i = 0; i < 7; i++) {
        LocalDate date = today.minusDays(i);
        File journalFile = userManager.getJournalFile(user, date);

        if (!journalFile.exists()) {
            continue;
        }

        String content = "";

        try (Scanner scanner = new Scanner(journalFile)) {
            while (scanner.hasNextLine()) {
                content += scanner.nextLine().toLowerCase() + " ";
            }
        } catch (Exception e) {
            continue;
        }

        if (content.contains("happy")
                || content.contains("good")
                || content.contains("great")
                || content.contains("excited")
                || content.contains("fun")) {
            positive++;
        }
        else if (content.contains("sad")
                || content.contains("tired")
                || content.contains("angry")
                || content.contains("stress")
                || content.contains("bad")) {
            negative++;
        }
        else {
            neutral++;
        }
    }

    System.out.println("Positive days: " + positive);
    System.out.println("Neutral days : " + neutral);
    System.out.println("Negative days: " + negative);
    System.out.println("\nPress Enter to go back.");
    new Scanner(System.in).nextLine();
}

    
    private static ArrayList<LocalDate> getJournalDates(User user) {

    ArrayList<LocalDate> dates = new ArrayList<>();

    File userFolder = new File("data/journals/" + user.getEmail());

    if (!userFolder.exists()) {
        return dates;
    }

    File[] files = userFolder.listFiles();

    if (files == null) {
        return dates;
    }

    for (File file : files) {
        String name = file.getName(); // e.g. 2026-01-08.txt

        if (name.endsWith(".txt")) {
            String datePart = name.replace(".txt", "");
            try {
                dates.add(LocalDate.parse(datePart));
            } catch (Exception e) {
                // Ignore invalid filenames
            }
        }
    }

    return dates;
}

    
}

    




