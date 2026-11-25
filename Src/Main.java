package Src;

import Src.services.DoctorService;
import Src.services.PatientService;
import Src.utils.DataFileManager;

public class Main {
    
    public static void main(String[] args) {
        
        //initialize data folder and files
        DataFileManager.initializeDataFiles();

        //create service object after initialization
        PatientService patientService = new PatientService();

        DoctorService doctorService = new DoctorService();

    }
}
