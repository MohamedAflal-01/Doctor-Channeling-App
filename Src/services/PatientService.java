package Src.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        if(searchPatientById(patient.getPatientId()) !=null){
            System.out.println("Patient ID is already exist.");
            return;
        }
        
        patients.add(patient);
        savePatientsToFile();

        System.out.println("Patient registered successfully.");
        System.out.println("Patient ID: " + patient.getPatientId());
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

        for (Patient patient : patients){
           if ( patient.getPatientId().equalsIgnoreCase(PatientId)){
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

        patient.setMobileNumber(mobileNumber);
        patient.setemail(email);
        patient.setCity(city);
        patient.setAge(age);
        patient.setMedicalHistory(medicalHistory);

        savePatientsToFile();

        System.out.println("Patient updated successfully.");
        return true;
    }

    //delete patient details
    public boolean deletePatient(String patientId){
        Patient patient = searchPatientById(patientId);

        if(patient == null){
            System.out.println("Patient not found.");
            return false;
        }

        patients.remove(patient);
        savePatientsToFile();

        System.out.println("Patient deleted successfully.");
        return true;
    }

    //generate unique patient id
    public String uniquePatientId(){
        int highestId = 0;

        for(Patient patient : patients){
            String id = patient.getPatientId();

            if(id.matches("p\\d+")){
                int number = Integer.parseInt(id.substring(1));
                highestId = Math.max(highestId, number);
            }
        }

        return String.format("P%03d", highestId + 1);   
    }

    //save patient records to text file
    public void savePatientsToFile(){
        File file = new File(filePath);

        try{
            File parent = file.getParentFile();

            if (parent != null){
                parent.mkdirs();
            }

            try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
                for (Patient patient : patients){
                    writer.write(patient.getPatientId() + "|" + patient.getName() + "|" + patient.getMobileNumber() + "|" + patient.getEmail() + "|" + patient.getCity() + "|" + patient.getAge() + "|" + patient.getMedicalHistory());
                    writer.newLine();
                }
            }
        } 
        
        catch (IOException e){
            System.out.println("Error occured. please conduct the admin." + e.getMessage());
        }
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

                    patients.add(patient);
                }

                catch(IllegalArgumentException e){
                    System.out.println("Skipping invalid patient data.");
                }
            } 
    
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
