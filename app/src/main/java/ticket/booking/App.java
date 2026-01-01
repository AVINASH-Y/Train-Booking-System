package ticket.booking;

import ticket.booking.entities.Ticket;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.service.UserBookingService;
import ticket.booking.util.UserServiceUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class App {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Welcome to Train Booking System");
        System.out.println("========================================");
        Scanner scanner = new Scanner(System.in);
        int option = 0;
        UserBookingService userBookingService;
        try{
            userBookingService = new UserBookingService();
        }catch(IOException ex){
            System.out.println("Error: Unable to initialize the system.");
            ex.printStackTrace();
            scanner.close();
            return;
        }
        Train trainSelectedForBooking = null;
        String sourceStation = null;
        String destinationStation = null;
        boolean isLoggedIn = false;
        String loggedInUsername = null;
        
        while(option!=7){
            System.out.println("\n========================================");
            if (isLoggedIn && loggedInUsername != null) {
                System.out.println("👤 Logged in as: " + loggedInUsername);
            }
            System.out.println("Choose an option:");
            if (!isLoggedIn) {
                System.out.println("1. Sign up");
                System.out.println("2. Login");
                System.out.println("3. Search Trains (View Only)");
                System.out.println("7. Exit the App");
            } else {
                System.out.println("2. Logout");
                System.out.println("3. Fetch Bookings");
                System.out.println("4. Search Trains");
                System.out.println("5. Book a Seat");
                System.out.println("6. Cancel my Booking");
                System.out.println("7. Exit the App");
            }
            System.out.println("========================================");
            System.out.print("Enter your choice: ");
            try {
                option = scanner.nextInt();
                scanner.nextLine(); // Consume newline
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number between 1-7.");
                scanner.nextLine(); // Clear the invalid input
                continue;
            }
            switch (option){
                case 1:
                    if (isLoggedIn) {
                        System.out.println("✗ Invalid option. You are already logged in. Use option 2 to logout.");
                        break;
                    }
                    System.out.println("\n--- Sign Up ---");
                    System.out.print("Enter username: ");
                    String nameToSignUp = scanner.nextLine().trim();
                    System.out.print("Enter password: ");
                    String passwordToSignUp = scanner.nextLine().trim();
                    if (nameToSignUp.isEmpty() || passwordToSignUp.isEmpty()) {
                        System.out.println("✗ Error: Username and password cannot be empty.");
                        break;
                    }
                    User userToSignup = new User(nameToSignUp, passwordToSignUp, UserServiceUtil.hashPassword(passwordToSignUp), new ArrayList<>(), UUID.randomUUID().toString());
                    Boolean signUpResult = userBookingService.signUp(userToSignup);
                    if (signUpResult != null && signUpResult) {
                        System.out.println("✓ Sign up successful! Please login to continue.");
                    } else {
                        System.out.println("✗ Sign up failed. Please try again.");
                    }
                    break;
                case 2:
                    if (isLoggedIn) {
                        // Logout
                        System.out.println("\n--- Logout ---");
                        System.out.println("✓ Logged out successfully. Goodbye, " + loggedInUsername + "!");
                        isLoggedIn = false;
                        loggedInUsername = null;
                        userBookingService = null;
                        trainSelectedForBooking = null;
                        sourceStation = null;
                        destinationStation = null;
                        try {
                            userBookingService = new UserBookingService();
                        } catch (IOException ex) {
                            System.out.println("✗ Error reinitializing service.");
                        }
                    } else {
                        // Login
                        System.out.println("\n--- Login ---");
                        System.out.print("Enter username: ");
                        String nameToLogin = scanner.nextLine().trim();
                        System.out.print("Enter password: ");
                        String passwordToLogin = scanner.nextLine().trim();
                        if (nameToLogin.isEmpty() || passwordToLogin.isEmpty()) {
                            System.out.println("✗ Error: Username and password cannot be empty.");
                            break;
                        }
                        User userToLogin = new User(nameToLogin, passwordToLogin, UserServiceUtil.hashPassword(passwordToLogin), new ArrayList<>(), UUID.randomUUID().toString());
                        try{
                            UserBookingService tempService = new UserBookingService(userToLogin);
                            if (tempService.loginUser()) {
                                userBookingService = tempService;
                                isLoggedIn = true;
                                loggedInUsername = nameToLogin;
                                System.out.println("✓ Login successful! Welcome, " + nameToLogin + "!");
                            } else {
                                System.out.println("✗ Login failed. Invalid username or password.");
                            }
                        }catch (IOException ex){
                            System.out.println("✗ Error during login. Please try again.");
                            ex.printStackTrace();
                        }catch (Exception ex){
                            System.out.println("✗ Error during login: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    }
                    break;
                case 3:
                    if (!isLoggedIn) {
                        // Allow viewing trains without login
                        System.out.println("\n--- Search Trains (View Only) ---");
                        System.out.println("Note: Login required to book tickets");
                        System.out.print("Enter source station: ");
                        String viewSource = scanner.nextLine().trim();
                        System.out.print("Enter destination station: ");
                        String viewDest = scanner.nextLine().trim();
                        
                        if (viewSource.isEmpty() || viewDest.isEmpty()) {
                            System.out.println("✗ Source and destination stations cannot be empty.");
                            break;
                        }
                        
                        if (viewSource.equalsIgnoreCase(viewDest)) {
                            System.out.println("✗ Source and destination cannot be the same.");
                            break;
                        }
                        
                        try {
                            UserBookingService tempService = new UserBookingService();
                            String sourceLower = viewSource.toLowerCase();
                            String destLower = viewDest.toLowerCase();
                            List<Train> viewTrains = tempService.getTrainsBothDirections(sourceLower, destLower);
                            
                            if (viewTrains == null || viewTrains.isEmpty()) {
                                System.out.println("✗ No trains found connecting " + viewSource + " and " + viewDest);
                            } else {
                                System.out.println("\n✓ Found " + viewTrains.size() + " train(s):");
                                System.out.println("========================================");
                                int idx = 1;
                                for (Train t : viewTrains) {
                                    int dir = tempService.getTrainDirection(t, sourceLower, destLower);
                                    String routeDisplay = dir == 1 ? (viewSource + " → " + viewDest) : (viewDest + " ← " + viewSource);
                                    System.out.println(idx + ". " + t.getTrainId() + " | " + routeDisplay);
                                    idx++;
                                }
                                System.out.println("\nPlease login to book tickets.");
                            }
                        } catch (Exception e) {
                            System.out.println("✗ Error searching trains.");
                        }
                        break;
                    }
                    System.out.println("\n--- Your Bookings ---");
                    try {
                        userBookingService.fetchBookings(loggedInUsername);
                    } catch (Exception e) {
                        System.out.println("✗ Error fetching bookings. Please login again.");
                    }
                    break;
                case 4:
                    if (!isLoggedIn) {
                        System.out.println("✗ Invalid option. Please login first.");
                        break;
                    }
                    System.out.println("\n--- Search Trains ---");
                    System.out.print("Enter source station: ");
                    sourceStation = scanner.nextLine().trim();
                    System.out.print("Enter destination station: ");
                    destinationStation = scanner.nextLine().trim();
                    
                    if (sourceStation.isEmpty() || destinationStation.isEmpty()) {
                        System.out.println("✗ Source and destination stations cannot be empty.");
                        break;
                    }
                    
                    if (sourceStation.equalsIgnoreCase(destinationStation)) {
                        System.out.println("✗ Source and destination cannot be the same.");
                        break;
                    }
                    
                    // Normalize to lowercase for search
                    String sourceLower = sourceStation.toLowerCase();
                    String destLower = destinationStation.toLowerCase();
                    
                    // Search in both directions automatically
                    List<Train> trains = userBookingService.getTrainsBothDirections(sourceLower, destLower);
                    
                    if (trains == null || trains.isEmpty()) {
                        System.out.println("✗ No trains found connecting " + sourceStation + " and " + destinationStation);
                        System.out.println("   Please check the station names and try again.");
                        break;
                    }
                    
                    System.out.println("\n✓ Found " + trains.size() + " train(s) connecting " + sourceStation + " and " + destinationStation + ":");
                    System.out.println("========================================");
                    int index = 1;
                    for (Train t: trains){
                        int direction = userBookingService.getTrainDirection(t, sourceLower, destLower);
                        String directionIndicator = direction == 1 ? "→" : (direction == -1 ? "←" : "");
                        String routeDisplay = direction == 1 ? 
                            (sourceStation + " → " + destinationStation) : 
                            (destinationStation + " ← " + sourceStation);
                        
                        System.out.println(index + ". Train ID: " + t.getTrainId() + " | Train No: " + t.getTrainNo());
                        System.out.println("   Direction: " + routeDisplay + " " + directionIndicator);
                        
                        if (t.getStationTimes() != null && t.getStations() != null) {
                            System.out.println("   Full Route: " + String.join(" → ", t.getStations()));
                            System.out.println("   Station Times:");
                            
                            // Show times for relevant stations
                            List<String> stations = t.getStations();
                            int sourceIdx = stations.indexOf(sourceLower);
                            int destIdx = stations.indexOf(destLower);
                            
                            int startIdx = Math.min(sourceIdx, destIdx);
                            int endIdx = Math.max(sourceIdx, destIdx);
                            
                            for (int i = startIdx; i <= endIdx; i++) {
                                String station = stations.get(i);
                                String time = t.getStationTimes().get(station);
                                if (time != null) {
                                    String marker = (i == sourceIdx || i == destIdx) ? " ★" : "";
                                    System.out.println("     " + station + ": " + time + marker);
                                }
                            }
                            
                            // Show available seats
                            if (t.getSeats() != null) {
                                int totalSeats = t.getSeats().stream()
                                    .mapToInt(row -> row.stream().mapToInt(seat -> seat == 0 ? 1 : 0).sum())
                                    .sum();
                                System.out.println("   Available Seats: " + totalSeats);
                            }
                        }
                        System.out.println();
                    }
                    System.out.print("Select a train (enter number 1-" + trains.size() + "): ");
                    try {
                        int trainIndex = scanner.nextInt() - 1; // Convert to 0-based index
                        scanner.nextLine(); // Consume newline
                        if (trainIndex >= 0 && trainIndex < trains.size()) {
                            trainSelectedForBooking = trains.get(trainIndex);
                            // Update source/destination based on direction
                            int direction = userBookingService.getTrainDirection(trainSelectedForBooking, sourceLower, destLower);
                            if (direction == -1) {
                                // Reverse direction - swap source and destination
                                String temp = sourceStation;
                                sourceStation = destinationStation;
                                destinationStation = temp;
                            }
                            System.out.println("✓ Train selected: " + trainSelectedForBooking.getTrainId());
                            System.out.println("  Route: " + sourceStation + " → " + destinationStation);
                        } else {
                            System.out.println("✗ Invalid train selection. Please try again.");
                        }
                    } catch (Exception e) {
                        System.out.println("✗ Invalid input. Please enter a valid number.");
                        scanner.nextLine(); // Clear the invalid input
                    }
                    break;
                case 5:
                    if (!isLoggedIn) {
                        System.out.println("✗ Invalid option. Please login first.");
                        break;
                    }
                    if (trainSelectedForBooking == null || trainSelectedForBooking.getTrainId() == null) {
                        System.out.println("✗ Please select a train first (option 4).");
                        break;
                    }
                    System.out.println("\n--- Book a Seat ---");
                    System.out.println("Train: " + trainSelectedForBooking.getTrainId());
                    System.out.println("Route: " + sourceStation + " → " + destinationStation);
                    System.out.println("\nSeat Layout (0 = Available, 1 = Booked):");
                    System.out.println("----------------------------------------");
                    List<List<Integer>> seats = userBookingService.fetchSeats(trainSelectedForBooking);
                    if (seats == null || seats.isEmpty()) {
                        System.out.println("✗ No seats available for this train.");
                        break;
                    }
                    // Print column headers
                    System.out.print("   ");
                    for (int col = 0; col < seats.get(0).size(); col++) {
                        System.out.print((col + 1) + " ");
                    }
                    System.out.println();
                    // Print rows with row numbers
                    for (int i = 0; i < seats.size(); i++) {
                        System.out.print((i + 1) + "  ");
                        for (Integer val: seats.get(i)){
                            System.out.print(val+" ");
                        }
                        System.out.println();
                    }
                    System.out.println("\nNote: Row and column numbers start from 1");
                    System.out.print("Enter row number: ");
                    try {
                        int row = scanner.nextInt();
                        System.out.print("Enter column number: ");
                        int col = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        System.out.println("\nProcessing your booking...");
                        Boolean booked = userBookingService.bookTrainSeat(trainSelectedForBooking, row - 1, col - 1, sourceStation, destinationStation, userBookingService.getCurrentUser());
                        if(booked != null && booked.equals(Boolean.TRUE)){
                            System.out.println("✓ Seat booked successfully! Enjoy your journey!");
                            trainSelectedForBooking = null; // Reset selection
                        }else{
                            System.out.println("✗ Cannot book this seat. It may be already booked or invalid.");
                        }
                    } catch (Exception e) {
                        System.out.println("✗ Invalid input. Please enter valid numbers.");
                        scanner.nextLine(); // Clear the invalid input
                    }
                    break;
                case 6:
                    if (!isLoggedIn) {
                        System.out.println("✗ Please login first (option 2).");
                        break;
                    }
                    System.out.println("\n--- Cancel Booking ---");
                    try {
                        // Show bookings first and get list
                        List<Ticket> userTickets = userBookingService.getUserTickets();
                        if (userTickets == null || userTickets.isEmpty()) {
                            System.out.println("You have no bookings to cancel.");
                            break;
                        }
                        
                        userBookingService.showBookings(loggedInUsername);
                        System.out.print("\nEnter the ticket number (1-" + userTickets.size() + ") or ticket ID to cancel: ");
                        String input = scanner.nextLine().trim();
                        
                        String ticketIdToCancel = null;
                        // Check if input is a number
                        try {
                            int ticketNumber = Integer.parseInt(input);
                            if (ticketNumber >= 1 && ticketNumber <= userTickets.size()) {
                                ticketIdToCancel = userTickets.get(ticketNumber - 1).getTicketId();
                            } else {
                                System.out.println("✗ Invalid ticket number. Please try again.");
                                break;
                            }
                        } catch (NumberFormatException e) {
                            // Not a number, treat as ticket ID
                            ticketIdToCancel = input;
                        }
                        
                        Boolean cancelled = userBookingService.cancelBooking(ticketIdToCancel);
                        if (cancelled != null && cancelled) {
                            System.out.println("✓ Booking cancelled successfully!");
                        }
                    } catch (Exception e) {
                        System.out.println("✗ Error cancelling booking: " + e.getMessage());
                    }
                    break;
                case 7:
                    System.out.println("\nThank you for using Train Booking System. Goodbye!");
                    break;
                default:
                    System.out.println("✗ Invalid option. Please choose a number between 1-7.");
                    break;
            }
        }
        scanner.close();
    }
}
