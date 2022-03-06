package Shazam;

import Shazam.Analysing.Comparer;
import Shazam.Analysing.HashFile;


import java.io.*;

public class Shazam {

    public Shazam(){
        try {
            AudioRecorder.record();

            Comparer compare = new Comparer();
            compare.setRecordFile(new File("recorded.wav"));

            File samplesFolder = new File("Hashes/");
            String[] samplesFiles = samplesFolder.list();

            for(String file : samplesFiles){

                HashFile hfh = new HashFile(new File("Hashes/"+file));
                hfh.read();

                compare.addMusic(hfh);
            }

            compare.compare();

        }catch (Exception err){
            err.printStackTrace();
        }
    }

    public static void main(String[] args){
        new Shazam();
    }
}
