package Src.services;

import java.util.ArrayList;

import Src.models.Appoinment;
import Src.models.Doctor;
import Src.models.Patient;

public class AppointmentService {

    //define the private attributes
    private ArrayList<Appoinment> appoinments;
    private final String filePath = "Data/appointments.txt";

    private PatientService patientService;
    private DoctorService doctorService;

    //set constructor for the attributes
    public AppointmentService(PatientService patientService, DoctorService doctorService){
        appoinments = new ArrayList<>();

        this.patientService = patientService;
        this.doctorService = doctorService;

        loadAppoinmentsFromFile();
    }

    //check slot availability
    public boolean checkSlotAvailability(String doctorId, LocalDate date, LocalTime time){
        
        Doctor doctor = doctorService.searchDoctorById(doctorId);

        if(doctor == null){
            return false;
        }

        String timeSlot = date + " " + time;

        return doctor.getAvailableTimeSlots().contains(timeSlot);
    }

    //generate appoinment id
    public string generateAppoinmentId(){
        
        int highestId = 0;

        for(Appoinment appoinment : appoinments){
            String id = appoinment.getAppoinmentId();

            if (id.matches("A\\d+")){
                int number = Integer.parseInt(id.substring(1));
                highestId = Math.max(highestId, number);
            }
        }

        return String.format("A%03d", highestId + 1);
    }

    //book appoinment
    
    
}
