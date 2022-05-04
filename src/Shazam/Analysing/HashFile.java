package Shazam.Analysing;

import Shazam.fingerprint.AudioFile;
import Shazam.fingerprint.hash.peak.HashedPeak;
import Shazam.fingerprint.util.Hash;

import java.io.*;
import java.util.ArrayList;

public class HashFile {

    private String title = "";
    private String author = "";
    private String year = "";
    private String album = "";

    private File file;

    private ArrayList<String> hashes ;

    public HashFile(File file){
        hashes = new ArrayList<>();
        this.file = file;

    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setAuthor(String author){
        this.author = author;
    }

    public void setYear(String year) throws Exception{

        char[] znaki = year.toCharArray();

        boolean isNumber = true;

        if(znaki.length==4){
            for(char znak: znaki){
                if(znak <=47 || znak >=58) isNumber = false;
            }

            if(isNumber){
                this.year = year;
            }else{
                throw new Exception("To nie jest data");
            }

        }else{
            throw new Exception("To nie jest data");
        }

    }

    public void setAlbum(String album){
        this.album = album;
    }

    public String getAlbum() {
        return album;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getAuthor() {
        return author;
    }

    public void convert(File audioFile) throws Exception {
        AudioFile fileA = new AudioFile(audioFile.getAbsoluteFile());

        fileA.getSpectrogram().render("spect_"+audioFile.getName()+".png");
        HashedPeak[] peaks = fileA.getFingerPrint().getHashes();
        ArrayList<String> peaksHex = new ArrayList<>();

        for(HashedPeak peak : peaks){
            peaksHex.add(peak.getHashAsHex());
        }

        hashes = peaksHex;


    }

    public boolean contains(String hashPeak){
        return hashes.contains(hashPeak);
    }

    public void save() throws Exception {
        if(file.exists()) file.delete();

        PrintWriter writer = new PrintWriter(file);

        writer.println(title);
        writer.println(author);
        writer.println(year);
        writer.println(album);

        for(String hash : hashes){
            writer.println(hash);
        }

        writer.close();
    }

    public String[] getHashes(){
        String[] hashesPeaks = new String[hashes.size()];

        int n=0;
        for(String hash: hashes){
            hashesPeaks[n] = hash;
            n++;
        }

        return hashesPeaks;
    }

    public void read() throws IOException {
        FileInputStream stream = new FileInputStream(file);
        BufferedInputStream streamBuf = new BufferedInputStream(stream);
        InputStreamReader streamReader = new InputStreamReader(streamBuf);
        LineNumberReader reader = new LineNumberReader(streamReader);

        int n=0;
        String line ="";
        while ((line = reader.readLine()) !=null){

            switch (n){
                case 0 -> {title = line;}
                case 1 -> {author = line;}
                case 2 -> {year = line;}
                case 3 -> {album = line;}
                default -> {
                    hashes.add(line);
                }
            }
            n++;
        }
    }
}
