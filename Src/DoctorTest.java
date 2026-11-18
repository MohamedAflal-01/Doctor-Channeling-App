package Src;

import Src.models.Doctor;

public class DoctorTest {
    public static void main(String[] args) {

        // Create doctor object
        Doctor doctor = new Doctor(
            "D001",
            "Dr. Ahmed",
            "Cardiologist",
            3500.00
        );

        // Add available time slots
        doctor.addAvailableTimeSlot("09:00 AM");
        doctor.addAvailableTimeSlot("09:30 AM");
        doctor.addAvailableTimeSlot("10:00 AM");

        // Display doctor details
        doctor.displayDoctorDetails();

        // Update consultation fee
        doctor.setConsultationFee(4000.00);

        // Display updated fee
        System.out.println(
            "\nUpdated Fee: Rs. " +
            doctor.getConsultationFee()
        );

        // Remove a time slot
        doctor.removeTimeSlot("09:30 AM");

        // Display updated doctor details
        doctor.displayDoctorDetails();
    }
   
}
