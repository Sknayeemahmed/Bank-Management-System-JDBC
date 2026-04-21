package com.bank;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Scanner;

public class BankManagementApp {

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);

		while (true) {
			System.out.println("\n1. Register");
			System.out.println("2. Login");
			System.out.println("3. Exit");

			int choice = sc.nextInt();
			sc.nextLine();

			switch (choice) {
			case 1:
				register();
				break;
			case 2:
				login();
				break;
			case 3:
				System.out.println("Thank you for using Bank System!");
				System.exit(0);
			default:
				System.out.println("Invalid choice");
			}
		}
	}

	public static void register() {
		Scanner sc = new Scanner(System.in);

		System.out.print("Enter name: ");
		String name = sc.nextLine();

		System.out.print("Enter email: ");
		String email = sc.nextLine();

		try {
			Connection conn = DBConnection.getConnection();

			String query = "INSERT INTO users (name, email) VALUES (?, ?)";
			PreparedStatement ps = conn.prepareStatement(query);

			ps.setString(1, name);
			ps.setString(2, email);

			int rows = ps.executeUpdate();

			if (rows > 0) {
				System.out.println("User Registered in Database!");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void login() {
		Scanner sc = new Scanner(System.in);

		System.out.print("Enter email: ");
		String email = sc.nextLine();

		try {
			Connection conn = DBConnection.getConnection();

			String query = "SELECT * FROM users WHERE email = ?";
			PreparedStatement ps = conn.prepareStatement(query);

			ps.setString(1, email);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				System.out.println("Login Successful!");
				System.out.println("Welcome, " + rs.getString("name"));

				int userId = rs.getInt("user_id");

				createAccount(userId);

				while (true) {
					System.out.println("\n1. Deposit");
					System.out.println("2. Withdraw");
					System.out.println("3. Transactions");
					System.out.println("4. Logout");
					int choice = sc.nextInt();

					switch (choice) {
					case 1:
					    deposit(userId);
					    break;
					case 2:
					    withdraw(userId);
					    break;
					case 3:
					    viewTransactions(userId);
					    break;
					case 4:
					    return;
					default:
						System.out.println("Invalid choice");
					}
				}

			} else {
				System.out.println("User not found!");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void createAccount(int userId) {

		try {
			Connection conn = DBConnection.getConnection();

			String checkQuery = "SELECT * FROM accounts WHERE user_id = ?";
			PreparedStatement checkPs = conn.prepareStatement(checkQuery);
			checkPs.setInt(1, userId);

			ResultSet rs = checkPs.executeQuery();

			if (rs.next()) {
				return;
			}

			String query = "INSERT INTO accounts (user_id, balance) VALUES (?, 0)";
			PreparedStatement ps = conn.prepareStatement(query);

			ps.setInt(1, userId);

			int rows = ps.executeUpdate();

			if (rows > 0) {
				System.out.println("Bank Account Created Successfully!");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	public static void deposit(int userId) {
	    Scanner sc = new Scanner(System.in);

	    System.out.print("Enter amount to deposit: ");
	    double amount = sc.nextDouble();

	    if (amount <= 0) {
	        System.out.println("Invalid amount!");
	        return;
	    }

	    try {
	        Connection conn = DBConnection.getConnection();

	        String query = "UPDATE accounts SET balance = balance + ? WHERE user_id = ?";
	        PreparedStatement ps = conn.prepareStatement(query);

	        ps.setDouble(1, amount);
	        ps.setInt(2, userId);

	        int rows = ps.executeUpdate();

	        if (rows > 0) {
	            String insertTxn = "INSERT INTO transactions (user_id, type, amount) VALUES (?, 'DEPOSIT', ?)";
	            PreparedStatement txnPs = conn.prepareStatement(insertTxn);

	            txnPs.setInt(1, userId);
	            txnPs.setDouble(2, amount);
	            txnPs.executeUpdate();

	            System.out.println("Amount Deposited Successfully!");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	public static void withdraw(int userId) {
	    Scanner sc = new Scanner(System.in);

	    System.out.print("Enter amount to withdraw: ");
	    double amount = sc.nextDouble();

	    if (amount <= 0) {
	        System.out.println("Invalid amount!");
	        return;
	    }

	    try {
	        Connection conn = DBConnection.getConnection();

	        String checkQuery = "SELECT balance FROM accounts WHERE user_id = ?";
	        PreparedStatement checkPs = conn.prepareStatement(checkQuery);
	        checkPs.setInt(1, userId);

	        ResultSet rs = checkPs.executeQuery();

	        if (rs.next()) {
	            double balance = rs.getDouble("balance");

	            if (balance >= amount) {
	                String updateQuery = "UPDATE accounts SET balance = balance - ? WHERE user_id = ?";
	                PreparedStatement ps = conn.prepareStatement(updateQuery);

	                ps.setDouble(1, amount);
	                ps.setInt(2, userId);

	                ps.executeUpdate();

	                String insertTxn = "INSERT INTO transactions (user_id, type, amount) VALUES (?, 'WITHDRAW', ?)";
	                PreparedStatement txnPs = conn.prepareStatement(insertTxn);

	                txnPs.setInt(1, userId);
	                txnPs.setDouble(2, amount);
	                txnPs.executeUpdate();

	                System.out.println("Withdrawal Successful!");
	            } else {
	                System.out.println("Insufficient Balance!");
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	public static void viewTransactions(int userId) {

	    try {
	        Connection conn = DBConnection.getConnection();

	        String query = "SELECT * FROM transactions WHERE user_id = ?";
	        PreparedStatement ps = conn.prepareStatement(query);

	        ps.setInt(1, userId);

	        ResultSet rs = ps.executeQuery();

	        System.out.println("\n--- Transaction History ---");

	        while (rs.next()) {
	            System.out.println(
	                rs.getInt("transaction_id") + " | " +
	                rs.getString("type") + " | " +
	                rs.getDouble("amount") + " | " +
	                rs.getTimestamp("date")
	            );
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}
//
//	public static void main(String[] args) {
//
//	    Connection conn = DBConnection.getConnection();
//
//	    if (conn != null) {
//	        System.out.println("Connected to Database!");
//	    } else {
//	        System.out.println("Connection Failed!");
//	    }
//	}


