package Src.algorithms;

import java.util.ArrayList;
import java.util.List;

import Src.models.Patient;

public class BubbleSort {
    
    //sort patient by age in ascending order
    public ArrayList<Patient> sortPatients(List<Patient> patients){

        //create a seperate collection
        ArrayList<Patient> sortedPatients = new ArrayList<>(patients);

        int n = sortedPatients.size();

        //bubble sort algorithm
        for(int i=0; i<n-1; i++){

            boolean swapped = false;

            //compare neighbouring patients
            for(int j=0; j<n-i-1; j++){

                int currentAge = sortedPatients.get(j).getAge();

                int nextAge = sortedPatients.get(j + 1).getAge();

                //swap when the current age is greater
                if(currentAge > nextAge){

                    Patient temp = sortedPatients.get(j);

                    sortedPatients.set(j, sortedPatients.get(j+1));

                    sortedPatients.set(j+1, temp);

                    swapped = true;
                }
            }

            //stopp if no swaps occurred
            if(!swapped){
                break;
            }
        }

        return sortedPatients;
    }

    //display sorted patient records
    public void displaySortedPatients(List<Patient> patients){

        if(patients == null || patients.isEmpty()){
            System.out.println("No patient records available.");
            return;
        }

        System.out.println("\n========== BUBBLE SORT - PATIENTS ==========");
        System.out.println("Age: Ascending Order");
        System.out.println("============================================");

        for (Patient patient : patients){

            System.out.println("Patient ID: " + patient.getPatientId());

            System.out.println("Patient NAme: " + patient.getName());

            System.out.println("Patient Age: " + patient.getAge());

            System.out.println("============================================");
        }
    }
}
