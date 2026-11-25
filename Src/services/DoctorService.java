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

        //check null value
        if(doctor == null){
            System.out.println("Invalid doctor record");
            return;
        }
        
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
        
        if(saveDoctorsToFile()){

            NotificationService.notifyDoctorAvailability(doctor);

            System.out.println("Doctor registered successfully!");
            System.out.println("Doctor ID: " + doctor.getDoctorId());
        }

        else{
            
            //role back if saving fails
            doctors.remove(doctor);

            System.out.println("Doctor registration failed because data could not be saved.");
        }
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
            if(doctor.getDoctorId().equalsIgnoreCase(doctorId.trim())){
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

        //store previous details for rollback
        String oldName = doctor.getName();
        String oldSpecialization = doctor.getSpecialization();
        Double oldFee = doctor.getConsultationFee();

        //update doctor details
        doctor.setName(name);
        doctor.setSpecialization(specialization);
        doctor.setConsultationFee(consultationFee);

        if(saveDoctorsToFile()){

            System.out.println("Doctor updated successfully!");
            return true;
        }

        else{

            //restore previous details
            doctor.setName(oldName);
            doctor.setSpecialization(oldSpecialization);
            doctor.setConsultationFee(oldFee);
            
            System.out.println("Doctor updated failed because data could not be saved.");
            return false;
        }
    }

    //delete doctor
    public boolean deleteDoctor(String doctorId){

        Doctor doctor = searchDoctorById(doctorId);

        if(doctor == null){
            System.out.println("Doctor not found");
            return false;
        }

        doctors.remove(doctor);
        
        if(saveDoctorsToFile()){

            System.out.println("Doctor deleted successfully!");
            return true;
        }

        else{

            doctors.add(doctor);

            System.out.println("Doctor deletion failed because data could not be saved.");
            return false;
        }
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

        String normalizedSlot = timeSlot.trim();

        String[] slotParts = normalizedSlot.split("\\s+");

        if(slotParts.length != 2 || !InputValidator.isValidDate(slotParts[0]) || !InputValidator.isValidTime(slotParts[1])){

            System.out.println("Invalid time slot Use YYYY-MM-DD HH:mm.");
            return false;
        }

        if(doctor.getAvailableTimeSlots().contains(timeSlot.trim())){

            System.out.println("Time slot already exists.");
            return false;
        }

        doctor.addAvailableTimeSlot(normalizedSlot);
        
        if(saveDoctorsToFile()){

            NotificationService.notifyDoctorAvailability(doctor);
            System.out.println("Time slot added successfully!");
            return true;
        }

        else{

            doctor.removeTimeSlot(normalizedSlot);

            System.out.println("Unable to save the new time slot.");

            return false;
        }
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

        String normalizedSlot = timeSlot.trim();

        String[] slotParts = normalizedSlot.split("\\s+");

        if(slotParts.length !=2 || !InputValidator.isValidDate(slotParts[0]) || !InputValidator.isValidDate(slotParts[1])){

            System.out.println("Invalid time slot. Use YYYY-MM-DD HH:mm.");
            return false;
        }

        if(!doctor.getAvailableTimeSlots().contains(timeSlot.trim())){
            System.out.println("Time slot not found.");
            return false;
        }

        doctor.removeTimeSlot(normalizedSlot);
        
        if(saveDoctorsToFile()){

            System.out.println("Time slot removed successfully!");
            return true;
        }

        else{

            doctor.addAvailableTimeSlot(normalizedSlot);

            System.out.println("Unable to dave the time slot changes.");
            return false;
        }
    }

    //generate unique doctor id
    public String generateDoctorId(){

        int highestId = 0;

        for(Doctor doctor : doctors){

            String id = doctor.getDoctorId();

            if( id != null && id.matches("D\\d+")){
                try{

                    int number = Integer.parseInt(id.substring(1));

                    highestId = Math.max(highestId, number);
                }

                catch(NumberFormatException e){

                    System.out.println("Invalid doctor ID: " + id);
                }
            }
        }

        return String.format("D%03d", highestId+1);
    }

    //check whether a field can safely be stored in the text file
    private boolean isValidFileField(String value){

        return value != null
            && !value.contains("|")
            && !value.contains("\n")
            && !value.contains("\r");
    }

    //save doctor records
    public boolean saveDoctorsToFile(){

        File file = new File(filePath);

        try{

            File parent = file.getParentFile();

            if(parent != null && !parent.exists()){
                if(!parent.mkdirs() && !parent.exists()){

                    System.out.println("Unable to create data folder.");
                    return false;
                }
            }

            try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){

                for(Doctor doctor : doctors){

                    //validate doctor fields
                    if(!isValidFileField(doctor.getDoctorId()) || !isValidFileField(doctor.getName()) || !isValidFileField(doctor.getSpecialization())){

                        System.out.println("Invalid doctor information cannot be saved.");
                        return false;
                    }

                    List<String> slots = doctor.getAvailableTimeSlots();

                    //validate time slots
                    for (String slot : slots){

                        if(!isValidFileField(slot) || slot.contains(";")){

                            System.out.println("Invalid time slot cannot be saved.");
                            return false;
                        }
                    }

                    String slotData = String.join(";", slots);

                    writer.write(doctor.getDoctorId() + "|" + doctor.getName() + "|" + doctor.getSpecialization() + "|" + slotData + "|" + doctor.getConsultationFee());

                    writer.newLine();
                }
            }

            return true;
        }

        catch(IOException e){

                System.out.println("Error occured while saving doctor records: " + e.getMessage());

                return false;
            }
    }

    //validate a loaded dcotor record
    private boolean isValidDoctorRecord(Doctor doctor){

        return doctor != null
            && InputValidator.isNotEmpty(doctor.getDoctorId())
            && InputValidator.isValidName(doctor.getName())
            && InputValidator.isNotEmpty(doctor.getSpecialization())
            && InputValidator.isValidFee(doctor.getConsultationFee());
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

                    Doctor doctor = new Doctor(
                            data[0],
                            data[1],
                            data[2],
                            fee);

                    //validate doctor details
                    if(!isValidDoctorRecord(doctor)){

                        System.out.println("Invalid doctor data skipped.");
                        continue;
                    }

                    //check duplicate doctor id
                    if(searchDoctorById(doctor.getDoctorId()) != null){

                        System.out.println("Duplicate doctor ID skipped.");
                        continue;
                    } 

                    if (!data[3].isEmpty()) {

                        String[] slots = data[3].split(";");

                        for (String slot : slots) {

                            String normalizedSlot = slot.trim();

                            String[] parts = normalizedSlot.split("\\s+");

                            if (parts.length == 2
                                    && InputValidator.isValidDate(parts[0])
                                    && InputValidator.isValidTime(parts[1])) {

                                if(!doctor.getAvailableTimeSlots().contains(normalizedSlot)){

                                    doctor.addAvailableTimeSlot(normalizedSlot);
                                }
                            }

                            else{
                                System.out.println("Invalid doctor time slot skipped: " + normalizedSlot);
                            }
                        }
                    }

                    doctors.add(doctor);

                } catch (IllegalArgumentException e) {

                    System.out.println("Invalid doctor data skipped: " + line);
                }
            }

            System.out.println("Doctor records loaded successfully.");

        } catch (IOException e) {

            System.out.println("Error occurred while loading doctor records: " + e.getMessage());
        }

    }

    //return a copy of all doctors
    public List<Doctor> getAllDoctors(){

        return new ArrayList<>(doctors);
    }
}
