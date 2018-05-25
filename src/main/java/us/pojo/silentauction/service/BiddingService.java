package us.pojo.silentauction.service;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import us.pojo.silentauction.model.Auction;
import us.pojo.silentauction.model.Bid;
import us.pojo.silentauction.model.Item;
import us.pojo.silentauction.model.User;
import us.pojo.silentauction.repository.AuctionRepository;
import us.pojo.silentauction.repository.BidRepository;
import us.pojo.silentauction.repository.UserRepository;

@Service
public class BiddingService {
    private double US_GDP = 18.56 * 1000000000000d;
    private final BidRepository bids;
    private final NotificationService notify;
    private final Auction auction;
    @Autowired
    public BiddingService(BidRepository bids, UserRepository users, NotificationService notify, AuctionRepository auctionRepo) {
        this.bids = bids;
        this.notify = notify;
        auction = auctionRepo.findOne(1);
    }

    public Item bid(User user, Item item, double bidAmount, AtomicReference<String> error) {
        if (item == null) {
            error.set("Unable to find item.");
            return null;
        }
        
        if (auction.isAuctionClosed()) {
            error.set("Auction is closed. No more bidding allowed.");
            return item;
        }

        if (bidAmount < item.getMinimumBid()) {
            error.set(String.format("Bid must be greater than the minimum bid $%.2f", item.getMinimumBid()));
            return item;
        }
        
        Optional<Bid> highBid = Optional.ofNullable(item.getHighBid());
        double highBidAmount = highBid.map(Bid::getBid).orElse(0.0); 
        if (highBidAmount+1 > bidAmount) {
            error.set(String.format("Bid must be greater than $%.2f", highBidAmount+1));
        } else if (bidAmount > US_GDP) {
            error.set("The bid must be less than the United States Gross Domestic Product!");
        } else {
            Bid bid = bids.save(new Bid(user, bidAmount));
            item.getBids().add(bid);
            highBid.ifPresent(oldBid->notify.outbid(item, oldBid, bid));
        }
        
        return item;
    }
}
