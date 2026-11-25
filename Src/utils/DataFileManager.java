package Src.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DataFileManager {
    
    private static final String DATA_FOLDER = "Data";
    
    private static final String[] DATA_FILES = {
        "patients.txt", "doctors.txt", "appointments.txt", "reschedule_requests.txt", "home_visits.txt"
    };

    private DataFileManager(){
        //pervent from object creation
    }

    public static void initializeDataFiles(){

        try{
            //create the data folder
            Path folderPath = Paths.get(DATA_FOLDER);

            if(!Files.exists(folderPath)){

                Files.createDirectories(folderPath);

                System.out.println("Data folder created successfully.");
            }

            //create missing data files
            for(String fileName : DATA_FILES){
                
                Path filePath = folderPath.resolve(fileName);

                if(!Files.exists(filePath)){

                    Files.createFile(filePath);
                    System.out.println(fileName + "created successfully.");
                }
            }

            System.out.println("Data storage initialized successfully.");
        }

        catch(IOException e){

            System.out.println("Error initializing data storage: " + e.getMessage());
        }
    }
}
