package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Phoenix on 11/27/13.
 */

public class SQLite {
    private Statement statement;
    private Connection connection;
    private Status lastConnectionAttemptStatus;

    public SQLite(){
        //Initializations
        statement=null;
        connection=null;
        lastConnectionAttemptStatus=Status.FAIL;
    }

    private Status setupConnection(String DbName) {
        try {
            Class.forName("org.sqlite.JDBC");
            connection= DriverManager.getConnection("jdbc:sqlite:"+DbName+".db");
            lastConnectionAttemptStatus=Status.SUCCESS;
            System.out.println("Opened new connection to DB : "+DbName);
        }
        catch (Exception e){
            System.out.println("Error connecting to DB "+DbName+" : ");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            lastConnectionAttemptStatus=Status.FAIL;

        }
        return lastConnectionAttemptStatus;
    }

    private void getStatement() {
        if(statement==null&&lastConnectionAttemptStatus!=Status.FAIL){
            try {
                statement=connection.createStatement();
                System.out.println("Created a new statement");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error creating new Statement :");
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                statement=null;
            }
        }
        else{
            System.out.println("Statement already exists. Returning");
            return;
        }
    }

    public Status initializeDb(String DbName){
        setupConnection(DbName);
        getStatement();
        if(lastConnectionAttemptStatus==Status.SUCCESS&&statement!=null)
            return Status.SUCCESS;
        else
            return Status.FAIL;
    }


}
