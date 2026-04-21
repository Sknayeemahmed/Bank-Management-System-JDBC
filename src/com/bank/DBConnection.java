package com.bank;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                "jdbc:mysql://localhost:3307/bank_db",
                "root",
                "root"   // replace with your MySQL password
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
