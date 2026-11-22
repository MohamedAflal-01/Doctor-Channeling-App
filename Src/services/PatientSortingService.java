package Src.services;

import Src.models.Patient;

import java.util.List;
import java.util.ArrayList;

import Src.algorithms.BubbleSort;
import Src.algorithms.QuickSort;

public class PatientSortingService {
    
    //set attributes
    private PatientService patientService;
    private BubbleSort bubbleSort;
    private QuickSort quickSort;

    //set the constructor
    public PatientSortingService(PatientService patientService){

        this.patientService = patientService;

        bubbleSort = new BubbleSort();
        quickSort = new QuickSort();
    }

    //sorting using bubble sort
    public void sortUsingBubbleSort(){

        List<Patient> patients = patientService.getAllPatients();

        ArrayList<Patient> sortedPatients = bubbleSort.sortPatients(patients);

        bubbleSort.displaySortedPatients(sortedPatients);
    }

    //sort using quick sort
    public void sortUsingQuickSort(){

        List<Patient> patients = patientService.getAllPatients();

        ArrayList<Patient> sortedPatients = quickSort.sortPatients(patients);

        quickSort.displaySortedPatients(sortedPatients);
    }
}
