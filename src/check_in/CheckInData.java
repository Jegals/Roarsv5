/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package check_in;

/**
 *
 * @author Acer
 */
public class CheckInData {
    
    public static void main(String args[]){
    }
    
    // Basic Info
    private String name;
    private String mobile;
    private String birthday;
    private String email;
    private String sex;
    private String nationality;
    private String address;
    
    private String roomNumber;
    private String roomType;
    private String bedType;
    private double roomPrice;
    private String checkInDate;
    private int persons;

    private String additionalFee;
    // checkboxes
    private boolean extraBed;
    private boolean extraComforter;
    private boolean extraPillow;
    private boolean foodService;
    private int bedQty, comfQty, pillowQty, foodQty;
    private boolean seniorDiscount;
    private boolean pwdDiscount;
    private boolean childDiscount;
    private String promoCode;
    private double totalCharge;
    
    public CheckInData(String name, String mobile, String birthday, String email, String sex, String nationality, String address,
                       String roomNumber, String roomType, String bedType, double roomPrice, String checkInDate, int persons,
                       String additionalFee, boolean extraBed, boolean extraComforter, boolean extraPillow, boolean foodService, int bedQty, int comfQty, int pillowQty, int foodQty, 
                       boolean seniorDiscount, boolean pwdDiscount, boolean childDiscount, String promoCode, double totalCharge) {
        this.name = name;
        this.mobile = mobile;
        this.birthday = birthday;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.bedType = bedType;
        this.roomPrice = roomPrice;
        this.checkInDate = checkInDate;
        this.totalCharge = totalCharge;
        this.persons = persons;
        
        this.extraBed = extraBed;
        this.extraComforter = extraComforter;
        this.extraPillow = extraPillow;
        this.foodService = foodService;
        this.bedQty = bedQty;
        this.comfQty = comfQty;
        this.pillowQty = pillowQty;
        this.foodQty = foodQty;
        this.seniorDiscount = seniorDiscount;
        this.pwdDiscount = pwdDiscount;
        this.childDiscount = childDiscount;
        
        this.email = email;
        this.sex = sex;
        this.nationality = nationality;
        this.address = address;
        this.additionalFee = additionalFee;
        this.promoCode = promoCode;
        
    }
    
    // Getters
    public String getName() {
        return name;
    }
    public String getMobile() {
        return mobile;
    }
    public String getBirthday() {
        return birthday;
    }
    public String getRoomNumber() {
        return roomNumber;
    }
    public String getRoomType() {
        return roomType;
    }
    public String getBedType() {
        return bedType;
    }
    public double getRoomPrice() {
        return roomPrice;
    }
    public String getCheckInDate() {
        return checkInDate;
    }
    public double getTotalCharge() {
        return totalCharge;
    }
    public int getPersons() {
        return persons;
    }
    public boolean isExtraBed() {
        return extraBed;
    }
    public boolean isExtraComforter() {
        return extraComforter;
    }
    public boolean isExtraPillow() {
        return extraPillow;
    }
    public boolean isFoodService() {
        return foodService;
    }
    public int getBedQty() {
        return bedQty;
    }
    public int getComfQty() {
        return comfQty;
    } 
    public int getPillowQty() {
        return pillowQty;
    }
    public int getFoodQty() {
        return foodQty;
    }
    public boolean isSeniorDiscount() {
        return seniorDiscount;
    }
    public boolean isPwdDiscount() {
        return pwdDiscount;
    }
    public boolean isChildDiscount() {
        return childDiscount;
    }
    public String getEmail() {
        return email;
    }
    public String getSex() {
        return sex;
    }
    public String getNationality() {
        return nationality;
    }
    public String getAddress() {
        return address;
    }
    public String getAdditionalFee() {
        return additionalFee;
    }
    public String getPromoCode() {
        return promoCode;
    }
    
    // Setters
    
    public void setName(String name) {
        this.name = name;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public void setBirthday (String birthday) {
        this.birthday = birthday;
    }
    public void setRoomNumber(String roomNumber) {
       this.roomNumber = roomNumber;
    }
    public void setRoomType(String r) {
        this.roomType = r;
    }
    public void setBedType(String b) {
        this.bedType = b;
    }
    public void setRoomPrice(double p) {
        this.roomPrice = p;
    }
    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }
    public void setTotalCharge(double totalCharge) {
        this.totalCharge = totalCharge;
    }
    public void setPersons(int persons) {
        this.persons = persons;
    }
    public void setEmail(String e) {
        this.email = e;
    }
    public void setSex(String s) {
        this.sex = s;
    }
    public void setNationality(String n) {
        this.nationality = n;
    }
    public void setAddress(String a) {
        this.address = a;
    }
    public void setAdditionalFee(String f) {
        this.additionalFee = f;
    }
    public void setPromoCode(String p) {
        this.promoCode = p;
    }
    public void setExtraBed(boolean b) {
        this.extraBed = b;
    }
    public void setExtraComforter(boolean b){
        this.extraComforter = b;
    }
    public void setExtraPillow(boolean b){
        this.extraPillow = b;
    }
    public void setFoodService(boolean b) {
        this.foodService = b;
    }
    public void setBedQty (int bedQty) {
        this.bedQty = bedQty;
    }
    public void setComfQty (int comfQty) {
        this.comfQty = comfQty;
    }
    public void setPillowQty (int pillowQty) {
        this.pillowQty = pillowQty;
    }
    public void setFoodQty (int foodQty) {
        this.foodQty = foodQty;
    }
    public void setSeniorDiscount(boolean b) {
        this.seniorDiscount = b;
    }
    public void setPwdDiscount(boolean p) {
        this.pwdDiscount = p;
    }
    public void setChildDiscount(boolean b) {
        this.childDiscount = b;
    }

}
