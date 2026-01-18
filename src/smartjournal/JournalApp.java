package smartjournal;

import smartjournal.manager.UserManager;
import smartjournal.model.User;
import smartjournal.service.Weather;
import smartjournal.service.Sentiment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class JournalApp extends JFrame {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
    private static final Color PRIMARY = new Color(79, 70, 229);
    private static final Color SECONDARY = new Color(249, 250, 251);
    private static final Color ACCENT = new Color(16, 185, 129);
    
    private UserManager userManager;
    private User loggedInUser;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel loginPanel, menuPanel, journalPanel, statsPanel;
    
    public JournalApp() {
        userManager = new UserManager();
        setTitle("Smart Journal App");
        setSize(900, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        createLoginPanel();
        createMenuPanel();
        createJournalPanel();
        createStatsPanel();
        
        add(mainPanel);
        cardLayout.show(mainPanel, "login");
        setVisible(true);
    }
    
    private void createLoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(SECONDARY);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JLabel title = new JLabel("📔 Smart Journal");
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        loginPanel.add(title, gbc);
        
        JLabel subtitle = new JLabel("Your Personal Digital Diary");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridy = 1;
        loginPanel.add(subtitle, gbc);
        
        gbc.gridwidth = 1; gbc.gridy = 2; gbc.gridx = 0;
        loginPanel.add(new JLabel("Email:"), gbc);
        
        JTextField emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        loginPanel.add(emailField, gbc);
        
        gbc.gridy = 3; gbc.gridx = 0;
        loginPanel.add(new JLabel("Password:"), gbc);
        
        JPasswordField passField = new JPasswordField(20);
        gbc.gridx = 1;
        loginPanel.add(passField, gbc);
        
        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(PRIMARY);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 14));
        loginBtn.setFocusPainted(false);
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginPanel.add(loginBtn, gbc);
        
        JLabel status = new JLabel(" ");
        gbc.gridy = 5;
        loginPanel.add(status, gbc);
        
        loginBtn.addActionListener(e -> {
            loggedInUser = userManager.authenticate(
                emailField.getText().trim(),
                new String(passField.getPassword()).trim()
            );
            
            if (loggedInUser != null) {
                status.setForeground(ACCENT);
                status.setText("✓ Login successful!");
                updateMenuPanel();
                cardLayout.show(mainPanel, "menu");
            } else {
                status.setForeground(Color.RED);
                status.setText("✗ Invalid credentials");
            }
        });
        
        mainPanel.add(loginPanel, "login");
    }
    
    private void createMenuPanel() {
        menuPanel = new JPanel(new BorderLayout());
        menuPanel.setBackground(Color.WHITE);
        
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel welcome = new JLabel("Welcome!");
        welcome.setFont(new Font("Arial", Font.BOLD, 28));
        welcome.setForeground(Color.WHITE);
        header.add(welcome, BorderLayout.NORTH);
        
        JLabel date = new JLabel(LocalDate.now().format(DATE_FORMATTER));
        date.setFont(new Font("Arial", Font.PLAIN, 16));
        date.setForeground(Color.WHITE);
        header.add(date, BorderLayout.CENTER);
        
        menuPanel.add(header, BorderLayout.NORTH);
        
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JButton journalBtn = createMenuButton("📝 My Journals", "Create, view, and edit entries");
        journalBtn.addActionListener(e -> {
            updateJournalPanel();
            cardLayout.show(mainPanel, "journal");
        });
        gbc.gridy = 0;
        center.add(journalBtn, gbc);
        
        JButton statsBtn = createMenuButton("📊 Mood Statistics", "View weekly mood summary");
        statsBtn.addActionListener(e -> {
            updateStatsPanel();
            cardLayout.show(mainPanel, "stats");
        });
        gbc.gridy = 1;
        center.add(statsBtn, gbc);
        
        JButton logoutBtn = createMenuButton("🚪 Logout", "Exit application");
        logoutBtn.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Logout?", "Confirm", 
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                cardLayout.show(mainPanel, "login");
            }
        });
        gbc.gridy = 2;
        center.add(logoutBtn, gbc);
        
        menuPanel.add(center, BorderLayout.CENTER);
        mainPanel.add(menuPanel, "menu");
    }
    
    private JButton createMenuButton(String title, String desc) {
        JButton btn = new JButton(
            "<html><b>" + title + "</b><br><small>" + desc + "</small></html>"
        );
        btn.setPreferredSize(new Dimension(400, 70));
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY, 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        btn.setFocusPainted(false);
        return btn;
    }
    
    private void createJournalPanel() {
        journalPanel = new JPanel(new BorderLayout());
        journalPanel.setBackground(Color.WHITE);
        mainPanel.add(journalPanel, "journal");
    }
    
    private void updateJournalPanel() {
        journalPanel.removeAll();
        
        JPanel header = createHeader("📝 My Journals", "Select a date");
        journalPanel.add(header, BorderLayout.NORTH);
        
        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        ArrayList<LocalDate> dates = getJournalDates();
        LocalDate today = LocalDate.now();
        if (!dates.contains(today)) dates.add(today);
        dates.sort((d1, d2) -> d2.compareTo(d1));
        
        DefaultListModel<String> model = new DefaultListModel<>();
        for (LocalDate d : dates) {
            model.addElement(d.format(DATE_FORMATTER) + (d.equals(today) ? " (Today)" : ""));
        }
        
        JList<String> list = new JList<>(model);
        list.setFont(new Font("Arial", Font.PLAIN, 16));
        center.add(new JScrollPane(list), BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel();
        JButton viewBtn = new JButton("View/Edit");
        viewBtn.addActionListener(e -> {
            int idx = list.getSelectedIndex();
            if (idx >= 0) showJournalEntry(dates.get(idx));
        });
        btnPanel.add(viewBtn);
        
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "menu"));
        btnPanel.add(backBtn);
        
        center.add(btnPanel, BorderLayout.SOUTH);
        journalPanel.add(center, BorderLayout.CENTER);
        journalPanel.revalidate();
        journalPanel.repaint();
    }
    
    private void showJournalEntry(LocalDate date) {
        File file = userManager.getJournalFile(loggedInUser, date);
        if (date.isBefore(LocalDate.now())) {
            if (file.exists()) viewJournal(date);
        } else {
            if (!file.exists()) createJournal(date);
            else editJournal(date);
        }
    }
    
    private void createJournal(LocalDate date) {
        JDialog dlg = new JDialog(this, "Create Entry", true);
        dlg.setSize(600, 400);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());
        
        JTextArea area = new JTextArea();
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Arial", Font.PLAIN, 14));
        dlg.add(new JScrollPane(area), BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel();
        JButton save = new JButton("Save");
        save.addActionListener(e -> {
            String text = area.getText().trim();
            if (!text.isEmpty()) {
                Weather w = new Weather();
                Sentiment s = new Sentiment();
                String entry = String.format("Weather: %s\nMood: %s\n%s\n%s",
                    w.getCurrentWeather(), s.analyze(text), "─".repeat(40), text);
                userManager.writeJournalEntry(loggedInUser, date, entry);
                JOptionPane.showMessageDialog(dlg, "Saved!");
                dlg.dispose();
                updateJournalPanel();
            }
        });
        btnPanel.add(save);
        
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dlg.dispose());
        btnPanel.add(cancel);
        
        dlg.add(btnPanel, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }
    
    private void viewJournal(LocalDate date) {
        JDialog dlg = new JDialog(this, "View Entry", true);
        dlg.setSize(600, 400);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());
        
        StringBuilder content = new StringBuilder();
        try (Scanner sc = new Scanner(userManager.getJournalFile(loggedInUser, date))) {
            while (sc.hasNextLine()) content.append(sc.nextLine()).append("\n");
        } catch (Exception e) {}
        
        JTextArea area = new JTextArea(content.toString());
        area.setEditable(false);
        area.setLineWrap(true);
        area.setFont(new Font("Arial", Font.PLAIN, 14));
        dlg.add(new JScrollPane(area), BorderLayout.CENTER);
        
        JButton close = new JButton("Close");
        close.addActionListener(e -> dlg.dispose());
        JPanel p = new JPanel();
        p.add(close);
        dlg.add(p, BorderLayout.SOUTH);
        
        dlg.setVisible(true);
    }
    
    private void editJournal(LocalDate date) {
        JDialog dlg = new JDialog(this, "Edit Entry", true);
        dlg.setSize(600, 400);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());
        
        JTextArea area = new JTextArea();
        area.setLineWrap(true);
        area.setFont(new Font("Arial", Font.PLAIN, 14));
        dlg.add(new JScrollPane(area), BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel();
        JButton save = new JButton("Save");
        save.addActionListener(e -> {
            String text = area.getText().trim();
            if (!text.isEmpty()) {
                Weather w = new Weather();
                Sentiment s = new Sentiment();
                String entry = String.format("Weather: %s\nMood: %s\n%s\n%s",
                    w.getCurrentWeather(), s.analyze(text), "─".repeat(40), text);
                userManager.overwriteJournal(loggedInUser, date, entry);
                JOptionPane.showMessageDialog(dlg, "Updated!");
                dlg.dispose();
                updateJournalPanel();
            }
        });
        btnPanel.add(save);
        
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dlg.dispose());
        btnPanel.add(cancel);
        
        dlg.add(btnPanel, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }
    
    private void createStatsPanel() {
        statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBackground(Color.WHITE);
        mainPanel.add(statsPanel, "stats");
    }
    
    private void updateStatsPanel() {
        statsPanel.removeAll();
        
        JPanel header = createHeader("📊 Weekly Mood", "Last 7 days");
        statsPanel.add(header, BorderLayout.NORTH);
        
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        int[] moods = new int[6];
        int total = 0;
        
        for (int i = 0; i < 7; i++) {
            LocalDate d = LocalDate.now().minusDays(i);
            File f = userManager.getJournalFile(loggedInUser, d);
            if (!f.exists()) continue;
            
            total++;
            try (Scanner sc = new Scanner(f)) {
                StringBuilder txt = new StringBuilder();
                while (sc.hasNextLine()) txt.append(sc.nextLine().toLowerCase()).append(" ");
                String mood = new Sentiment().analyze(txt.toString());
                switch (mood) {
                    case "very positive": moods[0]++; break;
                    case "positive": moods[1]++; break;
                    case "mixed": moods[2]++; break;
                    case "negative": moods[3]++; break;
                    case "very negative": moods[4]++; break;
                    default: moods[5]++; break;
                }
            } catch (Exception e) {}
        }
        
        if (total == 0) {
            center.add(new JLabel("No entries found"));
        } else {
            String[] labels = {"😄 Very Positive", "😊 Positive", "😐 Mixed", 
                              "😔 Negative", "😢 Very Negative", "😶 Neutral"};
            for (int i = 0; i < moods.length; i++) {
                if (moods[i] > 0) {
                    int pct = moods[i] * 100 / total;
                    center.add(new JLabel(labels[i] + ": " + moods[i] + " (" + pct + "%)"));
                    center.add(Box.createVerticalStrut(10));
                }
            }
        }
        
        statsPanel.add(new JScrollPane(center), BorderLayout.CENTER);
        
        JButton back = new JButton("Back");
        back.addActionListener(e -> cardLayout.show(mainPanel, "menu"));
        JPanel p = new JPanel();
        p.add(back);
        statsPanel.add(p, BorderLayout.SOUTH);
        
        statsPanel.revalidate();
        statsPanel.repaint();
    }
    
    private JPanel createHeader(String title, String sub) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(PRIMARY);
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel t = new JLabel(title);
        t.setFont(new Font("Arial", Font.BOLD, 24));
        t.setForeground(Color.WHITE);
        p.add(t, BorderLayout.NORTH);
        
        JLabel s = new JLabel(sub);
        s.setFont(new Font("Arial", Font.PLAIN, 14));
        s.setForeground(Color.WHITE);
        p.add(s, BorderLayout.CENTER);
        
        return p;
    }
    
    private ArrayList<LocalDate> getJournalDates() {
        ArrayList<LocalDate> dates = new ArrayList<>();
        File folder = new File("data/journals/" + loggedInUser.getEmail());
        if (!folder.exists()) return dates;
        
        File[] files = folder.listFiles();
        if (files == null) return dates;
        
        for (File f : files) {
            if (f.getName().endsWith(".txt")) {
                try {
                    dates.add(LocalDate.parse(f.getName().replace(".txt", "")));
                } catch (Exception e) {}
            }
        }
        return dates;
    }
    
    private void updateMenuPanel() {
        if (loggedInUser == null) return;
        Component[] comps = menuPanel.getComponents();
        for (Component c : comps) {
            if (c instanceof JPanel && ((JPanel)c).getBackground().equals(PRIMARY)) {
                JPanel p = (JPanel)c;
                for (Component cc : p.getComponents()) {
                    if (cc instanceof JLabel) {
                        JLabel lbl = (JLabel)cc;
                        if (lbl.getFont().getSize() == 28) {
                            LocalTime now = LocalTime.now();
                            String greeting = now.isBefore(LocalTime.NOON) ? "Good Morning" :
                                            now.isBefore(LocalTime.of(17, 0)) ? "Good Afternoon" :
                                            "Good Evening";
                            lbl.setText(greeting + ", " + loggedInUser.getDisplayName() + "!");
                        }
                    }
                }
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JournalApp());
    }
}