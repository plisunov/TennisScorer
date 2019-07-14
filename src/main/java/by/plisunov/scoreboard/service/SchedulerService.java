package by.plisunov.scoreboard.service;

import java.io.IOException;

public interface SchedulerService {

    void writeInfoToDataBase() throws IOException;

    void getScheduledGames() throws IOException;
}
