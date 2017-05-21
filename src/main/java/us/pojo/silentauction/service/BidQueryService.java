package us.pojo.silentauction.service;

import static java.util.stream.Collectors.toList;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import us.pojo.silentauction.model.Item;
import us.pojo.silentauction.model.User;
import us.pojo.silentauction.repository.AuctionRepository;
import us.pojo.silentauction.repository.ItemRepository;
import us.pojo.silentauction.repository.UserRepository;

@Service
public class BidQueryService {

    @Autowired
    private UserRepository userService;
    
    @Autowired
    private AuctionRepository auctions;
    
    @Autowired
    private ItemRepository items;
    
    public void populateEndOfAuctionModel(int userId, BiConsumer<String, Object> model) {
        User user = userService.findOne(userId);
        List<Item> items = getItemsUserBidOn(user);
        model.accept("user", user);
        Double totalDonation = items.stream().filter(i->i.isUserHighBidder(user)).mapToDouble(Item::getHighBidAmount).sum();
        model.accept("totalDonation", totalDonation);
        List<Item> wonItems = items.stream().filter(i->i.isUserHighBidder(user)).collect(toList());
        List<Item> lostItems = items.stream().filter(i->!i.isUserHighBidder(user)).collect(toList());
        model.accept("wonItems", wonItems);
        model.accept("lostItems", lostItems);
        model.accept("auction", auctions.findOne(1));
    }
    
    public List<Item> getItemsUserBidOn(User user) {
        Comparator<Item> sortItemsByWinAndAmount = new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                if (user.equals(o1.getHighBidder()) && user.equals(o2.getHighBidder())) {
                    return Double.compare(o2.getHighBidAmount(), o1.getHighBidAmount());
                } else if (user.equals(o1.getHighBidder())) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }; 

        List<Item> results = items.findItemsByBidder(user);
        return results.stream().sorted(sortItemsByWinAndAmount).collect(toList());
    }
}
