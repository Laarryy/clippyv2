package dev.laarryy.clippyv2.storage;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogStorage {
    Logger logger = Logger.getLogger(LogStorage.class.getName());

    // Max Log Size ~= 1GB
    private static final int FILE_SIZE = 999999999;
    protected void loadLog() {
        try {
            // Creating an instance of FileHandler with limit of 100 logging files
            FileHandler handler = new FileHandler("./luckpermslog.log", FILE_SIZE, 100, true);
            handler.setFormatter(new SimpleFormatter());
            logger.addHandler(handler);
            logger.setUseParentHandlers(false);
        } catch (IOException e) {
            logger.warning("Failed to initialize logger handler.");
        }
    }
    public void sendToLog(String message) {
        logger.info("|| SOM ||" + message);
    }
}

