
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import Src.models.Appoinment;
import Src.models.Doctor;
import Src.models.Patient;
import Src.models.RescheduleRequest;
import Src.structures.PatientQueue;
import Src.structures.RescheduleQueue;
import Src.utils.NotificationService;

public class AppointmentService {

    //set attributes in private
    private ArrayList<Appoinment> appoinments;
    private ArrayList<RescheduleRequest> rescheduleRequests;

    private RescheduleQueue rescheduleQueue;

    //seperate waiting queue for each doctor and time slot
    private Map<String, PatientQueue> waitingQueues;

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
        waitingQueues = new HashMap<>();

        this.patientService = patientService;
        this.doctorService = doctorService;

        loadAppoinmentsFromFile();
        rebuildWaitingQueues();
        loadRescheduleRequestsFromFile();

        System.out.println("Appoinment service initialized successfully.");
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

            if (id != null && id.matches("A\\d+")) {

                int number = Integer.parseInt(id.substring(1));

                highestId = Math.max(highestId, number);
            }
        }

        return String.format("A%03d", highestId + 1);
    }

    //generate a unique key for each doctor time slot
    private String generateWaitingQueueKey(String doctorId, LocalDate date, LocalTime time){

        return doctorId.toUpperCase() + "|" + date + "|" + time;
    }

    //rebuild waiting queue from saved appoinment records
    private void rebuildWaitingQueues(){

        waitingQueues.clear();

        ArrayList<Appoinment> waitingAppoinments = new ArrayList<>();

        //collect all waiting appoinments
        for(Appoinment appoinment : appoinments){

            if(appoinment.getStatus() == Appoinment.AppoinmentStatus.WAITING){

                waitingAppoinments.add(appoinment);
            }
        }

        //sort by queue number to preserve FIFO order
        waitingAppoinments.sort(Comparator.comparingInt(Appoinment::getQueueNumber));

        //reconstruct sperate waiting queue
        for(Appoinment appoinment : waitingAppoinments){

            String key = generateWaitingQueueKey(appoinment.getdoctorId(), appoinment.getAppoinmentDate(), appoinment.getAppoinmentTime());

            PatientQueue queue = waitingQueues.computeIfAbsent(key, k -> new PatientQueue());

            queue.enqueue(appoinment);
        }
        
        System.out.println("Waiting queues reconstructed sussfully!");
    }

    //get the next wueue number for a particular time slot
    private int getNextQueueNumber(String doctorId, LocalDate date, LocalTime time){

        int highestNumber = 0;

        for(Appoinment appoinment : appoinments){

            if(appoinment.getdoctorId().equalsIgnoreCase(doctorId) && appoinment.getAppoinmentDate().equals(date) && appoinment.getAppoinmentTime().equals(time) && appoinment.getStatus() == Appoinment.AppoinmentStatus.WAITING){

                highestNumber = Math.max(highestNumber, appoinment.getQueueNumber());
            }
        }

        return highestNumber +1;
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
        } 
        
        else {

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

        //add waiting appoinment to the correct FIFO queue
        if(status == Appoinment.AppoinmentStatus.WAITING){

            String key = generateWaitingQueueKey(doctorID, date, time);

            PatientQueue queue = waitingQueues.computeIfAbsent(key, k -> new PatientQueue());

            queue.enqueue(appoinment);
        }

        saveAppointmentsToFile();
        
        //notify patient according to appinment status
        if(status == Appoinment.AppoinmentStatus.CONFIRMED){

            NotificationService.notifyAppoinmentBooking(patient, appoinment);
        }

        else if(status == Appoinment.AppoinmentStatus.WAITING){

            NotificationService.notifyWaitingList(patient, appoinment);
        }
    }

    // Search appointment by ID
    public Appoinment searchAppoinmentById(String appoinmentId) {

        if(appoinmentId == null){
            return null;
        }

        for (Appoinment appoinment : appoinments) {

            if (appoinment.getAppoinmentId()
                    .equalsIgnoreCase(appoinmentId.trim())) {

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

        //update appoinment status
        appoinment.setStatus(
                Appoinment.AppoinmentStatus.CANCELLED);

        Doctor doctor =
                doctorService.searchDoctorById(
                        appoinment.getdoctorId());

        String timeSlot =
                appoinment.getAppoinmentDate()
                + " " + appoinment.getAppoinmentTime();

        if (doctor != null) {

            //release cancelled appoinment slot
            doctor.addAvailableTimeSlot(timeSlot);

            //promot the next waiting list
            assignNextWaitingPatient(
                    appoinment.getdoctorId(),
                    appoinment.getAppoinmentDate(),
                    appoinment.getAppoinmentTime());

            doctorService.saveDoctorsToFile();
        }

        saveAppointmentsToFile();

        //Notify patient about cancellation
        Patient patient = patientService.searchPatientById((appoinment.getPatientId()));

        if(patient != null){

            NotificationService.notifyAppoinmentCancellation(patient, appoinment);
        }

        System.out.println("Appointment cancelled successfully!");

        return true;
    }

    // Assign released slot to the next waiting patient
    public void assignNextWaitingPatient(
            String doctorId,
            LocalDate date,
            LocalTime time) {

        String key = generateWaitingQueueKey(doctorId, date, time);

        PatientQueue queue = waitingQueues.get(key);

        if(queue == null || queue.isEmpty()){
            
            System.out.println("No patients are waiting for this time slot");
            return ;
        }

        //get the first patient from the FIFO queue
        Appoinment nextPatient = queue.dequeue();

        if(nextPatient == null){
            return ;
        }

        //confirm the waiting appoinment
        nextPatient.setStatus(Appoinment.AppoinmentStatus.CONFIRMED);

        nextPatient.setQueueNumber(0);

        //reserve the released doctor slot
        Doctor doctor = doctorService.searchDoctorById(doctorId);

        if(doctor != null){

            String timeSlot = date + " " + time;

            doctor.removeTimeSlot(timeSlot);
        }

        //remove empty queue from the map
        if(queue.isEmpty()){

            waitingQueues.remove(key);
        }

        //notify to the promoted patient
        Patient patient = patientService.searchPatientById(nextPatient.getPatientId());

        if(patient != null){

            NotificationService.notifyWaitingListPromotion(patient, nextPatient);
        }

        System.out.println("Waiting patient promoted successfully!");

        System.out.println("Patient ID: " + nextPatient.getPatientId());

        saveAppointmentsToFile();

        doctorService.saveDoctorsToFile();
    }

    //display all waiting list
    public void displayAllWaitingLists(){

        if(waitingQueues.isEmpty()){

            System.out.println("No patients are currently in waiting.");
            return ;
        }

        System.out.println("\n========== ALL WAITING LISTS ==========");

        for(Map.Entry<String, PatientQueue> entry : waitingQueues.entrySet()){

            PatientQueue queue = entry.getValue();

            if(!queue.isEmpty()){

                System.out.println("\nWaiting List: " + entry.getKey());

                queue.displayQueue();
            }
        }

    }

    //display waiting list for a specific doctor and time slot
    public void displayWaitingList(String doctorId, LocalDate date, LocalTime time){

        String key = generateWaitingQueueKey(doctorId, date, time);

        PatientQueue queue = waitingQueues.get(key);

        if(queue == null || queue.isEmpty()){

            System.out.println("No patients are waiting for this slot.");
            return ;
        }

        System.out.println("\n========== WAITING LIST ==========");

        System.out.println("Doctor ID: " + doctorId);
        System.out.println("Date: " + date);
        System.out.println("Time: " + time);

        queue.displayQueue();
    }

    //display the next witing patient without removing
    public void diplayNextWaitingPatient(String doctorId, LocalDate date, LocalTime time){

        String key = generateWaitingQueueKey(doctorId, date, time);

        PatientQueue queue = waitingQueues.get(key);

        if(queue == null || queue.isEmpty()){

            System.out.println("No patients are waiting for this time slot.");
            return ;
        }

        Appoinment nextPatient = queue.peek();

        System.out.println("\n========== NEXT WAITING PATIENT ==========");

        if(nextPatient != null){

            nextPatient.displayAppoinmentDetails();
        }
    }

    // RESCHEDULE MANAGEMENT USING QUEUE starting

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

        //notify patient about pending request
        Patient patient = patientService.searchPatientById(request.getPatientId());

        if(patient != null){

            NotificationService.notifyRescheduleRequest(patient, request);
        }

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

            if (id != null && id.matches("R\\d+")) {

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
            
            notifyRescheduleStatus(request);

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

            notifyRescheduleStatus(request);

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

        //notify patient about successfull rescheduling
        Patient patient = patientService.searchPatientById(appoinment.getPatientId());

        if(patient != null){

            NotificationService.notifyAppoinmentRescheduling(patient, appoinment);

            NotificationService.notifyRescheduleRequest(patient, request);
        }

        System.out.println(
                "Reschedule request approved successfully.");

        System.out.println(
                "Appointment ID: " + appoinment.getAppoinmentId());

        System.out.println(
                "Patient ID: " + appoinment.getPatientId());
    }

    //notify patient about reschedule request status
    private void notifyRescheduleStatus(RescheduleRequest request){

        Patient patient = patientService.searchPatientById(request.getPatientId());

        if(patient != null){

            NotificationService.notifyRescheduleRequest(patient, request);
        }
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
                            "Invalid appointment data skipped: " + line);
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
                            "Invalid reschedule request skipped."
                    );

                    continue;
                }

                try {

                    // Actual RescheduleRequest constructor accepts six arguments
                    RescheduleRequest request = new RescheduleRequest(
                            data[0],
                            data[1],
                            data[2],
                            data[3],
                            LocalDate.parse(data[4]),
                            LocalTime.parse(data[5])
                    );

                    // Restore request status separately
                    request.setRequestStatus(
                            RescheduleRequest.RequestStatus.valueOf(data[6])
                    );

                    // Prevent duplicate request ID
                    boolean duplicate = false;

                    for (RescheduleRequest existing : rescheduleRequests) {

                        if (existing.getRequestId()
                                .equalsIgnoreCase(request.getRequestId())) {

                            duplicate = true;
                            break;
                        }
                    }

                    if (duplicate) {
                        System.out.println(
                                "Duplicate reschedule request skipped."
                        );
                        continue;
                    }

                    rescheduleRequests.add(request);

                    // Only pending requests enter the runtime queue
                    if (request.getRequestStatus()
                            == RescheduleRequest.RequestStatus.PENDING) {

                        rescheduleQueue.enqueue(request);
                    }

                } catch (IllegalArgumentException e) {

                    System.out.println(
                            "Invalid reschedule request data skipped: "
                            + line
                    );
                }
            }

            System.out.println(
                    "Reschedule requests loaded successfully."
            );

        } catch (IOException e) {

            System.out.println(
                    "Error loading reschedule requests: "
                    + e.getMessage()
            );
        }

    }
}
