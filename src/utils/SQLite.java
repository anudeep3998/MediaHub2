package utils;

import utils.constants.MyQueries;

import java.sql.*;
import java.util.Arrays;

/**
 * Created by Phoenix on 11/27/13.
 * This file contains methods to accomplish tasks concerning sql.
 */

public class SQLite {
    private final Hash myHasher;
    private PreparedStatement[] statement;
    private static Connection connection;
    private static Status lastConnectionAttemptStatus;
    private StringBuilder sqlQuery, hashText;                //reusing the same string builders to avoid 'new' calls, and optimize.
    private static int executedQueryCount;
    private static String lastConnectedDatabase;

    public SQLite() {
        //Initializations
        statement = new PreparedStatement[MyQueries.getSize()];
        Arrays.fill(statement, null);
        executedQueryCount = 0;
        connection = null;
        lastConnectionAttemptStatus = Status.FAIL;
        sqlQuery = new StringBuilder();
        hashText = new StringBuilder();
        myHasher = new Hash();
    }

    private static void setupConnection(String DbName) {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + DbName + ".db");
            lastConnectionAttemptStatus = Status.SUCCESS;
            lastConnectedDatabase = DbName;
            System.out.println("Opened new connection to DB : " + DbName);
        } catch (Exception e) {
            System.out.println("Error connecting to DB " + DbName + " : ");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            lastConnectionAttemptStatus = Status.FAIL;

        }
    }

    private PreparedStatement getStatement(MyQueries query) throws SQLException {
        if (lastConnectionAttemptStatus == Status.SUCCESS) {
            switch (query) {
                case CREATE_WATCHED_DIRECTORY_TABLE:
                    if (statement[0] == null) {
                        try {
                            statement[0] = connection.prepareStatement("CREATE TABLE WatchedDir ( DirectoryName VARCHAR NOT NULL,DirectoryPath VARCHAR NOT NULL, URI VARCHAR PRIMARY KEY  NOT NULL, AddedOn DATETIME, LastUpdatedOn DATETIME )");
                            System.out.println("Created a new CWDT statement");
                        } catch (SQLException e) {
                            e.printStackTrace();
                            System.out.println("Error creating new CWDT Statement :");
                            System.err.println(e.getClass().getName() + ": " + e.getMessage());
                            statement[0] = null;
                        }
                    }
                    return statement[0];

                case CREATE_MEDIA_ITEM_TABLE:
                    if (statement[1] == null) {
                        try {
                            statement[1] = connection.prepareStatement("CREATE TABLE WatchedDir ( DirectoryName VARCHAR NOT NULL,DirectoryPath VARCHAR NOT NULL, URI VARCHAR PRIMARY KEY  NOT NULL, AddedOn DATETIME, LastUpdatedOn DATETIME )");
                            System.out.println("Created a new CMIT statement");
                        } catch (SQLException e) {
                            e.printStackTrace();
                            System.out.println("Error creating new CMIT Statement :");
                            System.err.println(e.getClass().getName() + ": " + e.getMessage());
                            statement[1] = null;
                        }
                    }
                    return statement[1];

                case INSERT_WATCHED_DIRECTORY:
                    if (statement[2] == null) {
                        try {
                            statement[2] = connection.prepareStatement("INSERT INTO WatchedDir (DirectoryName,DirectoryPath,URI,AddedOn,LastUpdatedOn) VALUES(?,?,?,?,?)");
                            System.out.println("Created a new IWD statement");
                        } catch (SQLException e) {
                            e.printStackTrace();
                            System.out.println("Error creating new IWD Statement :");
                            System.err.println(e.getClass().getName() + ": " + e.getMessage());
                            statement[2] = null;
                        }
                    }
                    return statement[2];

                case INSERT_MEDIA_ITEM:
                    if (statement[3] == null) {
                        try {
                            statement[3] = connection.prepareStatement("CREATE TABLE WatchedDir ( DirectoryName VARCHAR NOT NULL,DirectoryPath VARCHAR NOT NULL, URI VARCHAR PRIMARY KEY  NOT NULL, AddedOn DATETIME, LastUpdatedOn DATETIME )");
                            System.out.println("Created a new IMT statement");
                        } catch (SQLException e) {
                            e.printStackTrace();
                            System.out.println("Error creating new IMT Statement :");
                            System.err.println(e.getClass().getName() + ": " + e.getMessage());
                            statement[3] = null;
                        }
                    }
                    return statement[3];

                case SEARCH_MEDIA_ITEM:
                    if (statement[4] == null) {
                        try {
                            statement[4] = connection.prepareStatement("CREATE TABLE WatchedDir ( DirectoryName VARCHAR NOT NULL,DirectoryPath VARCHAR NOT NULL, URI VARCHAR PRIMARY KEY  NOT NULL, AddedOn DATETIME, LastUpdatedOn DATETIME )");
                            System.out.println("Created a new SMT statement");
                        } catch (SQLException e) {
                            e.printStackTrace();
                            System.out.println("Error creating new SMT Statement :");
                            System.err.println(e.getClass().getName() + ": " + e.getMessage());
                            statement[4] = null;
                        }
                    }
                    return statement[4];

                case SEARCH_WATCHED_FOLDER:
                    if (statement[5] == null) {
                        try {
                            statement[5] = connection.prepareStatement("SELECT COUNT(*) AS count FROM WatchedDir WHERE URI=?");
                            System.out.println("Created a new SWF statement");
                        } catch (SQLException e) {
                            e.printStackTrace();
                            System.out.println("Error creating new SWF Statement :");
                            System.err.println(e.getClass().getName() + ": " + e.getMessage());
                            statement[5] = null;
                        }
                    }
                    return statement[5];

                default:
                    return null;
            }

        } else {
            System.out.println("Connection not available. Try reconnecting");
            return null;
        }
    }

    public Status initializeDb(String DbName) {
        setupConnection(DbName);              //updates lastConnectionAttemptStatus on execution
        return lastConnectionAttemptStatus;
    }

    /*public Status addWatchedDirectory(PreparedStatement stmt) {

        //PreparedStatement stmt = null;
        try {
            stmt = getStatement(MyQueries.INSERT_WATCHED_DIRECTORY);

            if (stmt != null) {
                try {
                    connection.setAutoCommit(false);
                    stmt.setString(1, DirectoryName);
                    stmt.setString(2, DirectoryPath);
                    stmt.setString(3, String.valueOf(myHasher.getHash(DirectoryPath)));
                    java.sql.Timestamp timestamp = new java.sql.Timestamp(System.currentTimeMillis());
                    stmt.setTimestamp(4, timestamp);
                    stmt.setTimestamp(5, timestamp);
                    //stmt.execute();
                    //stmt.clearParameters();
                if(++executedQueryCount>=15){
                    connection.commit();
                    executedQueryCount=0;
                }
                } catch (SQLException e) {
                    //e.printStackTrace();

                    String error = e.getMessage();
                    if (error.contains("URI is not unique"))
                        System.out.println("Folder [" + DirectoryPath + "] is already watched. Ignoring insert command");
                    else {
                        System.err.println(e.getClass().getName() + ": " + e.getMessage());
                        //e.printStackTrace();
                        try {
                            stmt.close();
                            //connection.close();
                            setupConnection(lastConnectedDatabase);
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }

                    }

                }


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Status.FAIL;
    }
*/
    public boolean isFolderWatched(String DirectoryHash) {
        PreparedStatement stmt = null;
        try {
            stmt = getStatement(MyQueries.SEARCH_WATCHED_FOLDER);
            stmt.setString(1, DirectoryHash);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt("count") > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addToInsertBatch(int batchId, String DirectoryName, String DirectoryPath) {
        switch (batchId) {
            case 1:
                try {
                    PreparedStatement stmt = getStatement(MyQueries.INSERT_WATCHED_DIRECTORY);
                    connection.setAutoCommit(false);
                    if (stmt != null) {
                        try {
                            String DirectoryHash = String.valueOf(myHasher.getHash(DirectoryPath));
                            if (!isFolderWatched(DirectoryHash)) {
                                stmt.setString(1, DirectoryName);
                                stmt.setString(2, DirectoryPath);
                                stmt.setString(3, DirectoryHash);
                                java.sql.Timestamp timestamp = new java.sql.Timestamp(System.currentTimeMillis());
                                stmt.setTimestamp(4, timestamp);
                                stmt.setTimestamp(5, timestamp);
                                if (++executedQueryCount > 1000) {
                                    forceExecuteStatement(MyQueries.INSERT_WATCHED_DIRECTORY);
                                } else {
                                    stmt.addBatch();
                                    //stmt.clearParameters();
                                    System.out.println("Folder [" + DirectoryPath + "] added to insert batch");
                                }
                            } else {
                                System.out.println("Folder [" + DirectoryPath + "] is already watched. Ignoring insert command");
                            }


                            //stmt.execute();
                            //stmt.clearParameters();
                            /*if(++executedQueryCount>=15){
                                connection.commit();
                                executedQueryCount=0;
                            }*/
                        } catch (SQLException e) {
                            //e.printStackTrace();

                            String error = e.getMessage();

                            System.err.println(e.getClass().getName() + ": " + e.getMessage());
                            //e.printStackTrace();


                        }

                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
    }

    public void forceExecuteStatement(MyQueries myQueries) {
        try {
            PreparedStatement stmt = getStatement(myQueries);
            long time = System.currentTimeMillis();
            stmt.executeBatch();
            connection.commit();
            System.out.println("Executed Batch in : " + (System.currentTimeMillis() - time));
            executedQueryCount = 0;
            statement[2] = null;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
