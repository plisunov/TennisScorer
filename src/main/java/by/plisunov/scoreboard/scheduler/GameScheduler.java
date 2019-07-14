package by.plisunov.scoreboard.scheduler;


import by.plisunov.scoreboard.service.SchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GameScheduler {

    @Autowired
    private SchedulerService schedulerService;

    @Scheduled(fixedRate = 1000 * 60 * 5)
    public void replaceGameToSiteDB() throws IOException {
        schedulerService.writeInfoToDataBase();
    }

    @Scheduled(fixedRate = 1000 *60)
    public void fillScheduler() throws IOException {
        schedulerService.getScheduledGames();
    }


}
