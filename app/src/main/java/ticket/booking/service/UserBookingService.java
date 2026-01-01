package ticket.booking.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ticket.booking.entities.Ticket;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.util.UserServiceUtil;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.UUID;

public class UserBookingService{
    
    private ObjectMapper objectMapper = new ObjectMapper();

    private List<User> userList;

    private User user;

    private final String USER_FILE_PATH = "app/src/main/java/ticket/booking/localDB/users.json";

    public UserBookingService(User user) throws IOException {
        this.user = user;
        loadUserListFromFile();
    }

    public UserBookingService() throws IOException {
        loadUserListFromFile();
    }

    private void loadUserListFromFile() throws IOException {
        File userFile = getFile(USER_FILE_PATH);
        if (!userFile.exists()) {
            // If file doesn't exist, create an empty list
            userList = new ArrayList<>();
            return;
        }
        if (userFile.length() == 0) {
            // If file is empty, create an empty list
            userList = new ArrayList<>();
            return;
        }
        userList = objectMapper.readValue(userFile, new TypeReference<List<User>>() {});
    }
    
    private File getFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            return file;
        }
        // Try alternative path from project root
        String altPath = System.getProperty("user.dir") + "/" + path;
        File altFile = new File(altPath);
        if (altFile.exists()) {
            return altFile;
        }
        // Return the original file path - will be created if needed
        return file;
    }

    public Boolean loginUser(){
        if (user == null || user.getName() == null || user.getPassword() == null) {
            return Boolean.FALSE;
        }
        
        try {
            Optional<User> foundUser = userList.stream().filter(user1 -> {
                if (user1 == null || user1.getName() == null || user1.getHashedPassword() == null) {
                    return false;
                }
                // First check if username matches
                if (!user1.getName().equals(user.getName())) {
                    return false;
                }
                // Then safely check password
                try {
                    return UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
                } catch (Exception e) {
                    // If password check fails (invalid hash, etc.), return false
                    return false;
                }
            }).findFirst();
            return foundUser.isPresent();
        } catch (Exception e) {
            // Catch any unexpected exceptions during login
            return Boolean.FALSE;
        }
    }

    public Boolean signUp(User user1){
        if (user1 == null || user1.getName() == null || user1.getName().trim().isEmpty()) {
            System.out.println("✗ Invalid user data. Username cannot be empty.");
            return Boolean.FALSE;
        }
        
        // Check if user already exists
        boolean userExists = userList.stream()
            .anyMatch(u -> u.getName() != null && u.getName().equalsIgnoreCase(user1.getName()));
        
        if (userExists) {
            System.out.println("✗ Username already exists. Please choose a different username.");
            return Boolean.FALSE;
        }
        
        try{
            userList.add(user1);
            saveUserListToFile();
            return Boolean.TRUE;
        }catch (IOException ex){
            System.out.println("✗ Error saving user data: " + ex.getMessage());
            return Boolean.FALSE;
        }
    }

    private void saveUserListToFile() throws IOException {
        File usersFile = getFile(USER_FILE_PATH);
        // Ensure parent directories exist
        usersFile.getParentFile().mkdirs();
        objectMapper.writeValue(usersFile, userList);
    }

    public void fetchBookings(){
        if (user == null) {
            System.out.println("Please login first to view your bookings.");
            return;
        }
        Optional<User> userFetched = userList.stream().filter(user1 -> {
            if (user1 == null || user1.getName() == null || user1.getHashedPassword() == null || 
                user == null || user.getName() == null || user.getPassword() == null) {
                return false;
            }
            if (!user1.getName().equals(user.getName())) {
                return false;
            }
            try {
                return UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
            } catch (Exception e) {
                return false;
            }
        }).findFirst();
        if(userFetched.isPresent()){
            User loggedInUser = userFetched.get();
            List<Ticket> tickets = loggedInUser.getTicketsBooked();
            if (tickets == null || tickets.isEmpty()) {
                System.out.println("You have no bookings yet.");
            } else {
                System.out.println("You have " + tickets.size() + " booking(s):");
                System.out.println("----------------------------------------");
                for (int i = 0; i < tickets.size(); i++) {
                    System.out.println((i + 1) + ". " + tickets.get(i).getTicketInfo());
                }
            }
        } else {
            System.out.println("No bookings found.");
        }
    }
    
    public User getCurrentUser() {
        if (user == null || user.getName() == null || user.getPassword() == null) {
            return null;
        }
        try {
            return userList.stream().filter(user1 -> {
                if (user1 == null || user1.getName() == null || user1.getHashedPassword() == null) {
                    return false;
                }
                if (!user1.getName().equals(user.getName())) {
                    return false;
                }
                try {
                    return UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
                } catch (Exception e) {
                    return false;
                }
            }).findFirst().orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    public void showBookings() {
        if (user == null) {
            System.out.println("Please login first.");
            return;
        }
        
        User loggedInUser = getCurrentUser();
        if (loggedInUser == null) {
            System.out.println("User not found. Please login again.");
            return;
        }
        
        List<Ticket> tickets = loggedInUser.getTicketsBooked();
        if (tickets == null || tickets.isEmpty()) {
            System.out.println("You have no bookings to cancel.");
            return;
        }
        
        // Show user's tickets
        System.out.println("Your bookings:");
        System.out.println("----------------------------------------");
        for (int i = 0; i < tickets.size(); i++) {
            Ticket ticket = tickets.get(i);
            System.out.println((i + 1) + ". " + ticket.getTicketInfo());
        }
    }
    
    public Boolean cancelBooking(String ticketId){
        if (user == null) {
            System.out.println("Please login first.");
            return Boolean.FALSE;
        }
        
        User loggedInUser = getCurrentUser();
        if (loggedInUser == null) {
            System.out.println("User not found. Please login again.");
            return Boolean.FALSE;
        }
        
        List<Ticket> tickets = loggedInUser.getTicketsBooked();
        if (tickets == null || tickets.isEmpty()) {
            System.out.println("You have no bookings to cancel.");
            return Boolean.FALSE;
        }

        if (ticketId == null || ticketId.isEmpty()) {
            System.out.println("✗ Ticket ID cannot be empty.");
            return Boolean.FALSE;
        }

        String finalTicketId = ticketId;
        boolean removed = tickets.removeIf(ticket -> ticket.getTicketId() != null && ticket.getTicketId().equals(finalTicketId));
        
        if (removed) {
            loggedInUser.setTicketsBooked(tickets);
            // Update user in the list
            for (int i = 0; i < userList.size(); i++) {
                if (userList.get(i).getUserId().equals(loggedInUser.getUserId())) {
                    userList.set(i, loggedInUser);
                    break;
                }
            }
            try {
                saveUserListToFile();
                System.out.println("✓ Ticket with ID " + ticketId + " has been cancelled successfully.");
                return Boolean.TRUE;
            } catch (IOException e) {
                System.out.println("✗ Error saving cancellation. Please try again.");
                return Boolean.FALSE;
            }
        } else {
            System.out.println("✗ No ticket found with ID: " + ticketId);
            return Boolean.FALSE;
        }
    }
        

    public List<Train> getTrains(String source, String destination){
        try{
            TrainService trainService = new TrainService();
            return trainService.searchTrains(source, destination);
        }catch(IOException ex){
            return new ArrayList<>();
        }
    }
    
    public List<Train> getTrainsBothDirections(String source, String destination){
        try{
            TrainService trainService = new TrainService();
            return trainService.searchTrainsBothDirections(source, destination);
        }catch(IOException ex){
            return new ArrayList<>();
        }
    }
    
    public int getTrainDirection(Train train, String source, String destination) {
        try{
            TrainService trainService = new TrainService();
            return trainService.getTrainDirection(train, source, destination);
        }catch(IOException ex){
            return 0;
        }
    }

    public List<List<Integer>> fetchSeats(Train train){
            return train.getSeats();
    }

    public Boolean bookTrainSeat(Train train, int row, int seat, String source, String destination, User loggedInUser) {
        if (loggedInUser == null) {
            System.out.println("✗ Please login first.");
            return Boolean.FALSE;
        }
        
        try{
            TrainService trainService = new TrainService();
            List<List<Integer>> seats = train.getSeats();
            if (row < 0 || row >= seats.size() || seat < 0 || seat >= seats.get(row).size()) {
                System.out.println("✗ Invalid seat selection. Row: " + (row + 1) + ", Column: " + (seat + 1));
                return Boolean.FALSE;
            }
            
            if (seats.get(row).get(seat) == 0) {
                // Book the seat
                seats.get(row).set(seat, 1);
                train.setSeats(seats);
                trainService.addTrain(train);
                
                // Create a ticket
                String ticketId = UUID.randomUUID().toString();
                String dateOfTravel = java.time.LocalDateTime.now().plusDays(1).format(java.time.format.DateTimeFormatter.ISO_DATE_TIME);
                
                Ticket newTicket = new Ticket(ticketId, loggedInUser.getUserId(), source, destination, dateOfTravel, train);
                
                // Add ticket to user's bookings
                List<Ticket> userTickets = loggedInUser.getTicketsBooked();
                if (userTickets == null) {
                    userTickets = new ArrayList<>();
                }
                userTickets.add(newTicket);
                loggedInUser.setTicketsBooked(userTickets);
                
                // Update user in the list
                for (int i = 0; i < userList.size(); i++) {
                    if (userList.get(i).getUserId().equals(loggedInUser.getUserId())) {
                        userList.set(i, loggedInUser);
                        break;
                    }
                }
                
                // Save updated user list
                saveUserListToFile();
                
                System.out.println("Ticket ID: " + ticketId);
                return Boolean.TRUE;
            } else {
                System.out.println("✗ Seat is already booked.");
                return Boolean.FALSE;
            }
        }catch (IOException ex){
            System.out.println("✗ Error processing booking: " + ex.getMessage());
            return Boolean.FALSE;
        }
    }
}
