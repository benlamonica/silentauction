package us.pojo.silentauction.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Bid {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private User user;

    private Double bid;

    private LocalDateTime bidTime;

    public Bid() { }
    
    public Bid(User user, Double bid) {
        this.user = user;
        this.bid = bid;
        this.bidTime = LocalDateTime.now();
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getBid() {
        return bid;
    }

    public void setBid(Double bid) {
        this.bid = bid;
    }

    public LocalDateTime getBidTime() {
        return bidTime;
    }

    private final static DateTimeFormatter SHORT_TIME_FORMAT = DateTimeFormatter.ofPattern("MM/dd hh:mma");
    public String getFormattedBidTime() {
        return bidTime.format(SHORT_TIME_FORMAT);
    }
    
    public void setBidTime(LocalDateTime bidTime) {
        this.bidTime = bidTime;
    }
}
