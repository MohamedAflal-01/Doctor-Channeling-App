package Src.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appoinment {
    
    //appoinment status
    public enum AppoinmentStatus {
        CONFIRMED,
        WAITING,
        CANCELLED,
        COMPLETED,
        RESCHEDULED  
    }

    //private attributes for the appoinment entity
    private String appoinmentId;
    private String patientId;
    private String doctorId;
    private LocalDate appoinmentDate;
    private LocalTime appoinmentTime;
    private int queueNumber;
    private AppoinmentStatus status;

    //define the attributes using constructor
    public Appoinment(String appoinmentId, String patientId, String doctorId, LocalDate appoinmentDate, LocalTime appoinmentTime, int queueNumber){
        this.appoinmentId = appoinmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appoinmentDate = appoinmentDate;
        this.appoinmentTime = appoinmentTime;
        this.queueNumber = queueNumber;

        this.status =AppoinmentStatus.CONFIRMED;
    }

    //getter method for the all attributes
    public String getAppoinmentId(){
        return appoinmentId;
    }

    public String getPatientId(){
        return patientId;
    }

    public String getdoctorId(){
        return doctorId;
    }

    public LocalDate getAppoinmentDate(){
        return appoinmentDate;
    }    

    public LocalTime getAppoinmentTime(){
        return appoinmentTime;
    }

    public int getQueueNumber(){
        return queueNumber;
    }

    public AppoinmentStatus getStatus(){
        return status;
    }

    //update appoinment status
    public void setStatus(AppoinmentStatus status){
        this.status = status;
    }

    //update queue number
    public void setQueueNumber(int queueNumber){
        this.queueNumber = queueNumber;
    }

    //update appoinment date and time
    public void reschedule(LocalDate newDate, LocalTime newTime){
        this.appoinmentDate = newDate;
        this.appoinmentTime = newTime;

        this.status = AppoinmentStatus.CONFIRMED;
    }

    //display appoinment details
    public void displayAppoinmentDetails(){
        System.out.println("\n======================= APPOINMENT DETAILS =============================");

        System.out.println("Appoinment ID    : " + appoinmentId);
        System.out.println("Patient ID       : " + patientId);
        System.out.println("Doctor ID        : " + doctorId);
        System.out.println("Date             : " + appoinmentDate);
        System.out.println("Time             : "  + appoinmentTime);
        System.out.println("Queue Number     : " + queueNumber);
        System.out.println("Appoinment Status: " + status);

        System.out.println("===========================================================================");
    }
}
