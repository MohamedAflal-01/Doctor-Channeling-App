package Src.structures;

import Src.models.Appoinment;

public class PatientQueue {
    
    //node class
    private static class Node{

        private Appoinment appoinment;
        private Node next;

        //node constructor
        public Node(Appoinment appoinment){
            this.appoinment = appoinment;
            this.next =null;
        }
    }

    //queue attributes
    private Node front;
    private Node rear;
    private int size;

    //call the constructor
    public PatientQueue(){
        front = null;
        rear = null;
        size = 0;
    }

    //add appoinment to the rear of the queue
    public void enqueue(Appoinment appoinment){

        if(appoinment == null){
            System.out.println("Cannot add a null appoinment");
            return;
        }

        Node newNode = new Node(appoinment);

        //if queue is empty
        if(isEmpty()){
            front = newNode;
            rear = newNode;
        }

        else{
            rear.next = newNode;
            rear = newNode;
        }

        size++;

        System.out.println("Patient added to waiting quer successfully!");
    }

    //remove appoinment from the front
    public Appoinment dequeue(){
        
        if(isEmpty()){
            System.out.println("Queue is empty.");
            return null;
        }

        Appoinment removedAppoinment = front.appoinment;

        front = front.next;

        //if queue become isEmpty
        if(front == null){
            rear = null;
        }

        size--;

        return removedAppoinment;
    }

    //view the first appoinment without removing it
    public Appoinment peek(){

        if(isEmpty()){
            System.out.println("Queue is empty");
            return null;
        }

        return front.appoinment;
    }

    //check whether the queue is empty
    public boolean isEmpty(){
        return front == null;
    }

    //return queue size
    public int size(){
        return size;
    }

    //display all waiting appoinments
    public void displayQueue(){

        if(isEmpty()){
            System.out.println("No patient in the waiting queue.");
            return;
        }

        System.out.println("\n========== PATIENT WAITING QUEUE ==========");

        Node current = front;

        int position = 1;

        while(current != null){
            System.out.println("\nQueue Position: " + position);

            current.appoinment.displayAppoinmentDetails();

            current = current.next;
            position++;
        }

        System.out.println("Total Waiting Appoinment: " + size);

        System.out.println("===========================================");
    }

    //clear the entire queue
    public void clearQueue(){

        front = null;
        rear = null;
        size =0;

        System.out.println("Waiting queue cleared successfully!");
    }
}
