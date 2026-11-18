package Src;

import Src.models.Patient;

public class PatientTest {
    public static void main(String[] args) {

        // Create patient object
        Patient patient = new Patient(
            "P001",
            "Mohamed Aflal",
            "0771234567",
            "aflal@gmail.com",
            "Kalmunai",
            25,
            "None"
        );

        // Display patient details
        patient.displayPatientDetails();

        // Update patient email
        patient.setemail("newemail@gmail.com");

        // Display updated email
        System.out.println(
            "Updated Email: " + patient.getEmail()
        );
    }

}
