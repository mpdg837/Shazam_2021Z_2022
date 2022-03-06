package Shazam;

import Shazam.Analysing.Comparer;
import Shazam.Analysing.HashFile;
import Shazam.fingerprint.util.Directory;

import java.io.File;

public class ShazamConverter {
    public ShazamConverter(){
        try {

            File samplesFolder = new File("Samples/");
            String[] samplesFiles = samplesFolder.list();

            for(String file : samplesFiles){
                HashFile fileN = new HashFile(new File("Hashes/"+file+".hash"));

                fileN.setTitle(file);
                fileN.convert(new File("Samples/"+file));

                fileN.save();
            }


        }catch (Exception err){
            err.printStackTrace();
        }
    }

    public static void main(String[] args){
        new ShazamConverter();
    }
}
