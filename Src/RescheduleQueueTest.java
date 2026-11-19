package Src;

import Src.models.RescheduleRequest;
import Src.structures.RescheduleQueue;

import java.time.LocalDate;
import java.time.LocalTime;

public class RescheduleQueueTest {
    
    public static void main(String[] args) {

        RescheduleQueue queue = new RescheduleQueue();

        // Create reschedule requests
        RescheduleRequest request1 = new RescheduleRequest(
            "R001", "A001", "P001", "D001",
            LocalDate.of(2026, 11, 20),
            LocalTime.of(10, 0)  
        );

        RescheduleRequest request2 = new RescheduleRequest(
            "R002", "A002", "P002", "D001",
            LocalDate.of(2026, 11, 21),
            LocalTime.of(11, 0)
        );

        RescheduleRequest request3 = new RescheduleRequest(
            "R003", "A003", "P003", "D002",
            LocalDate.of(2026, 11, 22),
            LocalTime.of(12, 0)
        );

        // Add requests to queue
        queue.enqueue(request1);
        queue.enqueue(request2);
        queue.enqueue(request3);

        // Display queue
        queue.displayQueue();

        // Check queue size
        System.out.println("\nQueue Size: " + queue.size());

        // View first request
        System.out.println("\nFirst Request:");

        RescheduleRequest first = queue.peek();

        if (first != null) {
            System.out.println(first.getRequestId());
        }

        // Remove first request
        RescheduleRequest removed = queue.dequeue();

        if (removed != null) {
            System.out.println(
                "\nRemoved Request: " + removed.getRequestId()
            );
        }

        // Display updated queue
        queue.displayQueue();
    }

}
