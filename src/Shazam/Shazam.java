package Shazam;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class Shazam {

    private static final int RECORDER_BPP = 16;
    private static final int RECORDER_SAMPLERATE = 44100;
    private static final int FFT_SAMPLE_SIZE = 4096;



    public Shazam() {
        byte[] audioBytes = new byte[1];

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedInputStream in = new BufferedInputStream(new FileInputStream("sample.wav"));

            int read;
            byte[] buff = new byte[1024];
            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
            }
            out.flush();
            audioBytes = out.toByteArray();


        } catch (Exception err) {

        }

        Spectogram spect = new Spectogram(audioBytes);

        double[][] ob = spect.getValues();


        File outputfile = new File("image.jpg");
        try {
            ImageIO.write(spect.drawSpectrogram(), "jpg", outputfile);
        }catch (IOException err){

        }

    }





    public static void main(String[] args){
        new Shazam();
    }
}
