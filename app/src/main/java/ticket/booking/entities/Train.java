package ticket.booking.entities;

import java.sql.Time;
import java.util.List;
import java.util.Map;

public class Train {

    private String trainId;
    private String trainNo;

    private List<List<Integer>> seats;

    private Map<String, String> stationTimes;

    private List<String> stations;

    public Train(){}

    public Train(String trainId, String trainNo, List<List<Integer>> seats, List<String> stations, Map<String, String> stationTimes){
        this.trainId = trainId;
        this.trainNo = trainNo;
        this.seats = seats;
        this.stations = stations;
        this.stationTimes = stationTimes;
    }

    public String getTrainId(){
        return trainId;
    }
    public String getTrainNo(){
        return trainNo;
    }
    public List<List<Integer>> getSeats(){
        return seats;
    }
    public List<String> getStations(){
        return stations;
    }
    public Map<String, String> getStationTimes(){
        return stationTimes;
    }

    public void setTrainId(String trainId){
        this.trainId = trainId;
    }
    public String settrainNo(){
        return trainNo;
    }
    public List<String> setstations() {
        return stations;
    }
    public void setSeats(List<List<Integer>> seats) {
        this.seats = seats;
    }
    public void setStationTimes(Map<String, String> stationTimes) {
        this.stationTimes = stationTimes;
    }

    public String getTrainInfo(){
        return String.format("Train ID: %s Train NO: %s", trainId, trainNo);
    }
}
