package utils;

import utils.constants.MyQueries;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
        executedQueryCount=0;
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

    private PreparedStatement getStatement(MyQueries query) {
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

    public Status addWatchedDirectory(String DirectoryName, String DirectoryPath) {

        PreparedStatement stmt = getStatement(MyQueries.INSERT_WATCHED_DIRECTORY);
        if (stmt != null) {
            try {
                stmt.setString(1, DirectoryName);
                stmt.setString(2, DirectoryPath);
                stmt.setString(3, String.valueOf(myHasher.getHash(DirectoryPath)));
                java.sql.Timestamp timestamp = new java.sql.Timestamp(System.currentTimeMillis());
                stmt.setTimestamp(4, timestamp);
                stmt.setTimestamp(5, timestamp);
                stmt.execute();
                stmt.clearParameters();
                /*if(++executedQueryCount>=15){
                    connection.commit();
                    executedQueryCount=0;
                }*/
            } catch (SQLException e) {
                //e.printStackTrace();

                String error = e.getMessage();
                if (error.contains("URI is not unique"))
                    System.out.println("Folder [" + DirectoryPath + "] is already watched. Ignoring insert command");
                else{
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

        return Status.FAIL;
    }


}
