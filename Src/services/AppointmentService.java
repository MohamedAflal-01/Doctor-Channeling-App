package Src.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
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
    public String generateAppoinmentId(){
        
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
    public void bookAppoinment(String patientId, String doctorID, LocalDate date, LocalTime time){

        Patient patient = patientService.searchPatientById(patientId);

        Doctor doctor = doctorService.searchDoctorById(doctorID);

        if(patient == null){
            System.out.println("Patient not found");
            return;
        }

        if (doctor == null){
            System.out.println("Doctor not found");
            return ;
        }

        String timeSlot = date + " " + time;

        int queueNumber = 0;

        Appoinment.AppoinmentStatus status;

        if(checkSlotAvailability(doctorID, date, time)){
            status = Appoinment.AppoinmentStatus.CONFIRMED;

            doctor.removeTimeSlot(timeSlot);
            doctorService.saveDoctorsToFile();

            System.out.println("Appoinment confirmed!");
        }

        else{
            status = Appoinment.AppoinmentStatus.WAITING;

            queueNumber = getNextQueueNumber(doctorID, date, time);

            System.out.println("Slot unavailable. so Please try again Later!");

            System.out.println("Patient added to waiting list.");

            System.out.println("Queue Number: " + queueNumber);
        }

        Appoinment appoinment = new Appoinment(
            generateAppoinmentId(), 
            patientId,
            doctorID, 
            date, 
            time, 
            queueNumber);

        appoinment.setStatus(status);

        appoinments.add(appoinment);

        saveAppointmentsToFile();
    }

    //generating waiting queue number
    private int getNextQueueNumber(String doctorId, LocalDate date, LocalTime time){

        int highestNumber = 0;

        for(Appoinment appoinment : appoinments){
            if(appoinment.getdoctorId().equals(doctorId) 
                && appoinment.getAppoinmentDate().equals(date) 
                && appoinment.getAppoinmentTime().equals(time) 
                && appoinment.getStatus() == Appoinment.AppoinmentStatus.WAITING){

                highestNumber = Math.max(highestNumber, appoinment.getQueueNumber());
            }
        }

        return highestNumber +1;
    }

    //search appoinment by id
    public Appoinment searchAppoinmentById(String appoinmentId){
        for(Appoinment appoinment : appoinments){
            if(appoinment.getAppoinmentId().equalsIgnoreCase(appoinmentId)){

                return appoinment;
            }
        }

        return null;
    }

    //view all appoinment
    public void viewAllAppoinment(){

        if(appoinments.isEmpty()){
            System.out.println("No appoinment found yet.");
            return;
        }

        for(Appoinment appoinment : appoinments){
            appoinment.displayAppoinmentDetails();
        }
    }

    //view patient appoinments
    public void viewPatientAppoinments(String patientId){

        boolean found = false;

        for(Appoinment appoinment : appoinments){

            if(appoinment.getPatientId().equalsIgnoreCase(patientId)){

                appoinment.displayAppoinmentDetails();
                found = true;
            }
        }

        if(!found){
            System.out.println("No appoinments found for this patient.");
        }
    }

    //view doctor appoinment
    public void viewDoctorAppoinments(String doctorId){

        boolean found = false;

        for(Appoinment appoinment : appoinments){

            if(appoinment.getdoctorId().equalsIgnoreCase(doctorId)){

                appoinment.displayAppoinmentDetails();
                found = true;
            }
        }

        if(!found){
            System.out.println("No appoinment found for this doctor.");
        }
    }

    //cancel appoinment
    public boolean cancelAppoinment(String appoinmentId){

        Appoinment appoinment = searchAppoinmentById(appoinmentId);

        if(appoinment==null){
            System.out.println("Appoinment not found.");
            return false;
        }

        if(appoinment.getStatus() != Appoinment.AppoinmentStatus.CONFIRMED){

            System.out.println("Only conformed appoinments can be cancelled.");
            return false;
        }

        appoinment.setStatus(Appoinment.AppoinmentStatus.CANCELLED);

        Doctor doctor = doctorService.searchDoctorById(appoinment.getdoctorId());

        String timeSlot = appoinment.getAppoinmentDate() + " " + appoinment.getAppoinmentTime();

        if(doctor != null){

            //return released slot to doctor
            doctor.addAvailableTimeSlot(timeSlot);

            //assign slot to next waiting patient
            assignNextWaitingPatient(appoinment.getdoctorId(), appoinment.getAppoinmentDate(), appoinment.getAppoinmentTime());

            doctorService.saveDoctorsToFile();
        }

        saveAppointmentsToFile();

        System.out.println("Appoinment cancelled successfully!");

        return true;
    }

    //assign released slot to next waiting patient
    public void assignNextWaitingPatient(String doctorId, LocalDate date, LocalTime time){

        Appoinment nextPatient = null;

        for(Appoinment appoinment : appoinments){

            if(appoinment.getdoctorId().equals(doctorId) 
                && appoinment.getAppoinmentDate().equals(date) 
                && appoinment.getAppoinmentTime().equals(time) 
                && appoinment.getStatus() == Appoinment.AppoinmentStatus.WAITING){
                
                if(nextPatient == null || appoinment.getQueueNumber() < nextPatient.getQueueNumber()){

                    nextPatient = appoinment;
                }
            }
        }

        if (nextPatient != null){
            nextPatient.setStatus(Appoinment.AppoinmentStatus.CONFIRMED);

            nextPatient.setQueueNumber(0);

            Doctor doctor = doctorService.searchDoctorById(doctorId);

            if(doctor != null){

                String timeSlot = date + " " + time;

                doctor.removeTimeSlot(timeSlot);
            }

            System.out.println("Waiting patient promoted successfully!");

            System.out.println("Patient ID: " + nextPatient.getPatientId());
        }
    }

    //reschedule appoinment
    public boolean rescheduleAppoinment(String appoinmentId, LocalDate newDate, LocalTime newTime){

        Appoinment appoinment = searchAppoinmentById(appoinmentId);

        if(appoinment == null){
            System.out.println("Appoinment not found.");
            return false;
        }

        if(appoinment.getStatus() != Appoinment.AppoinmentStatus.CONFIRMED){

            System.out.println("Only conformed appoinments can be rescheduled.");
            return false;
        }

        if(!checkSlotAvailability(appoinment.getdoctorId(), newDate, newTime)){
            
            System.out.println("Requested slot is unavailable.");
            return false;
        }

        Doctor doctor = doctorService.searchDoctorById(appoinment.getdoctorId());

        String oldSlot = appoinment.getAppoinmentDate() + " " + appoinment.getAppoinmentTime();

        String newSlot = newDate + " " + newTime;

        doctor.addAvailableTimeSlot(oldSlot);
        doctor.removeTimeSlot(newSlot);

        appoinment.reschedule(newDate, newTime);

        doctorService.saveDoctorsToFile();
        saveAppointmentsToFile();

        System.out.println("Appoinment rescheduled successfully!");

        return true;
    }
    
    //Save appoinments to file
    public void saveAppointmentsToFile(){
        File file = new File(filePath);

        try{
            File parent = file.getParentFile();

            if(parent != null){
                parent.mkdirs();
            }

            try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){

                for(Appoinment appoinment : appoinments){

                    writer.write(appoinment.getAppoinmentId() + "|" 
                    + appoinment.getPatientId() + "|" 
                    + appoinment.getdoctorId() + "|" 
                    + appoinment.getAppoinmentDate() + "|" 
                    + appoinment.getAppoinmentTime() + "|" 
                    + appoinment.getQueueNumber() + "|" 
                    + appoinment.getStatus());

                    writer.newLine();
                }
            }
        }

        catch(IOException e){
            System.out.println("Error occured: " + e.getMessage());
        }
    }

    //Load appoinments from file
    public void loadAppoinmentsFromFile(){
        File file = new File(filePath);

        if(!file.exists()){
            return;
        }

        try(BufferedReader reader = new BufferedReader(new FileReader(file))){

            String line;

            while((line = reader.readLine()) != null){

                String[] data = line.split("\\|", -1);

                if(data.length !=7){
                    System.out.println("Invalid appoinment record skipped.");
                    continue;
                }

                try{
                    Appoinment appoinment = new Appoinment(
                        data[0],
                        data[1],
                        data[2],
                        LocalDate.parse(data[3]),
                        LocalTime.parse(data[4]),
                        Integer.parseInt(data[5])
                    );

                    appoinment.setStatus(Appoinment.AppoinmentStatus.valueOf(data[6]));

                    if(searchAppoinmentById(appoinment.getAppoinmentId()) == null){

                        appoinments.add(appoinment);
                    }
                }

                catch(IllegalArgumentException e){
                    System.out.println("Invalid appoinment data skipped.");
                }
            }
        }

        catch(IOException e){
            System.out.println("Error loading appoinments: " + e.getMessage());
        }

    }
}
