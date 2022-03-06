package Shazam.Analysing;

import Shazam.fingerprint.AudioFile;
import Shazam.fingerprint.hash.peak.HashedPeak;

import java.io.File;
import java.util.ArrayList;

public class Comparer {
    private ArrayList<HashFile> simplifiedMusic = new ArrayList<>();
    private ArrayList<Integer> counterMusic = new ArrayList<>();
    private AudioFile recordFile;

    public Comparer(){

    }

    public void addMusic(HashFile objFile){
        simplifiedMusic.add(objFile);
        counterMusic.add(0);
    }

    public void setRecordFile(File file) throws Exception {
        recordFile = new AudioFile(file);
    }

    public void compare(){

        int sum = 0;
        int max = 0;
        int maxN = 0;


        ArrayList<String> peaks = new ArrayList<>();

        try {
            for (HashedPeak peak : recordFile.getFingerPrint().getHashes()) {
                peaks.add(peak.getHashAsHex());
            }

            int n = 0;

            System.out.println("Analiza bazy utworów");
            System.out.println("Ilość utworów: " + simplifiedMusic.size());


            System.out.println("===");

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

            int avrg = sum/simplifiedMusic.size();

            System.out.println("===");
            System.out.println("Podsumowanie analizy:");
            System.out.println("");

            if(simplifiedMusic.size()>0) {
                int avrga = (sum - max) / (simplifiedMusic.size() - 1);
                if (max < avrga * 2) {
                    throw new AnalyseException("Nie można dokonać jednoznaczengo wykrycia, nie znaleziono jednoznacznie pasującego utworu");
                }else{
                    System.out.println("Średnia ilość wspólnych punktów z pominięciem maksymalnego dopasowania: "+avrga);
                }

            }




            System.out.println("Średnia ilość wspólnych punktów: "+avrg);
            System.out.println("Maksymalna ilość wspólnych punktów: "+max);
            System.out.println("");
            System.out.println("Wykryto utwór: nr "+maxN);
            System.out.println("Nazwa: "+simplifiedMusic.get(maxN).getTitle());
            System.out.println("Album: "+simplifiedMusic.get(maxN).getAlbum());
            System.out.println("Autor: "+simplifiedMusic.get(maxN).getAuthor());
            System.out.println("Rok: "+simplifiedMusic.get(maxN).getYear());

        }catch (AnalyseException err){
            System.out.println("Błąd: "+err.toString());
        }



    }

}
