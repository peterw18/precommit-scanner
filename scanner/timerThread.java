package scanner;
import java.time.*;

public class timerThread extends Thread{
    
    public void run(){
        Instant startTime = Instant.now();
        while (true) {
            System.out.print("\rTime elapsed: " +
                Duration.between(startTime, Instant.now()).getSeconds() + "s");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("\rCompleted process in "+Duration.between(startTime, Instant.now()).getSeconds() + "s");
                break;
            }
            
            
        }
    }
}
