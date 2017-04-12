package us.pojo.silentauction.model;

import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

@Entity
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String description;

    private boolean isHidden;
    
    @ManyToOne
    private User seller;
    
    private String donor;

    @OneToMany(cascade={CascadeType.REMOVE})
    @OrderBy("bid DESC")
    private List<Bid> bids;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public List<Bid> getBids() {
        return bids;
    }

    public void setBids(List<Bid> bids) {
        this.bids = bids;
    }

    public String getDonor() {
        if (donor == null) {
            return getSeller().getName();
        }
        return donor;
    }

    public void setDonor(String donor) {
        this.donor = donor;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }
    
    public Bid getHighBid() {
        if (bids != null && !bids.isEmpty()) {
            return bids.get(0);
        }
        return null;
    }
    
    public Double getHighBidAmount() {
        Bid bid = getHighBid();
        if (bid != null) {
            return bid.getBid();
        } else {
            return 0.0;
        }
    }
    
    public String getHighBidder() {
        return Optional.ofNullable(getHighBid()).map(Bid::getUser).map(User::getShortName).orElse(null);
    }
}
