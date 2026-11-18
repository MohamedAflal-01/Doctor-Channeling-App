package Src.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Src.models.Doctor;

public class DoctorService {
    
    //define the attributes in private
    private ArrayList<Doctor>doctors;
    private final String filePath = "Data/doctors.txt";

    //set constructor for above attributes
    public DoctorService(){
        doctors = new ArrayList<>();
        loadDoctorsFromFile();
    }

    //register doctor
    public void registerDoctor(Doctor doctor){
        if(searchDoctorById(doctor.getDoctorId()) != null){
            System.out.println("Doctor ID is already exists");
            return ;
        }

        doctors.add(doctor);
        saveDoctorsToFile();

        System.out.println("Doctor registered successfully!");
        System.out.println("Doctor ID: " + doctor.getDoctorId());
    }

    //display all doctors
    public void viewAllDoctors(){
        if(doctors.isEmpty()){
            System.out.println("No doctors record found. Please try again.");
            return ;
        }

        for(Doctor doctor : doctors){
            doctor.displayDoctorDetails();
        }
    }

    //search doctor by id
    public Doctor searchDoctorById(String doctorId){
        for(Doctor doctor :doctors){
            if(doctor.getDoctorId().equalsIgnoreCase(doctorId)){
                return doctor;
            }
        }
        return null;
    }

    //search doctor by specialization
    public void searchDoctorBySpecialization(String specialization){
        boolean found = false;

        for(Doctor doctor : doctors){
            if(doctor.getSpecialization().equalsIgnoreCase(specialization)){
                doctor.displayDoctorDetails();
                found = true;
            }
        }

        if (!found){
            System.out.println("No doctors found for this specialization");
        }
    }

    //update doctor details
    public boolean updateDoctor(String doctorId, String name, String specialization, double consultationFee){
        Doctor doctor = searchDoctorById(doctorId);

        if(doctor == null){
            System.out.println("Doctor not found");
            return false;
        }

        doctor.setName(name);
        doctor.setSpecialization(specialization);
        doctor.setConsultationFee(consultationFee);

        saveDoctorsToFile();

        System.out.println("Doctor updated successfully!");
        return true;
    }

    //delete doctor
    public boolean deleteDoctor(String doctorId){
        Doctor doctor = searchDoctorById(doctorId);

        if(doctor == null){
            System.out.println("Doctor not found");
            return false;
        }

        doctors.remove(doctor);
        saveDoctorsToFile();

        System.out.println("Doctor deleted successfully!");
        return true;
    }

    //add available time slot
    public boolean addTimeSlot(String doctorId, String timeSlot){
        Doctor doctor = searchDoctorById(doctorId);

        if(doctor == null){
            System.out.println("Doctor not found");
            return false;
        }

        if(doctor.getAvailableTimeSlots().contains(timeSlot)){
            System.out.println("Time slot already exists.");
            return false;
        }

        doctor.addAvailableTimeSlot(timeSlot);
        saveDoctorsToFile();

        System.out.println("Time slot added successfully!");
        return  true;
    }

    //remove available time slot
    public boolean removeTimeSlot(String doctorId, String timeSlot){
        Doctor doctor = searchDoctorById(doctorId);

        if(doctor == null){
            System.out.println("Doctor not found.");
            return false;
        }

        if(!doctor.getAvailableTimeSlots().contains(timeSlot)){
            System.out.println("Time slot not found.");
            return false;
        }

        doctor.removeTimeSlot(timeSlot);
        saveDoctorsToFile();

        System.out.println("Time slot removed successfully!");
        return true;
    }

    //generate unique doctor id
    public String generateDoctorId(){
        int highestId = 0;

        for(Doctor doctor : doctors){
            String id = doctor.getDoctorId();

            if(id.matches("D\\d+")){
                int number = Integer.parseInt(id.substring(1));
                highestId = Math.max(highestId, number);
            }
        }

        return String.format("D%03d", highestId+1);
    }

    //save doctor records
    public void saveDoctorsToFile(){
        File file = new File(filePath);

        try{
            File parent = file.getParentFile();

            if(parent != null){
                parent.mkdirs();
            }

            try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
                for(Doctor doctor : doctors){
                    String slots = String.join(";", doctor.getAvailableTimeSlots());

                    writer.write(doctor.getDoctorId() + "|" + doctor.getName() + "|" + doctor.getSpecialization() + "|" + slots + "|" + doctor.getConsultationFee());

                    writer.newLine();
                }
            }
        }

        catch(IOException e){
                System.out.println("Error occured conduct the admin: " + e.getMessage());
            }
    }

    //load doctor records
    public void loadDoctorsFromFile(){

        File file = new File(filePath);

        if(!file.exists()){
            return;
        }

        try(BufferedReader reader = new BufferedReader(new FileReader(file))){
            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|", -1);

                if(data.length !=5){
                    System.out.println("Invalid doctor record skipped.");
                    continue;
                }

                try{
                    List<String> slots = new ArrayList<>();

                    if(!data[3].isEmpty()){
                        slots = new ArrayList<>(List.of(data[3].split(";"
                        )));
                    }

                    Doctor doctor = new Doctor(
                        data[0], 
                        data[1], 
                        data[2], 
                        Double.parseDouble(data[4]));

                    for(String slot : slots){
                        doctor.addAvailableTimeSlot(slot);
                    }

                    if(searchDoctorById(doctor.getDoctorId()) == null){
                        doctors.add(doctor);
                    }
                }

                catch(IllegalArgumentException e){
                    System.out.println("Invalid doctor data skipped.");
                }
            }
        }

        catch(IOException e){
            System.out.println("Error occured. please coonduct the admin: " + e.getMessage());
        }
    }

    //return a copy of all doctors
    public List<Doctor> getAllDoctors(){
        return new ArrayList<>(doctors);
    }
}
