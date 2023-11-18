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
        private volatile boolean timerActive = false;
        private final long workDuration = 25 * 60 * 1000; // 25 minutes work session
        private final long breakDuration = 5 * 60 * 1000; // 5 minutes break
        private final int sleepInterval = 1000; // 1 second
        private StringBuilder asciiTimerTemplate;
        private final Object lock = new Object();
        
        public PomodoroController() 
        {
            asciiTimerTemplate = new StringBuilder();
            asciiTimerTemplate.append("╭────────────────╮\n");
            asciiTimerTemplate.append("│   Pomodoro     │\n");
            asciiTimerTemplate.append("│                │\n");
            asciiTimerTemplate.append("│  Time Remaining│\n");
            asciiTimerTemplate.append("│   %02d:%02d     │\n");
            asciiTimerTemplate.append("│                │\n");
            asciiTimerTemplate.append("╰────────────────╯\n");
        } 

        @GetMapping("/start")
        public String startTimer() 
        {
            synchronized (lock) {
                if (!timerActive) 
                {
                    timerActive = true;
                    new Thread(this::runTimer).start();
                    return "Timer started";
                }
            }
            return "Timer is already active";
        }

        @GetMapping("/stop")
        public String stopTimer() 
        {
            synchronized (lock) {
                if (timerActive) 
                {
                    timerActive = false;
                    return "Timer stopped";
                }
            }
            return "No active timer to stop";
        }

        private void runTimer() {
            while (timerActive) {
                performWorkSession();
                performBreak();
            }
        }

        private void performWorkSession() 
        {
            long remainingTime = workDuration;
            while (remainingTime > 0 && timerActive) 
            {
                updateAndDisplayAsciiTimer(remainingTime);
                try {
                    Thread.sleep(sleepInterval);
                    remainingTime -= sleepInterval;
                } catch (InterruptedException e) 
                {
                    Thread.currentThread().interrupt();
                    System.err.println("Timer thread interrupted: " + e.getMessage());
                }
            }
            if (timerActive) 
            {
                System.out.println("Work session completed!");
                playAlertSound();
            }
        }
        
        private void performBreak() 
        {
            long remainingTime = breakDuration;
            while (remainingTime > 0 && timerActive) 
            {
                updateAndDisplayAsciiTimer(remainingTime);
                try {
                    Thread.sleep(sleepInterval);
                    remainingTime -= sleepInterval;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Timer thread interrupted: " + e.getMessage());
                }
            }
            if (timerActive) 
            {
                System.out.println("Break time completed!");
                playAlertSound();
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
            try {
                File soundFile = new File("/Users/dgodstand/Downloads/trumpet_x.wav");
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
            } catch (UnsupportedAudioFileException e) {
                System.err.println("Unsupported audio file: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("IO error while playing sound: " + e.getMessage());
            } catch (LineUnavailableException e) {
                System.err.println("Line unavailable: " + e.getMessage());
            }
        }
    } 

    public static void main(String[] args) 
    {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down...");
        }));
        
        SpringApplication.run(PomodoroTimerApplication.class, args);
    }
}

