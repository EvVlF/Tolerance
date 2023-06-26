package me.EvVlF;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBFetcher {
    private double minRangeSize;
    private double maxRangeSize;
    private double upperToleranceRange;
    private double lowerToleranceRange;
    private Connection connection;
    private Tolerance tolerance;

    private static final class DBFetcherSingletonHolder {
        private static final DBFetcher DBFETCHER_INSTANCE = new DBFetcher();
    }

    static DBFetcher getInstance() {
        return DBFetcher.DBFetcherSingletonHolder.DBFETCHER_INSTANCE;
    }

    private DBFetcher() {
    }

    void setConnection(Connection connection) {
        this.connection = connection;
    }

    <T extends Tolerance> void setTolerance(T tolerance) {
        this.tolerance = tolerance;
    }

    void setVariablesFromSql() throws SQLException {
        setMaxRangeSizeFromDB();
        setMinRangeSizeFromDB();
        setLowerToleranceRange();
        setUpperToleranceRange();
    }

    private String getMinOrMaxRangeSizeQuery(String minOrMax, String comparisonOperator) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT " + minOrMax + "(CAST(COLUMN_NAME AS DOUBLE)) AS " + minOrMax + "_COL " +
                "FROM ALL_DIGIT_COLUMN_NAMES " +
                "WHERE CAST(COLUMN_NAME AS DOUBLE) " + comparisonOperator);
        sb.append(tolerance.getSize());
        sb.append(";");
        return sb.toString();
    }

    private String getMinRangeSizeQuery() {
        return getMinOrMaxRangeSizeQuery("MAX", "<");
    }

    private String getMaxRangeSizeQuery() {
        return getMinOrMaxRangeSizeQuery("MIN", ">=");
    }

    private void setMinOrMaxRangeSizeFromDB(String minOrMaxRangeSizeQuery) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(minOrMaxRangeSizeQuery)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                if (minOrMaxRangeSizeQuery.contains("MAX")) {
                    minRangeSize = resultSet.getDouble(1);
                } else if (minOrMaxRangeSizeQuery.contains("MIN")) {
                    maxRangeSize = resultSet.getDouble(1);
                }
            }
        }
    }

    private void setMinRangeSizeFromDB() throws SQLException {
        setMinOrMaxRangeSizeFromDB(getMinRangeSizeQuery());
    }

    private void setMaxRangeSizeFromDB() throws SQLException {
        setMinOrMaxRangeSizeFromDB(getMaxRangeSizeQuery());
    }

    private String upperOrLowerToleranceRangeQuery(double minOrMaxRangeSize) {
        StringBuilder sqlQueryStringBuilder = new StringBuilder();
        sqlQueryStringBuilder.append("SELECT \"");
        sqlQueryStringBuilder.append(formatNumber(minOrMaxRangeSize));
        sqlQueryStringBuilder.append("\" FROM \"0\" " +
                "WHERE \"ПОЛЕ ДОПУСКА по ЕСДП\" = '");
        sqlQueryStringBuilder.append(tolerance.getQuality() + tolerance.getDeviation());
        sqlQueryStringBuilder.append("';");
        return sqlQueryStringBuilder.toString();
    }

    private void setUpperOrLowerToleranceRange(double minOrMaxRangeSize, String upperOrLowerToleranceRange) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(upperOrLowerToleranceRangeQuery(minOrMaxRangeSize))) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                if (upperOrLowerToleranceRange.equals("upperToleranceRange")) {
                    upperToleranceRange = resultSet.getDouble(1);
                } else if (upperOrLowerToleranceRange.equals("lowerToleranceRange")) {
                    lowerToleranceRange = resultSet.getDouble(1);
                }
            }
        }
    }

    private void setUpperToleranceRange() throws SQLException {
        setUpperOrLowerToleranceRange(getMinRangeSize(), "upperToleranceRange");
    }

    private void setLowerToleranceRange() throws SQLException {
        setUpperOrLowerToleranceRange(getMaxRangeSize(), "lowerToleranceRange");
    }

    private String formatNumber(double number) {
        Pattern pattern = Pattern.compile("\\d+\\.0");
        Matcher matcher = pattern.matcher(String.valueOf(number));
        return matcher.matches() ? String.valueOf(Math.round(number)) : String.valueOf(number);
    }

    double getMaxRangeSize() {
        return maxRangeSize;
    }

    double getMinRangeSize() {
        return minRangeSize;
    }

    double getLowerToleranceRange() {
        return lowerToleranceRange;
    }

    double getUpperToleranceRange() {
        return upperToleranceRange;
    }
}