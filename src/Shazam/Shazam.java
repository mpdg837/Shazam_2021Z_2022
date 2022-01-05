package Shazam;

import main.java.io.honerlaw.audio.fingerprint.AudioFile;
import main.java.io.honerlaw.audio.fingerprint.hash.FingerPrint;
import main.java.io.honerlaw.audio.fingerprint.hash.Spectrogram;
import main.java.io.honerlaw.audio.fingerprint.hash.peak.HashedPeak;
import main.java.io.honerlaw.audio.fingerprint.util.Hash;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Shazam {

    public static void writeAudioToWavFile(byte[] data, AudioFormat format, String fn) throws Exception {
        AudioInputStream ais = new AudioInputStream(new ByteArrayInputStream(data), format, data.length);
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(fn));
    }

    public void record(){
        AudioFormat format = new AudioFormat(44100, 16, 1, true, true);
        TargetDataLine microphone;
        try {
            microphone = AudioSystem.getTargetDataLine(format);

            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int numBytesRead;
            byte[] data = new byte[microphone.getBufferSize() / 5];
            microphone.start();

            int bytesRead = 0;

            try {
                while (bytesRead < 1000000) { //Just so I can test if recording my mic works...
                    numBytesRead = microphone.read(data, 0, data.length);
                    bytesRead = bytesRead + numBytesRead;
                    //    System.out.println(bytesRead);
                    out.write(data, 0, numBytesRead);

                }
                microphone.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            writeAudioToWavFile(out.toByteArray(),format,"recorded.wav");

        }catch ( LineUnavailableException err){
            err.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public HashedPeak[] getPoints(String nazwa){
        record();
        File toFile = new File(nazwa);

        try {
            Spectrogram spect = new Spectrogram(new AudioFile(toFile));
            spect.render(toFile.getName()+"spect.png");
            FingerPrint fingerprint = new FingerPrint(new AudioFile(toFile));

            HashedPeak[] hashes = fingerprint.getHashes();

            System.out.println(hashes.length);

            return hashes;
        }catch (Exception err){
            err.printStackTrace();
            return null;
        }


    }
    public Shazam() {

        record();
        HashedPeak[] recorded = getPoints("recorded.wav");
        HashedPeak[] sample = getPoints("sample.wav");

        ArrayList<HashedPeak> detected = new ArrayList<>();

        int detects = 0;
        for(int x=0;x<recorded.length;x++){
            HashedPeak recordedpoints = recorded[x];
            if(recordedpoints.getPeakOne().getFreq()>5 && recordedpoints.getPeakOne().getFreq()<300 ) {
                if(recordedpoints.getPeakTwo().getFreq()>5 && recordedpoints.getPeakTwo().getFreq()<300 ) {

                for (int y = 0; y < sample.length; y++) {
                    HashedPeak samplePoints = sample[y];
                    if(samplePoints.getPeakOne().getFreq()>5 && samplePoints.getPeakOne().getFreq()<300 ) {
                        if (samplePoints.getPeakTwo().getFreq() > 5 && samplePoints.getPeakTwo().getFreq() < 300) {
                            if (!detected.contains(samplePoints)) {

                                if (samplePoints.getPeakOne().getFreq() == recordedpoints.getPeakOne().getFreq()) {

                                    if (samplePoints.getPeakTwo().getFreq() == recordedpoints.getPeakTwo().getFreq()) {

                                        if (samplePoints.getDelta() == recordedpoints.getDelta()) {

                                            detected.add(samplePoints);
                                            detects++;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    }
                }

            }
        }
        System.out.println(detects);

    }


    public static void main(String[] args){
        new Shazam();
    }
}
