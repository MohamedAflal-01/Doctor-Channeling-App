package Src.utils;

import Src.models.Patient;
import Src.models.Doctor;
import Src.models.Appoinment;
import Src.models.RescheduleRequest;

public class NotificationService {
    
    //set a constructor as private
    private NotificationService(){

    }

    //notify patient about successfull registration
    public static void notifyPatientRegistration(Patient patient){

        System.out.println("\n========== NOTIFICATION ==========");
        System.out.println("Patient Registration Successfull!");
        System.out.println("Patient ID: " + patient.getPatientId());
        System.out.println("Patient Name: " + patient.getName());
        System.out.println("Your registration has been completed.");
        System.out.println("==================================");
    }

    //notify patient about successfull appoinment booking
    public static void notifyAppoinmentBooking(Patient patient, Appoinment appoinment){

        System.out.println("\n========== NOTIFICATION ==========");
        System.out.println("Appoinment Booking Successful!");
        System.out.println("Patient Name: " + patient.getName());
        System.out.println("Appoinment ID: " + appoinment.getAppoinmentId());
        System.out.println("Appoinment Date: " + appoinment.getAppoinmentDate());
        System.out.println("Appoinment Time: " + appoinment.getAppoinmentTime());
        System.out.println("Appoinment Status: " + appoinment.getStatus());
        System.out.println("==================================");
    }

    //notify patient about appoinment cancellation
    public static void notifyAppoinmentCancellation(Patient patient, Appoinment appoinment){

        System.out.println("\n========== NOTIFICATION ==========");
        System.out.println("Appoinment Cancelled!");
        System.out.println("Patient Name: " + patient.getName());
        System.out.println("Appoinment ID: " + appoinment.getAppoinmentId());
        System.out.println("Your appoinment has been cancelled.");
        System.out.println("==================================");
    }

    //notify patient about sucessfull rescheduling
    public static void notifyAppoinmentRescheduling(Patient patient, Appoinment appoinment){

        System.out.println("\n========== NOTIFICATION ==========");
        System.out.println("Appoinment Rescheduled Successfully!.");
        System.out.println("Patient Name: " + patient.getName());
        System.out.println("Appoinment ID: " + appoinment.getAppoinmentId());
        System.out.println("New Appoinment Date: " + appoinment.getAppoinmentDate());
        System.out.println("New Appoinment Time: " + appoinment.getAppoinmentTime());
        System.out.println("==================================");
    }

    //notify patient about waiting list status
    public static void notifyWaitingList(Patient patient, Appoinment appoinment){

        System.out.println("\n========== NOTIFICATION ==========");
        System.out.println("Appoinment Waiting List!");
        System.out.println("Patient Name: " + patient.getName());
        System.out.println("Appoinment ID: " + appoinment.getAppoinmentId());
        System.out.println("Queue Number: " + appoinment.getQueueNumber());
        System.out.println("You have been added to the waiting list.");
        System.out.println("==================================");
    }

    //notify patient when promoted from waiting list
    public static void notifyWaitingListPromotion(Patient patient, Appoinment appoinment){

        System.out.println("\n========== NOTIFICATION ==========");
        System.out.println("Appoinment Confirmed!");
        System.out.println("Patient Name: " + patient.getName());
        System.out.println("Appoinment ID: " + appoinment.getAppoinmentId());
        System.out.println("Your waiting-list appoinment is now confirmed.");
        System.out.println("Appoinment Date: " + appoinment.getAppoinmentDate());
        System.out.println("Appoinment Time: " + appoinment.getAppoinmentTime());
        System.out.println("==================================");
    }

    //notify patient about reschedule request status
    public static void notifyRescheduleRequest(Patient patient, RescheduleRequest request){

        System.out.println("\n========== NOTIFICATION ==========");
        System.out.println("Reschedule Request Update.");
        System.out.println("Patient Name: " + patient.getName());
        System.out.println("Request ID: " + request.getRequestId());
        System.out.println("Appoinment ID: " + request.getAppoinmentId());
        System.out.println("Request Status: " + request.getRequestStatus());
        System.out.println("==================================");
    }

    //notify patient about doctor availability
    public static void notifyDoctorAvailability(Doctor doctor){

        System.out.println("\n========== NOTIFICATION ==========");
        System.out.println("Doctor Availability Updated!");
        System.out.println("Doctor Name: " + doctor.getName());
        System.out.println("Specialization: " + doctor.getSpecialization());
        System.out.println("Available Time Slot: " + doctor.getAvailableTimeSlots());
        System.out.println("==================================");
    }
}
