package Src;

import java.time.LocalDate;
import java.time.LocalTime;

import Src.models.Appoinment;
import Src.models.Appoinment.AppoinmentStatus;

public class AppointmentTest {
    public static void main(String[] args) {

        // Create appointment object
        Appoinment appointment = new Appoinment(
            "A001",
            "P001",
            "D001",
            LocalDate.of(2026, 11, 15),
            LocalTime.of(9, 0),
            1
        );

        // Display appointment
        appointment.displayAppoinmentDetails();

        // Update appointment status
        appointment.setStatus(
            AppoinmentStatus.COMPLETED
        );

        System.out.println("\nAfter consultation:");

        appointment.displayAppoinmentDetails();

        // Reschedule appointment
        appointment.reschedule(
            LocalDate.of(2026, 11, 17),
            LocalTime.of(10, 30)
        );

        System.out.println("\nAfter rescheduling:");

        appointment.displayAppoinmentDetails();
    }

}
