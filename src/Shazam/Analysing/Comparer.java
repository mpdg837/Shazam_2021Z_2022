package Shazam.Analysing;

import Shazam.DataBase.Analysing.RecordTable;
import Shazam.DataBase.LoginData;
import Shazam.fingerprint.AudioFile;
import Shazam.fingerprint.hash.peak.HashedPeak;

import javax.swing.plaf.nimbus.State;
import java.io.File;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Comparer {
    private ArrayList<HashFile> simplifiedMusic = new ArrayList<>();
    private ArrayList<Integer> counterMusic = new ArrayList<>();
    private AudioFile recordFile;

    ResultSet maximumTimes;

    private int maxTime = 0;
    private int sum = 0;
    private int max = 0;
    private int maxN = 0;
    private ArrayList<String> peaks;

    private int n = 0;

    int maxId= 0;

    public Comparer(){

    }

    public void addMusic(HashFile objFile){
        simplifiedMusic.add(objFile);
        counterMusic.add(0);
    }

    public void addMusicFromOnline(int id){

    }
    public void setRecordFile(File file) throws Exception {
        recordFile = new AudioFile(file);
    }
    public void onlineAnalyse(Statement state) throws SQLException, AnalyseException{
        RecordTable recordTable = new RecordTable(peaks);
        recordTable.send(state);

        ArrayList<Integer> times = new ArrayList<>();


        for (HashFile file : simplifiedMusic) {
            if (file.online) {

                ResultSet result = state.executeQuery("SELECT COUNT(*) FROM (SELECT DISTINCT record.HashId as hid1, record.HashCode as hash1, total.HashCode as hash2 " +
                        "FROM (SELECT DISTINCT HashCode FROM Hashe WHERE UtworId = "+file.idOnline+") as total," +
                        "Record as record WHERE record.HashCode = total.HashCode) as total2");

                int count = 0;
                while (result.next()) {
                    count = result.getInt(1);
                }



                if (max == count && max != 0) {
                    throw new AnalyseException("Nie można dokonać jednoznaczengo wykrycia, znaleziono dwa utowry pasujące");
                }
                if (max < count) {
                    times.clear();
                    max = count;
                    maxN = n;
                    maxId = file.idOnline;

                }

                sum += count;
                System.out.println((n + 1) + ") Nazwa: '" + file.getTitle() + "' Ilość wspólnych punktów charakterystycznych: " + count);
                n++;

            }
        }


    }

    public void analyseOffline() throws AnalyseException{
        for (HashFile file : simplifiedMusic) {

            for (String hash : peaks) {
                if (file.contains(hash)) {
                    int number = counterMusic.get(n);
                    number++;
                    counterMusic.set(n, number);
                }
            }

            if (max == counterMusic.get(n) && max != 0) {
                throw new AnalyseException("Nie można dokonać jednoznaczengo wykrycia, znaleziono dwa utowry pasujące");
            }
            if (max < counterMusic.get(n)) {
                max = counterMusic.get(n);
                maxN = n;
            }

            sum += counterMusic.get(n);
            System.out.println((n + 1) + ") Nazwa: '" + file.getTitle() + "' Ilość wspólnych punktów charakterystycznych: " + counterMusic.get(n));
            n++;


        }
    }

    public void compare(boolean isConnected, Statement state) throws SQLException{

        sum = 0;
        max = 0;
        maxN = 0;

        n=0;


        peaks = new ArrayList<>();

        try {
            for (HashedPeak peak : recordFile.getFingerPrint().getHashes()) {
                peaks.add(peak.getHashAsHex());
            }



            System.out.println("Analiza bazy utworów");
            System.out.println("Ilość utworów: " + simplifiedMusic.size());


            System.out.println("===");
            if(isConnected) {

                onlineAnalyse(state);

            }else {
                analyseOffline();
            }

            int avrg = sum/simplifiedMusic.size();

            System.out.println("===");
            System.out.println("Podsumowanie analizy:");
            System.out.println("");

            System.out.println("Średnia ilość wspólnych punktów: "+avrg);
            System.out.println("Maksymalna ilość wspólnych punktów: "+max);
            System.out.println("");

            if(simplifiedMusic.size()>0) {


                int avrga = (sum - max) / (simplifiedMusic.size() - 1);

                System.out.println("Średnia ilość wspólnych punktów z pominięciem maksymalnego dopasowania: "+avrga);

                if(max < avrga * 1.75){
                    throw new AnalyseException("Nie można stwierdzić jednoznacznie który utwór jest odsłuchiwany");
                }

            }

            System.out.println("Wykryto utwór: nr "+maxN);
            System.out.println("Nazwa: "+simplifiedMusic.get(maxN).getTitle());
            System.out.println("Album: "+simplifiedMusic.get(maxN).getAlbum());
            System.out.println("Autor: "+simplifiedMusic.get(maxN).getAuthor());
            System.out.println("Rok: "+simplifiedMusic.get(maxN).getYear());

            if(isConnected) {
                ResultSet result = state.executeQuery("SELECT MAX(TimeHash) FROM Hashe WHERE UtworId="+maxId);

                int maximum = 0;
                if(result.next()){
                    maximum = result.getInt(1);
                }

                int maxmax=0;
                int maxTim = 0;

                for(int n=0;n<=maximum;n++) {
                    ResultSet result2 = state.executeQuery("SELECT COUNT(*) FROM (SELECT DISTINCT record.HashId as hid1, record.HashCode as hash1, total.HashCode as hash2 " +
                            "FROM (SELECT DISTINCT HashCode FROM Hashe WHERE UtworId = " + maxId + " AND TimeHash = " + n + ") as total," +
                            "Record as record WHERE record.HashCode = total.HashCode) as total2");

                    if (result2.next()) {
                        int getNum = result2.getInt(1);

                        if(maxmax<getNum){
                            maxmax = getNum;
                            maxTim = n;
                        }
                    }


                }

                System.out.println("Czas: "+(maxTim*8.73)+" s");
            }

        }catch (AnalyseException err){
            System.out.println("Błąd: "+err.toString());
        }



    }

}
