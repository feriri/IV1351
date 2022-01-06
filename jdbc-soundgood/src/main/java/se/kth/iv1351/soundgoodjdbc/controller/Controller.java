package se.kth.iv1351.soundgoodjdbc.controller;

import java.util.List;

import se.kth.iv1351.soundgoodjdbc.integration.SoundGoodDAO;
import se.kth.iv1351.soundgoodjdbc.integration.SoundGoodDBException;
import se.kth.iv1351.soundgoodjdbc.model.InstrumentDTO;
import se.kth.iv1351.soundgoodjdbc.model.InstrumentException;


/**
 * This is the application's only controller, all calls to the model pass here.
 * The controller is also responsible for calling the DAO. Typically, the
 * controller first calls the DAO to retrieve data (if needed), then operates on
 * the data, and finally tells the DAO to store the updated data (if any).
 */
public class Controller {
    private final SoundGoodDAO soundGoodDb;

    /**
     * Creates a new instance, and retrieves a connection to the database.
     * @throws SoundGoodDBException If unable to connect to the database.
     */
    public Controller() throws SoundGoodDBException {
        soundGoodDb = new SoundGoodDAO();
    }

    /**
     * Retrieves all available instruments
     */
    public List<? extends InstrumentDTO> getAllAvailableInstruments(String instrument) throws InstrumentException {
        try {
            return soundGoodDb.findAllAvailableInstruments(instrument);
        } catch (Exception e) {
            throw new InstrumentException("Unable to list instruments.", e);
        }
    }

    /**
     * Creates a new rental for the specified instrument
     */
    public void rentInstrument(int instrumentID, int studentID) throws InstrumentException {
        String errorMessage = "Could not rent instrument for: " + studentID;
        try {
            soundGoodDb.rentInstrument(instrumentID, studentID);
        } catch (Exception e) {
            throw new InstrumentException(errorMessage, e);
        }
    }

    /**
     * Terminates an ongoing rental with the specified instrument id
     */
    public void terminateRental(int instrumentID) throws InstrumentException {
        String errorMessage = "Could not terminate rental for: " + instrumentID;
        try {
            soundGoodDb.terminateRental(instrumentID);
        } catch (Exception e) {
            throw new InstrumentException(errorMessage, e);
        }
    }
}
