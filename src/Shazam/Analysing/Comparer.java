package Shazam.Analysing;

import Shazam.Audio.MakeSound;
import Shazam.DataBase.Analysing.RecordTable;
import Shazam.DataBase.LoginData;
import Shazam.fingerprint.AudioFile;
import Shazam.fingerprint.hash.peak.HashedPeak;

import javax.swing.plaf.nimbus.State;
import java.io.File;
import java.io.PrintWriter;
import java.sql.*;
import java.time.Duration;
import java.time.Instant;
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


                if (max < count) {
                    times.clear();
                    max = count;
                    maxN = n;
                    maxId = file.idOnline;

                }

                sum += count;

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

            if (max < counterMusic.get(n)) {
                max = counterMusic.get(n);
                maxN = n;
            }

            sum += counterMusic.get(n);

            n++;


        }
    }

    public boolean compare(boolean isConnected, Statement state, Instant start) throws SQLException{

        sum = 0;
        max = 0;
        maxN = 0;

        n=0;


        peaks = new ArrayList<>();

        try {
            for (HashedPeak peak : recordFile.getFingerPrint().getHashes()) {
                peaks.add(peak.getHashAsHex());
            }





            System.out.println("===");
            if(isConnected) {

                onlineAnalyse(state);

            }else {
                analyseOffline();
            }

            int avrg = sum/simplifiedMusic.size();

            if(simplifiedMusic.size()>0) {


                int avrga = (sum - max) / (simplifiedMusic.size() - 1);

                if(max < avrga * 1.5 || max < 110){
                    throw new AnalyseException("Nie można stwierdzić jednoznacznie który utwór jest odsłuchiwany");
                }

            }

            System.out.println("Wykryto utwór: nr "+maxN);
            System.out.println("Nazwa: "+simplifiedMusic.get(maxN).getTitle());
            System.out.println("Album: "+simplifiedMusic.get(maxN).getAlbum());
            System.out.println("Autor: "+simplifiedMusic.get(maxN).getAuthor());
            System.out.println("Rok: "+simplifiedMusic.get(maxN).getYear());

            maxTime(isConnected,state,simplifiedMusic.get(maxN).getTitle(),start);

        }catch (AnalyseException err){
            System.out.println("Błąd: "+err.toString());
            return false;
        }

        return true;

    }

    void maxTime(boolean isConnected,Statement state,String filename,Instant start) throws SQLException{
        if(isConnected) {
            ResultSet result = state.executeQuery("SELECT MAX(TimeHash) FROM Hashe WHERE UtworId=" + maxId);

            int maximum = 0;
            if (result.next()) {
                maximum = result.getInt(1);
            }

            int maxmax = 0;
            int maxTim = 0;
            int lastMaxTim = 0;

            int lastanalysed = 0;

            for (int n = 0; n <= maximum; n++) {
                ResultSet result2 = state.executeQuery("SELECT COUNT(*) FROM (SELECT DISTINCT record.HashId as hid1, record.HashCode as hash1, total.HashCode as hash2 " +
                        "FROM (SELECT DISTINCT HashCode FROM Hashe WHERE UtworId = " + maxId + " AND TimeHash = " + n + ") as total," +
                        "Record as record WHERE record.HashCode = total.HashCode) as total2");

                if (result2.next()) {
                    int getNum = result2.getInt(1);
                    if ((maxmax + lastMaxTim) / 2 < (getNum + lastanalysed) / 2) {
                        maxmax = getNum;
                        maxTim = n;
                        lastMaxTim = lastanalysed;
                    }

                    lastanalysed = getNum;
                }


            }
            try {
                Instant end = Instant.now();
                Duration timeElapsed = Duration.between(start, end);

                double partTime = ((double) maxTim / (double) maximum);
                int delta = (int) timeElapsed.toSeconds();

                MakeSound sound = new MakeSound();
                int full = (int)sound.time(new File("Samples/" + filename));

                double extra = (double)delta/(double)full;

                partTime += extra;

                int maxTime = sound.playSound("Samples/" + filename, 0, true);

                int actTim = (int) ((double) maxTime * (double) partTime);
                System.out.println("Czas: "+(partTime*full)+"s");
                sound.playSound("Samples/" + filename, actTim, false);
            }catch (Exception ignore){

            }
        }
    }

}
