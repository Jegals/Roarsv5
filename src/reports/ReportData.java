package reports;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class ReportData {
    // Exact path to your file
    private final String BILL_FILE = "D:\\Acer\\Documents\\NetBeansProjects\\HotelManagementSystem\\data\\bills.txt"; 

    public ArrayList<String[]> getAllBills() {
        ArrayList<String[]> list = new ArrayList<>();
        File file = new File(BILL_FILE);
        
        if (!file.exists()) {
            System.err.println("File not found at: " + BILL_FILE);
            return list;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    // Split and trim each individual value to remove hidden spaces
                    String[] data = line.split(",");
                    for (int i = 0; i < data.length; i++) {
                        data[i] = data[i].trim();
                    }
                    list.add(data);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return list;
    }
    public void updateRevenueLabels(javax.swing.JLabel dailyL, javax.swing.JLabel monthlyL, javax.swing.JLabel checkoutL) {
        double dailyTotal = 0;
        double monthlyTotal = 0;
        int checkoutsToday = 0;
        
        String today = LocalDate.now().toString(); // "2025-12-18"
        String thisMonth = today.substring(0, 7);   // "2025-12"

        ArrayList<String[]> bills = getAllBills();

        for (String[] d : bills) {
            // Your bills.txt: Index 10 is Date, Index 15 is Grand Total
            if (d.length >= 16) { 
                try {
                    String fileDate = d[10]; 
                    double amount = Double.parseDouble(d[15]);

                    if (fileDate.equals(today)) {
                        dailyTotal += amount;
                        checkoutsToday++;
                    }
                    
                    if (fileDate.startsWith(thisMonth)) {
                        monthlyTotal += amount;
                    }
                } catch (Exception e) {
                    System.err.println("Skipping row due to error: " + e.getMessage());
                }
            }
        }

        dailyL.setText("₱" + String.format("%.2f", dailyTotal));
        monthlyL.setText("₱" + String.format("%.2f", monthlyTotal));
        checkoutL.setText(String.valueOf(checkoutsToday));
    }
}