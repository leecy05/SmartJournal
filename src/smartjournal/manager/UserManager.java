/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smartjournal.manager;

/**
 *
 * @author leecy
 */


import smartjournal.model.User;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.IOException;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;


public class UserManager {

    private ArrayList<User> users;

    public UserManager() {
        users = new ArrayList<>();
        loadUsers();
    }

    private void loadUsers() {
    try {
        File file = new File("data/UserData.txt");

        

        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            String email = scanner.nextLine().trim();
            if (email.isEmpty()) {
                continue;
            }

            String displayName = scanner.nextLine().trim();
            String password = scanner.nextLine().trim();

            users.add(new User(email, displayName, password));
        }

        scanner.close();

    } catch (FileNotFoundException e) {
        System.out.println("ERROR: UserData.txt not found.");
    }
}


    public ArrayList<User> getUsers() {
        return users;
    }
    
    public User authenticate(String email, String password) {
    for (User user : users) {
        if (user.getEmail().equals(email) &&
            user.getPassword().equals(password)) {
            return user; // login success
        }
    }
    return null; // login failed
    }
    
    public File getJournalFile(User user) {

        String fileName = user.getEmail() + ".txt";
        File journalFile = new File("data/journals/" + fileName);

        try {
            if (!journalFile.exists()) {
                journalFile.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("ERROR: Unable to create journal file.");
        }

        return journalFile;
    }
    
    public void writeJournalEntry(User user, String entry) {

      File journalFile = getJournalFile(user);

      try {
        FileWriter writer = new FileWriter(journalFile, true); // append mode

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        String timestamp = LocalDateTime.now().format(formatter);

        writer.write("[" + timestamp + "] " + entry + System.lineSeparator());
        writer.close();

      } catch (IOException e) {
        System.out.println("ERROR: Unable to write journal entry.");
        }
    }
    


       public void readJournalEntries(User user) {

           File journalFile = getJournalFile(user);

           try {
              Scanner scanner = new Scanner(journalFile);

              System.out.println("\n--- Your Journal Entries ---");

              if (!scanner.hasNextLine()) {
                 System.out.println("(No entries yet)");
              }

              while (scanner.hasNextLine()) {
                 System.out.println(scanner.nextLine());
              }

              scanner.close();

            } catch (FileNotFoundException e) {
                 System.out.println("ERROR: Unable to read journal file.");
              }
       }   
}

    
    
    
    
    



