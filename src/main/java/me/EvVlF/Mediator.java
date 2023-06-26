package me.EvVlF;

import java.io.IOException;
import java.sql.SQLException;

public class Mediator {

    private DBConnection dbConnection;
    private UserInput userInput;
    private DBFetcher dbFetcher;
    private Tolerance tolerance;

    private static final class FacadeSingletonHolder {
        private static final Mediator MEDIATOR_INSTANCE = new Mediator();
    }

    private Mediator() {
        this.dbConnection = DBConnection.getInstance();
        this.dbFetcher = DBFetcher.getInstance();
        this.tolerance = Tolerance.getInstance();
        this.userInput = UserInput.getInstance();
    }

    static Mediator getInstance() {
        return Mediator.FacadeSingletonHolder.MEDIATOR_INSTANCE;
    }

    static void main() throws SQLException, IOException {
        Mediator mediator = Mediator.getInstance();
        mediator.dbConnection.connect();
        mediator.dbFetcher.setConnection(mediator.dbConnection.getConnection());
        mediator.dbConnection.readDBinMem();
        mediator.dbFetcher.setTolerance(mediator.tolerance);
        while(!mediator.userInput.isExitProgram()) {
            mediator.userInput.interactWithUser();
            mediator.tolerance.splitAndSetUserInputOnSizeQualityDeviation(mediator.userInput.matcherText_ESDiP_Range());
            mediator.dbFetcher.setVariablesFromSql();
            mediator.tolerance.setMinRangeTableSize(mediator.dbFetcher.getMinRangeSize());
            mediator.tolerance.setMaxRangeTableSize(mediator.dbFetcher.getMaxRangeSize());
            mediator.tolerance.setUpperTolerance(mediator.dbFetcher.getUpperToleranceRange());
            mediator.tolerance.setLowerTolerance(mediator.dbFetcher.getLowerToleranceRange());
            mediator.tolerance.printAllValues();
        }
        mediator.dbConnection.closeConnect();
    }

}
