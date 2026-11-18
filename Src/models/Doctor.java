package Src.models;

import java.util.ArrayList;
import java.util.List;

public class Doctor {

    //Define the private attributes for patient entity
    private String doctorId;
    private String name;
    private String specialization;
    private List<String> availableTimeSlots;
    private double consultationFee;

    public Doctor(String doctorId, String name, String specialization, double consultationFee){
        this.doctorId = doctorId;
        this.name = name;
        this.specialization = specialization;
        this.consultationFee = consultationFee;
        this.availableTimeSlots = new ArrayList<>();
    }

    //setter method for the all attributes
    public String getDoctorId(){
        return doctorId;
    }

    public String getName(){
        return name;
    }

    public String getSpecialization(){
        return specialization;
    }

    public List<String> getAvailableTimeSlots(){
        return new ArrayList<>(availableTimeSlots);
    }

    public double getConsultationFee(){
        return consultationFee;
    }

    //getter method for the all attributes
    public void setName(String name){
        this.name = name;
    }

    public void setSpecialization(String specialization){
        this.specialization = specialization;
    }

    public void setConsultationFee(double consultationFee){
        this.consultationFee = consultationFee;
    }

    //Add available time slot
    public void addAvailableTimeSlot(String timeSlot){
        if(!availableTimeSlots.contains(timeSlot)){
            availableTimeSlots.add(timeSlot);

            System.out.println("Time slot addedd successfully!!");
        }
        else{
            System.out.println("This time slot already exist. please choose another time slot.");
        }
    }

    //remove time slot
    public void removeTimeSlot(String timeSlot){
        if(availableTimeSlots.remove(timeSlot)){
            System.out.println("Time slot removed successfully!!");
        }
        else{
            System.out.println("Time slot not fount. please try again!!");
        }
    }

    //display doctor details
    public void displayDoctorDetails(){
        System.out.println("\n=========================== DOCTOR DETAILS ================================");

        System.out.println("Doctor ID       : " + doctorId);
        System.out.println("Name            : " + name);
        System.out.println("Specialization  : " + specialization);
        System.out.println("Consultation Fee: Rs. " + consultationFee);

        System.out.println("\nAvailable time slot: ");

        if(availableTimeSlots.isEmpty()){
            System.out.println("No available time slot");
        }
        else{
            for(String slot : availableTimeSlots){
                System.out.println("- " + slot);
            }
        }

        System.out.println("===========================================================================");
    }
}
