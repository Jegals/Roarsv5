/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hsm;

/**
 *
 * @author Elen Jean Lolo
 */

import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Check_outClass {

    private static final String CUSTOMER_FILE = "customer.txt";
    private static final String HISTORY_FILE  = "records.txt";
    private static final String ROOMS_FILE    = "room.txt";
    private static final String BILL_FILE     = "bills.txt";

    // ================= CUSTOMER MODEL =================
   public static class Customer {
    public String roomNumber, name, mobile, email, roomType, bedType, checkIn, status;
    public double price, extraPersonFee, extraRequestFee, discount; // Added discount

    public Customer(String roomNumber, String name, String mobile, String email, 
                    String roomType, String bedType, double price, String checkIn, 
                    String status, double extraPersonFee, double extraRequestFee, double discount) {
        this.roomNumber = roomNumber;
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.roomType = roomType;
        this.bedType = bedType;
        this.price = price;
        this.checkIn = checkIn;
        this.status = status;
        this.extraPersonFee = extraPersonFee;
        this.extraRequestFee = extraRequestFee;
        this.discount = discount;
    }
}
           
    // ================= LOAD CUSTOMERS =================
    public ArrayList<Customer> loadCustomers() {
        ArrayList<Customer> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CUSTOMER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] d = line.split(",");
                // Inside loadCustomers() loop
            if (d.length >= 23) {
                list.add(new Customer(
                  d[0].trim(),  
                   d[1].trim(),  
                     d[2].trim(),  
                     d[3].trim(),  
                    d[7].trim(),  
                     d[8].trim(),  
                     Double.parseDouble(d[9]), 
                    d[10].trim(), 
                    d[22].trim(), 
                     Double.parseDouble(d[12]), // Extra Person Fee
                 0.0,                       // Extra Request Fee (place at correct index if available)
                         Double.parseDouble(d[20].equals("null") ? "0" : d[20]) // Discount/Credit from index 20
    ));
}
            }
        } catch (Exception e) {
            System.err.println("Error loading customers: " + e.getMessage());
        }
        return list;
    }

    // ================= SEARCH BY ROOM =================
    public Customer searchCustomerByRoom(String roomNumber) {
        for (Customer c : loadCustomers()) {
            if (c.roomNumber.equals(roomNumber) && c.status.equalsIgnoreCase("CheckedIn")) {
                return c;
            }
        }
        return null;
    }

    // ================= CHECKOUT LOGIC =================
    // Added "double total" at the end of the parameters
public boolean checkoutCustomer(String roomNumber, double extraPerson, double extraRequest, double discount, double total) {
    ArrayList<String> updatedLines = new ArrayList<>();
    boolean success = false;

    try (BufferedReader br = new BufferedReader(new FileReader(CUSTOMER_FILE))) {
        String line;
        while ((line = br.readLine()) != null) {
            String[] d = line.split(",");
            if (d.length >= 23 && d[0].trim().equals(roomNumber.trim()) && d[22].trim().equals("CheckedIn")) {
                
                // We don't need to recalculate 'total' here because it's passed from JFrame
                long days = ChronoUnit.DAYS.between(LocalDate.parse(d[10]), LocalDate.now());
                if (days <= 0) days = 1;
                double roomCharge = Double.parseDouble(d[9]) * days;

                // 2. Save to History and Bills
                saveHistory(d);
                saveBill(d, days, roomCharge, extraPerson, extraRequest, discount, total);

                // 3. Update Status to CheckedOut
                d[22] = "CheckedOut";
                updatedLines.add(String.join(",", d));
                success = true;
            } else {
                updatedLines.add(line);
            }
        }
    } catch (Exception e) {
        return false;
    }

    if (success) {
        writeBackFile(CUSTOMER_FILE, updatedLines);
        updateRoomStatus(roomNumber, "Available");
    }
    return success;
}
    public boolean isRoomValid(String roomNumber) {
    if (roomNumber == null || roomNumber.trim().isEmpty()) return false;

    try (BufferedReader br = new BufferedReader(new FileReader(ROOMS_FILE))) {
        String line;
        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            
            String[] d = line.split(",");
            // Check index 0 for the Room Number
            if (d.length >= 1 && d[0].trim().equals(roomNumber.trim())) {
                return true; 
            }
        }
    } catch (IOException e) {
        System.err.println("Error reading room.txt: " + e.getMessage());
    }
    return false;
}
    
    private void updateRoomStatus(String roomNumber, String status) {
        ArrayList<String> roomLines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ROOMS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] d = line.split(",");
                if (d.length >= 1 && d[0].trim().equals(roomNumber.trim())) {
                    if (d.length > 5) d[5] = status; // Assuming index 5 is status in room.txt
                    line = String.join(",", d);
                }
                roomLines.add(line);
            }
        } catch (Exception e) { }
        writeBackFile(ROOMS_FILE, roomLines);
    }

    private void saveBill(String[] d, long days, double rc, double ep, double er, double disc, double total) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(BILL_FILE, true))) {
            pw.println(d[1] + "," + d[0] + "," + days + "," + rc + "," + ep + "," + er + "," + disc + "," + total + "," + LocalDate.now());
        } catch (Exception e) { }
    }

    private void saveHistory(String[] d) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(HISTORY_FILE, true))) {
            pw.println(String.join(",", d) + "," + LocalDate.now());
        } catch (Exception e) { }
    }

    private void writeBackFile(String path, ArrayList<String> lines) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            for (String l : lines) pw.println(l);
        } catch (Exception e) { }
    }
}