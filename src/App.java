import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

class TicketReservationSystem extends JFrame {
    private final Color PRIMARY_BLUE = new Color(29, 78, 216); // Pura solid deep blue button background ke liye
    private final Color HOVER_BLUE = new Color(30, 58, 138); // Hover karne par thoda dark blue
    private final Color APP_BACKGROUND = new Color(255, 255, 255); // Pure Clean White App Canvas
    private final Color TEXT_DARK = new Color(17, 24, 39); // Near Black Main Text
    private final Color TEXT_MUTED = new Color(107, 114, 128); // Cool Gray Subtext
    private final Color INPUT_BG = new Color(249, 250, 251); // Soft Input Background
    private final Color OUTPUT_BG = new Color(229, 231, 235); // Darker Contrast Gray for Output Logs
    private final Color OUTPUT_TEXT = new Color(31, 41, 55); // Clear Charcoal text for logs

    private final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 26);

    class Ticket {
        String category;
        String customerName;
        String seatNo;
        boolean booked;

        Ticket(String category, String seatNo) {
            this.category = category;
            this.seatNo = seatNo;
            this.customerName = "";
            this.booked = false;
        }
    }

    private final HashMap<String, Ticket> seats = new HashMap<>();
    private final Stack<Ticket> bookingStack = new Stack<>();
    private final Stack<Ticket> cancellationStack = new Stack<>();

    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JTextArea seatsDisplayArea;

    public TicketReservationSystem() {
        initializeSeats();

        setTitle("Ticket Reservation System");
        setSize(850, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(APP_BACKGROUND);

        // CardLayout setup full screen window flush fill karne ke liye
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(APP_BACKGROUND);
        cardPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Views generation
        cardPanel.add(createHomePanel(), "Home");
        cardPanel.add(createBookingPanel(), "Book");
        cardPanel.add(createCancelPanel(), "Cancel");
        cardPanel.add(createViewSeatsPanel(), "View");

        add(cardPanel, BorderLayout.CENTER);
        createMenus();

        setVisible(true);
    }

    private void initializeSeats() {
        for (int i = 1; i <= 10; i++)
            seats.put("E" + i, new Ticket("Economy", "E" + i));
        for (int i = 1; i <= 10; i++)
            seats.put("B" + i, new Ticket("Business", "B" + i));
        for (int i = 1; i <= 10; i++)
            seats.put("V" + i, new Ticket("VIP", "V" + i));
    }

    private void createMenus() {
        JMenuBar bar = new JMenuBar();
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 231, 235)));

        JMenu fileMenu = new JMenu("File");
        JMenu ticketMenu = new JMenu("Ticket");
        JMenu undoMenu = new JMenu("Undo");
        JMenu viewMenu = new JMenu("View");
        JMenu helpMenu = new JMenu("Help");

        JMenuItem homeItem = new JMenuItem("Home");
        JMenuItem exitItem = new JMenuItem("Exit");
        JMenuItem bookItem = new JMenuItem("Book Ticket");
        JMenuItem cancelItem = new JMenuItem("Cancel Ticket");
        JMenuItem undoBookItem = new JMenuItem("Undo Booking");
        JMenuItem undoCancelItem = new JMenuItem("Undo Cancellation");
        JMenuItem viewItem = new JMenuItem("View All Seats");
        JMenuItem aboutItem = new JMenuItem("About");

        fileMenu.add(homeItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        ticketMenu.add(bookItem);
        ticketMenu.add(cancelItem);
        undoMenu.add(undoBookItem);
        undoMenu.add(undoCancelItem);
        viewMenu.add(viewItem);
        helpMenu.add(aboutItem);

        bar.add(fileMenu);
        bar.add(ticketMenu);
        bar.add(undoMenu);
        bar.add(viewMenu);
        bar.add(helpMenu);
        setJMenuBar(bar);

        // Window Card Layout Action Handlers
        homeItem.addActionListener(e -> cardLayout.show(cardPanel, "Home"));
        exitItem.addActionListener(e -> System.exit(0));
        bookItem.addActionListener(e -> cardLayout.show(cardPanel, "Book"));
        cancelItem.addActionListener(e -> cardLayout.show(cardPanel, "Cancel"));
        viewItem.addActionListener(e -> {
            showAllSeats();
            cardLayout.show(cardPanel, "View");
        });

        undoBookItem.addActionListener(e -> undoBooking());
        undoCancelItem.addActionListener(e -> undoCancellation());
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Dynamic Ticket Reservation System\nJava Swing Clean UI Modernization",
                "About", JOptionPane.INFORMATION_MESSAGE));
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout(30, 30));
        panel.setBackground(APP_BACKGROUND);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel heading = new JLabel("Welcome back!", SwingConstants.LEFT);
        heading.setFont(TITLE_FONT);
        heading.setForeground(TEXT_DARK);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(MAIN_FONT);
        area.setForeground(TEXT_MUTED);
        area.setBackground(APP_BACKGROUND);
        area.setLineWrap(true);
        area.setText("Navigate through the application seamlessly using the Menu Bar above.\n\n" +
                "• Quick Access Guide:\n" +
                "  - Ticket -> Book Ticket (Secure your seat)\n" +
                "  - Ticket -> Cancel Ticket (Release a reservation)\n" +
                "  - Undo -> Rollback your recent steps\n" +
                "  - View -> Check real-time seat availability map");

        panel.add(heading, BorderLayout.NORTH);
        panel.add(area, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBookingPanel() {
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(APP_BACKGROUND);
        container.setBorder(new EmptyBorder(40, 50, 40, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Book a Ticket");
        title.setFont(TITLE_FONT);
        title.setForeground(TEXT_DARK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(title, gbc);

        gbc.gridwidth = 1;
        JTextField nameField = createStyledTextField();
        JComboBox<String> categoryBox = new JComboBox<>(new String[] { "Economy", "Business", "VIP" });
        categoryBox.setFont(MAIN_FONT);
        categoryBox.setPreferredSize(new Dimension(320, 38));
        categoryBox.setBackground(Color.WHITE);

        JTextField seatField = createStyledTextField();
        JTextArea resultArea = createStyledTextArea();
        JButton bookButton = createStyledButton("Confirm Booking");

        addFormRow(container, gbc, "Customer Name:", nameField, 1);
        addFormRow(container, gbc, "Seat Category:", categoryBox, 2);
        addFormRow(container, gbc, "Seat Number (e.g., E1, B3):", seatField, 3);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(bookButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(null);
        container.add(scrollPane, gbc);

        bookButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String seat = seatField.getText().trim().toUpperCase();

            if (name.isEmpty() || seat.isEmpty()) {
                resultArea.setText("⚠️ Error: All fields must be filled out.");
                return;
            }
            if (!seats.containsKey(seat)) {
                resultArea.setText("⚠️ Error: Seat " + seat + " does not exist.");
                return;
            }

            Ticket t = seats.get(seat);
            if (t.booked) {
                resultArea.setText("❌ Status: Seat " + seat + " is already booked.");
                return;
            }

            t.customerName = name;
            t.booked = true;
            bookingStack.push(t);

            resultArea.setText("🎉 Booking Confirmed!\n\n" +
                    "• Name: " + name + "\n" +
                    "• Seat No: " + seat + "\n" +
                    "• Tier: " + t.category);

            nameField.setText("");
            seatField.setText("");
        });

        return container;
    }

    private JPanel createCancelPanel() {
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(APP_BACKGROUND);
        container.setBorder(new EmptyBorder(40, 50, 40, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Cancel a Ticket");
        title.setFont(TITLE_FONT);
        title.setForeground(TEXT_DARK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(title, gbc);

        gbc.gridwidth = 1;
        JTextField seatField = createStyledTextField();
        JButton cancelButton = createStyledButton("Cancel Reservation");
        JTextArea output = createStyledTextArea();

        addFormRow(container, gbc, "Enter Seat Number:", seatField, 1);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(cancelButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        JScrollPane scrollPane = new JScrollPane(output);
        scrollPane.setBorder(null);
        container.add(scrollPane, gbc);

        cancelButton.addActionListener(e -> {
            String seat = seatField.getText().trim().toUpperCase();

            if (!seats.containsKey(seat)) {
                output.setText("⚠️ Error: Seat layout context not recognized.");
                return;
            }

            Ticket t = seats.get(seat);
            if (!t.booked) {
                output.setText("❌ Status: Seat is already open/vacant.");
                return;
            }

            cancellationStack.push(t);
            t.booked = false;
            t.customerName = "";

            output.setText("✔️ Success: Seat " + seat + " has been completely released.");
            seatField.setText("");
        });

        return container;
    }

    private JPanel createViewSeatsPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(APP_BACKGROUND);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel title = new JLabel("Live Seat Matrix Status");
        title.setFont(TITLE_FONT);
        title.setForeground(TEXT_DARK);
        panel.add(title, BorderLayout.NORTH);

        seatsDisplayArea = createStyledTextArea();
        JScrollPane scrollPane = new JScrollPane(seatsDisplayArea);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void showAllSeats() {
        if (seatsDisplayArea == null)
            return;
        seatsDisplayArea.setText("");
        seatsDisplayArea.append(String.format("   %-15s | %-18s | %-20s\n", "SEAT NO", "CATEGORY", "STATUS"));
        seatsDisplayArea.append("   -------------------------------------------------------------------\n");

        TreeMap<String, Ticket> sortedSeats = new TreeMap<>(seats);
        for (Ticket t : sortedSeats.values()) {
            String status = t.booked ? "🔴 Reserved [" + t.customerName + "]" : "🟢 Available";
            seatsDisplayArea.append(String.format("   %-15s | %-18s | %-20s\n", t.seatNo, t.category, status));
        }
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, String labelText, Component comp, int yRow) {
        JLabel label = new JLabel(labelText);
        label.setFont(MAIN_FONT);
        label.setForeground(TEXT_DARK);

        gbc.gridx = 0;
        gbc.gridy = yRow;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.gridy = yRow;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(comp, gbc);
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(MAIN_FONT);
        tf.setPreferredSize(new Dimension(320, 38));
        tf.setMinimumSize(new Dimension(320, 38));
        tf.setBackground(INPUT_BG);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(229, 231, 235), 1, true),
                new EmptyBorder(4, 12, 4, 12)));
        return tf;
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(PRIMARY_BLUE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(190, 42));
        btn.setBorder(new LineBorder(PRIMARY_BLUE, 1, true));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Pura solid hover dynamics listeners handles
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(HOVER_BLUE);
                btn.setBorder(new LineBorder(HOVER_BLUE, 1, true));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(PRIMARY_BLUE);
                btn.setBorder(new LineBorder(PRIMARY_BLUE, 1, true));
            }
        });
        return btn;
    }

    private JTextArea createStyledTextArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 14));
        area.setBackground(OUTPUT_BG);
        area.setForeground(OUTPUT_TEXT);
        area.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(209, 213, 219), 1, true),
                new EmptyBorder(15, 15, 15, 15)));
        return area;
    }

    private void undoBooking() {
        if (bookingStack.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No bookings found on the transaction logs.", "Undo Log Empty",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        Ticket t = bookingStack.pop();
        t.booked = false;
        t.customerName = "";
        showAllSeats();
        JOptionPane.showMessageDialog(this, "Booking successfully reversed for Seat: " + t.seatNo);
    }

    private void undoCancellation() {
        if (cancellationStack.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No cancellations found on the history logs.", "Undo Log Empty",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        Ticket t = cancellationStack.pop();
        t.booked = true;
        showAllSeats();
        JOptionPane.showMessageDialog(this, "Cancellation reversed! Seat restored to: " + t.seatNo);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(TicketReservationSystem::new);
    }
}