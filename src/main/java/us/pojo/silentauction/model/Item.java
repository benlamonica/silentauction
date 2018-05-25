package us.pojo.silentauction.model;

import java.util.Optional;
import java.util.SortedSet;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SortNatural;

@Cacheable
@Entity
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length=512)
    private String name;

    @Column(length=8192)
    private String description;

    private boolean isHidden;
    
    @ManyToOne
    private User seller;
    
    @Column(length=8192)
    private String donor;

    @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
    @SortNatural
    @OneToMany(cascade={CascadeType.REMOVE})
    private SortedSet<Bid> bids;

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

    public SortedSet<Bid> getBids() {
        return bids;
    }

    public void setBids(SortedSet<Bid> bids) {
        this.bids = bids;
    }

    public String getDonor() {
        if (donor == null && getSeller() != null) {
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
            return bids.first();
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
    
    public Double getHighBidForUser(User user) {
        return bids.stream().filter(b->user.equals(b.getUser())).mapToDouble(Bid::getBid).findFirst().orElse(0.0);
    }
    
    public boolean isUserHighBidder(User user) {
        return user != null && user.equals(getHighBidder());
    }
    
    public Double getAmountOutBidByForUser(User user) {
        if (isUserHighBidder(user)) {
            return 0.0;
        } else {
            return getHighBidAmount() - getHighBidForUser(user);
        }
    }
    
    public User getHighBidder() {
        return Optional.ofNullable(getHighBid()).map(Bid::getUser).orElse(null);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Item other = (Item) obj;
        if (id != other.id)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Item [id=" + id + ", name=" + name + ", donor=" + donor + "]";
    }
    
    
}
