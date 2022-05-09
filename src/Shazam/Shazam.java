package Shazam;

import Shazam.Analysing.Comparer;
import Shazam.Analysing.HashFile;
import Shazam.Audio.AudioRecorder;
import Shazam.Audio.ShzazamRecorder;
import Shazam.DataBase.LoginData;
import Shazam.DataBase.Processing.ConnectionTest;
import Shazam.DataBase.Processing.GetMusicList;
import Shazam.fingerprint.AudioFile;
import Shazam.fingerprint.hash.peak.HashedPeak;


import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;

public class Shazam {


    public Shazam(){
        try {

            int trym = 0;
            boolean success = false;



            while (!success && trym < 4) {
                System.out.println("Proba numer: "+(trym+1));
                trym ++;
                ShzazamRecorder record = new ShzazamRecorder();
                Comparer compare = new Comparer();
                Instant start = Instant.now();

                record.listen();

                System.out.println("Koniec nagrywania ...");
                compare.setRecordFile(record.getHashes());

                Statement connected = ConnectionTest.isConnected();
                boolean isConnected = connected != null;


                if (!isConnected) {
                    System.out.println("Wyszukiwania utworów w ograniczonym trybie offline");
                    File samplesFolder = new File("Hashes/");
                    String[] samplesFiles = samplesFolder.list();

                    for (String file : samplesFiles) {

                        HashFile hfh = new HashFile(new File("Hashes/" + file));
                        hfh.read();

                        compare.addMusic(hfh);
                    }
                    System.out.println("Skanowanie ...");
                    success = compare.compare(isConnected, null,start);
                } else {

                    try (Connection connection = DriverManager.getConnection(LoginData.url, LoginData.username, LoginData.password)) {


                        Statement state = connection.createStatement();

                        GetMusicList list = new GetMusicList(state);
                        int[] samplesFiles = list.musicIds();

                        for (int file : samplesFiles) {

                            HashFile hfh = new HashFile(file, state);

                            compare.addMusic(hfh);

                        }
                        System.out.println("Skanowanie ...");
                        success = compare.compare(isConnected, state,start);
                    }
                }
            }
            if(!success){
                System.out.println("Nie udalo wykryc się utworu, program konczy dzialanie");
            }else{
                System.out.println("Program wykryl utwor i konczy dzialanie");
            }

        }catch (Exception err){
            err.printStackTrace();
        }
    }

    public static void main(String[] args){
        new Shazam();
    }
}
