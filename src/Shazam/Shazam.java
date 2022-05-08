package Shazam;

import Shazam.Analysing.Comparer;
import Shazam.Analysing.HashFile;
import Shazam.Audio.AudioRecorder;
import Shazam.DataBase.LoginData;
import Shazam.DataBase.Processing.ConnectionTest;
import Shazam.DataBase.Processing.GetMusicList;


import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Shazam {

    public Shazam(){
        try {
            AudioRecorder.record();

            Comparer compare = new Comparer();
            compare.setRecordFile(new File("recorded.wav"));

            Statement connected = ConnectionTest.isConnected();
            boolean isConnected = connected != null;

            if(!isConnected) {
                System.out.println("Wyszukiwania utworów w ograniczonym trybie offline");
                File samplesFolder = new File("Hashes/");
                String[] samplesFiles = samplesFolder.list();

                for (String file : samplesFiles) {

                    HashFile hfh = new HashFile(new File("Hashes/" + file));
                    hfh.read();

                    compare.addMusic(hfh);
                }

                compare.compare(isConnected,null);
            }else{
                try (Connection connection = DriverManager.getConnection(LoginData.url, LoginData.username, LoginData.password)) {
                    System.out.println("Połączono się z bazą");

                    Statement state = connection.createStatement();

                    GetMusicList list = new GetMusicList(state);
                    int[] samplesFiles = list.musicIds();

                    for (int file : samplesFiles) {

                        HashFile hfh = new HashFile(file,state);

                        compare.addMusic(hfh);

                    }

                    compare.compare(isConnected,state);
                }
            }



        }catch (Exception err){
            err.printStackTrace();
        }
    }

    public static void main(String[] args){
        new Shazam();
    }
}
