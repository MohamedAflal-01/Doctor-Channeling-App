package Src;

import java.time.format.DateTimeParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

import Src.services.PatientService;
import Src.services.DoctorService;
import Src.models.Doctor;
import Src.models.Patient;
import Src.models.Appoinment;
import Src.services.AppointmentService;
import Src.services.HomeVisitService;
import Src.structures.PatientStack;

import Src.utils.DataFileManager;

public class Main {
    
    //scanner for user input
    private static Scanner scanner = new Scanner(System.in);

    //service objects
    private static PatientService patientService;
    private static DoctorService doctorService;
    private static AppointmentService appointmentService;
    private static HomeVisitService homeVisitService;
    private static PatientStack patientStack;

    public static void main(String[] args) {
        
        System.out.println("========================================");
        System.out.println("          WELCOME TO MEDIQUEUE");
        System.out.println("       DOCTOR CHANNELING SYSTEM");
        System.out.println("=========================================");

        //initialized data file
        DataFileManager.initializeDataFiles();

        //initialize service objects
        initializeServices();

        displayMainMenue();

        scanner.close();

        System.out.println("\nThank you for using Mediqueue!");
        System.out.println("Application closed successfully!");
    }

    private static void initializeServices(){

        patientService = new PatientService();

        doctorService = new DoctorService();

        appointmentService = new AppointmentService(patientService, doctorService);

        homeVisitService = new HomeVisitService();

        patientStack = new PatientStack();

        rebuildPatientStack();

        System.out.println("\nMediQueue services initialized successfully.");
    }

    //display main menu
    private static void displayMainMenue(){

        int choice;

        do{

            System.out.println("\n========================================");
            System.out.println("               MEDIQUEUE");
            System.out.println("               MAIN MENU");
            System.out.println("========================================");

            System.out.println("1. Patient Management");
            System.out.println("2. Doctor Management");
            System.out.println("3. Appoinment Management");
            System.out.println("4. Waiting List Management");
            System.out.println("5. Reschedule Management");
            System.out.println("6. Patient Registration History");
            System.out.println("7. Home Visit Management");
            System.out.println("8. Reports and Analytics");
            System.out.println("9. Display All Appoinments");
            System.out.println("10. Exit");

            System.out.println("========================================");

            choice = readInt("Enter your choice: ");

            switch(choice){
                case 1: 
                    patientMenu();
                    break;

                case 2: 
                    doctorMenu();
                    break;

                case 3:
                    appoinmentMenu();
                    break;

                case 4:
                    waitingListMenu();
                    break;

                case 5:
                    rescheduleMenu();
                    break;

                case 6:
                    patientHistoryMenu();
                    break;

                case 7:
                    homeVisitMenu();
                    break;

                case 8:
                    System.out.println("report comming soon.");
                    break;

                case 9:
                    appointmentService.viewAllAppoinment();
                    break;

                case 10:
                    System.out.println("Exiting MediQueue....");
                    break;

                default:
                    System.out.println("Invalid Choice. Please Select 1 to 10 only.");
            }   
        }

        while(choice != 10);
    }

    //==========================================\\
    //      starting patient management         \\
    //==========================================\\

    private static void patientMenu(){

        int choice;

        do{
            System.out.println("\n========== PATIENT MANAGEMENT ==========");

            System.out.println("1. Register Patient");
            System.out.println("2. View All Patient");
            System.out.println("3. Search Patient");
            System.out.println("4. Update Patient");
            System.out.println("5. Delete Patient");
            System.out.println("6. Back to Main Menu");

            System.out.println("========================================");

            choice = readInt("Enter your choice: ");
            
            switch (choice){

                case 1:
                    registerPatient();
                    break;

                case 2:
                    patientService.viewAllPatients();
                    break;

                case 3:
                    searchPatient();
                    break;

                case 4:
                    updatePatient();
                    break;

                case 5:
                    deletePatient();
                    break;

                case 6:
                    System.out.println("Returning to Main Menu");
                    break;

                default:
                    System.out.println("Invalid choice. Please select 1 to 6 only.");
            }
        }

        while(choice != 6);
    }

    //register patient
    private static void registerPatient(){
        
        System.out.println("\n========== REGISTER PATIENT ==========");

        String patientId = patientService.uniquePatientId();

        System.out.println("Generated Patient ID: " + patientId);

        String name = readString("Enter patient name: ");
        String mobile = readString("Enter mobile number: ");
        String email = readString("Enter email address: ");
        String city = readString("Enter city: ");
        int age = readInt("Enter age: ");
        String medicalHistory = readString("Enter medical history: ");

        Patient patient = new Patient(patientId, name, mobile, email, city, age, medicalHistory);

        patientService.registerPatient(patient);

        //check whether registration success
        Patient registerPatient = patientService.searchPatientById(patientId);

        if(registerPatient != null){

            patientStack.push(registerPatient);

            System.out.println("Patient added to registration history.");
        }
    }

    //rebuild patient registration history
    private static void rebuildPatientStack(){

        patientStack.clearStack();

        //patient records are loaded oldest to newest
        for(Patient patient : patientService.getAllPatients()){
            patientStack.push(patient);
        }

        System.out.println("Patient history stack reconstructed successfully.");
    }

    //search patient
    private static void searchPatient(){

        System.out.println("\n========== SEARCH PATIENT ==========");

        String patientId = readString("Enter Patient ID: ");

        Patient patient = patientService.searchPatientById(patientId);

        if(patient != null){
            
            System.out.println("Patient found!");
            patient.displayPatientDetails();
        }

        else{

            System.out.println("Patient not found.");
        }
    }

    //update patient
    private static void updatePatient(){

        System.out.println("\n========== UPDATE PATIENT ==========");

        String patientId = readString("Enter Patient ID: ");

        Patient patient = patientService.searchPatientById(patientId);

        if(patient == null){

            System.out.println("Patient not found.");
            return ;
        }

        System.out.println("Enter the corrected patient details for update.");

        String mobile = readString("Enter new mobile number: ");
        String email = readString("Enter new email: ");
        String city = readString("Enter new city: ");
        int age = readInt("Enter new age: ");
        String medicalHistory = readString("Enter medical history: ");

        patientService.updatePatient(patientId, mobile, email, city, age, medicalHistory);
    }

    //delete patient
    private static void deletePatient(){

        System.out.println("\n========== DELETE PATIENT ==========");

        String patientId = readString("Enter Patient ID: ");

        patientService.deletePatient(patientId);
    }

    //==========================================\\
    //         starting doctor management       \\
    //==========================================\\

    private static void doctorMenu(){

        int choice;

        do{

            System.out.println("\n========== DOCTOR MANAGEMENT ==========");

            System.out.println("1. Register Doctor");
            System.out.println("2. View All Doctors");
            System.out.println("3. Search Doctor by ID");
            System.out.println("4. Search Doctor by Specialization");
            System.out.println("5. Update Doctor");
            System.out.println("6. Delete Doctor");
            System.out.println("7. Add Available Time Slot");
            System.out.println("8. Remove Available Time Slot");
            System.out.println("9. Back to Main Menu");

            System.out.println("=======================================");

            choice = readInt("Enter your choice: ");

            switch(choice){

                case 1:
                    registerDoctor();
                    break;

                case 2:
                    doctorService.viewAllDoctors();
                    break;

                case 3:
                    searchDoctor();
                    break;

                case 4:
                    searchDoctorBySpecialization();
                    break;

                case 5:
                    updateDoctor();
                    break;

                case 6:
                    deleteDoctor();
                    break;

                case 7:
                    addDoctorTimeSlot();
                    break;

                case 8:
                    removeDoctorTimeSlot();
                    break;

                case 9:
                    System.out.println("Returning to Main Menu......");

                default:
                    System.out.println("Invalid choice. Please select 1 to 9 only.");
            }
        }

        while(choice != 9);
    }

    //register doctor
    private static void registerDoctor(){

        System.out.println("\n========== REGISTER DOCTOR ==========");

        String doctorId = doctorService.generateDoctorId();

        System.out.println("Generated Doctor ID: " + doctorId);

        String name = readString("Enter doctor name: ");
        String specialization = readString("Enter Specialization: ");
        double fee = readDouble("Enter consultation fee: ");

        Doctor doctor = new Doctor(doctorId, name, specialization, fee);

        doctorService.registerDoctor(doctor);
    }

    //search doctor by id
    private static void searchDoctor(){
        
        System.out.println("\n========== SEARCH DOCTOR ==========");

        String doctorId = readString("Enter Doctor ID: ");

        Doctor doctor = doctorService.searchDoctorById(doctorId);

        if(doctor != null){

            System.out.println("Doctor found");
            doctor.displayDoctorDetails();
        }

        else{

            System.out.println("Doctor not found.");
        }
    }

    //search doctor by specialization
    private static void searchDoctorBySpecialization(){

        System.out.println("\n========== SEARCH BY SPECIALIZATION ==========");

        String specialization = readString("Enter specialization: ");

        doctorService.searchDoctorBySpecialization(specialization);
    }

    //update doctor
    private static void updateDoctor(){

        System.out.println("\n========== UPDATE DOCTOR ==========");

        String doctorId = readString("Enter Doctor ID: ");

        Doctor doctor = doctorService.searchDoctorById(doctorId);

        if(doctor == null){

            System.out.println("Doctor not found.");
            return ;
        }

        String name = readString("Enter new doctor name: ");
        String specialization = readString("Enter new specialization: ");
        double fee = readDouble("Enter new consultation fee: ");

        doctorService.updateDoctor(doctorId, name, specialization, fee);
    }

    //delete doctor
    private static void deleteDoctor(){

        System.out.println("\n========== DELETE DOCTOR ==========");

        String doctorId = readString("Enter Doctor ID: ");

        doctorService.deleteDoctor(doctorId);
    }

    //add doctor time slot
    private static void addDoctorTimeSlot(){

        System.out.println("\n========== ADD AVAILABLE TIME SLOT ==========");

        String doctorId = readString("Enter Doctor ID: ");

        String timeSlot = readString("Enter time slot (YYYY-MM-DD HH:mm): ");

        doctorService.addTimeSlot(doctorId, timeSlot);
    }

    //remove doctor time slot 
    private static void removeDoctorTimeSlot(){

        System.out.println("\n========== REMOVE AVAILABLE TIME SLOT ==========");

        String doctorId = readString("Enter Doctor ID: ");

        String timeSlot = readString("Enter time slot (YYYY-MM-DD HH:mm): ");

        doctorService.removeTimeSlot(doctorId, timeSlot);
    }

    //==========================================\\
    //      starting appoinment management      \\
    //==========================================\\

    private static void appoinmentMenu(){
        
        int choice;

        do{

            System.out.println("\n========== APPOINMENT MANAGEMENT ==========");

            System.out.println("1. Book Appoinment");
            System.out.println("2. View All Appoinments");
            System.out.println("3. Search Appoinment by ID");
            System.out.println("4. View Patient Appoinments");
            System.out.println("5. View Doctor Appoinments");
            System.out.println("6. Cancel Appoinments");
            System.out.println("7. Back to Main Menu");

            System.out.println("===========================================");

            choice = readInt("Enter your choice: ");

            switch(choice){

                case 1:
                    bookAppoinment();
                    break;

                case 2:
                    appointmentService.viewAllAppoinment();
                    break;

                case 3:
                    searchAppoinment();
                    break;

                case 4:
                    viewPatientAppoinments();
                    break;

                case 5:
                    viewDoctorAppoinments();
                    break;

                case 6:
                    cancelAppoinment();
                    break;

                case 7:
                    System.out.println("Returning to Main Manue.....");

                default:
                    System.out.println("Invalid choice. Please select 1 to 7 only.");
            }
        }

        while(choice != 7);
    }

    //book a new appoinment
    private static void bookAppoinment(){

        System.out.println("\n========== BOOK APPOINMENT ==========");

        String patientId = readString("Enter Patient ID: ");

        //check whether patient exists
        if(patientService.searchPatientById(patientId) == null){

            System.out.println("Patient not found. please register first.");
            return ;
        }

        String doctorId = readString("Enter Doctor ID: ");

        //check whether doctor existss
        if(doctorService.searchDoctorById(doctorId) == null){

            System.out.println("Doctor not found.");
            return ;
        }

        LocalDate date = readAppoinmentDate();
        LocalTime time = readAppoinmentTime();

        appointmentService.bookAppoinment(patientId, doctorId, date, time);
    }

    //search appoinment by id
    private static void searchAppoinment(){

        System.out.println("\n========== SEARCH APPOINMENT ==========");

        String appoinmentId = readString("Enter Appoinment ID: ");

        Appoinment appoinment = appointmentService.searchAppoinmentById(appoinmentId);

        if(appoinment != null){

            System.out.println("Appoinment found!");

            appoinment.displayAppoinmentDetails();
        }

        else{

            System.out.println("Appoinment not found.");
        }
    }

    //view appoinment of a patient
    private static void viewPatientAppoinments(){

        System.out.println("\n========== PATIENT APPOINMENTS ==========");

        String patientId = readString("Enter Patient ID: ");

        if(patientService.searchPatientById(patientId) == null){

            System.out.println("Patient not found.");
            return ;
        }

        appointmentService.viewPatientAppoinments(patientId);
    }

    //view appoinments of a doctor
    private static void viewDoctorAppoinments(){

        System.out.println("\n========== DOCTOR APPOINMENTS ==========");

        String doctorId = readString("Enter Doctor ID: ");

        if(doctorService.searchDoctorById(doctorId) == null){

            System.out.println("Doctor not found");
            return ;
        }

        appointmentService.viewDoctorAppoinments(doctorId);
    }
    
    //cancell appoinment
    private static void cancelAppoinment(){

        System.out.println("\n========== CANCEL APPOINMENT ==========");

        String appoinmentId = readString("Enter Appoinment ID: ");

        Appoinment appoinment = appointmentService.searchAppoinmentById(appoinmentId);

        if(appoinment == null){

            System.out.println("Appoinment not found.");
            return ;
        }

        System.out.println("\nAppoinment details.");
        appoinment.displayAppoinmentDetails();

        String confirmation = readString("Are you want to cancel the appoinment? (Y/N): ");

        if(confirmation.equalsIgnoreCase("Y")){

            appointmentService.cancelAppoinment(appoinmentId);
        }
        
        else{

            System.out.println("Appoinment cancellation stopped.");
        }
    }

    //read appoinment date
    private static LocalDate readAppoinmentDate(){

        while(true){

            String date = readString("Enter appoinment date (YYYY-MM-DD): ");

            try{

                return LocalDate.parse(date);
            }

            catch(DateTimeParseException e){
                
                System.out.println("Invalid date. Please use YYYY-MM-DD format.");
            }
        }
    }

    //read appoinment time
    private static LocalTime readAppoinmentTime(){

        while (true){

            String time = readString("Enter appoinment time (HH:mm): ");

            try{

                return LocalTime.parse(time);
            }

            catch(DateTimeParseException e){

                System.out.println("Invalid time. Please use HH:mm format.");
            }
        }
    }

    //===========================================\\
    //        starting waiting list menu         \\
    //===========================================\\

    //diplay waiting lsit menu
    private static void waitingListMenu(){

        int choice;

        do{

            System.out.println("\n========== WAITING LIST MANAGEMENT ==========");

            System.out.println("1. View All Waiting Lists");
            System.out.println("2. View Waiting List by Doctor and Slot");
            System.out.println("3. View Next Waiting Patient");
            System.out.println("4. BAck to Main Menu");

            System.out.println("=============================================");
            choice = readInt("Enter your choice: ");

            switch (choice){

                case 1:
                    viewAllWaitingLists();
                    break;

                case 2:
                    viewWaitingListBySlot();
                    break;

                case 3:
                    viewNextWaitingPatient();
                    break;

                case 4:
                    System.out.println("Returning to Main Menu.....");
                    break;

                default:
                    System.out.println("Invalid choice. Please select 1 to 4 only.");
            }
        }

        while(choice != 4);
    }

    //view all patient list
    private static void viewAllWaitingLists(){

        System.out.println("\n========== ALL WAITING LISTS ==========");

        appointmentService.displayAllWaitingLists();
    }

    //view waiting list by doctor and slot
    private static void viewWaitingListBySlot(){

        System.out.println("\n========== VIEW WAITING LIST ==========");

        String doctorId = readString("Enter Doctor ID: ");

        //check whether doctor exists
        Doctor doctor = doctorService.searchDoctorById(doctorId);

        if(doctor == null){

            System.out.println("Doctor not found.");
            return ;
        }

        LocalDate date = readAppoinmentDate();

        LocalTime time = readAppoinmentTime();

        //display the selected waiting list
        appointmentService.displayWaitingList(doctorId, date, time);
    }

    //view next waiting patient
    private static void viewNextWaitingPatient(){

        System.out.println("\n========== NEXT WAITING PATIENT ==========");

        String doctorId = readString("Enter Doctor ID: ");

        //check whether doctor exists
        Doctor doctor = doctorService.searchDoctorById(doctorId);

        if(doctor == null){

            System.out.println("Doctor not found.");
            return ;
        }

        LocalDate date = readAppoinmentDate();

        LocalTime time = readAppoinmentTime();

        //display next patient without removimg from queue
        appointmentService.diplayNextWaitingPatient(doctorId, date, time);
    }

    //===========================================\\
    //      starting reschedule management      \\
    //==========================================\\

    //diplay reschedule mangement menu
    private static void rescheduleMenu(){

        int choice;

        do{

            System.out.println("\n========== RESCHEDULE MANAGEMENT ==========");

            System.out.println("1. Submit Reschedule Request");
            System.out.println("2. View Pending Reschedule Requests");
            System.out.println("3. Process Reschedule Requests");
            System.out.println("4. Back to Main Menu");

            System.out.println("===========================================");

            choice = readInt("Enter your choice: ");

            switch (choice){

                case 1:
                    submitRescheduleRequest();
                    break;

                case 2:
                    viewPendingRescheduleRequests();
                    break;

                case 3:
                    processRescheduleRequests();
                    break;

                case 4:
                    System.out.println("Returning to Main Menu.......");
                    break;
                
                default:
                    System.out.println("Invalid choice. Please select 1 to 4 only.");
            }
        }

        while(choice != 4);
    }

    //submit reschedule request
    private static void submitRescheduleRequest(){

        System.out.println("\n========== SUBMIT RESCHEDULE REQUEST ==========");

        String appoinmentId = readString("Enter Appoinment ID: ");

        Appoinment appoinment = appointmentService.searchAppoinmentById(appoinmentId);

        if(appoinment == null){

            System.out.println("Appoinment not found.");
            return ;
        }

        //only confirmed appoinments can be reschedule
        if(appoinment.getStatus() != Appoinment.AppoinmentStatus.CONFIRMED){

            System.out.println("Only confirmed appoinments can be reschedule.");
            return ;
        }

        System.out.println("\nCurrent appoinment details.");

        appoinment.displayAppoinmentDetails();

        LocalDate newDate = readAppoinmentDate();

        LocalTime newTime = readAppoinmentTime();

        appointmentService.rescheduleAppoinment(appoinmentId, newDate, newTime);
    }

    //view pending reschedule request
    private static void viewPendingRescheduleRequests(){

        System.out.println("\n========== PENDING RESCHEDULE REQUESTS ==========");

        appointmentService.displayRescheduleQueue();
    }

    //process reschedule request
    private static void processRescheduleRequests(){

        System.out.println("\n========== PROCESS RESCHEDULE REQUESTS ==========");

        String confirmation = readString("Do you want to process pending requests? (Y/N): ");

        if(confirmation.equalsIgnoreCase("Y")){

            appointmentService.processRescheduleRequests();
        }

        else{
            System.out.println("Reschedule processign cancelled.");
        }
    }

    //===========================================\\
    //   starting patient registration history   \\
    //===========================================\\

    //patient history menu
    private static void patientHistoryMenu(){

        int choice;

        do{

            System.out.println("\n========== PATIENT REGISTRATION HISTORY ==========");

            System.out.println("1. View Registration History");
            System.out.println("2. View Latest Registered Patient");
            System.out.println("3. Remove Latest History Entry");
            System.out.println("4. View Stack Size");
            System.out.println("5. Back to MAin Menu");

            System.out.println("==================================================");

            choice = readInt("Enter your choice: ");

            switch(choice){

                case 1:
                    viewPatientHistory();
                    break;

                case 2:
                    viewLatestPatient();
                    break;

                case 3:
                    removeLatestHistoryEntry();
                    break;

                case 4:
                    displayPatientStackSize();
                    break;

                case 5:
                    System.out.println("Returning to Main Menu.......");
                    break;

                default:
                    System.out.println("Invalid choice. Please select 1 to 5 only.");
            }
        }

        while(choice != 5);
    }

    //view patient registration history
    private static void viewPatientHistory(){

        System.out.println("\n========== PATIENT REGISTRATION HISTORY ==========");

        if(patientStack.isEmpty()){

            System.out.println("No patient registration history found.");
            return ;
        }

        //display patient from newest to oldest
        patientStack.displayStack();
    }

    //view latest registered patient
    private static void viewLatestPatient(){

        System.out.println("\n========== LATEST REGISTERED PATIENT ==========");

        if(patientStack.isEmpty()){

            System.out.println("Patient history stack is empty.");
            return ;
        }

        Patient patient = patientStack.peek();

        if(patient != null){

            patient.displayPatientDetails();
        }
    }

    //remove latest history entry
    private static void removeLatestHistoryEntry(){

        System.out.println("\n========== REMOVE LATEST HISTORY ENTRY ==========");

        if(patientStack.isEmpty()){

            System.out.println("Patient history is empty.");
            return ;
        }

        String confirmation = readString("Are you remove the latest patient from the runtime stack? (Y/N): ");

        if(confirmation.equalsIgnoreCase("Y")){

            Patient patient = patientStack.pop();

            if(patient != null){

                System.out.println("Latest history entry removed successfully.");
                patient.displayPatientDetails();
            }
        }

        else{

            System.out.println("Operation cancelled.");
        }
    }

    //display stack size
    private static void displayPatientStackSize(){

        System.out.println("\n========== PATIENT HISTORY STACK SIZE ==========");

        System.out.println("Total patients in history stack: " + patientStack.size());
    }

    //=========================================\\
    //     starting home visit management      \\
    //=========================================\\

    //home visit menu
    private static void homeVisitMenu(){

        int choice;

        do {

            System.out.println("\n========== HOME VISIT MANAGEMENT ==========");

            System.out.println("1. Add Location");
            System.out.println("2. Add Road");
            System.out.println("3. Display All Locations");
            System.out.println("4. Display Road Networks");
            System.out.println("5. Find Shortest Route Using (Dijkstra)");
            System.out.println("6. Find Shortest Distances Using (Floyd-Warshall)");
            System.out.println("7. Display All Shortest Distances");
            System.out.println("8. Back to Main Menu");

            choice = readInt("Enter your choice: ");

            switch(choice){

                case 1:
                    addHomeVisitLocation();
                    break;

                case 2:
                    addHomeVisitRoad();
                    break;

                case 3:
                    homeVisitService.displayAllLocationNames();
                    break;

                case 4:
                    homeVisitService.displayLocations();
                    break;

                case 5:
                    findDijkstraRoute();
                    break;

                case 6:
                    findFloydWarshallDistance();
                    break;

                case 7:
                    homeVisitService.calculateFloyedWarshallRoutes();

                case 8:
                    System.out.println("Returning to Main Menu......");
                    break;

                default:
                    System.out.println("Invalid choice. Please choice 1 to 8 only.");
            }
        }

        while(choice != 8);
    }

    //add home visit location
    private static void addHomeVisitLocation(){

        System.out.println("\n========== ADD lOCATION ==========");

        String location = readString("Enter location name: ");

        if(location.isEmpty()){
            System.out.println("Location cannot be empty.");
            return ;
        }

        homeVisitService.addLocation(location);

        System.out.println("Location operation completed.");
    }

    //add home visit road
    private static void addHomeVisitRoad(){

        System.out.println("\n========== ADD ROAD ==========");

        String source = readString("Enter starting location: ");

        String destination = readString("Enter destination location: ");

        double distance = readDouble("Entere distance in kilometer: ");

        if(distance <= 0){
            System.out.println("Distance must be grater than zero: ");
            return ;
        }

        homeVisitService.addRoad(source, destination, distance);

        System.out.println("Road operation completed");
    }

    //find shortest rout
    private static void findDijkstraRoute(){

        System.out.println("\n========== DIJKSTRA SHORTEST ROUTE ==========");

        String source = readString("Enter starting location: ");

        String destination = readString("Enter destination location");

        if(!homeVisitService.locationExists(source) || !homeVisitService.locationExists(destination)){

            System.out.println("Invalid location. Please check the loaction names.");
            return ;
        }

        homeVisitService.calculateDijkstraRoute(source, destination);
    }

    //find the shortest distance
    private static void findFloydWarshallDistance(){

        System.out.println("\n========== FLOYED-WARSHALL ==========");

        String source = readString("Enter starting loaction: ");

        String destination = readString("Enter destination location: ");

        if(!homeVisitService.locationExists(source) || !homeVisitService.locationExists(destination)){

            System.out.println("Invalid location. please check the location names.");
            return ;
        }

        homeVisitService.displayShortestRoute(source, destination);
    }
    
    //=========================================\\
    //              input method               \\
    //=========================================\\

    //read text input 
    private static String readString(String message){

        System.out.print(message);

        return scanner.nextLine().trim();
    }

    //read integer input
    private static int readInt(String message){

        while(true){
            System.out.print(message);

            String input = scanner.nextLine().trim();

            try{
                return Integer.parseInt(input);
            }

            catch(NumberFormatException e){

                System.out.println("Invalid input. please enter a whole number.");
            }
        }
    }

    //read double input
    private static double readDouble(String message){

        while(true){
            
            System.out.print(message);

            String input = scanner.nextLine().trim();

            try{

                return Double.parseDouble(input);
            }

            catch(NumberFormatException e){

                System.out.println("Invalid input. Please enter a valid amount.");
            }
        }
    }
}
