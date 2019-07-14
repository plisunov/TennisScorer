package by.plisunov.scoreboard.service;

import by.plisunov.scoreboard.model.modxsite.SiteContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SiteContentRepository extends JpaRepository<SiteContent, Integer> {

    @Query(value = "select sc.id from modx_site_content sc where sc.parent = 21 and sc.pagetitle = :dateAsString", nativeQuery = true)
    Integer getTodaysTournament(@Param("dateAsString") String dateAsString);

    @Query(value = "select sc.id from SiteContent sc where sc.parenId = 21 and sc.title = :dateAsString")
    Integer getTodaysTournamentHQL(@Param("dateAsString") String dateAsString);




}
