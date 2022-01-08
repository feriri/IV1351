package se.kth.iv1351.soundgoodjdbc.integration;

import se.kth.iv1351.soundgoodjdbc.model.Instrument;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * This data access object (DAO) encapsulates all database calls in the school
 * application. No code outside this class shall have any knowledge about the
 * database.
 */
public class SoundGoodDAO {
    private static final String RENTAL_INSTRUMENT_TABLE_NAME = "rental_instrument";
    private static final String INSTRUMENT_ID_PK_COLUMN_NAME = "instrument_id";
    private static final String INSTRUMENT_COLUMN_NAME = "instrument";
    private static final String BRAND_COLUMN_NAME= "brand";
    private static final String MONTHLY_PRICE_COLUMN_NAME = "monthly_price";
    private static final String RETURN_DATE_COLUMN_NAME = "return_date";
    private static final String STUDENT_ID_FK_COLUMN_NAME = "student_id";

    private Connection connection;
    private PreparedStatement findAllAvailableInstrumentsStmt;
    private PreparedStatement rentInstrumentStmt;
    private PreparedStatement terminateRentalStmt;
    private PreparedStatement limitRentalStmt;
    private PreparedStatement availableInstrumentStmt;
    LocalDateTime now =  LocalDateTime.now();

    /**
     * Constructs a new DAO object connected to the school database.
     */
    public SoundGoodDAO() throws SoundGoodDBException {
        try {
            connectToSoundGoodDB();
            prepareStatements();
        } catch (ClassNotFoundException | SQLException exception) {
            throw new SoundGoodDBException("Could not connect to datasource", exception);
        }
    }

    /**
     * Finds all available instruments
     */
    public List<Instrument> findAllAvailableInstruments(String instrument) throws SoundGoodDBException {
        String failureMsg = "Could not list available instruments.";
        List<Instrument> instruments = new ArrayList<>();
        try {
            findAllAvailableInstrumentsStmt.setString(1, instrument);
        } catch(SQLException sqle){
            handleException(failureMsg, sqle);
        }
        try (ResultSet result = findAllAvailableInstrumentsStmt.executeQuery()) {
            while (result.next()) {
                instruments.add(new Instrument(result.getInt(INSTRUMENT_ID_PK_COLUMN_NAME),
                        result.getString(INSTRUMENT_COLUMN_NAME),
                        result.getInt(MONTHLY_PRICE_COLUMN_NAME),
                        result.getString(BRAND_COLUMN_NAME),
                        result.getString(RETURN_DATE_COLUMN_NAME),
                        result.getInt(STUDENT_ID_FK_COLUMN_NAME)));
            }
            connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        }
        return instruments;
    }

    /**
     * Creates a new rental for the specified instrument
     */
    public void rentInstrument(int instrumentID, int studentID) throws SoundGoodDBException {
        String failureMsg = "Could not rent the instrument";
        try{
            if (checkAvailability(instrumentID) != null)
                System.out.println("The instrument is already rented");
            else if (checkLimit(studentID) < 2 && checkAvailability(instrumentID) == null) {
                rentInstrumentStmt.setTimestamp(1, Timestamp.valueOf(now.plusMonths(12)));
                rentInstrumentStmt.setInt(2, studentID);
                rentInstrumentStmt.setInt(3, instrumentID);
                int updatedRows = rentInstrumentStmt.executeUpdate();
                if (updatedRows != 1) {
                    handleException(failureMsg, null);
                }
                connection.commit();
                System.out.println("Instrument " + instrumentID + " is now being rented to student " + studentID);
            } else System.out.println("The maximum number of rentals exceeded");
        } catch(SQLException sqle){
            handleException(failureMsg, sqle);
        }
    }

    /**
     * Checks if the maximum limit for a student to rent an instrument is reached
     */
    private int checkLimit(int studentID)throws SQLException{
        limitRentalStmt.setInt(1, studentID);
        ResultSet result = limitRentalStmt.executeQuery();
        result.next();
        int quantity = result.getInt(1);
        return quantity;
    }

    /**
     * Checks if the instrument is available for rent
     */
    private String checkAvailability(int instrumentID) throws SQLException{
        availableInstrumentStmt.setInt(1, instrumentID);
        ResultSet result = availableInstrumentStmt.executeQuery();
        result.next();
        String availability = result.getString(1);
        return availability;
    }

    /**
     * Terminates an ongoing rental with the specified instrument id
     */
    public void terminateRental(int instrumentID) throws SoundGoodDBException {
        String failureMsg = "Could not terminate the rental";
        try {
            if((checkAvailability(instrumentID) == null)){
                System.out.println("The instrument is not rented!");
            } else {
                terminateRentalStmt.setInt(1, instrumentID);
                int updatedRows = terminateRentalStmt.executeUpdate();
                if (updatedRows != 1) {
                    handleException(failureMsg, null);
                }
                connection.commit();
                System.out.println("Terminates...");
            }
        } catch(SQLException sqle){
            handleException(failureMsg, sqle);
        }
    }

    private void connectToSoundGoodDB() throws ClassNotFoundException, SQLException {
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/soundgood",
                "farzaneh", "farzaneh");
        connection.setAutoCommit(false);
    }

    private void prepareStatements() throws SQLException {
        findAllAvailableInstrumentsStmt = connection.prepareStatement("SELECT * FROM " + RENTAL_INSTRUMENT_TABLE_NAME
                + " WHERE " + RETURN_DATE_COLUMN_NAME + " IS NULL AND " + INSTRUMENT_COLUMN_NAME + " = ?");
        rentInstrumentStmt = connection.prepareStatement("UPDATE " + RENTAL_INSTRUMENT_TABLE_NAME + " SET " + RETURN_DATE_COLUMN_NAME
                + " = ?, " + STUDENT_ID_FK_COLUMN_NAME + " = ? WHERE " + INSTRUMENT_ID_PK_COLUMN_NAME + " = ?");
        terminateRentalStmt = connection.prepareStatement("UPDATE " + RENTAL_INSTRUMENT_TABLE_NAME + " SET " + RETURN_DATE_COLUMN_NAME
                + " = NULL " + " WHERE " + INSTRUMENT_ID_PK_COLUMN_NAME + " = ?");
        availableInstrumentStmt = connection.prepareStatement("SELECT " + RETURN_DATE_COLUMN_NAME + " FROM " + RENTAL_INSTRUMENT_TABLE_NAME
                + " WHERE " + INSTRUMENT_ID_PK_COLUMN_NAME + " = ?");
        limitRentalStmt = connection.prepareStatement("SELECT COUNT(*) FROM " + RENTAL_INSTRUMENT_TABLE_NAME
                + " WHERE " + STUDENT_ID_FK_COLUMN_NAME + " = ? AND " + RETURN_DATE_COLUMN_NAME + " IS NOT NULL"
        );
        /*updateInstrumentStmt = connection.prepareStatement("INSERT INTO " + RENTAL_INSTRUMENT_TABLE_NAME + " (" + INSTRUMENT_ID_PK_COLUMN_NAME
         + ", " + INSTRUMENT_COLUMN_NAME + ", " + BRAND_COLUMN_NAME + ", " + MONTHLY_PRICE_COLUMN_NAME + ", " + RETURN_DATE_COLUMN_NAME + ", "
         + STUDENT_ID_FK_COLUMN_NAME + ") VALUES (?, ?, ?, ?, ?, ?)");*/
        //totalRentalsStmt = connection.prepareStatement("SELECT COUNT(*) FROM " + RENTAL_INSTRUMENT_TABLE_NAME);
        /*limitRentalStmt = connection.prepareStatement("SELECT " + STUDENT_ID_FK_COLUMN_NAME + " FROM "
                + RENTAL_INSTRUMENT_TABLE_NAME + " GROUP BY " + STUDENT_ID_FK_COLUMN_NAME + " HAVING COUNT(*) > 2 ");*/
    }

    /**
     * Commits the current transaction.
     * @throws SoundGoodDBException If unable to commit the current transaction.
     */
    public void commit() throws SoundGoodDBException {
        try {
            connection.commit();
        } catch (SQLException e) {
            handleException("Failed to commit", e);
        }
    }

    private void handleException(String failureMsg, Exception cause) throws SoundGoodDBException {
        String completeFailureMsg = failureMsg;
        try {
            connection.rollback();
        } catch (SQLException rollbackExc) {
            completeFailureMsg = completeFailureMsg + 
            ". Also failed to rollback transaction because of: " + rollbackExc.getMessage();
        }
        if (cause != null) {
            throw new SoundGoodDBException(failureMsg, cause);
        } else {
            throw new SoundGoodDBException(failureMsg);
        }
    }

}
