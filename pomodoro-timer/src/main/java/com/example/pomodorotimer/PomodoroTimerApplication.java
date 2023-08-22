package com.example.pomodorotimer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class PomodoroTimerApplication 
{
    @RestController
    public class PomodoroController 
    {
        private boolean timerActive = false;
        private final long pomodoroDuration = 25 * 60 * 1000; 
        private StringBuilder asciiTimerTemplate;

        public PomodoroController() 
        {
            // Initialize the ASCII template
            asciiTimerTemplate = new StringBuilder();
            asciiTimerTemplate.append("\u001B[36m"); // Cyan color
            asciiTimerTemplate.append("      /\\      \n");
            asciiTimerTemplate.append("     /  \\     \n");
            asciiTimerTemplate.append("    /    \\    \n");
            asciiTimerTemplate.append("   /------\\   \n");
            asciiTimerTemplate.append("  /        \\  \n");
            asciiTimerTemplate.append(" /          \\ \n");
            asciiTimerTemplate.append("╭────────────╮\n");
            asciiTimerTemplate.append("│ Pomodoro   │\n");
            asciiTimerTemplate.append("│ Time       │\n");
            asciiTimerTemplate.append("│ Remaining  │\n");
            asciiTimerTemplate.append("│ %02d:%02d   │\n");
            asciiTimerTemplate.append("╰────────────╯\n");
            asciiTimerTemplate.append("\u001B[0m"); // Reset color
        } 

        @GetMapping("/start")
        public String startTimer() 
        {
            if (!timerActive) 
            {
                timerActive = true;
                new Thread(this::runTimer).start();
                return "Timer started";
            }
            return "Timer is already active";
        }

        @GetMapping("/stop")
        public String stopTimer() 
        {
            if (timerActive) 
            {
                timerActive = false;
                return "Timer stopped";
            }
            return "No active timer to stop";
        }

        private void runTimer() {
            while (timerActive) {
                long remainingTime = pomodoroDuration;
        
                while (remainingTime > 0 && timerActive) {
                    updateAndDisplayAsciiTimer(remainingTime);
                    try {
                        Thread.sleep(1000); // Sleep for 1 second
                        remainingTime -= 1000;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.err.println("Timer thread interrupted: " + e.getMessage());
                    }
                }
        
                if (timerActive) {
                    System.out.println("Pomodoro completed!");
                    playAlertSound(); // Play the ringing sound here
                    timerActive = false; // Stop the timer loop
                }
            }
        }
        

        private void updateAndDisplayAsciiTimer(long millis) 
        {
            long minutes = millis / 60000;
            long seconds = (millis % 60000) / 1000;

            String formattedAsciiTimer = String.format(asciiTimerTemplate.toString(), minutes, seconds);
            System.out.print("\033[H\033[2J"); // Clear console
            System.out.print(formattedAsciiTimer); // Print updated timer
            System.out.flush();
        }

        private void playAlertSound() 
        {
            try 
            {
                File soundFile = new File("/Users/dgodstand/Downloads/trumpet_x.wav");
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) 
            {
                System.err.println("Error playing ring sound: " + e.getMessage());
            }
        }
    } 
    public static void main(String[] args) 
    {
        SpringApplication.run(PomodoroTimerApplication.class, args);
    }
}
