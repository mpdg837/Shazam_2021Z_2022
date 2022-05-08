package Shazam.Audio;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.*;

public class MakeSound {

    public static int BUFFER_SIZE = 128000;
    private URL soundFile;
    private File soundFile1;
    private AudioInputStream audioStream;
    private AudioFormat audioFormat;
    private SourceDataLine sourceLine;

    public double time(File file) throws UnsupportedAudioFileException, IOException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
        AudioFormat format = audioInputStream.getFormat();
        long frames = audioInputStream.getFrameLength();
       return (frames+0.0) / format.getFrameRate();
    }
    public int playSoundURL(String url,int timeStart,boolean countTime){

        String strFilename = url;

        try {
            soundFile = new URL(strFilename);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            audioStream = AudioSystem.getAudioInputStream(soundFile);
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }

        return playing(timeStart,countTime);
    }

    public int playSound(String filename,int timeStart, boolean countTime){

        String strFilename = filename;

        try {
            soundFile1 = new File(strFilename);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            audioStream = AudioSystem.getAudioInputStream(soundFile1);
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }

        return playing(timeStart,countTime);
    }

    private int playing(int timeStart,boolean countTime){

        audioFormat = audioStream.getFormat();

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        try {
            sourceLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceLine.open(audioFormat);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        sourceLine.start();

        int time = 0;
        int nBytesRead = 0;
        byte[] abData = new byte[BUFFER_SIZE];
        while (nBytesRead != -1) {
            try {
                nBytesRead = audioStream.read(abData, 0, abData.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (nBytesRead >= 0 && time >timeStart && !countTime) {
                @SuppressWarnings("unused")
                int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
            }

            time += 1;
        }


        sourceLine.drain();
        sourceLine.close();

        return time;
    }
}