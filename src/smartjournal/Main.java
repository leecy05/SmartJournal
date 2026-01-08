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
                    System.out.println("Weekly mood summary(to be implemented)");
                }else if(choice.equals("3")){
                    System.out.println("Logged out. Goodbye!");
                    break;
                }
                else{
                      System.out.println("Invalid option. Please try again.");
                }
            }
            
            File journalFile = userManager.getJournalFile(loggedInUser);
            System.out.println("Journal file ready: " + journalFile.getPath());
            System.out.print("\nWrite your journal entry: ");
            String entry = scanner.nextLine().trim();
            if(entry.isEmpty()){
                System.out.println("Journal entry is empty. Nothing was saved.");
            }else{
                userManager.writeJournalEntry(loggedInUser,entry);
                System.out.println("Journal entry saved.");
            }
             userManager.readJournalEntries(loggedInUser);

            
            
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
    
    private static void journalMenu(User user,UserManager userManager,Scanner scanner){
         LocalDate today = LocalDate.now();
         
          System.out.println("\n=== Journal Dates ===");
          
          System.out.println("1. " + today + " (Today)");
          System.out.println("Select a date to view journal, or create a new journal for today:");
          System.out.print("> ");

          String choice = scanner.nextLine();
          if (choice.equals("1")) {
              handleJournalForDate(user, userManager, scanner, today);
          }else{
               System.out.println("Invalid selection.");
          }
    }
    
    
    private static void handleJournalForDate(User user, UserManager userManager,Scanner scanner,LocalDate date){
        File journalFile = userManager.getJournalFile(user, date);
         if (!journalFile.exists()) {
             System.out.println("Enter your journal entry for " + date + ":");
             System.out.print("> ");
             String entry = scanner.nextLine().trim();

             if (!entry.isEmpty()) {
               userManager.writeJournalEntry(user, date, entry);
               System.out.println("Journal saved successfully!");
             } else {
                 System.out.println("Empty entry. Nothing saved.");
             }    
         }else{
             System.out.println("1. View Journal");
             System.out.println("2. Edit Journal");
             System.out.println("3. Back");
             System.out.print("> ");
             
             String option=scanner.nextLine();
             if (option.equals("1")) {
                 userManager.readJournalEntries(user, date);
             }else if(option.equals("2")){
                     System.out.println("Edit your journal entry for " + date + ":");
                     System.out.print("> ");
                     String newEntry = scanner.nextLine();
                     userManager.overwriteJournal(user, date, newEntry);
                     System.out.println("Journal updated successfully!");
                 
             }

         }
    }
    
}



