package us.pojo.silentauction.controller;

import java.util.Optional;

import org.apache.commons.validator.routines.DoubleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import us.pojo.silentauction.model.Bid;
import us.pojo.silentauction.model.Item;
import us.pojo.silentauction.model.User;
import us.pojo.silentauction.repository.BidRepository;
import us.pojo.silentauction.repository.ItemRepository;
import us.pojo.silentauction.repository.UserRepository;
import us.pojo.silentauction.service.ImageService;

@Controller
public class AuctionController {

    @Autowired
    private ItemRepository items;
    
    @Autowired
    private UserRepository users;
    
    @Autowired 
    private BidRepository bids;
    
    @Autowired
    private ImageService images;
    
    @GetMapping(value="/items.html")
    public ModelAndView getItems(@RequestParam(name="filter", required=false) String filter) {
        ModelAndView model = new ModelAndView("items");
        model.addObject("items", items.findAll());
        model.addObject("navUrl", "edit-item.html");
        model.addObject("navIcon", "glyphicon-plus");
        model.addObject("navText", "Add Item");
        return model;
    }
    
    @Transactional
    @PostMapping("/bid.html")
    public ModelAndView bid(@RequestParam(name="id", required=true) int id, @RequestParam(name="bidAmount", required=true) String bidAmountInput) {
        Double bidAmount = DoubleValidator.getInstance().validate(bidAmountInput);
        Item item = items.findOne(id);
        ModelAndView mav = new ModelAndView("item");
        mav.addObject("item", item);
        mav.addObject("navUrl", "items.html");
        mav.addObject("navIcon", "glyphicon-menu-left");
        mav.addObject("navText", "Items");

        double highBidAmount = Optional.ofNullable(item.getHighBid()).map(Bid::getBid).orElse(0.0); 
        if (bidAmount == null) {
            mav.addObject("errorMessage", String.format("'%s' is not a number. Bids must be numbers.", bidAmountInput));
        } else if (highBidAmount > bidAmount) {
            mav.addObject("errorMessage", String.format("Bid must be greater than $%.2f", highBidAmount));
        } else {
            User user = users.findOne(1);
            Bid bid = bids.save(new Bid(user, bidAmount));
            item.getBids().add(0, bid);
        }
        return mav;
    }
    
    @PostMapping(value="/delete-item.html")
    public String deleteItem(@RequestParam(name="id", required=true) int id) {
        items.delete(id);
        return "redirect:/items.html";
    }
    
    
    @GetMapping(value="/item.html")
    public ModelAndView getItem(@RequestParam(name="id", required=true) int id) {
        ModelAndView mav = new ModelAndView("item");
        mav.addObject("item", items.findOne(id));
        mav.addObject("navUrl", "items.html");
        mav.addObject("navIcon", "glyphicon-menu-left");
        mav.addObject("navText", "Items");
        return mav;
    }
    
    @GetMapping(value="/edit-item.html")
    public ModelAndView editItem(@RequestParam(name="id", defaultValue="-1", required=false) int id) {
        Item item;
        if (id == -1) {
            item = new Item();
            item.setId(-1);
        } else {
            item = items.findOne(id);
        }
        
        ModelAndView mav = new ModelAndView("edit-item");
        mav.addObject("item", item);
        mav.addObject("showRemoveButton", true);
        mav.addObject("navUrl", "items.html");
        mav.addObject("navIcon", "glyphicon-menu-left");
        mav.addObject("navText", "Items");
        return mav;
    }
    
    @PostMapping(value="/edit-item.html")
    public String saveItem(@RequestParam("item_picture") MultipartFile picture, @RequestParam("id") int id, @RequestParam("name") String name, @RequestParam("description") String description) {
        Item item;
        if (id != -1) {
           item = items.findOne(id); 
        } else {
            item = new Item();
        }
        
        item.setName(name);
        item.setDescription(description);
        
        User user = users.findOne(1);
        if (user == null) {
            user = new User(-1, "ben.lamonica@gmail.com", "Ben La Monica");
            user = users.save(user);
        }
        item.setSeller(user);
        item = items.save(item);
        images.save(item.getId(), picture);
        return "redirect:/item.html?id="+item.getId();
    }

}
