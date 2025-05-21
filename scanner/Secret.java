package scanner;
import java.io.File;

public class Secret {
    // immutable attributes
    private File file;
    private Integer lineNumber;
    private Key keyData;

    // init
    public Secret(File file, Integer lineNumber, Key keyData){
        this.file = file;
        this.lineNumber = lineNumber;
        this.keyData = keyData;
    }

    // getters
    public String getFileName(){
        return this.file.getAbsolutePath();
    }

    public Integer getLineNumber(){
        return this.lineNumber;
    }

    public Key getKeyData(){
        return this.keyData;
    }
}
