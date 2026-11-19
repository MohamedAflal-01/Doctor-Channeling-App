package Src;

import Src.models.Appoinment;
import Src.structures.PatientQueue;

import java.time.LocalDate;
import java.time.LocalTime;

public class PatientQueueTest {
    
    public static void main(String[] args) {

        PatientQueue queue = new PatientQueue();

        // Create appointments
        Appoinment appointment1 = new Appoinment(
                "A001", "P001", "D001",
                LocalDate.of(2026, 11, 20),
                LocalTime.of(9, 0), 1
        );

        Appoinment appointment2 = new Appoinment(
                "A002", "P002", "D001",
                LocalDate.of(2026, 11, 20),
                LocalTime.of(9, 0), 2
        );

        Appoinment appointment3 = new Appoinment(
                "A003", "P003", "D001",
                LocalDate.of(2026, 11, 20),
                LocalTime.of(9, 0), 3
        );

        // Enqueue appointments
        queue.enqueue(appointment1);
        queue.enqueue(appointment2);
        queue.enqueue(appointment3);

        // Display queue
        queue.displayQueue();

        // Check queue size
        System.out.println("Queue Size: " + queue.size());

        // Peek front appointment
        System.out.println("\nFirst Waiting Appointment:");

        Appoinment first = queue.peek();

        if (first != null) {
            first.displayAppoinmentDetails();
        }

        // Dequeue appointment
        System.out.println("\nRemoving First Appointment:");

        Appoinment removed = queue.dequeue();

        if (removed != null) {
            removed.displayAppoinmentDetails();
        }

        // Display updated queue
        System.out.println("\nUpdated Waiting Queue:");

        queue.displayQueue();
    }

}
