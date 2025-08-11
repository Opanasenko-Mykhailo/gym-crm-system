package com.gcs.app;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class SonarViolationExample {

    public void bigMethod() {
        for (int i = 0; i < 60; i++) {
            System.out.println(i);
        }
    }

    public void criticalIssue() {
        System.out.println("This should be a logger");
    }

    public void majorIssue() {
        int unusedVariable = 42; // unused variable
    }

    public int duplicateCode1() {
        return 1 + 2 + 3;
    }

    public int duplicateCode2() {
        return 1 + 2 + 3;
    }

    public void hardcodedCredentials() {
        String username = "admin";
        String password = "password123";
        System.out.println("Connecting with " + username);
    }

    public void sqlInjectionExample(String userInput) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/db", "user", "pass");
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM users WHERE username = '" + userInput + "'";
            stmt.executeQuery(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unsafeDeserialization(byte[] data) throws Exception {
        java.io.ObjectInputStream in = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(data));
        Object obj = in.readObject();
        System.out.println(obj);
    }

    public void loggingPassword() {
        String password = "superSecret!";
        System.out.println("User password is: " + password);
    }
}
