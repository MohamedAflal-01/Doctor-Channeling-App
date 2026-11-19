package Src.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class RescheduleRequest {

    //request status
    public enum RequestStatus {
        PENDING,
        APPROVED,
        REJECTED,
        COMPLETED
    }

    //set the attribute in private for the reschedule request entity
    private String requestId;
    private String appoinmentId;
    private String patientId;
    private String doctorId;
    private LocalDate requestedDate;
    private LocalTime requestedTime;
    private RequestStatus requestStatus;

    //call the values in the constructor
    public RescheduleRequest(String requestId, String appoinmentId, String patientId, String doctorId, LocalDate requestedDate, LocalTime requestedTime){
        this.requestId = requestId;
        this.appoinmentId = appoinmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.requestedDate = requestedDate;
        this.requestedTime = requestedTime;

        this.requestStatus = RequestStatus.PENDING;
    }

    //seven parameter constructor
    public RescheduleRequest(String requestId, String appoinmentId, String patientId, String doctorId, LocalDate requestedDate, LocalTime requestedTime, RequestStatus requestStatus){
        this(requestId, appoinmentId, patientId, doctorId, requestedDate, requestedTime);

        this.requestStatus = requestStatus;
    }

    //getter method for all attributes
    public String getRequestId(){
        return requestId;
    }

    public String getAppoinmentId(){
        return appoinmentId;
    }

    public  String getPatientId(){
        return patientId;
    }

    public String getDoctorId(){
        return doctorId;
    }

    public LocalDate getRequestedDate(){
        return requestedDate;
    }

    public LocalTime getRequestdTime(){
        return requestedTime;
    }

    public RequestStatus getRequestStatus(){
        return requestStatus;
    }

    //update request status
    public void setRequestStatus(RequestStatus requestStatus){
        this.requestStatus = requestStatus;
    }

    //display request details
    public void displayRequestDetails(){
        System.out.println("\n========================= RESCHEDULE REQUEST ==========================");

        System.out.println("Request ID    : " + requestId);
        System.out.println("Appoinment ID : " + appoinmentId);
        System.out.println("Patient ID    : " + patientId);
        System.out.println("Doctor ID     : " + doctorId);
        System.out.println("Request Date  : " + requestedDate);
        System.out.println("Request Time  : " + requestedTime);
        System.out.println("Request Status: " + requestStatus);

        System.out.println("=====================================================================");
    }

}
