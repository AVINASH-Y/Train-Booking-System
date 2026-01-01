package ticket.booking.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.Train;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrainService {

    private List<Train> trainList;
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final String TRAIN_DB_PATH = "app/src/main/java/ticket/booking/localDB/trains.json";

    public TrainService() throws IOException {
        File trains = getFile(TRAIN_DB_PATH);
        if (!trains.exists()) {
            throw new IOException("Cannot find trains.json file. Tried: " + trains.getAbsolutePath());
        }
        if (trains.length() == 0) {
            trainList = new ArrayList<>();
            return;
        }
        trainList = objectMapper.readValue(trains, new TypeReference<List<Train>>() {});
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
        // Return the original file path
        return file;
    }

    public List<Train> searchTrains(String source, String destination) {
        return trainList.stream().filter(train -> validTrain(train, source, destination)).collect(Collectors.toList());
    }
    
    /**
     * Search trains in both directions (forward and reverse)
     * Returns a list of trains that can travel from source to destination in either direction
     */
    public List<Train> searchTrainsBothDirections(String source, String destination) {
        List<Train> forwardTrains = searchTrains(source, destination);
        List<Train> reverseTrains = searchTrains(destination, source);
        
        // Combine both lists, avoiding duplicates based on trainId
        List<Train> allTrains = new ArrayList<>(forwardTrains);
        for (Train train : reverseTrains) {
            boolean exists = allTrains.stream()
                .anyMatch(t -> t.getTrainId() != null && t.getTrainId().equals(train.getTrainId()));
            if (!exists) {
                allTrains.add(train);
            }
        }
        return allTrains;
    }
    
    /**
     * Check if a train can travel from source to destination and return the direction
     * @return 1 for forward direction, -1 for reverse direction, 0 if not valid
     */
    public int getTrainDirection(Train train, String source, String destination) {
        if (train == null || train.getStations() == null) {
            return 0;
        }
        List<String> stationOrder = train.getStations();
        int sourceIndex = stationOrder.indexOf(source.toLowerCase());
        int destinationIndex = stationOrder.indexOf(destination.toLowerCase());
        
        if (sourceIndex == -1 || destinationIndex == -1) {
            return 0;
        }
        
        if (sourceIndex < destinationIndex) {
            return 1; // Forward direction
        } else if (sourceIndex > destinationIndex) {
            return -1; // Reverse direction
        }
        return 0; // Same station
    }

    public void addTrain(Train newTrain) {
        // Check if a train with the same trainId already exists
        Optional<Train> existingTrain = trainList.stream()
                .filter(train -> train.getTrainId().equalsIgnoreCase(newTrain.getTrainId()))
                .findFirst();

        if (existingTrain.isPresent()) {
            // If a train with the same trainId exists, update it instead of adding a new one
            updateTrain(newTrain);
        } else {
            // Otherwise, add the new train to the list
            trainList.add(newTrain);
            saveTrainListToFile();
        }
    }

    public void updateTrain(Train updatedTrain) {
        // Find the index of the train with the same trainId
        OptionalInt index = IntStream.range(0, trainList.size())
                .filter(i -> trainList.get(i).getTrainId().equalsIgnoreCase(updatedTrain.getTrainId()))
                .findFirst();

        if (index.isPresent()) {
            // If found, replace the existing train with the updated one
            trainList.set(index.getAsInt(), updatedTrain);
            saveTrainListToFile();
        } else {
            // If not found, treat it as adding a new train
            addTrain(updatedTrain);
        }
    }

    private void saveTrainListToFile() {
        try {
            File trainFile = getFile(TRAIN_DB_PATH);
            // Ensure parent directories exist
            trainFile.getParentFile().mkdirs();
            objectMapper.writeValue(trainFile, trainList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean validTrain(Train train, String source, String destination) {
        List<String> stationOrder = train.getStations();

        int sourceIndex = stationOrder.indexOf(source.toLowerCase());
        int destinationIndex = stationOrder.indexOf(destination.toLowerCase());

        return sourceIndex != -1 && destinationIndex != -1 && sourceIndex < destinationIndex;
    }
}
