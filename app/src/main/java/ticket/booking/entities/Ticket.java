package ticket.booking.entities;

import java.util.Date;
import java.util.List;

public class Ticket {

    private String ticketId;
    private String userId;
    private String source;
    private String destination;
    private Date dateOfTravel;

    private Train train;

    public Ticket(String ticketId, String source, String destination, String userId, Date dateOfTravel, Train train){
        this.ticketId = ticketId;
        this.source = source;
        this.destination = destination;
        this.userId = userId;
        this.dateOfTravel = dateOfTravel;
        this.train = train;
    }

    public String getTicketInfo(){
        return String.format("Ticket ID: %s belongs to user: %s from: %s to: %s on: %s", ticketId, userId, source, destination, dateOfTravel);
    }

    public Ticket(){}

    public String getTicketId(){
        return ticketId;
    }
    public String getSource(){
        return source;
    }
    public String getDestination(){
        return destination;
    }
    public String getUserId(){
        return userId;
    }
    public Date getDateOfTravel(){
        return dateOfTravel;
    }
    public Train getTrain(){
        return train;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public void setDateOfTravel(Date dateOfTravel) {
        this.dateOfTravel = dateOfTravel;
    }
}
