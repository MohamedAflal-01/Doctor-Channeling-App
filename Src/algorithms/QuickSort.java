package Src.algorithms;

import java.util.ArrayList;
import java.util.List;

import Src.models.Patient;

public class QuickSort {
    
    //sort patients by age in ascending order
    public ArrayList<Patient> sortPatients(List<Patient> patients){

        //create a seperate collection
        ArrayList<Patient> sortedPatients = new ArrayList<>(patients);

        if(!sortedPatients.isEmpty()){
            quickSort(sortedPatients, 0, sortedPatients.size() - 1);
        }

        return sortedPatients;
    }

    //quick sort recursive method
    private void quickSort(ArrayList<Patient> patients, int low, int high){

        if(low < high){
            int pivotIndex = partition(patients, low, high);

            //sort left partition
            quickSort(patients, low, pivotIndex -1);

            //sort right partition
            quickSort(patients, pivotIndex +1, high);
        }
    }

    //partition method 
    private int partition(ArrayList<Patient> patients, int low, int high){

        //select the last patient as pivot
        int pivot = patients.get(high).getAge();

        int i = low-1;

        for(int j=low; j<high; j++){

            if(patients.get(j).getAge() <= pivot){
                i++;

                //swap patients
                Patient temp = patients.get(i);

                patients.set(i, patients.get(j));
                patients.set(j, temp);
            }
        }

        //place pivot in the correct position
        Patient temp = patients.get(i+1);

        patients.set(i+1, patients.get(high));
        patients.set(high, temp);

        return i+1;
    }

    //display sorted patients
    public void displaySortedPatients(List<Patient> patients){

        if(patients == null || patients.isEmpty()){
            System.out.println("No patient records available.");
            return;
        }

        System.out.println("========== QUICK SORT - PATIENTS ==========");

        System.out.println("Age: Ascending Order");

        for(Patient patient : patients){

            System.out.println("Patient ID: " + patient.getPatientId());

            System.out.println("Patient Name: " + patient.getName());

            System.out.println("Patient Age: " + patient.getAge());

            System.out.println("===========================================");
        }
    }
}
