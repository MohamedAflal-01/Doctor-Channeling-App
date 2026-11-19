
package Src.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import Src.models.Appoinment;
import Src.models.Doctor;
import Src.models.Patient;
import Src.models.RescheduleRequest;
import Src.structures.RescheduleQueue;

public class AppointmentService {

    private ArrayList<Appoinment> appoinments;
    private ArrayList<RescheduleRequest> rescheduleRequests;

    private RescheduleQueue rescheduleQueue;

    private final String filePath = "Data/appointments.txt";
    private final String rescheduleFilePath =
            "Data/reschedule_requests.txt";

    private PatientService patientService;
    private DoctorService doctorService;

    // Constructor
    public AppointmentService(
            PatientService patientService,
            DoctorService doctorService) {

        appoinments = new ArrayList<>();
        rescheduleRequests = new ArrayList<>();
        rescheduleQueue = new RescheduleQueue();

        this.patientService = patientService;
        this.doctorService = doctorService;

        loadAppoinmentsFromFile();
        loadRescheduleRequestsFromFile();
    }

    // Check slot availability
    public boolean checkSlotAvailability(
            String doctorId,
            LocalDate date,
            LocalTime time) {

        Doctor doctor = doctorService.searchDoctorById(doctorId);

        if (doctor == null) {
            return false;
        }

        String timeSlot = date + " " + time;

        return doctor.getAvailableTimeSlots().contains(timeSlot);
    }

    // Generate appointment ID
    public String generateAppoinmentId() {

        int highestId = 0;

        for (Appoinment appoinment : appoinments) {

            String id = appoinment.getAppoinmentId();

            if (id.matches("A\\d+")) {
                int number = Integer.parseInt(id.substring(1));
                highestId = Math.max(highestId, number);
            }
        }

        return String.format("A%03d", highestId + 1);
    }

    // Book appointment
    public void bookAppoinment(
            String patientId,
            String doctorID,
            LocalDate date,
            LocalTime time) {

        Patient patient = patientService.searchPatientById(patientId);
        Doctor doctor = doctorService.searchDoctorById(doctorID);

        if (patient == null) {
            System.out.println("Patient not found.");
            return;
        }

        if (doctor == null) {
            System.out.println("Doctor not found.");
            return;
        }

        String timeSlot = date + " " + time;

        int queueNumber = 0;
        Appoinment.AppoinmentStatus status;

        if (checkSlotAvailability(doctorID, date, time)) {

            status = Appoinment.AppoinmentStatus.CONFIRMED;

            doctor.removeTimeSlot(timeSlot);
            doctorService.saveDoctorsToFile();

            System.out.println("Appointment confirmed!");
        } else {

            status = Appoinment.AppoinmentStatus.WAITING;

            queueNumber = getNextQueueNumber(
                    doctorID, date, time);

            System.out.println("Slot unavailable.");
            System.out.println("Patient added to waiting list.");
            System.out.println("Queue Number: " + queueNumber);
        }

        Appoinment appoinment = new Appoinment(
                generateAppoinmentId(),
                patientId,
                doctorID,
                date,
                time,
                queueNumber
        );

        appoinment.setStatus(status);

        appoinments.add(appoinment);

        saveAppointmentsToFile();
    }

    // Generate waiting queue number
    private int getNextQueueNumber(
            String doctorId,
            LocalDate date,
            LocalTime time) {

        int highestNumber = 0;

        for (Appoinment appoinment : appoinments) {

            if (appoinment.getdoctorId().equals(doctorId)
                    && appoinment.getAppoinmentDate().equals(date)
                    && appoinment.getAppoinmentTime().equals(time)
                    && appoinment.getStatus()
                    == Appoinment.AppoinmentStatus.WAITING) {

                highestNumber = Math.max(
                        highestNumber,
                        appoinment.getQueueNumber());
            }
        }

        return highestNumber + 1;
    }

    // Search appointment by ID
    public Appoinment searchAppoinmentById(String appoinmentId) {

        for (Appoinment appoinment : appoinments) {

            if (appoinment.getAppoinmentId()
                    .equalsIgnoreCase(appoinmentId)) {

                return appoinment;
            }
        }

        return null;
    }

    // View all appointments
    public void viewAllAppoinment() {

        if (appoinments.isEmpty()) {
            System.out.println("No appointments found yet.");
            return;
        }

        for (Appoinment appoinment : appoinments) {
            appoinment.displayAppoinmentDetails();
        }
    }

    // View patient appointments
    public void viewPatientAppoinments(String patientId) {

        boolean found = false;

        for (Appoinment appoinment : appoinments) {

            if (appoinment.getPatientId()
                    .equalsIgnoreCase(patientId)) {

                appoinment.displayAppoinmentDetails();
                found = true;
            }
        }

        if (!found) {
            System.out.println(
                    "No appointments found for this patient.");
        }
    }

    // View doctor appointments
    public void viewDoctorAppoinments(String doctorId) {

        boolean found = false;

        for (Appoinment appoinment : appoinments) {

            if (appoinment.getdoctorId()
                    .equalsIgnoreCase(doctorId)) {

                appoinment.displayAppoinmentDetails();
                found = true;
            }
        }

        if (!found) {
            System.out.println(
                    "No appointments found for this doctor.");
        }
    }

    // Cancel appointment
    public boolean cancelAppoinment(String appoinmentId) {

        Appoinment appoinment =
                searchAppoinmentById(appoinmentId);

        if (appoinment == null) {
            System.out.println("Appointment not found.");
            return false;
        }

        if (appoinment.getStatus()
                != Appoinment.AppoinmentStatus.CONFIRMED) {

            System.out.println(
                    "Only confirmed appointments can be cancelled.");
            return false;
        }

        appoinment.setStatus(
                Appoinment.AppoinmentStatus.CANCELLED);

        Doctor doctor =
                doctorService.searchDoctorById(
                        appoinment.getdoctorId());

        String timeSlot =
                appoinment.getAppoinmentDate()
                + " " + appoinment.getAppoinmentTime();

        if (doctor != null) {

            doctor.addAvailableTimeSlot(timeSlot);

            assignNextWaitingPatient(
                    appoinment.getdoctorId(),
                    appoinment.getAppoinmentDate(),
                    appoinment.getAppoinmentTime());

            doctorService.saveDoctorsToFile();
        }

        saveAppointmentsToFile();

        System.out.println("Appointment cancelled successfully!");

        return true;
    }

    // Assign released slot to the next waiting patient
    public void assignNextWaitingPatient(
            String doctorId,
            LocalDate date,
            LocalTime time) {

        Appoinment nextPatient = null;

        for (Appoinment appoinment : appoinments) {

            if (appoinment.getdoctorId().equals(doctorId)
                    && appoinment.getAppoinmentDate().equals(date)
                    && appoinment.getAppoinmentTime().equals(time)
                    && appoinment.getStatus()
                    == Appoinment.AppoinmentStatus.WAITING) {

                if (nextPatient == null
                        || appoinment.getQueueNumber()
                        < nextPatient.getQueueNumber()) {

                    nextPatient = appoinment;
                }
            }
        }

        if (nextPatient != null) {

            nextPatient.setStatus(
                    Appoinment.AppoinmentStatus.CONFIRMED);

            nextPatient.setQueueNumber(0);

            Doctor doctor =
                    doctorService.searchDoctorById(doctorId);

            if (doctor != null) {

                String timeSlot = date + " " + time;
                doctor.removeTimeSlot(timeSlot);
            }

            System.out.println(
                    "Waiting patient promoted successfully!");

            System.out.println(
                    "Patient ID: " + nextPatient.getPatientId());
        }
    }

    // --------------------------------------------------
    // PHASE 7: RESCHEDULE MANAGEMENT USING QUEUE
    // --------------------------------------------------

    // Submit a reschedule request
    public boolean rescheduleAppoinment(
            String appoinmentId,
            LocalDate newDate,
            LocalTime newTime) {

        Appoinment appoinment =
                searchAppoinmentById(appoinmentId);

        if (appoinment == null) {
            System.out.println("Appointment not found.");
            return false;
        }

        if (appoinment.getStatus()
                != Appoinment.AppoinmentStatus.CONFIRMED) {

            System.out.println(
                    "Only confirmed appointments can be rescheduled.");
            return false;
        }

        // Prevent duplicate pending requests
        if (hasPendingRescheduleRequest(appoinmentId)) {
            System.out.println(
                    "A reschedule request is already pending.");
            return false;
        }

        // Prevent requesting the same slot
        if (appoinment.getAppoinmentDate().equals(newDate)
                && appoinment.getAppoinmentTime().equals(newTime)) {

            System.out.println(
                    "The requested date and time are the same as the current appointment.");
            return false;
        }

        String requestId = generateRescheduleRequestId();

        RescheduleRequest request = new RescheduleRequest(
                requestId,
                appoinment.getAppoinmentId(),
                appoinment.getPatientId(),
                appoinment.getdoctorId(),
                newDate,
                newTime
        );

        // Add request to collection and FIFO queue
        rescheduleRequests.add(request);
        rescheduleQueue.enqueue(request);

        saveRescheduleRequestsToFile();

        System.out.println(
                "Reschedule request submitted successfully.");

        System.out.println(
                "Request ID: " + requestId);

        System.out.println(
                "Your request will be processed in FIFO order.");

        return true;
    }

    // Check for an existing pending request
    private boolean hasPendingRescheduleRequest(
            String appoinmentId) {

        for (RescheduleRequest request : rescheduleRequests) {

            if (request.getAppoinmentId()
                    .equalsIgnoreCase(appoinmentId)
                    && request.getRequestStatus()
                    == RescheduleRequest.RequestStatus.PENDING) {

                return true;
            }
        }

        return false;
    }

    // Generate reschedule request ID
    private String generateRescheduleRequestId() {

        int highestId = 0;

        for (RescheduleRequest request : rescheduleRequests) {

            String id = request.getRequestId();

            if (id.matches("R\\d+")) {

                int number = Integer.parseInt(id.substring(1));
                highestId = Math.max(highestId, number);
            }
        }

        return String.format("R%03d", highestId + 1);
    }

    // Process all requests currently in the queue
    public void processRescheduleRequests() {

        if (rescheduleQueue.isEmpty()) {
            System.out.println(
                    "No reschedule requests available.");
            return;
        }

        // Process only the requests currently in the queue.
        // Unavailable requests are re-enqueued for a later attempt.
        int requestsToProcess = rescheduleQueue.size();

        for (int i = 0; i < requestsToProcess; i++) {

            RescheduleRequest request =
                    rescheduleQueue.dequeue();

            if (request != null) {
                processSingleRescheduleRequest(request);
            }
        }

        saveRescheduleRequestsToFile();
        saveAppointmentsToFile();
        doctorService.saveDoctorsToFile();
    }

    // Process one reschedule request
    private void processSingleRescheduleRequest(
            RescheduleRequest request) {

        Appoinment appoinment =
                searchAppoinmentById(
                        request.getAppoinmentId());

        if (appoinment == null
                || appoinment.getStatus()
                != Appoinment.AppoinmentStatus.CONFIRMED) {

            request.setRequestStatus(
                    RescheduleRequest.RequestStatus.REJECTED);

            System.out.println(
                    "Reschedule request rejected: appointment is unavailable.");

            return;
        }

        Doctor doctor =
                doctorService.searchDoctorById(
                        appoinment.getdoctorId());

        if (doctor == null) {

            request.setRequestStatus(
                    RescheduleRequest.RequestStatus.REJECTED);

            System.out.println(
                    "Reschedule request rejected: doctor not found.");

            return;
        }

        LocalDate newDate = request.getRequestedDate();
        LocalTime newTime = request.getRequestdTime();

        // Check whether the new slot is available
        if (!checkSlotAvailability(
                appoinment.getdoctorId(),
                newDate,
                newTime)) {

            System.out.println(
                    "Requested slot unavailable for request "
                    + request.getRequestId());

            System.out.println(
                    "Request remains pending in the queue.");

            // Keep request pending and place it at the rear
            rescheduleQueue.enqueue(request);
            return;
        }

        // Store the original appointment slot
        LocalDate oldDate = appoinment.getAppoinmentDate();
        LocalTime oldTime = appoinment.getAppoinmentTime();

        String newSlot = newDate + " " + newTime;

        // Reserve the new slot first
        doctor.removeTimeSlot(newSlot);

        // Update the existing appointment
        appoinment.reschedule(newDate, newTime);

        // Release the original slot
        String oldSlot = oldDate + " " + oldTime;
        doctor.addAvailableTimeSlot(oldSlot);

        // Promote the next waiting patient for the old slot
        assignNextWaitingPatient(
                appoinment.getdoctorId(),
                oldDate,
                oldTime);

        // Update request status
        request.setRequestStatus(
                RescheduleRequest.RequestStatus.APPROVED);

        System.out.println(
                "Reschedule request approved successfully.");

        System.out.println(
                "Appointment ID: " + appoinment.getAppoinmentId());

        System.out.println(
                "Patient ID: " + appoinment.getPatientId());

        System.out.println(
                "Patient notification: Appointment rescheduled.");
    }

    // Display pending reschedule requests
    public void displayRescheduleQueue() {

        if (rescheduleQueue.isEmpty()) {
            System.out.println(
                    "No pending reschedule requests.");
            return;
        }

        System.out.println(
                "\n--- Reschedule Request Queue ---");

        rescheduleQueue.displayQueue();
    }

    // Save appointments to file
    public void saveAppointmentsToFile() {

        File file = new File(filePath);

        try {

            File parent = file.getParentFile();

            if (parent != null) {
                parent.mkdirs();
            }

            try (BufferedWriter writer =
                    new BufferedWriter(new FileWriter(file))) {

                for (Appoinment appoinment : appoinments) {

                    writer.write(
                            appoinment.getAppoinmentId() + "|"
                            + appoinment.getPatientId() + "|"
                            + appoinment.getdoctorId() + "|"
                            + appoinment.getAppoinmentDate() + "|"
                            + appoinment.getAppoinmentTime() + "|"
                            + appoinment.getQueueNumber() + "|"
                            + appoinment.getStatus()
                    );

                    writer.newLine();
                }
            }

        } catch (IOException e) {

            System.out.println(
                    "Error occurred: " + e.getMessage());
        }
    }

    // Load appointments from file
    public void loadAppoinmentsFromFile() {

        File file = new File(filePath);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader =
                new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split("\\|", -1);

                if (data.length != 7) {
                    System.out.println(
                            "Invalid appointment record skipped.");
                    continue;
                }

                try {

                    Appoinment appoinment = new Appoinment(
                            data[0],
                            data[1],
                            data[2],
                            LocalDate.parse(data[3]),
                            LocalTime.parse(data[4]),
                            Integer.parseInt(data[5])
                    );

                    appoinment.setStatus(
                            Appoinment.AppoinmentStatus.valueOf(data[6]));

                    if (searchAppoinmentById(
                            appoinment.getAppoinmentId()) == null) {

                        appoinments.add(appoinment);
                    }

                } catch (IllegalArgumentException e) {

                    System.out.println(
                            "Invalid appointment data skipped.");
                }
            }

        } catch (IOException e) {

            System.out.println(
                    "Error loading appointments: " + e.getMessage());
        }
    }

    // Save all reschedule requests to file
    private void saveRescheduleRequestsToFile() {

        File file = new File(rescheduleFilePath);

        try {

            File parent = file.getParentFile();

            if (parent != null) {
                parent.mkdirs();
            }

            try (BufferedWriter writer =
                    new BufferedWriter(new FileWriter(file))) {

                for (RescheduleRequest request : rescheduleRequests) {

                    writer.write(
                            request.getRequestId() + "|"
                            + request.getAppoinmentId() + "|"
                            + request.getPatientId() + "|"
                            + request.getDoctorId() + "|"
                            + request.getRequestedDate() + "|"
                            + request.getRequestdTime() + "|"
                            + request.getRequestStatus()
                    );

                    writer.newLine();
                }
            }

        } catch (IOException e) {

            System.out.println(
                    "Error saving reschedule requests: "
                    + e.getMessage());
        }
    }

    // Load reschedule requests and rebuild FIFO queue
    private void loadRescheduleRequestsFromFile() {

        File file = new File(rescheduleFilePath);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader =
                new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split("\\|", -1);

                if (data.length != 7) {
                    System.out.println(
                            "Invalid reschedule request skipped.");
                    continue;
                }

                try {

                    RescheduleRequest request =
                            new RescheduleRequest(
                                    data[0],
                                    data[1],
                                    data[2],
                                    data[3],
                                    LocalDate.parse(data[4]),
                                    LocalTime.parse(data[5]),
                                    RescheduleRequest.RequestStatus.valueOf(
                                            data[6])
                            );

                    rescheduleRequests.add(request);

                    // Only pending requests enter the queue
                    if (request.getRequestStatus()
                            == RescheduleRequest.RequestStatus.PENDING) {

                        rescheduleQueue.enqueue(request);
                    }

                } catch (IllegalArgumentException e) {

                    System.out.println(
                            "Invalid reschedule request data skipped.");
                }
            }

        } catch (IOException e) {

            System.out.println(
                    "Error loading reschedule requests: "
                    + e.getMessage());
        }
    }
}
