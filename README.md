# Dynamic Ticket Reservation System

A beautiful, high-contrast minimalist Desktop Ticket Reservation application built natively in Java using the Swing framework. This application features an edge-to-edge full-screen workspace design, complete in-memory data structures for fast processing, custom deep blue interactive action controls, and an advanced Transaction Undo rollback system.

---

## 🚀 Key Features

* **True White Minimalist UI:** Modern, distraction-free edge-to-edge layout matching current software interfaces.
* **Solid Deep Blue Components:** High-visibility action buttons with real-time reactive hover states (Cobalt Blue to Dark Navy transition).
* **In-Memory Data Architecture:** Zero external database configuration required. Leverages highly optimized Java Collection Framework arrays for instant state operations.
* **Dual-Engine Undo System:** Uses independent transaction logs (LIFO Stack structures) to undo recent bookings or cancellations on the fly.
* **Live Seat Matrix Status:** Live tabular tracking view showing categorized seats (Economy, Business, VIP) and their reservation states.

---

## 🛠️ Tech Stack & Architecture

* **Language:** Java 8 or higher
* **Framework:** Java Swing & AWT (Abstract Window Toolkit)
* **Design Pattern:** Object-Oriented Component Architecture
* **Data Core Components:**
  * `HashMap<String, Ticket>` - For instantaneous $O(1)$ seat retrieval and state modification.
  * `TreeMap<String, Ticket>` - To dynamically sort seat layouts alphanumerically for display.
  * `Stack<Ticket>` - Dual-stack tracking system to power transaction history rollbacks.

---

## 📂 Project Structure

```text
├── TicketReservationSystem.java   # Core application source code file
└── README.md                      # Project documentation overview
📋 System Requirements
Java Development Kit (JDK): Version 1.8 or higher installed.

Operating System: Platform independent (Windows, macOS, Linux supporting Java Swing).

⚙️ Compilation & Execution Guide
Follow these simple steps to compile and run your desktop application using any standard system terminal or command prompt:

Step 1: Clone or Create the File
Ensure your file is named exactly TicketReservationSystem.java and paste the provided application code inside it.

Step 2: Open Terminal / Command Prompt
Navigate to the directory where your .java file is saved:

Bash
cd path/to/your/project-directory
Step 3: Compile the Source Code
Compile the code using the Java Compiler (javac):

Bash
javac TicketReservationSystem.java
Note: This will output class binaries inside your current working directory without errors.

Step 4: Run the Application
Execute the compiled bytecode using the Java Runtime Environment (java):

Bash
java TicketReservationSystem
📖 How to Use the Application
1. Navigating Between Screens
Use the clean MenuBar located at the top of the interface frame:

Go to Ticket ➔ Book Ticket to reserve seats.

Go to Ticket ➔ Cancel Ticket to free up a reservation.

Go to View ➔ View All Seats to see the live status board.

Go to File ➔ Home to go back to the launch instruction screen.

2. Standard Booking Flow
Navigate to the Book a Ticket panel.

Enter the customer's full name.

Select the preferred Seat Category (Economy, Business, VIP).

Enter an alphanumeric Seat Number matching the class prefixes:

Economy: E1 to E10

Business: B1 to B10

VIP: V1 to V10

Press the solid blue Confirm Booking button. Look at the darkened status area at the bottom to verify receipt details.

3. Real-Time Transaction Rollbacks
Made a mistake?

Go to Undo ➔ Undo Booking to instantly remove the most recent customer booking and mark that seat as open again.

Go to Undo ➔ Undo Cancellation to instantly restore a customer reservation that was deleted by accident.
