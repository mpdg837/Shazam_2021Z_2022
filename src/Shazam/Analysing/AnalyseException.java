package Shazam.Analysing;

public class AnalyseException extends Exception{
    private String message="";

    public AnalyseException(String message){
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
