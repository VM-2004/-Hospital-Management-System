package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {

    private static final String url="jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password ="admin@123";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e ){
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);
        try {

            Connection connection = DriverManager.getConnection(url,username,password);
            patient patient = new patient(connection,scanner);
            Doctor doctor = new Doctor(connection);
            while (true){
                System.out.println(" HOSPITAL MANAGEMENT SYSTEM ");
                System.out.println("1.Add Patients");
                System.out.println("2.View Patients");
                System.out.println("3.View Doctors");
                System.out.println("4.Book Appointment");
                System.out.println("5.Exit");
                System.out.println("Enter your choice :");
                int choice = scanner.nextInt();

                switch (choice){

                    case 1:
                        patient.addPatients();
                        System.out.println();
                        break;

                    case 2:
                        patient.viewPatients();
                        System.out.println();
                        break;

                    case 3:
                        doctor.viewDoctors();
                        System.out.println();
                        break;

                    case 4:
                        bookAppointment(patient,doctor,connection,scanner);
                        System.out.println();
                        break;

                    case 5:
                        return;
                    default:
                        System.out.println("Enter Valid Choice");
                        break;
                }
            }

        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void bookAppointment(patient patient,Doctor doctor ,Connection connection ,Scanner scanner){

        System.out.println("Enter Patient Id : ");
        int patient_id = scanner.nextInt();
        System.out.println("Enter Doctor Id : ");
        int doctor_id = scanner.nextInt();
        System.out.println("Enter Appointment Date (yyyy-mm-dd) : ");
        String appointmentDate = scanner.next();

        if (patient.getPatientById(patient_id) && doctor.getDoctorById(doctor_id)){

            if (checkDoctorAvailability(doctor_id,appointmentDate,connection)){

                String appointmentQuery = " INSERT INTO appointments(patient_id,doctor_id,appointment_date) VALUES(?,?,?)";

                try {

                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1,patient_id);
                    preparedStatement.setInt(2,doctor_id);
                    preparedStatement.setString(3,appointmentDate);

                    int affectedRows = preparedStatement.executeUpdate();
                    if (affectedRows > 0){
                        System.out.println("Appointment Booked");
                    }
                    else {
                        System.out.println("Failed to Book Appointment...!");
                    }

                }
                catch (SQLException e){

                }
            }
            else {
                System.out.println("Doctor not Available on this Date");
            }
        }
        else {
            System.out.println("Either Doctor or Patient not exist..");
        }
    }

    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate,Connection connection){

        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,doctorId);
            preparedStatement.setString(2,appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                int count = resultSet.getInt(1);
                if (count == 0){
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}