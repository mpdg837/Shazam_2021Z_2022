package Shazam;

import Shazam.Analysing.HashFile;
import Shazam.DataBase.Processing.ConnectionTest;
import Shazam.DataBase.Processing.Preparing;

import java.io.File;
import java.sql.Statement;

public class ShazamConverter {

    public boolean tryConnect() throws Exception {

        Statement connection = ConnectionTest.isConnected();

        if(connection != null) {
            System.out.println("Połączono z bazą danych");
            System.out.println("Rozpoczecie przetwarzania utworow");
            File samplesFolder = new File("Samples/");
            String[] samplesFiles = samplesFolder.list();
            System.out.println("Wykryto " + samplesFiles.length + " utwory do zaktualizowania");

            System.out.println("=====");
            int fullsize = 0;

            for (String file : samplesFiles) {

                System.out.println("Przetworzenie utworu " + file);
                HashFile fileN = new HashFile(new File("Hashes/" + file + ".hash"));


                System.out.println("Rozpoczecie dodawania utworu do bazy danych");
                fileN.setTitle(file);
                fileN.convert(new File("Samples/" + file));

                int size = (int) ((double) (fileN.getHashes().length * 32) / (double) 1024);

                System.out.println("Wygenerowano " + fileN.getHashes().length + " haszy (" + size + " kB)");

                fileN.save();
                System.out.println("==");

                fullsize += size;
            }

            System.out.println("Uporzadkowanie indeksow");

            Preparing prepare = new Preparing();

            System.out.println("Proces aktualizacji został zakończony sukcesem");
            System.out.println("Baza została zaktualizowana o " + fullsize + " kB");
            return true;
        }else{
            return false;
        }
    }
    public ShazamConverter(){
        try {
            System.out.println("Narzędzie do dodawania utworów do systemu Shazam");
            System.out.println("Prosimy nie wyłączać narzędzia podczas działania");
            System.out.println("=====");
            System.out.println("Sprawdzam połączenie");

            int n=0;
            while (!tryConnect()){
                n++;
                System.out.println("Nie można połączyć się z bazą, ponowna próba połączenia numer: "+n);

                if(n==20){
                    System.out.println("Nie można połączyć się z bazą danych. Aktualizacja nie powiodła się.");
                    break;
                }
            }
        }catch (Exception err){
            err.printStackTrace();
        }
    }

    public static void main(String[] args){
        new ShazamConverter();
    }
}
