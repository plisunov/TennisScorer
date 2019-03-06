package by.plisunov.tennis.service;

import java.io.IOException;

public interface SchedulerService {

    void writeInfoToDataBase() throws IOException;

    void getScheduledGames() throws IOException;
}
