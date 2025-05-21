package scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;

public class DirScanner {
    // attributes
    private String currentdirectory;
    private String directory;
    private ArrayList<File> files = new ArrayList<File>();

    // init
    public DirScanner(String dir){
        setDirectory(dir);
        setCurrentDirectory(dir);
    }

    // getters and setters
    public String getDirectory(){
        return this.directory;
    }

    public ArrayList<File> getFiles(){
        return this.files;
    }

    public String getCurrentDirectory(){
        return this.currentdirectory;
    }

    public void setDirectory(String dir){
        this.directory = dir;
    }

    public void setCurrentDirectory(String dir){
        this.currentdirectory = dir;
    }

    public void addToFiles(File file){
        this.files.add(file);
    }

    // returns whether a file contains only ascii characters
    // if not, assume no key will be present
    // pure function
    private static boolean isAsciiFile(File file) throws IOException {
        try (InputStream is = new FileInputStream(file)) {
            int b;
            while ((b = is.read()) != -1) {
                if (b > 127) return false;
            }
        }
        return true;
    }

    // lists the files in a directory, recursively
    // uses a higher order function to go through the directory
    public ArrayList<File> searchDirectory(){
        File dir = new File(getCurrentDirectory());
        if (!dir.isDirectory()) {
            System.err.println("Provided path is not a directory.");
            return null;
        }

        File[] files = dir.listFiles();
        if (files == null) {
            System.err.println("Could not list files in the directory.");
            return null;
        }

        Arrays.stream(files).forEach(file -> {

            if (file.isFile()) {
                try {

                    if (isAsciiFile(file)) {
                        this.files.add(file);
                    }

                } catch (IOException e) {
                    System.err.printf("Failed to process file: %s (%s)%n", file.getName(), e.getMessage());
                }

            } else if (file.isDirectory()) {
                
                /* change the current directory to the new folder
                and iterate over them before continuing */
                setCurrentDirectory(file.getAbsolutePath());
                searchDirectory();
            }
        });


        return getFiles();
    }
}
