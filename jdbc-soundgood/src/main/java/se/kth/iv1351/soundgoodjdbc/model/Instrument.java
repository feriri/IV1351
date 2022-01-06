package se.kth.iv1351.soundgoodjdbc.model;

public class Instrument implements InstrumentDTO{
    private int instrumentID;
    private String instrument;
    private int price;
    private String brand;
    private String returnDate;
    private int studentID;

    public Instrument(int instrumentID, String instrument, int price, String brand, String returnDate, int studentID) {
        this.instrumentID = instrumentID;
        this.instrument = instrument;
        this.price = price;
        this.brand = brand;
        this.returnDate = returnDate;
        this.studentID = studentID;
    }

    /**
     * @return The instrument ID
     */
    public int getInstrumentID(){
        return instrumentID;
    }

    /**
     * @return The instruments name
     */
    public String getInstrument() {
        return instrument;
    }

    /**
     * @return The Instruments monthly price
     */
    public int getPrice(){
        return price;
    }

    /**
     * @return The Instruments Brand
     */
    public String getBrand(){
        return brand;
    }

    /**
     * @return return date
     */
    public String getReturnDate() {
        return returnDate;
    }

    /**
     * @return The student ID
     */
    public int getStudentID() {
        return studentID;
    }
}