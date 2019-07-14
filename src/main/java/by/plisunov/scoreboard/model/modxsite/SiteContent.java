package by.plisunov.scoreboard.model.modxsite;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "modx_site_content")
public class SiteContent {

    @Id
    private Integer id;

    @Column(name = "pagetitle")
    private String title;

    @Column(name = "longtitle")
    private String description;

    @Column(name = "parent")
    private Integer parenId;
}
