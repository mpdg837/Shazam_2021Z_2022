package Shazam;

import Shazam.fingerprint.AudioFile;
import Shazam.fingerprint.hash.FingerPrint;
import Shazam.fingerprint.hash.Spectrogram;
import Shazam.fingerprint.hash.peak.HashedPeak;


import java.io.File;

public class Shazam {

    public Shazam(){
        try {
            AudioFile file = new AudioFile(new File("sam.mp3"));


        }catch (Exception err){
            err.printStackTrace();
        }
    }

    public static void main(String[] args){
        new Shazam();
    }
}
