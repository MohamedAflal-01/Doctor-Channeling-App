package Src;

import java.time.LocalDate;
import java.time.LocalTime;

import Src.models.RescheduleRequest;

public class RescheduleRequestTest {
    public static void main(String[] args) {

        // Create reschedule request
        RescheduleRequest request = new RescheduleRequest(
            "R001",
            "A001",
            "P001",
            "D001",
            LocalDate.of(2026, 11, 17),
            LocalTime.of(10, 30)
        );

        // Display request
        request.displayRequestDetails();

        // Approve request
        request.setRequestStatus(
            RescheduleRequest.RequestStatus.APPROVED
        );

        System.out.println("\nAfter approval:");

        request.displayRequestDetails();

        // Complete request
        request.setRequestStatus(
            RescheduleRequest.RequestStatus.COMPLETED
        );

        System.out.println("\nAfter completion:");

        request.displayRequestDetails();
    }
}
