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
import Src.utils.InputValidator;
import Src.utils.NotificationService;

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
        
        //check duplicate doctor id
        if(searchDoctorById(doctor.getDoctorId()) != null){
            System.out.println("Doctor ID is already exists");
            return ;
        }

        //validate doctor id
        if (!InputValidator.isNotEmpty(doctor.getDoctorId())){
            System.out.println("Doctor ID cannot be empty.");
            return ;
        }

        //validate doctor name
        if(!InputValidator.isValidName(doctor.getName())){
            System.out.println("Invalid doctor name");
            return;
        }

        //validate specialization
        if(!InputValidator.isNotEmpty(doctor.getSpecialization())){
            System.out.println("Specialization cannot be empty.");
            return ;
        }

        //validate consultation fee
        if(!InputValidator.isValidFee(doctor.getConsultationFee())){
            System.out.println("Invalid consultation fee.");
            return ;
        }

        doctors.add(doctor);
        saveDoctorsToFile();

        //send doctor availability notification
        NotificationService.notifyDoctorAvailability(doctor);

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

        if(!InputValidator.isNotEmpty(doctorId)){
            return null;
        }

        for(Doctor doctor :doctors){
            if(doctor.getDoctorId().equalsIgnoreCase(doctorId)){
                return doctor;
            }
        }
        return null;
    }

    //search doctor by specialization
    public void searchDoctorBySpecialization(String specialization){

        if(!InputValidator.isNotEmpty(specialization)){
            System.out.println("Specialization cannot be empty.");
        }

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

        //validate doctor name 
        if (!InputValidator.isValidName(name)){
            System.out.println("Invalid doctor name");
            return false;
        }

        //validate specialization 
        if(!InputValidator.isNotEmpty(specialization)){
            System.out.println("Specialization cannot be empty.");
            return false;
        }

        //validate consultation fee
        if(!InputValidator.isValidFee(consultationFee)){
            System.out.println("Invalid consultation fee.");
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
    //expected formate: YYYY-MM-DD HH:mm
    public boolean addTimeSlot(String doctorId, String timeSlot){
        Doctor doctor = searchDoctorById(doctorId);

        if(doctor == null){
            System.out.println("Doctor not found");
            return false;
        }

        if(!InputValidator.isNotEmpty(timeSlot)){
            System.out.println("Time slot cannot be empty.");
            return false;
        }

        String[] slotParts = timeSlot.trim().split("\\s+");

        if(slotParts.length != 2 || !InputValidator.isValidDate(slotParts[0]) || !InputValidator.isValidTime(slotParts[1])){
            System.out.println("Invalid time slot Use YYYY-MM-DD HH:mm.");
            return false;
        }

        if(doctor.getAvailableTimeSlots().contains(timeSlot.trim())){
            System.out.println("Time slot already exists.");
            return false;
        }

        doctor.addAvailableTimeSlot(timeSlot.trim());
        saveDoctorsToFile();

        //notify about updated doctor availability
        NotificationService.notifyDoctorAvailability(doctor);

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

        if(!InputValidator.isNotEmpty(timeSlot)){
            System.out.println("Time slot cannot be empty.");
            return false;
        }

        String[] slotParts = timeSlot.trim().split("\\s+");

        if(slotParts.length !=2 || !InputValidator.isValidDate(slotParts[0]) || !InputValidator.isValidDate(slotParts[1])){
            System.out.println("Invalid time slot. Use YYYY-MM-DD HH:mm.");
            return false;
        }

        if(!doctor.getAvailableTimeSlots().contains(timeSlot.trim())){
            System.out.println("Time slot not found.");
            return false;
        }

        doctor.removeTimeSlot(timeSlot.trim());
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

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split("\\|", -1);

                if (data.length != 5) {
                    System.out.println("Invalid doctor record skipped.");
                    continue;
                }

                try {

                    double fee = Double.parseDouble(data[4]);

                    if (!InputValidator.isNotEmpty(data[0])
                            || !InputValidator.isValidName(data[1])
                            || !InputValidator.isNotEmpty(data[2])
                            || !InputValidator.isValidFee(fee)) {

                        System.out.println("Invalid doctor data skipped.");
                        continue;
                    }

                    Doctor doctor = new Doctor(
                            data[0],
                            data[1],
                            data[2],
                            fee);

                    if (!data[3].isEmpty()) {

                        String[] slots = data[3].split(";");

                        for (String slot : slots) {

                            String[] parts = slot.trim().split("\\s+");

                            if (parts.length == 2
                                    && InputValidator.isValidDate(parts[0])
                                    && InputValidator.isValidTime(parts[1])) {

                                doctor.addAvailableTimeSlot(slot.trim());
                            }
                        }
                    }

                    if (searchDoctorById(doctor.getDoctorId()) == null) {
                        doctors.add(doctor);
                    }

                } catch (IllegalArgumentException e) {

                    System.out.println("Invalid doctor data skipped.");
                }
            }

        } catch (IOException e) {

            System.out.println("Error occurred while loading doctor records: " + e.getMessage());
        }

    }

    //return a copy of all doctors
    public List<Doctor> getAllDoctors(){
        return new ArrayList<>(doctors);
    }
}
