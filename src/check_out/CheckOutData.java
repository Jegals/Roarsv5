/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package check_out;

/**
 *
 * @author Elen Jean Lolo
 */

import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class CheckOutData {

    private static final String CUSTOMER_FILE = "data/customers.txt";
    private static final String HISTORY_FILE  = "data/records.txt";
    private static final String ROOMS_FILE    = "data/rooms.txt";
    private static final String BILL_FILE     = "D:\\Acer\\Documents\\NetBeansProjects\\HotelManagementSystem\\data\\bills.txt";

    // ================= CUSTOMER MODEL =================
   public static class Customer {
    public String roomNumber, name, mobile, email, roomType, bedType, checkIn, status;
    
    public double price, extraPersonFee, extraRequestFee, discount; // Added discount
    
    public boolean isSenior,isChild;
    public String promoCode;
    
    public Customer(String roomNumber, String name, String mobile, String email, 
                    String roomType, String bedType, double price, String checkIn, 
                    String status, double extraPersonFee, double extraRequestFee, double discount,
                    boolean isSenior, boolean isChild, String promoCode) {
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
        this.isSenior = isSenior;
        this.isChild = isChild;
        this.promoCode = promoCode;
    }
}
           
    // ================= LOAD CUSTOMERS =================
public java.util.ArrayList<Customer> loadCustomers() {
        java.util.ArrayList<Customer> list = new java.util.ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CUSTOMER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] d = line.split(",");
                
                // YOUR FILE HAS EXACTLY 23 COLUMNS
                if (d.length >= 23) {
                    
                    // --- 1. BUILD REQUEST TEXT (Indices 13, 14, 15, 16) ---
                    double reqFee = 0;
                    
                    double roomPrice = Double.parseDouble(d[9]);
                    double extraPerson = Double.parseDouble(d[12]);

                    if (Boolean.parseBoolean(d[13])) { reqFee += 500; } // extra bed
                    if (Boolean.parseBoolean(d[14])) { reqFee += 200; } // comforter
                    if (Boolean.parseBoolean(d[15])) { reqFee += 100; } // pillow
                    if (Boolean.parseBoolean(d[16])) { reqFee += 300; } // food
                    
                    
                    double grossTotal = roomPrice + extraPerson + reqFee;
                    double runningTotal = grossTotal;
                    
                    boolean isSenior = Boolean.parseBoolean(d[17]);
                    boolean isChild = Boolean.parseBoolean(d[18]);
                    String promo = d[19];
                    
                    // Apply Logic: Senior 20%
                    if (isSenior) {
                        runningTotal = runningTotal * 0.80;
                    }
                    // Apply Logic: Child 10%
                    if (isChild) {
                        runningTotal = runningTotal * 0.90;
                    }
                    // Apply Logic: Promo -100
                    if (promo.trim().equals("PROMO100")) {
                        runningTotal = runningTotal - 100;
                    }

                    // Total Discount = Gross - What they actually pay
                    double calculatedDiscount = grossTotal - runningTotal;
                    if (calculatedDiscount < 0) calculatedDiscount = 0;
                    
                    // --- 2. CREATE CUSTOMER ---
                    list.add(new Customer(
                        d[0].trim(),  // Room Number (Index 0)
                        d[1].trim(),  // Name
                        d[2].trim(),  // Mobile
                        d[3].trim(),  // Email
                        d[7].trim(),  // Room Type
                        d[8].trim(),  // Bed Type
                        Double.parseDouble(d[9]),  // Price
                        d[10].trim(), // Check-In Date
                        d[22].trim(), // Status (Index 22 - The Last One)
                        Double.parseDouble(d[12]), // Extra Person Fee (Index 12)
                        reqFee,       // Calculated Request Fee
                        calculatedDiscount, // Discount (Index 20)
                        isSenior, isChild, promo
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading customers: " + e.getMessage());
        }
        return list;
    }


    
    public Customer searchCustomerByRoom(String roomNumber) {
        for(Customer c : loadCustomers()) {
            // check room number and status
            if(c.roomNumber.equalsIgnoreCase(roomNumber) && c.status.equalsIgnoreCase("CheckedIn")) {
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
            
            if (d.length >= 22 && d[0].trim().equals(roomNumber.trim()) && d[22].trim().equals("CheckedIn")) {
                
                // We don't need to recalculate 'total' here because it's passed from JFrame
                long days = ChronoUnit.DAYS.between(LocalDate.parse(d[10]), LocalDate.now());
                if (days <= 0) days = 1;
                double roomCharge = Double.parseDouble(d[9]) * days;

               
               

                // 3. Update Status to CheckedOut
                d[21] = LocalDate.now().toString();// checkout date
                d[22] = "CheckedOut"; // status
                
                 // 2. Save to History and Bills
                saveHistory(d);              
                saveBill(d, days, roomCharge, extraPerson, extraRequest, discount, total);
                
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

    private void saveBill(String[] d, long days, double rc, double ep, double er, double disc, double total) {
       try (PrintWriter pw = new PrintWriter(new FileWriter(BILL_FILE, true))) {
            
            // Format: Name, Room, CheckIn, CheckOut, Total, Mobile, Email, Type, Bed, Price, Days, RoomTotal, ExtraFees, Discount
            
            String line = "BILL-" + (System.currentTimeMillis() % 100000) + "," +
                          LocalDate.now() + "," +// (Bill Id)
                          d[1] + "," +           // 0: Name
                          d[2] + "," +           // 5: Mobile
                          d[3] + "," +           // 6: Email
                          d[0] + "," +           // 1: Room
                          d[7] + "," +           // 7: Room Type
                          d[8] + "," +           // 8: Bed Type
                          d[9] + "," +           // 9: Price per night
                          d[10] + "," +          // 2: Check-In
                          LocalDate.now() + "," + // 3: Check-Out Date
                          days + "," +           // 10: Total Days
                          rc + "," +             // 11: Room Charges Total
                         (ep + er) + "," +      // 12: Total Extra Fees (Person + Requests)
                          disc + "," +                  // 13: Discount Amount
                          total + "," +          // 4: Grand Total
                          "Cash";
                        
                          
                          
                          

            pw.println(line);
            
        } catch (Exception e) { 
            e.printStackTrace();
        }
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
                if (d.length >= 0 && d[0].trim().equals(roomNumber.trim())) {
                    if (d.length > 5) d[5] = status; // Assuming index 5 is status in room.txt
                    line = String.join(",", d);
                }
                roomLines.add(line);
            }
        } catch (Exception e) { }
        writeBackFile(ROOMS_FILE, roomLines);
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