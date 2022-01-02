package Shazam;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class Spectogram {

    private static final int RECORDER_BPP = 16;
    private static final int RECORDER_SAMPLERATE = 44100;
    private static final int FFT_SAMPLE_SIZE = 4096;

    byte[] audioBytes;
    double[][] values;
    double[][] spectrogram;

    public Spectogram(byte[] samples){
        audioBytes = samples;
    }

    private double[] getHammingWindow() {
        int m = FFT_SAMPLE_SIZE / 2;
        double r;
        double pi = Math.PI;
        double[] w = new double[FFT_SAMPLE_SIZE];
        r = pi / (m + 1);
        for (int n = -m; n < m; n++) {
            w[m + n] = 0.5f + 0.5f * Math.cos(n * r);
        }
        return w;
    }

    public double[][] getValues(){



        spectrogram = new double[audioBytes.length / FFT_SAMPLE_SIZE ][256];

        int p = 0;
        boolean finished = false;

        double minvalue = audioBytes[0];
        double maxvalue = audioBytes[0];

        double[] hamming = getHammingWindow();

        for (int n = 0; n < audioBytes.length; n += FFT_SAMPLE_SIZE ) {

            byte[] windowBytes = new byte[FFT_SAMPLE_SIZE ];
            for (int k = 0; k < FFT_SAMPLE_SIZE ; k++) {
                if (n + k < audioBytes.length) {

                    double value = (double) audioBytes[n + k]/(double)255;
                    value *= hamming[k];
                    int byteValue = (int)(value*(double) 255);

                    windowBytes[k] =(byte) byteValue;
                } else {
                    finished = true;
                }
            }

            if (!finished) {
                double[] values = CalculateFFT.calculateFFT(windowBytes);

                for (int k = 0; k < 512; k++) {


                    if (values[k] < minvalue) {
                        minvalue = values[k];
                    }

                    if (values[k] > maxvalue) {
                        maxvalue = values[k];
                    }
                    if(k<256){
                        spectrogram[p][k] = values[k];
                    }
                }
            }
            p++;
        }

        if(minvalue==0){
            minvalue = 0.000000001f;
        }

        double maxValue = (double) audioBytes.length / FFT_SAMPLE_SIZE ;

        BufferedImage img = new BufferedImage((int)maxValue, 256, BufferedImage.TYPE_3BYTE_BGR);

        for (int x = 0; x < (int)maxValue; x ++) {

            for (int y = 0; y < 256; y++) {
                spectrogram[(int)x][y] = Math.log10(spectrogram[(int) x][y] - minvalue) / Math.log10(maxvalue - minvalue);
                double intense = 255 - spectrogram[(int)x][y] * 255;

                img.setRGB(x,511-y-256,(int)intense);

            }

        }

        File outputfile = new File("image.jpg");
        try {
            ImageIO.write(img, "jpg", outputfile);
        }catch (IOException err){

        }

        return spectrogram;

    }

    public BufferedImage drawSpectrogram(){
        double maxValue = (double) audioBytes.length / FFT_SAMPLE_SIZE ;

        BufferedImage img = new BufferedImage((int)maxValue, 256, BufferedImage.TYPE_3BYTE_BGR);

        for (int x = 0; x < (int)maxValue; x ++) {

            for (int y = 0; y < 256; y++) {
                double intense = 255 - spectrogram[(int)x][y] * 255;

                img.setRGB(x,511-y-256,(int)intense);

            }

        }


        return img;
    }
}
