package by.plisunov.tennis.model.modxsite;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "modx_site_tmplvar_contentvalues")
public class SiteContentValue {

    @Id
    private Integer id;

    @Column(name = "contentid")
    private Integer contentId;

    @Column(name = "tmplvarid")
    private Integer tmplvarid;


    @Column(name = "value", columnDefinition = "MEDIUMTEXT")
    private String value;


}


