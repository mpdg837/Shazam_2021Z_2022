package Shazam.DataBase.Processing;

import Shazam.DataBase.LoginData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ConnectionTest {

    public static Statement isConnected(){
        try (Connection connection = DriverManager.getConnection(LoginData.url, LoginData.username, LoginData.password)) {

            return connection.createStatement();
        }catch (Exception err){
            return null;
        }
    }
}
