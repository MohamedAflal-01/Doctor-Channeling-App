package Src.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Src.utils.InputValidator;
import Src.utils.NotificationService;
import Src.models.Patient;

public class PatientService {
    
    //set the attributes for the patientService entity
    private ArrayList<Patient> patients;
    private final String filePath = "Data/patients.txt";

    //call the value inside the constructor
    public PatientService(){

        patients = new ArrayList<>();

        loadPatientsFromFile();
    }

    //register a new patient
    public void registerPatient(Patient patient){

        //check null patient
        if (patient == null){
            System.out.println("Invalid patient record.");
            return;
        }

        //check duplicate patient id
        if(searchPatientById(patient.getPatientId()) !=null){
            System.out.println("Patient ID is already exist.");
            return;
        }

        //validate patient id
        if(!InputValidator.isNotEmpty(patient.getPatientId())){
            System.out.println("Patient ID connot be empty");
            return ;
        }

        //validate patient name
        if(!InputValidator.isValidName(patient.getName())){
            System.out.println("Invalid patient name");
            return ;
        }

        //validate mobile number
        if(!InputValidator.isValidMobileNumber(patient.getMobileNumber())){
            System.out.println("Invalid mobile number. Enter in 10 digits.");
            return ;
        }

        //validate email
        if(!InputValidator.isValidEmail(patient.getEmail())){
            System.out.println("Invalid email address.");
            return ;
        }

        //validate city
        if (!InputValidator.isNotEmpty(patient.getCity())){
            System.out.println("City cannot be empty.. so please fill that.");
            return ;
        }

        //validate age
        if (!InputValidator.isValidAge(patient.getAge())){
            System.out.println("Invalid age. Age must be between 1 to 120.");
            return ;
        }

        //validate medical history
        if(!InputValidator.isNotEmpty(patient.getMedicalHistory())){
            System.out.println("Medical history cannot be empty.");
            return ;
        }

        //add patient after successfull validation
        patients.add(patient);
        
        //save patient records
        if(savePatientsToFile()){

            //send registration notification
            NotificationService.notifyPatientRegistration(patient);

            System.out.println("Patient registered successfully.");
        }

        else{

            patients.remove(patient);
            System.out.println("Patient registration failed because data could not be saved.");
        }
    }

    //display all patient
    public void viewAllPatients(){

        if(patients.isEmpty()){
            System.out.println("No patient record is found");
            return;
        }

        for(Patient patient : patients){
            patient.displayPatientDetails();
        }
    }

    //Search patient by id (lenear search)
    public Patient searchPatientById(String PatientId){

        if(PatientId == null){

            return null;
        }

        for (Patient patient : patients){

           if ( patient.getPatientId().equalsIgnoreCase(PatientId.trim())){

                return patient;
           }
        }

        return null;
    }

    //update patient details
    public boolean updatePatient(String patientId, String mobileNumber, String email, String city, int age, String medicalHistory){

        Patient patient = searchPatientById(patientId);

        if(patient ==null){
            System.out.println("Patient not fount.");
            return false;
        }

        //validate mobile number
        if(!InputValidator.isValidMobileNumber(mobileNumber)){
            System.out.println("Invalid mobile number.");
            return false;
        }

        //validate email
        if(!InputValidator.isValidEmail(email)){
            System.out.println("Invalid email address.");
            return false;
        }

        //validate city
        if(!InputValidator.isNotEmpty(city)){
            System.out.println("City cannot be empty.");
            return false;
        }

        //validate age
        if(!InputValidator.isValidAge(age)){
            System.out.println("Invalid patient age.");
            return false;
        }

        //validate medical history
        if(!InputValidator.isNotEmpty(medicalHistory)){
            System.out.println("Medical history cannot be empty.");
            return false;
        }

        //Storre old values for rollback
        String oldMobileNumber = patient.getMobileNumber();
        String oldEmail = patient.getEmail();
        String oldCity = patient.getCity();
        int oldAge = patient.getAge();
        String oldMedicalHistory = patient.getMedicalHistory();

        //update patient details after validation
        patient.setMobileNumber(mobileNumber);
        patient.setemail(email);
        patient.setCity(city);
        patient.setAge(age);
        patient.setMedicalHistory(medicalHistory);

        //save updated records
        if(savePatientsToFile()){

            System.out.println("Patient updated successfully.");
            return true;
        }

        else{

            //restore previous values if saving fails
            patient.setMobileNumber(oldMobileNumber);
            patient.setemail(oldEmail);
            patient.setCity(oldCity);
            patient.setAge(oldAge);
            patient.setMedicalHistory(oldMedicalHistory);

            System.out.println("Patient updated failed because data could not be saved.");
            return false;
        }
    }

    //delete patient details
    public boolean deletePatient(String patientId){
        Patient patient = searchPatientById(patientId);

        if(patient == null){
            System.out.println("Patient not found.");
            return false;
        }

        patients.remove(patient);
        
        //save updated records
        if(savePatientsToFile()){

            System.out.println("Patient deleted successfully.");
            return true;
        }

        else{

            //restore patient if saving fails
            patients.add(patient);
            System.out.println("PAtient deletion faild because data could not be saved.");
            return false;
        }
    }


    //generate unique patient id
    public String uniquePatientId(){

        int highestId = 0;

        for(Patient patient : patients){

            String id = patient.getPatientId();

            if(id != null && id.matches("p\\d+")){
                
                try{
                    int number = Integer.parseInt(id.substring(1));

                    highestId = Math.max(highestId, number);
                }

                catch(NumberFormatException e){
                    System.out.println("Invalid patient ID formate: " + id);
                }
            }
        }

        return String.format("P%03d", highestId + 1);   
    }

    //save patient records to text file
    public boolean savePatientsToFile(){

        File file = new File(filePath);

        try{

            //create data folder if it does not exists
            File parent = file.getParentFile();

            if (parent != null && !parent.exists()){
                
                if(!parent.mkdirs() && !parent.exists()){
                    System.out.println("Unable to create data folder.");
                }
            }

            //save patient record
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){

                for (Patient patient : patients){

                    //prevent seperator conflicts
                    if(!isValidFileField(patient.getPatientId()) || !isValidFileField(patient.getName()) || !isValidFileField(patient.getMobileNumber()) || !isValidFileField(patient.getEmail()) || !isValidFileField(patient.getCity()) || !isValidFileField(patient.getMedicalHistory())){

                        System.out.println("Unable to save patient record containing invalid file charecters.");
                        return false;
                    }

                    writer.write(patient.getPatientId() + "|" + patient.getName() + "|" + patient.getMobileNumber() + "|" + patient.getEmail() + "|" + patient.getCity() + "|" + patient.getAge() + "|" + patient.getMedicalHistory());
                    
                    writer.newLine();
                }
            }
            return true;
        } 
        
        catch (IOException e){
            System.out.println("Error occured while saving patient records." + e.getMessage());
            return false;
        }
    }

    //check whether a field can safly be stored in the text file
    private boolean isValidFileField(String value){
        return value != null && !value.contains("|") && !value.contains("\n") && !value.contains("\r");
    }

    //validate a patient record loaded from the file
    private boolean isValidPatientRecord(Patient patient){

        return patient != null
            && InputValidator.isNotEmpty(patient.getPatientId())
            && InputValidator.isValidName(patient.getName())
            && InputValidator.isValidMobileNumber(patient.getMobileNumber())
            && InputValidator.isValidEmail(patient.getEmail())
            && InputValidator.isNotEmpty(patient.getCity())
            && InputValidator.isValidAge(patient.getAge())
            &&InputValidator.isNotEmpty(patient.getMedicalHistory());
    }

    //load patient record from text file
    public void loadPatientsFromFile(){

        File file = new File(filePath);

        if (!file.exists()){
            return;
        }

        try(BufferedReader reader = new BufferedReader(new FileReader(file))){

            String line;

            while ((line = reader.readLine()) != null) {
                
                String[] data = line.split("\\|", -1);

                if(data.length !=7){
                    System.out.println("Skipping invalid patient record.");
                    continue;
                }

                try{
                    Patient patient =new Patient(data[0], data[1], data[2], data[3], data[4], Integer.parseInt(data[5]), data[6]);

                    if(!isValidPatientRecord(patient)){

                        System.out.println("Skipping invalid patient data.");
                        continue;
                    }

                    if(searchPatientById(patient.getPatientId()) != null){

                        System.out.println("Skipping duplicate patient ID.");
                        continue;
                    }

                    patients.add(patient);
                }

                catch(IllegalArgumentException e){

                    System.out.println("Skipping invalid patient data.");
                }
            } 

            System.out.println("Patient records loaded successfully.");
    
        }

        catch(IOException e){
            System.out.println("Error loading patient record: " + e.getMessage());
        }
    }

    //get all patient details
    public List<Patient> getAllPatients(){
        return new ArrayList<>(patients);
    }
}
