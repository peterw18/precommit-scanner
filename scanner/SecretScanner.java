package scanner;
import java.util.ArrayList;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class SecretScanner {

    // attributes
    private ArrayList<Secret> exposedSecrets = new ArrayList<Secret>();
    private ArrayList<Key> establishedKeys = new ArrayList<Key>();
    private String directory;
    private Integer timer_cleanup_ms = 500;

    private void loadKeys(){
        
        // define all common key regular expressions
        this.establishedKeys.add(new Key("AWS Access Key ID", "AKIA[0-9A-Z]{16}"));

        this.establishedKeys.add(new Key("Google API Key", "AIza[0-9A-Za-z\\-_]{35}"));
        this.establishedKeys.add(new Key("Google Cloud Service Account", "\"type\":\\s*\"service_account\""));

        this.establishedKeys.add(new Key("Slack Token", "xox[baprs]-([0-9a-zA-Z]{10,48})?"));
        this.establishedKeys.add(new Key("Stripe Secret Key", "sk_live_[0-9a-zA-Z]{24}"));
        this.establishedKeys.add(new Key("Stripe Publishable Key", "pk_live_[0-9a-zA-Z]{24}"));

        this.establishedKeys.add(new Key("GitHub Token", "ghp_[0-9a-zA-Z]{36}"));
        this.establishedKeys.add(new Key("GitHub App Token", "ghu_[0-9a-zA-Z]{36}"));
        this.establishedKeys.add(new Key("GitHub Fine-grained Token", "github_pat_[0-9a-zA-Z_]{22,255}"));

        this.establishedKeys.add(new Key("JWT", "eyJ[A-Za-z0-9_-]+\\.eyJ[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+"));

        this.establishedKeys.add(new Key("SSH Private Key", "-----BEGIN OPENSSH PRIVATE KEY-----"));
        this.establishedKeys.add(new Key("PGP Private Key Block", "-----BEGIN PGP PRIVATE KEY BLOCK-----"));

        this.establishedKeys.add(new Key("RSA Private Key", "-----BEGIN RSA PRIVATE KEY-----"));
        this.establishedKeys.add(new Key("DSA Private Key", "-----BEGIN DSA PRIVATE KEY-----"));
        this.establishedKeys.add(new Key("EC Private Key", "-----BEGIN EC PRIVATE KEY-----"));

        this.establishedKeys.add(new Key("JSON Web Encryption (JWE)", "eyJ[A-Za-z0-9_-]{10,}\\.[A-Za-z0-9_-]{10,}\\.[A-Za-z0-9_-]{10,}\\.[A-Za-z0-9_-]{10,}\\.[A-Za-z0-9_-]{10,}"));

        this.establishedKeys.add(new Key("Facebook Access Token", "EAACEdEose0cBA[0-9A-Za-z]+"));

        this.establishedKeys.add(new Key("Heroku API Key", "[hH]eroku[\\s\\n]*[a-z0-9]{32}"));
        this.establishedKeys.add(new Key("Mailgun API Key", "key-[0-9a-zA-Z]{32}"));

        this.establishedKeys.add(new Key("Firebase Server Key", "AAAA[A-Za-z0-9_-]{7}:[A-Za-z0-9_-]{140,}"));
        this.establishedKeys.add(new Key("SendGrid API Key", "SG\\.[A-Za-z0-9._-]{22}\\.[A-Za-z0-9._-]{43}"));

        this.establishedKeys.add(new Key("Square Access Token", "sq0atp-[0-9A-Za-z\\-_]{22}"));
        this.establishedKeys.add(new Key("Square OAuth Token", "sq0csp-[0-9A-Za-z\\-_]{43}"));

        this.establishedKeys.add(new Key("Shopify Access Token", "shpat_[a-fA-F0-9]{32}"));
        this.establishedKeys.add(new Key("Shopify Shared Secret", "shpss_[a-fA-F0-9]{32}"));

        this.establishedKeys.add(new Key("DigitalOcean Token", "do[0-9a-f]{30,32}"));

        this.establishedKeys.add(new Key("Private Key (Generic PEM)", "-----BEGIN PRIVATE KEY-----"));
    }

    // getters and setters
    public ArrayList<Key> getKeys(){
        return this.establishedKeys;
    }

    public ArrayList<Secret> getSecrets(){
        return this.exposedSecrets;
    }

    public Integer getCleanupTime(){
        return this.timer_cleanup_ms;
    }

    public String getDirectory(){
        return this.directory;
    }

    private void setDirectory(String dir){
        this.directory = dir;
    }

    public void addSecrets(ArrayList<Secret> secrets){
        if (secrets != null){
            this.exposedSecrets.addAll(secrets);
        }
    }


    public static void main(String[] args){

        // establish scanner
        SecretScanner secretScanner = new SecretScanner();

        // establish the directory
        if (args.length > 0){
            // get it from cli arguments
            secretScanner.setDirectory(args[0]);
        } else {
            // get it from stdin
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter directory: ");
            secretScanner.setDirectory(scanner.nextLine());

            scanner.close();
        }

        // add the given keys to the arrayList
        secretScanner.loadKeys();

        // create a fileFinder instance
        DirScanner fileFinder = new DirScanner(secretScanner.getDirectory().replace("../", ""));

        System.out.println("\nDirectory Path: "+new File(fileFinder.getDirectory()).getAbsolutePath());
        
        System.out.println("\nDiscovering all files...");

        // start the timer thread to show users the program is still running
        Thread discoverTimer = new timerThread();
        discoverTimer.start();

        // for every file, check if it is reasonable
        ArrayList<File> files = fileFinder.searchDirectory();

        // when finished, end the timer
        discoverTimer.interrupt();

        try {
            // gives time for the program to end the timer thread and run its cleanup
            Thread.sleep(secretScanner.getCleanupTime());
        } catch (InterruptedException e){
            System.out.println("sleep interrupted...");
        }

        System.out.println("\nSearching all files...");

        if (files != null){
            // start timer for user
            Thread searchTimer = new timerThread();
            searchTimer.start();

            // for every file that is appropriate
            for (File item : files) {
                // create a new FileChecker instance and run it over that file
                FileChecker keyChecker = new FileChecker(item);
                secretScanner.addSecrets(keyChecker.searchFile(secretScanner.getKeys()));
            }
            // stop the timer
            searchTimer.interrupt();

            try {
                // allow timer thread to cleanup
                Thread.sleep(secretScanner.getCleanupTime());
            } catch (InterruptedException e){
                System.out.println("sleep interrupted...");
            }
    
            System.out.println("\n\nSecrets found: " + secretScanner.getSecrets().size()+"\n\n");
    
            // display all secrets to the user
            for (Secret secret : secretScanner.getSecrets()){

                // get the path of the file respective to the given directory
                Path relativePath = Paths.get(secretScanner.getDirectory()).toAbsolutePath().relativize(Paths.get(secret.getFileName()).toAbsolutePath());
                System.out.println("\n🚨 "+secret.getKeyData().getKeyFor()+" found on line "+secret.getLineNumber()+" of file "+relativePath);
            }
        } else {
            System.err.println("\nNo files were found.");
        }
        
        System.out.println("\n\n");

        // exit the program with a code to pass back to the precommit
        System.exit(secretScanner.getSecrets().isEmpty() ? 0 : 1);
    }
}
