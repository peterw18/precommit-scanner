package scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.*;
import java.util.stream.IntStream;

public class FileChecker {
    // attributes
    private File target;
    private Integer lineCount;
    private ArrayList<Secret> secrets = new ArrayList<Secret>();

    // init
    public FileChecker(File file){
        setTargetFile(file);
    }

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

    public void setTargetFile(File file){
        this.target = file;
        try{
            this.lineCount = countLines(file);
        } catch (IOException e){
            System.err.printf("Failed to read file: %s (%s)%n", file.getName(), e.getMessage());
        }  
    }

    // counts the number of lines in a file
    // pure function
    private static int countLines(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.US_ASCII))) {
            Integer lines = 0;
            while (reader.readLine() != null) {
                lines++;
            }
            return lines;
        }
    }

    // returns whether there is a secret in a given line with a given key
    // pure function
    private Secret findSecretInLine(File filename, String line, Integer lineNum, Key key){

        Matcher matcher = key.getExpression().matcher(line);
        return matcher.find() ? new Secret(filename, lineNum, key) : null;

    }

    // iterates over the file and checks for secrets
    // uses higher-order functions where possible
    public ArrayList<Secret> searchFile(ArrayList<Key> keys){
        if (getTargetFile() == null){
            System.err.printf("Target file not set.");
            return null;
        }
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(getTargetFile()), StandardCharsets.US_ASCII))) {
                
            IntStream.rangeClosed(1, getLineCount())
                .mapToObj(i -> {
                    try {
                        return new AbstractMap.SimpleEntry<>(i, reader.readLine());
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                })
                .forEach(entry -> {
                    int lineNumber = entry.getKey();
                    String line = entry.getValue();
                    keys.stream()
                        .map(key -> findSecretInLine(getTargetFile(), line, lineNumber, key))
                        .filter(Objects::nonNull)
                        .forEach(this::addSecret);
                });

        } catch (IOException e){
            System.err.printf("Failed to read file: %s (%s)%n", getTargetFile().getName(), e.getMessage());
        }

        return getSecrets();
    }
}
