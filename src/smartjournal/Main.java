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
                    System.out.println("Journal menu(to be implemented)");
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
}

