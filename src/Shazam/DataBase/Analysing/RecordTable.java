package Shazam.DataBase.Analysing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class RecordTable {

    private ArrayList<String> peaks;

    public RecordTable(ArrayList<String> peaks){
        this.peaks = peaks;
    }

    public void send(Statement state) throws SQLException {

        StringBuilder queryX = new StringBuilder();

        state.executeUpdate("CREATE TEMPORARY TABLE Record(HashId int, HashCode varchar(40)); \n");


        queryX.append("INSERT INTO Record VALUES \n");
        int k = 0;
        for (String peak : peaks) {
            k++;
            if (k != peaks.size()) {
                queryX.append( "("+k+",'"+peak+"'),\n");
            } else {
                queryX.append( "("+k+",'"+peak+"');");
            }
        }

        try {
            PrintWriter printer = new PrintWriter(new File("record.sql"));
            printer.println("CREATE TEMPORARY TABLE Record(HashId int, HashCode varchar(40))");
            printer.println(queryX.toString());
            printer.close();
        }catch (FileNotFoundException ignore) {
        }

        state.executeUpdate(queryX.toString());
    }
}