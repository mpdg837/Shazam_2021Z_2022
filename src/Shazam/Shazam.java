package Shazam;

import Shazam.fingerprint.AudioFile;
import Shazam.fingerprint.hash.FingerPrint;
import Shazam.fingerprint.hash.Spectrogram;
import Shazam.fingerprint.hash.peak.HashedPeak;


import java.io.*;

public class Shazam {

    public Shazam(){
        try {
            AudioRecorder.record();
            AudioFile file = new AudioFile(new File("recorded.wav"));

            file.getSpectrogram().render("spect.png");
        }catch (Exception err){
            err.printStackTrace();
        }
    }

    public static void main(String[] args){
        new Shazam();
    }
}
