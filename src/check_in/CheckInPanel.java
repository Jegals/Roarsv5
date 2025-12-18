package check_in;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */


/**
 *
 * @author Acer
 */

import java.util.ArrayList;
import javax.swing.JOptionPane;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;


public class CheckInPanel extends javax.swing.JPanel {

    /**
     * Creates new form checkInPanel
     */
    
    java.util.ArrayList<CheckInData> customerDatabase = new java.util.ArrayList<>();
    
    java.util.ArrayList<String[]> roomList = new java.util.ArrayList<>();
    
    String oldName = ""; // remember who we are editing
    
    public CheckInPanel() {
        initComponents();
        setupRooms();
        
        loadData();
    }
    
    boolean isPromoMessageShown = false;
    
    // Setup Hotel Rooms
    public void setupRooms() {
        
        // Clear the Existing Items (yung words na default ng Combo Box)
        roomList.clear();
        roomNumberComboBox.removeAllItems();
        roomNumberComboBox.addItem("Select Room");
        try {
            java.io.File file = new java.io.File("data/rooms.txt");
            
            // check if file exists to avaoid crash
            if(!file.exists()) {
                System.out.println("rooms.txt not found in data folder");
                return;
            }
            
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file));
            String line;
            
            // Read the file line by line 
            while((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                
                // we expect 6 columns room num, type, bed, max, price, status
                if(parts.length >= 6){
                   String roomNum = parts[0];
                   String type = parts[1];
                   String bed = parts[2];
                   //parts[3] maximum people - (hindi siya autofill)
                   String price = parts[4];
                   String status = parts[5].trim(); // remove spaces
                   
                   if(status.equalsIgnoreCase("Available")) {
                       roomNumberComboBox.addItem(roomNum);
                       
                       // add to RAM List (so the auto fill code works)
                       roomList.add(new String[]{roomNum, type, bed, price});
                   }
                }
            }
            reader.close();
        } catch (Exception e){
            System.out.println("Error loading rooms: " + e.getMessage());
        }
    }
    
    private void updateRoomStatus(String roomNumberToBook) {
        java.util.ArrayList<String> fileContent = new java.util.ArrayList<>();
        java.io.File file = new java.io.File("data/rooms.txt");
        
        // read and modify the specific lien
        try {
            if(!file.exists()) {
                System.out.println("rooms.txt does not exist.");
                return;
            }
            
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file));
            String line;
            
            while((line = reader.readLine()) != null) {
                //split by comma (change to " " if your file uses spaces
                String[] parts = line.split(",");
                
                if(parts.length >= 6) {
                    String currentRoomNum = parts[0];
                    
                    if (currentRoomNum.equals(roomNumberToBook)) {
                        // reconstruct the line with "occupied" at the end
                        // format: num,type,bed,max,price,status
                        String newLine = parts[0] + "," + parts[1] + "," + parts[2] + "," +
                                         parts[3] + "," + parts[4] + "," + "Occupied";
                        fileContent.add(newLine);
                    } else {
                        //keep other rooms exactly as they are
                        fileContent.add(line);
                    }                   
                } else {
                    fileContent.add(line); // CRITICAL: Keep existing lines even if they look weird
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this, "Error Rading Rooms: " + e.getMessage());
            return;
        }
        //write the update list back ti the file
        try {
            java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(file));
            for(String str : fileContent){
                writer.write(str);
                writer.newLine();
            }
            writer.close();
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error Updating Rooms Status: " + e.getMessage());
        }
    }   
    private void calculateTotal() {
        try{
            String priceText = priceField.getText();
            if(priceText.isEmpty()) return;
            
            double total = Double.parseDouble(priceText);
            
            // add extra charges
            if(extraBedCheckBox.isSelected()) total += 500;
            if(extraComforterCheckBox.isSelected()) total += 200;
            if(extraPillowCheckBox.isSelected()) total += 100;
            if(foodServiceCheckBox.isSelected()) total += 300;
            
           // Add the Additional Person Fee from the text box
           String addFeeText = additionalPerFeeField.getText();
           if(!addFeeText.isEmpty()) {
               // user typed "500", we add 500 to the total
               total += Double.parseDouble(addFeeText);
            }
            
            //discounts
            if(seniorDisCheckBox.isSelected()) total = total * 0.80; // 20% off
            if(childDisCheckBox.isSelected()) total = total * 0.90; // 10% off
            
            // promo code
            String code = txtPromo.getText().trim();
            
            if(txtPromo.getText().equals("PROMO100")) {
                total -= 100; // deduct 100 pesos
                
            if(!isPromoMessageShown) {
                JOptionPane.showMessageDialog(null, "Promo Applied: -100");
                isPromoMessageShown = true;
            }
            } else {
                isPromoMessageShown = false;
            }
            
            totalChargeField.setText(String.valueOf(total));
            
        } catch (Exception e) {
            // ignore errors while typing
        }
    }
    
    // save to text file
    private void saveData() {
        try {
            java.io.File file = new java.io.File("data/customers.txt");
            java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(file));
            
            for(int i = 0; i < customerDatabase.size(); i++){
                CheckInData guest = customerDatabase.get(i);
                
                // format
                String line = guest.getRoomNumber() + "," +
                              guest.getName() + "," +
                              guest.getMobile() + "," +
                              guest.getEmail() + "," +
                              guest.getGender() + "," +
                              guest.getNationality() + "," +
                              guest.getAddress() + "," +
                        
                               
                              guest.getRoomType() + "," +
                              guest.getBedType() + "," +
                              guest.getRoomPrice() + "," +
                              guest.getCheckInDate() + "," +
                              guest.getPersons() + "," +
                        
                              guest.getAdditionalFee() + "," +
                              // Booleans
                              // Extra Request
                              guest.isExtraBed() + "," +
                              guest.isExtraComforter() + "," +
                              guest.isExtraPillow() + "," +
                              guest.isFoodService() + "," +
                              guest.isSeniorDiscount() + "," +
                              guest.isChildDiscount() + "," +
                              guest.getPromoCode() + "," +
                              // New Strings
                              
                              guest.getTotalCharge() + "," +
                              "null" + "," + // place holder for checkout date
                              "CheckedIn"; // initial status
                
                writer.write(line);
                writer.newLine(); // move to next line
            }
            
            writer.close(); // save and close the file
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error Saving: " + e.getMessage());
        }
    }
    
    // lod from the text file
    private void loadData() {
        try {
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader("data/customers.txt"));
            String line;
            
            // clear list first so we dont double copy
            customerDatabase.clear();
            
            // read line by liine
            while ((line = reader.readLine()) != null) {
                
                //split the line by comma
                String[] parts = line.split(","); // why we do this
                
                // we expect 5 parts; name, mobile, room, date, price
                if(parts.length >= 21) {
                    
                    String room = parts[0];
                    
                    String name = parts[1];
                    String mobile = parts[2];
                    String email = parts[3];
                    String gender = parts[4];
                    String nationality = parts[5];
                    String address = parts[6];
                                        
                    
                    String rType = parts[7];
                    String bType = parts[8];
                    double rPrice = Double.parseDouble(parts[9]);
                    String date = parts[10];
                    int persons = Integer.parseInt(parts[11]);
                    String addFee = parts[12];
                    
                    boolean exBed = Boolean.parseBoolean(parts[13]);
                    boolean exComf = Boolean.parseBoolean(parts[14]);
                    boolean exPillow = Boolean.parseBoolean(parts[15]);
                    boolean food = Boolean.parseBoolean(parts[16]);
                    boolean senior = Boolean.parseBoolean(parts[17]);
                    boolean child = Boolean.parseBoolean(parts[18]);
                    String promo = parts[19];
                    double total = Double.parseDouble(parts[20]); // total price
 
                    // create object and add to Ram list
                    CheckInData guest = new CheckInData(name, mobile, email, gender, nationality, address, 
                                                        room, rType, bType, rPrice, date, persons, addFee,
                                                        exBed, exComf, exPillow, food, senior, child, promo, total);
                    customerDatabase.add(guest);
                }
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("No database file found yet.");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        checkIn = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        fullName = new javax.swing.JLabel();
        fullNameField = new javax.swing.JTextField();
        mobileNumber = new javax.swing.JLabel();
        mobileNumberField = new javax.swing.JTextField();
        emailAddress = new javax.swing.JLabel();
        emailAddressField = new javax.swing.JTextField();
        gender = new javax.swing.JLabel();
        maleButton = new javax.swing.JRadioButton();
        femaleButton = new javax.swing.JRadioButton();
        otherButton = new javax.swing.JRadioButton();
        nationality = new javax.swing.JLabel();
        nationalityField = new javax.swing.JTextField();
        address = new javax.swing.JLabel();
        addressField = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        roomNumber = new javax.swing.JLabel();
        roomNumberComboBox = new javax.swing.JComboBox<>();
        roomType = new javax.swing.JLabel();
        roomTypeField = new javax.swing.JTextField();
        bedType = new javax.swing.JLabel();
        bedTypeField = new javax.swing.JTextField();
        price = new javax.swing.JLabel();
        priceField = new javax.swing.JTextField();
        checkInDate = new javax.swing.JLabel();
        checkInDateField = new javax.swing.JTextField();
        numPersonStay = new javax.swing.JLabel();
        numPersonStaySpinner = new javax.swing.JSpinner();
        jPanel5 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        additionalPerFee = new javax.swing.JLabel();
        additionalPerFeeField = new javax.swing.JTextField();
        extraBedCheckBox = new javax.swing.JCheckBox();
        extraComforterCheckBox = new javax.swing.JCheckBox();
        extraPillowCheckBox = new javax.swing.JCheckBox();
        foodServiceCheckBox = new javax.swing.JCheckBox();
        jLabel18 = new javax.swing.JLabel();
        seniorDisCheckBox = new javax.swing.JCheckBox();
        childDisCheckBox = new javax.swing.JCheckBox();
        jLabel19 = new javax.swing.JLabel();
        txtPromo = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        confirmCheckInButton = new javax.swing.JButton();
        totalChargeField = new javax.swing.JTextField();
        cancelReservationButton = new javax.swing.JButton();
        UpdateCheckInButton = new javax.swing.JButton();
        delCheckInDataButton = new javax.swing.JButton();
        searchField = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        searchButton = new javax.swing.JButton();

        setBackground(new java.awt.Color(153, 204, 255));
        setPreferredSize(new java.awt.Dimension(1200, 700));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        checkIn.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        checkIn.setText("CUSTOMER CHECK-IN");
        add(checkIn, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 381, -1));

        jPanel2.setBackground(new java.awt.Color(75, 146, 219));
        jPanel2.setToolTipText("");

        jLabel1.setBackground(new java.awt.Color(0, 102, 204));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("CUSTOMER INFORMATION");
        jLabel1.setOpaque(true);

        fullName.setText("Full Name");

        fullNameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fullNameFieldActionPerformed(evt);
            }
        });

        mobileNumber.setText("Mobile Number");

        mobileNumberField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mobileNumberFieldActionPerformed(evt);
            }
        });

        emailAddress.setText("Email Address");

        emailAddressField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emailAddressFieldActionPerformed(evt);
            }
        });

        gender.setText("Gender");

        maleButton.setText("Male");
        maleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maleButtonActionPerformed(evt);
            }
        });

        femaleButton.setText("Female");
        femaleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                femaleButtonActionPerformed(evt);
            }
        });

        otherButton.setText("Other");
        otherButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                otherButtonActionPerformed(evt);
            }
        });

        nationality.setText("Nationality");

        nationalityField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nationalityFieldActionPerformed(evt);
            }
        });

        address.setText("Address");

        addressField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addressFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(address)
                            .addComponent(nationality))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(gender)
                                .addGap(24, 24, 24)
                                .addComponent(maleButton)
                                .addGap(18, 18, 18)
                                .addComponent(femaleButton)
                                .addGap(18, 18, 18)
                                .addComponent(otherButton))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(addressField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                                .addComponent(nationalityField)
                                .addComponent(emailAddress, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(mobileNumber, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(fullName, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(fullNameField, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(mobileNumberField, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(emailAddressField, javax.swing.GroupLayout.Alignment.LEADING)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(fullName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fullNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mobileNumber)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mobileNumberField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(emailAddress)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(emailAddressField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(gender)
                    .addComponent(maleButton)
                    .addComponent(femaleButton)
                    .addComponent(otherButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nationality)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nationalityField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(address)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addressField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 210, 410, 570));

        jPanel4.setBackground(new java.awt.Color(75, 146, 219));

        jLabel8.setBackground(new java.awt.Color(0, 102, 204));
        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("ROOM BOOKING INFORMATION");
        jLabel8.setOpaque(true);

        roomNumber.setText("Room Number");

        roomNumberComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        roomNumberComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                roomNumberComboBoxActionPerformed(evt);
            }
        });

        roomType.setText("Room Type (Auto-filled)");

        roomTypeField.setEditable(false);
        roomTypeField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                roomTypeFieldActionPerformed(evt);
            }
        });

        bedType.setText("Bed Type (Auto-filled)");

        bedTypeField.setEditable(false);
        bedTypeField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bedTypeFieldActionPerformed(evt);
            }
        });

        price.setText("Price (Auto-filled)");

        priceField.setEditable(false);
        priceField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                priceFieldActionPerformed(evt);
            }
        });

        checkInDate.setText("Check-In Date (YYYY-MM-DD)");

        checkInDateField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkInDateFieldActionPerformed(evt);
            }
        });

        numPersonStay.setText("Number of Person Staying");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(numPersonStay)
                    .addComponent(checkInDate)
                    .addComponent(price)
                    .addComponent(bedType)
                    .addComponent(roomType)
                    .addComponent(roomNumber)
                    .addComponent(roomNumberComboBox, 0, 373, Short.MAX_VALUE)
                    .addComponent(roomTypeField)
                    .addComponent(bedTypeField)
                    .addComponent(priceField)
                    .addComponent(checkInDateField)
                    .addComponent(numPersonStaySpinner))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(roomNumber)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(roomNumberComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(roomType)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(roomTypeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bedType)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bedTypeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(price)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(priceField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(checkInDate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkInDateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(numPersonStay)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(numPersonStaySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 210, 410, 570));

        jPanel5.setBackground(new java.awt.Color(75, 146, 219));

        jLabel15.setBackground(new java.awt.Color(0, 102, 204));
        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("ADDITIONAL CHARGES & DISCOUNTS");
        jLabel15.setOpaque(true);

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel16.setText("Additional Charges");

        additionalPerFee.setText("Additional Person Fee (if exceeding max occupancy)");

        additionalPerFeeField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                additionalPerFeeFieldActionPerformed(evt);
            }
        });
        additionalPerFeeField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                additionalPerFeeFieldKeyReleased(evt);
            }
        });

        extraBedCheckBox.setText("Extra Bed");
        extraBedCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                extraBedCheckBoxActionPerformed(evt);
            }
        });

        extraComforterCheckBox.setText("Extra comforter");
        extraComforterCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                extraComforterCheckBoxActionPerformed(evt);
            }
        });

        extraPillowCheckBox.setText("Extra Pillow");
        extraPillowCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                extraPillowCheckBoxActionPerformed(evt);
            }
        });

        foodServiceCheckBox.setText("Food Service");
        foodServiceCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                foodServiceCheckBoxActionPerformed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel18.setText("Discount Types");

        seniorDisCheckBox.setText("Senior Discount");
        seniorDisCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seniorDisCheckBoxActionPerformed(evt);
            }
        });

        childDisCheckBox.setText("Children discount");
        childDisCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                childDisCheckBoxActionPerformed(evt);
            }
        });

        jLabel19.setText("Promotional discount code");

        txtPromo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPromoActionPerformed(evt);
            }
        });
        txtPromo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPromoKeyReleased(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("TOTAL EXPECTED CHARGE");

        confirmCheckInButton.setText("Confrim Check-In");
        confirmCheckInButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmCheckInButtonActionPerformed(evt);
            }
        });

        totalChargeField.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        totalChargeField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                totalChargeFieldActionPerformed(evt);
            }
        });

        cancelReservationButton.setText("Cancel Reservation");
        cancelReservationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelReservationButtonActionPerformed(evt);
            }
        });

        UpdateCheckInButton.setText("Update Check-In");
        UpdateCheckInButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UpdateCheckInButtonActionPerformed(evt);
            }
        });

        delCheckInDataButton.setText("Delete Check-In");
        delCheckInDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delCheckInDataButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(childDisCheckBox)
                            .addComponent(seniorDisCheckBox)
                            .addComponent(jLabel18)
                            .addComponent(foodServiceCheckBox)
                            .addComponent(extraPillowCheckBox)
                            .addComponent(extraComforterCheckBox)
                            .addComponent(extraBedCheckBox)
                            .addComponent(jLabel16)
                            .addComponent(additionalPerFee, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtPromo, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(additionalPerFeeField, javax.swing.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
                            .addComponent(totalChargeField)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(71, 71, 71)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(cancelReservationButton)
                                .addGap(18, 18, 18)
                                .addComponent(delCheckInDataButton, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(confirmCheckInButton, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(UpdateCheckInButton, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(additionalPerFee)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(additionalPerFeeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(extraBedCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(extraComforterCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(extraPillowCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(foodServiceCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(seniorDisCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(childDisCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(txtPromo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(totalChargeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(UpdateCheckInButton, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(confirmCheckInButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelReservationButton)
                    .addComponent(delCheckInDataButton))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(1150, 210, 410, 570));

        searchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchFieldActionPerformed(evt);
            }
        });
        add(searchField, new org.netbeans.lib.awtextra.AbsoluteConstraints(1200, 30, 330, 40));
        add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 40, -1, -1));

        searchButton.setText("Search");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });
        add(searchButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(1540, 30, -1, 40));
    }// </editor-fold>//GEN-END:initComponents

    private void maleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maleButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_maleButtonActionPerformed

    private void otherButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_otherButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_otherButtonActionPerformed

    private void femaleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_femaleButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_femaleButtonActionPerformed

    private void priceFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_priceFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_priceFieldActionPerformed

    private void extraBedCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_extraBedCheckBoxActionPerformed
        // TODO add your handling code here:
        calculateTotal();
    }//GEN-LAST:event_extraBedCheckBoxActionPerformed

    private void confirmCheckInButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmCheckInButtonActionPerformed
        // TODO add your handling code here:
        
        //Get Basic Info
        String room = roomNumberComboBox.getSelectedItem().toString();
        
        String name = fullNameField.getText().replace(" ","_");
        String mobile = mobileNumberField.getText();
        String email = emailAddressField.getText();
        String gender = "Other";
        if(maleButton.isSelected()) gender = "Male";
        else if(femaleButton.isSelected()) gender = "Female";
        String nationality = nationalityField.getText();
        String address = addressField.getText().replace(", ","_").replace(",","_");
        
        
        String rType = roomTypeField.getText();
        String bType = bedTypeField.getText();
        double rPrice = 0;
        try {
            rPrice = Double.parseDouble(priceField.getText());
        } catch (Exception e){}
        String date = checkInDateField.getText();
        int persons = (Integer)numPersonStaySpinner.getValue();
          
        String addFee = additionalPerFeeField.getText();
        // Get Checkboxes
        boolean exBed = extraBedCheckBox.isSelected();
        boolean exComf = extraComforterCheckBox.isSelected();
        boolean exPillow = extraPillowCheckBox.isSelected();
        boolean food = foodServiceCheckBox.isSelected();
        boolean senior = seniorDisCheckBox.isSelected();
        boolean child = childDisCheckBox.isSelected();
        String promo = txtPromo.getText();
        
        double total = 0;
        try {
            total = Double.parseDouble(totalChargeField.getText());
        } catch (NumberFormatException e) {
            total = 0;
        } 
 
        CheckInData newGuest = new CheckInData(name, mobile, email, gender, nationality, address,
                                               room, rType, bType, rPrice, date, persons, addFee, exBed,
                                               exComf, exPillow, food, senior, child, promo, total);
        
        customerDatabase.add(newGuest); // saan ito mapupunta, it is a file ba?
                       
        saveData();
        
        updateRoomStatus(room); // refresh the dropdown so this room dissapears from the "
        
        javax. swing.JOptionPane.showMessageDialog(this, "Check-In Saved Successfuly and Room Occupied");
    }//GEN-LAST:event_confirmCheckInButtonActionPerformed
    
    private CheckInData performLinearSearch(String searchKey) {
        //loop through the Data Structure (ArrayList)
        for(int i = 0; i < customerDatabase.size(); i++) {
            
            //access the object
            CheckInData guest = customerDatabase.get(i);
            
           String cleanName = guest.getName().replace("_", " ");
            
            // compate data
            if(cleanName.toLowerCase().contains(searchKey.toLowerCase()) || 
               guest.getRoomNumber().equals(searchKey)) {
                
                return guest; // found it, return the object immediately
            }
        }
        return null;
    }
    
    // abstraction (display)
    private void displayCustomerData(CheckInData guest) {
        oldName = guest.getName();
       
        fullNameField.setText(guest.getName().replace("_"," "));
        mobileNumberField.setText(guest.getMobile());
        emailAddressField.setText(guest.getEmail());
        
        if(guest.getGender().equals("Male")) maleButton.setSelected(true);
        else if(guest.getGender().equals("Female")) femaleButton.setSelected(true);
        else otherButton.setSelected(true);
        
        nationalityField.setText(guest.getNationality());
        addressField.setText(guest.getAddress().replace("_", ", "));
        
        
        String guestRoom = guest.getRoomNumber();
        boolean exists = false;
        
        // check if the room is already in the dropdown
        for(int i = 0; i < roomNumberComboBox.getItemCount(); i++) {
            if(roomNumberComboBox.getItemAt(i).equals(guestRoom)) {
                exists = true;
                break;
            }
        }
        
        // if it not there (becuase its occupied), add it temporarily can see it
        if (!exists) {
            roomNumberComboBox.addItem(guestRoom);
        }
        
        roomNumberComboBox.setSelectedItem(guestRoom);
        
        roomTypeField.setText(guest.getRoomType());
        bedTypeField.setText(guest.getBedType());
        priceField.setText(String.valueOf(guest.getRoomPrice()));
        checkInDateField.setText(guest.getCheckInDate());
        numPersonStaySpinner.setValue(guest.getPersons());
        
        additionalPerFeeField.setText(guest.getAdditionalFee());
        
        //checkboxes
        extraBedCheckBox.setSelected(guest.isExtraBed());
        extraComforterCheckBox.setSelected(guest.isExtraComforter());
        extraPillowCheckBox.setSelected(guest.isExtraPillow());
        foodServiceCheckBox.setSelected(guest.isFoodService());
        
        seniorDisCheckBox.setSelected(guest.isSeniorDiscount());
        childDisCheckBox.setSelected(guest.isChildDiscount());
        txtPromo.setText(guest.getPromoCode());
        
        totalChargeField.setText(String.valueOf(guest.getTotalCharge()));
        
    }
    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        // TODO add your handling code here:    
        
        // get input
        String searchKey = searchField.getText().trim();
        
        //safety check
        if(searchKey.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Please enter a name or room number.");
        }
        
        // call the dsa method (linear search)
        CheckInData foundGuest = performLinearSearch(searchKey);
        
        // handle the result
        if(foundGuest != null){
            //success call the oop display method
            displayCustomerData(foundGuest);
            javax.swing.JOptionPane.showMessageDialog(this, "Guest Found!");
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Guest not found.");
        }
    }//GEN-LAST:event_searchButtonActionPerformed

    private void bedTypeFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bedTypeFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bedTypeFieldActionPerformed

    private void fullNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fullNameFieldActionPerformed
        // TODO add your handling code here:
        //fullNameField.setText("First Name Last Name");
    }//GEN-LAST:event_fullNameFieldActionPerformed

    private void mobileNumberFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mobileNumberFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mobileNumberFieldActionPerformed

    private void emailAddressFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emailAddressFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_emailAddressFieldActionPerformed

    private void nationalityFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nationalityFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nationalityFieldActionPerformed

    private void addressFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addressFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addressFieldActionPerformed

    private void roomNumberComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_roomNumberComboBoxActionPerformed
        // TODO add your handling code here:
        
    if (roomNumberComboBox.getSelectedItem() == null) {
        return;
    }

    String selected = roomNumberComboBox.getSelectedItem().toString();
    
    // Check if they selected the placeholder "Selec Room"
    if (selected.equals("Selec Room")) {
        // Clear fields or do nothing
        return;
    }

    for(int i = 0; i < roomList.size(); i++){
        
        // get specifc room array at index i
        String[] room = roomList.get(i);
        
        // index 0 is the room number
        if(room[0].equals(selected)) {
            
            // fill the text boxes
            roomTypeField.setText(room[1]);
            bedTypeField.setText(room[2]);
            priceField.setText(room[3]);
            
            // calculation 
            calculateTotal();
            
            //stop the loop
            return;
        }
    }
    }//GEN-LAST:event_roomNumberComboBoxActionPerformed

    private void roomTypeFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_roomTypeFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_roomTypeFieldActionPerformed

    private void checkInDateFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkInDateFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_checkInDateFieldActionPerformed

    private void additionalPerFeeFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_additionalPerFeeFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_additionalPerFeeFieldActionPerformed

    private void extraComforterCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_extraComforterCheckBoxActionPerformed
        // TODO add your handling code here:
        calculateTotal();
    }//GEN-LAST:event_extraComforterCheckBoxActionPerformed

    private void extraPillowCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_extraPillowCheckBoxActionPerformed
        // TODO add your handling code here:
        calculateTotal();
    }//GEN-LAST:event_extraPillowCheckBoxActionPerformed

    private void foodServiceCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_foodServiceCheckBoxActionPerformed
        // TODO add your handling code here:
        calculateTotal();
    }//GEN-LAST:event_foodServiceCheckBoxActionPerformed

    private void seniorDisCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seniorDisCheckBoxActionPerformed
        // TODO add your handling code here:
        calculateTotal();
    }//GEN-LAST:event_seniorDisCheckBoxActionPerformed

    private void childDisCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_childDisCheckBoxActionPerformed
        // TODO add your handling code here:
        calculateTotal();
    }//GEN-LAST:event_childDisCheckBoxActionPerformed

    private void txtPromoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPromoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPromoActionPerformed

    private void totalChargeFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_totalChargeFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_totalChargeFieldActionPerformed

    private void cancelReservationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelReservationButtonActionPerformed
        // TODO add your handling code here:
        String dateStr = checkInDateField.getText(); // User Types YYYY-MM-DD
        
        try {
            java.time.LocalDate checkIn = java.time.LocalDate.parse(dateStr);
            java.time.LocalDate today = java.time.LocalDate.now();
            
            long days = java.time.temporal.ChronoUnit.DAYS.between(today, checkIn);
            
            if(days >= 2){
                //allowed to cancel
                javax.swing.JOptionPane.showMessageDialog(this, "Cancellation Approved.");
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Cancellation Failed. Must be 2 days before.");
            }
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Use format YYYY-MM-DD (e.g., 2025-12-25)");
        }
    }//GEN-LAST:event_cancelReservationButtonActionPerformed
    
    private void setRoomAvailable(String roomNumber) {
        java.util.ArrayList<String> fileContent = new java.util.ArrayList<>();
        java.io.File file = new java.io.File("data/rooms.txt");
        
        try {
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file));
            String line;
            
            while((line = reader.readLine()) != null){
                String[] parts = line.split(",");
                
                if(parts.length >= 6) {
                    String currentRoomNum = parts[0];
                    
                    if(currentRoomNum.equals(roomNumber)) {
                        // reconstruct line with available
                        String newLine = parts[0] + "," + parts[1] + "," + parts[2] + "," +
                                         parts[3] + "," + parts[4] + "," + "Available";
                        fileContent.add(newLine);
                    } else {
                        fileContent.add(line);
                    }
                } else {
                    fileContent.add(line);
                }
            }
            reader.close();
            
            java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(file));
            for(String str : fileContent) {
                writer.write(str);
                writer.newLine();
            }
            writer.close();
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Erro freeing room: " + e.getMessage());
        }
    }
    private void UpdateCheckInButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UpdateCheckInButtonActionPerformed
        // TODO add your handling code here:
        
        // get the name form the text box to find the person
        String targetName = fullNameField.getText();
        
        
       // safety check
       if(targetName.isEmpty()) {
           javax.swing.JOptionPane.showMessageDialog(this, "Please select/search a customer first.");
           return;
       }
       
       // loop to find th person in the list
       for(int i = 0; i < customerDatabase.size(); i++) {
   
           CheckInData guest = customerDatabase.get(i);
           
           
           // match found?
           if(guest.getName().equalsIgnoreCase(oldName)) {
               
               guest.setRoomNumber(roomNumberComboBox.getSelectedItem().toString());
               
               String newName = (fullNameField.getText().replace(" ","_"));
               guest.setName(newName);
               
              
               // use setters to update the data
               guest.setMobile(mobileNumberField.getText());
               
               guest.setRoomType(roomTypeField.getText());
               guest.setBedType(bedTypeField.getText());
               try {
                   guest.setRoomPrice(Double.parseDouble(priceField.getText()));
               } catch (Exception e){}
               guest.setCheckInDate(checkInDateField.getText());
               int persons = (Integer)numPersonStaySpinner.getValue();
               guest.setPersons(persons); // save that value into the guest object
               
               guest.setAdditionalFee(additionalPerFeeField.getText());
               guest.setPromoCode(txtPromo.getText());
               
               boolean isExtraBed = extraBedCheckBox.isSelected();
               boolean isComforter = extraComforterCheckBox.isSelected();
               boolean isPillow = extraPillowCheckBox.isSelected();
               boolean isFood = foodServiceCheckBox.isSelected();
               boolean isSenior = seniorDisCheckBox.isSelected();
               boolean isChild = childDisCheckBox.isSelected();
               
               guest.setExtraBed(isExtraBed);
               guest.setExtraComforter(isComforter);
               guest.setExtraPillow(isPillow);
               guest.setFoodService(isFood);
               
               guest.setSeniorDiscount(isSenior);
               guest.setChildDiscount(isChild);
               
               guest.setEmail(emailAddressField.getText());
               guest.setNationality(nationalityField.getText());
               guest.setAddress(addressField.getText().replace(", ","_").replace(",","_"));
               guest.setAdditionalFee(additionalPerFeeField.getText());
               guest.setPromoCode(txtPromo.getText());
               
               String gender = "Other";
               if(maleButton.isSelected()) gender = "Male";
               else if(femaleButton.isSelected()) gender = "Female";
               guest.setGender(gender);
               // handle price (convert string to double
               try {
                   double newTotal = Double.parseDouble(totalChargeField.getText());
                   guest.setTotalCharge(newTotal);
                   
                   saveData();
                   
               } catch (Exception e) {
                   
               }
               
               saveData();
               
               oldName = newName;
               
               javax.swing.JOptionPane.showMessageDialog(this, "Customer Details Updated Successfully!");
               return; //           
           }
       }
       
       javax.swing.JOptionPane.showMessageDialog(this, "Customer not found. (Name cannot be changed)");
    }//GEN-LAST:event_UpdateCheckInButtonActionPerformed

    private void delCheckInDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delCheckInDataButtonActionPerformed
        // TODO add your handling code here:
        
        String currentName = fullNameField.getText();
        
        if(currentName.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Search for a customer first.");
            return;
        }
        
        boolean found = false;
        for(int i = 0; i < customerDatabase.size(); i++) {
            
            CheckInData guest = customerDatabase.get(i);
            
            String storedNameClean = guest.getName().replace("_", " ");
            
            if(storedNameClean.equalsIgnoreCase(currentName)) {
                
                // make the room available again
                setRoomAvailable(guest.getRoomNumber());
                
                customerDatabase.remove(i);
                saveData();
                
                found = true;
                javax.swing.JOptionPane.showMessageDialog(this, "Check-In Deleted & Room is now Available.");
                
                clearAllFields();
                
                setupRooms();
                
                break;
            }
        }
        
        if(!found) {
            javax.swing.JOptionPane.showMessageDialog(this, "Record not found in database");
        }     
    }//GEN-LAST:event_delCheckInDataButtonActionPerformed
    
    private void clearAllFields() {
        fullNameField.setText("");
        mobileNumberField.setText("");
        emailAddressField.setText("");
        nationalityField.setText("");
        addressField.setText("");
        
        roomNumberComboBox.setSelectedIndex(0);
        roomTypeField.setText("");
        bedTypeField.setText("");
        priceField.setText("");
        checkInDateField.setText("");
        numPersonStaySpinner.setValue(0);
        
        additionalPerFeeField.setText("");
        txtPromo.setText("");
        totalChargeField.setText("");
        
        // Uncheck boxes
        extraBedCheckBox.setSelected(false);
        extraComforterCheckBox.setSelected(false);
        extraPillowCheckBox.setSelected(false);
        foodServiceCheckBox.setSelected(false);
        seniorDisCheckBox.setSelected(false);
        childDisCheckBox.setSelected(false);
        
        // Reset Radio Buttons
        maleButton.setSelected(false);
        femaleButton.setSelected(false);
        otherButton.setSelected(false);
    }
    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchFieldActionPerformed

    private void txtPromoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPromoKeyReleased
        // TODO add your handling code here:
        calculateTotal();
    }//GEN-LAST:event_txtPromoKeyReleased

    private void additionalPerFeeFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_additionalPerFeeFieldKeyReleased
        // TODO add your handling code here:
        calculateTotal();
    }//GEN-LAST:event_additionalPerFeeFieldKeyReleased
    
    
    public static void main(String[] args) {
    // 1. Create a Frame to hold the panel
    javax.swing.JFrame frame = new javax.swing.JFrame("Test Preview");
    
    // 2. Set what happens when you close the frame
    frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    
    // 3. Create an instance of YOUR panel
    // IMPORTANT: Replace 'YourPanelName' with the actual class name of your JPanel
    CheckInPanel content = new CheckInPanel(); 
    
    // 4. Add the panel to the frame
    frame.add(content);
    
    // 5. Pack the frame (resize it to fit your panel) and make it visible
    frame.pack();
    frame.setLocationRelativeTo(null); // Center on screen
    frame.setVisible(true);
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton UpdateCheckInButton;
    private javax.swing.JLabel additionalPerFee;
    private javax.swing.JTextField additionalPerFeeField;
    private javax.swing.JLabel address;
    private javax.swing.JTextField addressField;
    private javax.swing.JLabel bedType;
    private javax.swing.JTextField bedTypeField;
    private javax.swing.JButton cancelReservationButton;
    private javax.swing.JLabel checkIn;
    private javax.swing.JLabel checkInDate;
    private javax.swing.JTextField checkInDateField;
    private javax.swing.JCheckBox childDisCheckBox;
    private javax.swing.JButton confirmCheckInButton;
    private javax.swing.JButton delCheckInDataButton;
    private javax.swing.JLabel emailAddress;
    private javax.swing.JTextField emailAddressField;
    private javax.swing.JCheckBox extraBedCheckBox;
    private javax.swing.JCheckBox extraComforterCheckBox;
    private javax.swing.JCheckBox extraPillowCheckBox;
    private javax.swing.JRadioButton femaleButton;
    private javax.swing.JCheckBox foodServiceCheckBox;
    private javax.swing.JLabel fullName;
    private javax.swing.JTextField fullNameField;
    private javax.swing.JLabel gender;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JRadioButton maleButton;
    private javax.swing.JLabel mobileNumber;
    private javax.swing.JTextField mobileNumberField;
    private javax.swing.JLabel nationality;
    private javax.swing.JTextField nationalityField;
    private javax.swing.JLabel numPersonStay;
    private javax.swing.JSpinner numPersonStaySpinner;
    private javax.swing.JRadioButton otherButton;
    private javax.swing.JLabel price;
    private javax.swing.JTextField priceField;
    private javax.swing.JLabel roomNumber;
    private javax.swing.JComboBox<String> roomNumberComboBox;
    private javax.swing.JLabel roomType;
    private javax.swing.JTextField roomTypeField;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchField;
    private javax.swing.JCheckBox seniorDisCheckBox;
    private javax.swing.JTextField totalChargeField;
    private javax.swing.JTextField txtPromo;
    // End of variables declaration//GEN-END:variables
}
