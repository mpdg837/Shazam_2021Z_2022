package Shazam.Analysing;

import Shazam.Audio.MakeSound;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;

public class ComparerTime {

    Comparer comp;

    public ComparerTime(Comparer comp){
        this.comp = comp;
    }
    void maxTime(boolean isConnected, Statement state, String filename, Instant start) throws SQLException {
        if(isConnected) {
            System.out.println("Wykrywanie czasu");
            ResultSet result = state.executeQuery("SELECT MAX(TimeHash) FROM Hashe WHERE UtworId=" + comp.maxId);

            int maximum = 0;
            if (result.next()) {
                maximum = result.getInt(1);
            }

            int maxTim = 0;

            String query = "SELECT COUNT(total2.hid1) as sum, total2.timeX FROM\n" +
                    "(SELECT record.HashId as hid1, record.HashCode as hash1, total.HashCode as hash2, total.TimeHash as timeX \n" +
                    "FROM (SELECT DISTINCT HashCode, TimeHash FROM Hashe WHERE UtworId = "+comp.maxId+") as total,\n" +
                    "Record as record WHERE record.HashCode = total.HashCode) as total2 GROUP BY timeX ORDER BY sum DESC;";

            ResultSet result2 = state.executeQuery(query);

            if (result2.next()) {
                maxTim = result2.getInt(2);
            }

            try {
                Instant end = Instant.now();
                Duration timeElapsed = Duration.between(start, end);

                double partTime = ((double) maxTim / (double) maximum);
                int delta = (int) timeElapsed.toSeconds();

                MakeSound sound = new MakeSound();

                sound.playSound("http://192.168.156.171/" + filename, partTime,delta, false);
            }catch (Exception err){
                err.printStackTrace();
            }
        }
    }

}
