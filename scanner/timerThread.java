package scanner;
import java.time.*;

public class timerThread extends Thread{
    
    public void run(){
        // start the timer
        Instant startTime = Instant.now();

        while (true) {
            // update the user
            System.out.print("\rTime elapsed: " + Duration.between(startTime, Instant.now()).getSeconds() + "s");

            // sleep for a second-ish
            try {

                Thread.sleep(1000);

            // when the thread is interrupted, finish and clean up
            } catch (InterruptedException e) {

                System.out.println("\rCompleted process in "+Duration.between(startTime, Instant.now()).getSeconds() + "s");
                break;

            }  
            
        }
    }
}
