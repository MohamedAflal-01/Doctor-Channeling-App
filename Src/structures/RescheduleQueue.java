package Src.structures;

import Src.models.RescheduleRequest;

public class RescheduleQueue {
    
    //node class
    private static class Node{

        private RescheduleRequest request;
        private Node next;

        //node constructor
        public Node(RescheduleRequest request){
            this.request = request;
            this.next = null;
        }
    }

    //queue attributes
    private Node front;
    private Node rear;
    private int size;

    //call the constructor
    public RescheduleQueue(){
        front=null;
        rear=null;
        size=0;
    }

    //add request to the rear of the queue
    public void enqueue(RescheduleRequest request){

        if(request == null){
            System.out.println("Cannot add a null request");
            return;
        }

        Node newNode = new Node(request);

        //check whether queue is empty
        if(isEmpty()){
            front = newNode;
            rear = newNode;
        }

        else{
            rear.next = newNode;
            rear = newNode;
        }

        size++;

        System.out.println("Reschedule request added successfully!");
    }

    //remove request from the front
    public RescheduleRequest dequeue(){

        if(isEmpty()){
            System.out.println("Reschedule queue is empty.");
            return null;
        }

        RescheduleRequest removedRequest = front.request;

        front = front.next;

        //reset rear when queue becomes empty
        if(front == null){
            rear = null;
        }

        size--;

        return removedRequest;
    }

    //view the first request without removing it
    public RescheduleRequest peek(){
        if(isEmpty()){
            System.out.println("Reschedule queue is empty.");
            return null;
        }

        return front.request;
    }

    //check whether queue is empty
    public boolean isEmpty(){
        return front==null;
    }

    //get queue size 
    public int size(){
        return size;
    }

    //display all reschedule requests
    public void displayQueue(){
        if(isEmpty()){
            System.out.println("No reschedule requestes available.");
            return;
        }

        System.out.println("\n=========== RESCHEDULE QUEUE ==========");

        Node current = front;

        int position = 1;

        while(current != null){
            System.out.println("\nQueue Position: " + position);

            System.out.println("Request ID: " + current.request.getRequestId());

            System.out.println("Appoinment ID: " + current.request.getAppoinmentId());

            System.out.println("Patient ID: " + current.request.getPatientId());

            System.out.println("Requested Date: " + current.request.getRequestedDate());

            System.out.println("Requested Time: " + current.request.getRequestdTime());

            System.out.println("Request Status: " + current.request.getRequestStatus());

            current = current.next;
            position++;
        }

        System.out.println("\nTotal Requests: " + size);

        System.out.println("======================================");
    }

    //clear the entire queue
    public void clearQueue(){

        front = null;
        rear = null;
        size = 0;

        System.out.println("Reschedule queue cleared successfully!");
    }
}
