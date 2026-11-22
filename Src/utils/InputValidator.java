package Src.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class InputValidator {
    
    //private constructor too prevent object creation
    private InputValidator(){

    }

    //validate empty input
    public static boolean isNotEmpty(String input){
        return input != null && !input.trim().isEmpty();
    }

    //validate patient name
    public static boolean isValidName(String name){
        return name != null && name.trim().matches("[a-zA-Z .'-]+");
    }

    //validate mobile number
    public static boolean isValidMobileNumber(String mobile){
        return mobile != null && mobile.matches("\\d{10}");
    }

    //validate email address
    public static boolean isValidEmail(String email){
        return email != null && email.matches("^[A-Za-z0-9+_.-] +@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    //validate patient age
    public static boolean isValidAge(int age){

        return age>0 && age<=120;
    }

    //validate consultation fee
    public static boolean isValidFee(double fee){

        return fee >= 0 && Double.isFinite(fee);
    }

    //validate date formate
    public static boolean isValidDate(String date){

        try{

            LocalDate.parse(date);
            return true;
        }

        catch(DateTimeParseException e){

            return false;
        }
    }

    //validate time formate
    public static boolean isValidTime(String time){
        try{

            LocalTime.parse(time);
            return true;
        }

        catch(DateTimeParseException e){

            return false;
        }
    }

    //validate numeric input
    public static boolean isNumeric(String input){

        if(input == null || input.trim().isEmpty()){
            return false;
        }

        try{

            Double.parseDouble(input);
            return true;
        }

        catch(NumberFormatException e){

            return false;
        }
    }

    //validate positive integer
    public static boolean isPositiveInteger(String input){

        if(input == null || input.trim().isEmpty()){
            return false;
        }

        try{

            int number = Integer.parseInt(input);
            return number > 0;
        }
        
        catch(NumberFormatException e){

            return false;
        }
    }

    //validate password length
    public static boolean isValidPassword(String password){

        return password != null && password.length() >= 8;
    }
}
