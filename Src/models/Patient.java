package Src.models;

public class Patient {

    //Define the private attributes for patient entity
    private String patientId;
    private String name;
    private String mobileNumber;
    private String email;
    private String city;
    private int age;
    private String medicalHistory;

    //call the defined attributes in the constructor
    public Patient(String patientId, String name, String mobileNumber, String email, String city, int age, String medicalHistory){
        this.patientId = patientId;
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.city = city;
        this.age = age;
        this.medicalHistory = medicalHistory;
    }

    //call all attributes using getter method
    public String getPatientId(){
        return patientId;
    }

    public String getName(){
        return name;
    }

    public String getMobileNumber(){
        return mobileNumber;
    }

    public String getEmail(){
        return email;
    }

    public String getCity(){
        return city;
    }

    public int getAge(){
        return  age;
    }

    public String getMedicalHistory(){
        return medicalHistory;
    }

    //call the attributes using setter method
    public void setName(String name){
        this.name =name;
    }

    public void setMobileNumber(String mobileNumber){
        this.mobileNumber = mobileNumber;
    }

    public void setemail(String email){
        this.email = email;
    }

    public void setCity(String city){
        this.city = city;
    }

    public void setAge(int age){
        this.age = age;
    }

    public void setMedicalHistory(String medicalHistory){
        this.medicalHistory = medicalHistory;
    }

    //display patient details
    public void displayPatientDetails(){
        System.out.println("\n=======================PATIENT DETAILS========================");
        
        System.out.println("Patient ID     : " + patientId);
        System.out.println("Name           : " + name);
        System.out.println("Mobile Number  : " + mobileNumber);
        System.out.println("Email          : " + email);
        System.out.println("City           : " + city);
        System.out.println("Age            : " + age);
        System.out.println("Medical History: " + medicalHistory);

        System.out.println("==============================================================");
    }

}
