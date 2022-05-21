package Shazam;

import Shazam.Analysing.Comparer;
import Shazam.Analysing.HashFile;
import Shazam.Audio.ShzazamRecorder;
import Shazam.DataBase.LoginData;
import Shazam.DataBase.Processing.GetMusicList;


import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.Instant;
import java.util.Scanner;

public class Shazam {


    public Shazam(){
        try {
            System.out.println("Łączenie z bazą danych...");
            boolean isConnected = false;
            Statement statement = null;

            Connection connection = null;
            try {

                connection = DriverManager.getConnection(LoginData.url, LoginData.username, LoginData.password);
                statement = connection.createStatement();
                isConnected = true;
            }catch (Exception ignore){

            }


            System.out.println("Aby rozpocząć wykrywanie utworu naciśnij dowolony klawisz ...");

            Scanner scan = new Scanner(System.in);
            scan.next();

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
