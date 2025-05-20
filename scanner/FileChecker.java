package scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.*;

public class FileChecker {
    // attributes
    private File target;
    private Integer lineCount;
    private ArrayList<Secret> secrets = new ArrayList<Secret>();

    // getters and setters

    public File getTargetFile(){
        return this.target;
    }

    public ArrayList<Secret> getSecrets(){
        return this.secrets;
    }

    private Integer getLineCount(){
        return this.lineCount;
    }

    private void addSecret(Secret secret){
        this.secrets.add(secret);
    }



    private static int countLines(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.US_ASCII))) {
            int lines = 0;
            while (reader.readLine() != null) {
                lines++;
            }
            return lines;
        }
    }

    public void setTargetFile(File file){
        this.target = file;
        try{
            this.lineCount = countLines(file);
        } catch (IOException e){
            System.err.printf("Failed to read file: %s (%s)%n", file.getName(), e.getMessage());
        }  
    }

    public FileChecker(File file){
        setTargetFile(file);
    }

    public ArrayList<Secret> searchFile(ArrayList<Key> keys){
        if (getTargetFile() == null){
            System.err.printf("Target file not set.");
            return null;
        }
        
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(new FileInputStream(getTargetFile()), StandardCharsets.US_ASCII))) {
                
            for (int i = 1; i <= getLineCount(); i++){
                String line = reader.readLine();
                for (Key key : keys){
                    Matcher matcher = key.getExpression().matcher(line);
                    if (matcher.find()){
                        this.addSecret(new Secret(getTargetFile(), i, key));
                    }

                }
            }

        } catch (IOException e){
            System.err.printf("Failed to read file: %s (%s)%n", getTargetFile().getName(), e.getMessage());
        }

        return getSecrets();
    }
}
