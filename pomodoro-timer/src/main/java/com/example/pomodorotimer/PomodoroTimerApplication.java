package com.example.pomodorotimer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class PomodoroTimerApplication {

    @RestController
public class PomodoroController 
{

    private boolean timerActive = false;
    private final long pomodoroDuration = 25 * 60 * 1000;

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

    private void runTimer() 
    {
        try {
            Thread.sleep(pomodoroDuration);
            timerActive = false;
            System.out.println("Pomodoro completed!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

    public static void main(String[] args) 
    {
		SpringApplication.run(PomodoroTimerApplication.class, args);
	}
}


