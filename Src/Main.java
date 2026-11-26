package Src;

import java.util.Scanner;

import Src.services.PatientService;
import Src.services.DoctorService;
import Src.services.AppointmentService;
import Src.services.HomeVisitService;

import Src.utils.DataFileManager;

public class Main {
    
    //scanner for user input
    private static Scanner scanner = new Scanner(System.in);

    //service objects
    private static PatientService patientService;
    private static DoctorService doctorService;
    private static AppointmentService appointmentService;
    private static HomeVisitService homeVisitService;

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

        System.out.println("\nMediQueue services initialized successfully.");
    }

    //display main menu
    private static void displayMainMenue(){

        int choice;

        do{

            System.out.println("\n========================================");
            System.out.println("               MAIN MENU");
            System.out.println("========================================");

            System.out.println("1. Patient Management");
            System.out.println("2. Doctor Management");
            System.out.println("3. Appoinment Management");
            System.out.println("4. Reschedule Management");
            System.out.println("5. Home Visit Management");
            System.out.println("6. /patient Sorting");
            System.out.println("7. Patient History");
            System.out.println("8. Exit");

            System.out.println("========================================");

            choice = getMenuChoice();

            switch(choice){
                case 1: 
                    System.out.println("Patient Management Selected.");
                    break;

                case 2: 
                    System.out.println("Doctor Management Selected.");
                    break;

                case 3:
                    System.out.println("Appoinment Managemnt Selected.");
                    break;

                case 4:
                    System.out.println("Reschedule Management Selected.");
                    break;

                case 5:
                    System.out.println("Home Visit Management Selected.");
                    break;

                case 6:
                    System.out.println("Patient Sorting Selected.");
                    break;

                case 7:
                    System.out.println("Patient History Selected.");
                    break;

                case 8:
                    System.out.println("Exiting MediQueue....");
                    break;

                default:
                    System.out.println("Invalid Choice. Please Select 1 to 8 only.");
            }   
        }

        while(choice != 8);
    }

    //get and validate menu choice
    private static int getMenuChoice(){

        System.out.println("Enter your choice: ");

        String input = scanner.nextLine().trim();

        try{

            return Integer.parseInt(input);
        }

        catch(NumberFormatException e){
            return -1;
        }
    }
    
}
