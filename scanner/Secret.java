package scanner;
import java.io.File;

public class Secret {
    // attributes
    private File file;
    private Integer lineNumber;
    private Key keyData;

    // init
    public Secret(File file, Integer lineNumber, Key keyData){
        this.setFileName(file);
        this.setLineNumber(lineNumber);
        this.setKeyData(keyData);
    }

    // getters and setters
    public String getFileName(){
        return this.file.getAbsolutePath();
    }

    public Integer getLineNumber(){
        return this.lineNumber;
    }

    public Key getKeyData(){
        return this.keyData;
    }

    public boolean setFileName(File name){
        if (this.checkFileName(name)){

            this.file = name;
            return true;

        } else {

            return false;
        }
        
    }

    public boolean setLineNumber(Integer line){
        if (this.checkLineNumber(line)){

            this.lineNumber = line;
            return true;

        } else {

            return false;
        }
    }

    public boolean setKeyData(Key data){
        if (this.checkKey(data)){

            this.keyData = data;
            return true;

        } else {

            return false;
        }
    }

    // private methods
    private boolean checkFileName(File name){
        return true;
    }

    private boolean checkLineNumber(Integer line){
        return true;
    }

    private boolean checkKey(Key data){
        return true;
    }
}
