package reports;

import java.io.*;
import java.time.LocalDate;//for dates para macompare dates
import java.util.ArrayList;//store all bill

public class ReportData {
    // Exact path to your file
    private final String BILL_FILE = "D:\\Acer\\Documents\\NetBeansProjects\\HotelManagementSystem\\data\\bills.txt"; 

    public ArrayList<String[]> getAllBills() {//read yung datas
        ArrayList<String[]> list = new ArrayList<>();//empty list to store yung records
        File file = new File(BILL_FILE);
        
        if (!file.exists()) {
            System.err.println("File not found at: " + BILL_FILE);
            return list;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {//read each line
                line = line.trim();//remove yung spaces
                if (!line.isEmpty()) {
                    // Split and trim each individual value to remove spaces
                    String[] data = line.split(",");
                    for (int i = 0; i < data.length; i++) {
                        data[i] = data[i].trim();//remove yung apces
                    }
                    list.add(data);//store yung bill sa list
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
        
        String today = LocalDate.now().toString(); //compare yung bill dates
        String thisMonth = today.substring(0, 7);   

        ArrayList<String[]> bills = getAllBills(); //fetch lahat ng records

        for (String[] d : bills) {//process one by one
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