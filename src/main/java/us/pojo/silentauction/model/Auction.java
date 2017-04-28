package us.pojo.silentauction.model;

import java.time.LocalDateTime;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Cacheable
@Entity
public class Auction {

    @Id
    private int id = 1;
    
    @Column(length=512)
    private String name;
    
    @Column(length=4096)
    private String description;
    
    private LocalDateTime ends;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEnds() {
        return ends;
    }

    public void setEnds(LocalDateTime ends) {
        this.ends = ends;
    }
}
