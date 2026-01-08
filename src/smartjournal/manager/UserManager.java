/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smartjournal.manager;

import smartjournal.model.User;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
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
                if (email.isEmpty()) continue;

                String displayName = scanner.nextLine().trim();
                String password = scanner.nextLine().trim();

                users.add(new User(email, displayName, password));
            }

            scanner.close();

        } catch (FileNotFoundException e) {
            System.out.println("ERROR: UserData.txt not found.");
        }
    }

    public User authenticate(String email, String password) {
        for (User user : users) {
            if (user.getEmail().equals(email)
                    && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    // ===== JOURNAL METHODS (DATE-BASED ONLY) =====

    public File getJournalFile(User user, LocalDate date) {

        File userFolder = new File("data/journals/" + user.getEmail());

        if (!userFolder.exists()) {
            userFolder.mkdirs();
        }

        return new File(userFolder, date.toString() + ".txt");
    }

    public void writeJournalEntry(User user, LocalDate date, String entry) {

        File journalFile = getJournalFile(user, date);

        try (FileWriter writer = new FileWriter(journalFile)) {
            writer.write(entry);
        } catch (IOException e) {
            System.out.println("ERROR: Unable to write journal.");
        }
    }

    public void overwriteJournal(User user, LocalDate date, String entry) {
        writeJournalEntry(user, date, entry);
    }

    public void readJournalEntries(User user, LocalDate date) {

        File journalFile = getJournalFile(user, date);

        if (!journalFile.exists()) {
            System.out.println("Journal not found.");
            return;
        }

        try (Scanner scanner = new Scanner(journalFile)) {

            System.out.println("\n=== Journal Entry for " + date + " ===");

            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }

            System.out.println("\nPress Enter to go back.");
            new Scanner(System.in).nextLine();

        } catch (FileNotFoundException e) {
            System.out.println("Journal not found.");
        }
    }
}
