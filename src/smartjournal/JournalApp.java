package smartjournal;

import smartjournal.manager.UserManager;
import smartjournal.model.User;
import smartjournal.service.Sentiment;
import smartjournal.service.Weather;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

public class JournalApp {

   private UserManager userManager = new UserManager();
   private User loggedInUser;

   // UI Components
   private JFrame mainFrame;

   public JournalApp() {
      // Initialize the Main Window (Frame)
      mainFrame = new JFrame("SmartJournal Projects");
      mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      mainFrame.setSize(600, 600);
      mainFrame.setLocationRelativeTo(null); // Center on screen

      // Start with Login Screen
      initLoginScreen();

      mainFrame.setVisible(true);
   }

   // SCREEN 1: THE LOGIN PAGE
   private void initLoginScreen() {
      // Main Panel with Vertical Layout
      JPanel loginPanel = new JPanel();
      loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
      loginPanel.setBackground(Color.decode("#f0f4f8")); // Light Blue-Grey
      loginPanel.setBorder(new EmptyBorder(50, 50, 50, 50)); // Padding

      // Title
      JLabel titleLabel = new JLabel("Smart Journal");
      titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
      titleLabel.setForeground(Color.decode("#2c3e50"));
      titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

      // Inputs
      JTextField emailField = new JTextField(20);
      emailField.setMaximumSize(new Dimension(300, 35));
      
      JPasswordField passField = new JPasswordField(20);
      passField.setMaximumSize(new Dimension(300, 35));

      JLabel emailLabel = new JLabel("Email:");
      emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
      JLabel passLabel = new JLabel("Password:");
      passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

      // Login Button
      JButton loginButton = new JButton("Login");
      loginButton.setBackground(Color.decode("#ffffff"));
      loginButton.setForeground(Color.BLACK);
      loginButton.setFont(new Font("Arial", Font.BOLD, 14));
      loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
      
      // Status Label
      JLabel statusLabel = new JLabel(" ");
      statusLabel.setForeground(Color.RED);
      statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

      // Add Spacing and Components
      loginPanel.add(Box.createVerticalGlue()); // Push to center
      loginPanel.add(titleLabel);
      loginPanel.add(Box.createRigidArea(new Dimension(0, 20)));
      loginPanel.add(emailLabel);
      loginPanel.add(emailField);
      loginPanel.add(Box.createRigidArea(new Dimension(0, 10)));
      loginPanel.add(passLabel);
      loginPanel.add(passField);
      loginPanel.add(Box.createRigidArea(new Dimension(0, 20)));
      loginPanel.add(loginButton);
      loginPanel.add(Box.createRigidArea(new Dimension(0, 10)));
      loginPanel.add(statusLabel);
      loginPanel.add(Box.createVerticalGlue());

      // Action: When Login is Clicked
      loginButton.addActionListener(e -> {
         String email = emailField.getText().trim();
         String password = new String(passField.getPassword()).trim();

         User user = userManager.authenticate(email, password);

         if (user != null) {
               loggedInUser = user;
               initMainScreen(); // Switch to main screen
         } else {
               statusLabel.setText("Invalid credentials. Try again.");
         }
      });

      // Switch the frame content
      switchContent(loginPanel);
   }

   // SCREEN 2: THE DASHBOARD (Journal Entry)
   private void initMainScreen() {
      JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
      mainPanel.setBackground(Color.WHITE);
      mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

      // --- TOP HEADER ---
      JPanel headerPanel = new JPanel(new BorderLayout());
      headerPanel.setBackground(Color.WHITE);

      JLabel welcomeLabel = new JLabel("Welcome, " + loggedInUser.getDisplayName());
      welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));

      JButton logoutButton = new JButton("Logout");
      logoutButton.setBackground(Color.decode("#e74c3c"));
      logoutButton.setForeground(Color.WHITE);
      logoutButton.setContentAreaFilled(true);
      logoutButton.setOpaque(true);
      logoutButton.setBorderPainted(false);
      logoutButton.addActionListener(e -> {
         loggedInUser = null;
         initLoginScreen(); // Go back to login
      });

      headerPanel.add(welcomeLabel, BorderLayout.WEST);
      headerPanel.add(logoutButton, BorderLayout.EAST);
      mainPanel.add(headerPanel, BorderLayout.NORTH);

      // --- CENTER: JOURNAL AREA ---
      JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
      centerPanel.setBackground(Color.WHITE);

      JLabel dateLabel = new JLabel("Today's Entry (" + LocalDate.now() + ")");
      dateLabel.setForeground(Color.GRAY);

      JTextArea journalArea = new JTextArea();
      journalArea.setLineWrap(true);
      journalArea.setWrapStyleWord(true);
      journalArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
      
      // Wrap TextArea in ScrollPane (Essential for Swing)
      JScrollPane scrollPane = new JScrollPane(journalArea);

      centerPanel.add(dateLabel, BorderLayout.NORTH);
      centerPanel.add(scrollPane, BorderLayout.CENTER);
      mainPanel.add(centerPanel, BorderLayout.CENTER);

      // --- BOTTOM: ACTIONS ---
      JPanel bottomPanel = new JPanel(new BorderLayout(0, 10));
      bottomPanel.setBackground(Color.WHITE);

      JButton saveButton = new JButton("Save Entry with AI Analysis");
      saveButton.setBackground(Color.decode("#27ae60"));
      saveButton.setForeground(Color.WHITE);
      saveButton.setContentAreaFilled(true);
      saveButton.setOpaque(true);
      saveButton.setBorderPainted(false);
      saveButton.setFont(new Font("Arial", Font.BOLD, 14));
      
      JLabel aiStatusLabel = new JLabel("Ready to save.");
      aiStatusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
      aiStatusLabel.setForeground(Color.decode("#555555"));

      saveButton.addActionListener(e -> {
         String text = journalArea.getText().trim();
         if (text.isEmpty()) {
               aiStatusLabel.setText("Please write something first!");
               return;
         }

         // Disable button
         saveButton.setEnabled(false);
         aiStatusLabel.setText("Connecting to Satellite & AI Brain... (Please wait)");

         // Run API calls in a separate thread
         new Thread(() -> {
               saveJournalLogic(text, aiStatusLabel, saveButton);
         }).start();
      });

      bottomPanel.add(saveButton, BorderLayout.NORTH);
      bottomPanel.add(aiStatusLabel, BorderLayout.SOUTH);
      mainPanel.add(bottomPanel, BorderLayout.SOUTH);

      // Switch the frame content
      switchContent(mainPanel);
   }

   // Helper to switch screens
   private void switchContent(JPanel panel) {
      mainFrame.getContentPane().removeAll();
      mainFrame.getContentPane().add(panel);
      mainFrame.revalidate(); // Recalculate layout
      mainFrame.repaint();    // Redraw
   }

   // LOGIC: CONNECTING GUI TO BACKEND
   private void saveJournalLogic(String text, JLabel statusLabel, JButton saveButton) {
      try {
         // 1. Get Weather
         Weather weatherService = new Weather();
         String weather = weatherService.getCurrentWeather();

         // 2. Get Sentiment
         Sentiment sentimentService = new Sentiment();
         String sentiment = sentimentService.analyze(text);

         // 3. Format & Save
         String finalEntry = "Location Weather: " + weather + "\n" +
                              "Mood Analysis: " + sentiment + "\n" +
                              "_______________________\n" +
                              text;

         userManager.writeJournalEntry(loggedInUser, LocalDate.now(), finalEntry);

         // Update GUI (Must be done on Swing Event Dispatch Thread)
         SwingUtilities.invokeLater(() -> {
               statusLabel.setText("Saved! Weather: " + weather + " | Mood: " + sentiment);
               statusLabel.setForeground(Color.decode("#008000")); // Green
               saveButton.setEnabled(true);
         });

      } catch (Exception ex) {
         ex.printStackTrace(); // Good for debugging
         SwingUtilities.invokeLater(() -> {
               statusLabel.setText("Error saving journal.");
               saveButton.setEnabled(true);
         });
      }
   }

   public static void main(String[] args) {
      // Optional: Make it look like the native OS (Windows/Mac) instead of old Java style
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception ignored) {}

      // Start the app on the Swing Event Thread
      SwingUtilities.invokeLater(() -> new JournalApp());
   }
}