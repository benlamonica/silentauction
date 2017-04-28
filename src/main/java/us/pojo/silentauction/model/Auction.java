package us.pojo.silentauction.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
    
    private String organizer;
    
    private LocalDateTime ends;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

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

    public boolean isAuctionClosed() {
        return LocalDateTime.now().isAfter(ends);
    }
    
    public String getTimeLeft() {
        LocalDateTime now = LocalDateTime.now();
        long days = now.until(ends, ChronoUnit.DAYS);
        if (days == 0) {
            long hours = now.until(ends,  ChronoUnit.HOURS);
            if (hours == 0) {
                long minutes = now.until(ends,  ChronoUnit.MINUTES);
                if (minutes == 0) {
                    long seconds = now.until(ends,  ChronoUnit.SECONDS);
                    return seconds + " seconds";
                }
                return minutes + " minutes";
            }
            return hours + " hours";
        }
        return days + " days";
    }
}
