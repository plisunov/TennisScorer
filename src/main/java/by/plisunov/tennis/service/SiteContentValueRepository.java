package by.plisunov.tennis.service;

import by.plisunov.tennis.model.modxsite.SiteContentValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SiteContentValueRepository extends JpaRepository<SiteContentValue, Integer> {

    @Query(value = "select scr.value from SiteContentValue scr where scr.contentId = :contentId and tmplvarid=2")
    String getContentValue(@Param("contentId") Integer contentId);

    SiteContentValue findByContentId(Integer contentId);

}
