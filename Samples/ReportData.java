package reports;

import java.io.*;
import java.time.LocalDate;//for dates para macompare dates
import java.util.ArrayList;//store all bill
import java.time.temporal.WeekFields;
import java.util.Locale;

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
    public void updateRevenueLabels(javax.swing.JLabel dailyL, javax.swing.JLabel monthlyL, javax.swing.JLabel weeklyL, javax.swing.JLabel checkoutL) {
        double dailyTotal = 0;
        double weeklyTotal = 0;
        double monthlyTotal = 0;
        int checkoutsToday = 0;
        
        LocalDate currentDate = LocalDate.now();
        String today = LocalDate.now().toString(); //compare yung bill dates
        String thisMonth = today.substring(0, 7);   

        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int currentWeekNumber = currentDate.get(weekFields.weekOfWeekBasedYear());
        int currentYear = currentDate.getYear();
    
        ArrayList<String[]> bills = getAllBills(); //fetch lahat ng records

        for (String[] d : bills) {//process one by one
            // Your bills.txt: Index 10 is Date, Index 15 is Grand Total
            if (d.length >= 16) { 
                try {
                    String rawDateString = d[10]; 
                    double amount = Double.parseDouble(d[15]);
                      
                    LocalDate fileDate = LocalDate.parse(rawDateString);
                    
                    if (rawDateString.equals(today)) {
                        dailyTotal += amount;
                        checkoutsToday++;
                    }
                    
                    if (rawDateString.startsWith(thisMonth)) {
                        monthlyTotal += amount;
                    }
                    int fileWeekNumber = fileDate.get(weekFields.weekOfWeekBasedYear());
                    if (fileDate.getYear() == currentYear && fileWeekNumber == currentWeekNumber) {
                        weeklyTotal += amount;
                }
                } catch (Exception e) {
                    System.err.println("Skipping row due to error: " + e.getMessage());
                }
            }
        }

        dailyL.setText("₱" + String.format("%.2f", dailyTotal));
        monthlyL.setText("₱" + String.format("%.2f", monthlyTotal));
        weeklyL.setText("₱" + String.format("%.2f", weeklyTotal));
        checkoutL.setText(String.valueOf(checkoutsToday));
    }
}