package Src.structures;

import Src.models.Patient;
public class PatientStack {
    
    private static final int MAX_SIZE = 100;

    //Node class
    private static class Node{

        private Patient patient;
        private Node next;

        public Node(Patient patient){
            this.patient = patient;
            this.next = null;
        }
    }

    //stack attributes
    private Node top;
    private int size;

    //set constructor
    public PatientStack(){
        top = null;
        size = 0;
    }

    //push: add a patient to the top
    public void push(Patient patient){

        //check stack overflow
        if(size >= MAX_SIZE){
            System.out.println("Stack Overflow!");
            System.out.println("Maximum capacity reached. please clean the memory.");
        }

        if(patient == null){
            System.out.println("Invalid patient record.");
            return;
        }

        Node newNode = new Node(patient);

        newNode.next = top;
        top = newNode;

        size++;

        System.out.println("Patient added to stack successfully.");
    }

    //pop: remove and return the top patient
    public Patient pop(){

        //check stack underflow
        if(isEmpty()){
            System.out.println("Stack Underflow!");
            System.out.println("No patient record have to remove.");
            return null;
        }

        Patient removedPatient = top.patient;

        top = top.next;
        size--;

        System.out.println("Patient removed from stack.");

        return removedPatient;
    }

    //peek: return the top patient without removing 
    public Patient peek(){

        if(isEmpty()){
            System.out.println("Stack is empty.");
            return null;
        }

        return top.patient;
    }

    //check whether stack is empty
    public boolean isEmpty(){
        return top == null;
    }

    //return the number of patient
    public int size(){
        return size;
    }

    //display patients from newest to oldest
    public void displayStack(){

        if(isEmpty()){
            System.out.println("No patient records in the stack.");
            return;
        }

        Node current = top;

        System.out.println("\n=========== PATIENT STACK ==========");
        System.out.println("Retrieval Order: Newest to Oldest");

        while(current != null){
            System.out.println("Patient ID: " + current.patient.getPatientId());

            System.out.println("Patient Name: " + current.patient.getName());

            System.out.println("===================================");

            current = current.next;
        }

        System.out.println("Total Patients: " + size);
    }

    //remove all patients from the stack
    public void clearStack(){

        top = null;
        size = 0;

        System.out.println("Patient stack cleard.");
    }

    //check whether stack is full
    public boolean isFull(){
        return size >= MAX_SIZE;
    }
}
