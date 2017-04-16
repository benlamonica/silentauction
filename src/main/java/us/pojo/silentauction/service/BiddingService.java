package us.pojo.silentauction.service;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import us.pojo.silentauction.model.Bid;
import us.pojo.silentauction.model.Item;
import us.pojo.silentauction.model.User;
import us.pojo.silentauction.repository.BidRepository;
import us.pojo.silentauction.repository.ItemRepository;
import us.pojo.silentauction.repository.UserRepository;

@Service
public class BiddingService {

    private final ItemRepository items;
    private final BidRepository bids;
    private final UserRepository users;
    private final NotificationService notify;
    
    @Autowired
    public BiddingService(BidRepository bids, ItemRepository items, UserRepository users, NotificationService notify) {
        this.bids = bids;
        this.items = items;
        this.users = users;
        this.notify = notify;
    }

    public Item bid(int itemId, double bidAmount, AtomicReference<String> error) {
        Item item = items.findOne(itemId);
        
        if (item == null) {
            error.set("Unable to find item.");
            return null;
        }
        
        Optional<Bid> highBid = Optional.ofNullable(item.getHighBid());
        double highBidAmount = highBid.map(Bid::getBid).orElse(0.0); 
        if (highBidAmount > bidAmount) {
            error.set(String.format("Bid must be greater than $%.2f", highBidAmount));
        } else {
            User user = users.findOne(1);
            Bid bid = bids.save(new Bid(user, bidAmount));
            item.getBids().add(0, bid);
            highBid.ifPresent(oldBid->notify.outbid(item, oldBid, bid));
        }
        
        return item;
    }
}
