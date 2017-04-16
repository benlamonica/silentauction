package us.pojo.silentauction.controller;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.validator.routines.DoubleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import us.pojo.silentauction.model.Item;
import us.pojo.silentauction.model.User;
import us.pojo.silentauction.repository.BidRepository;
import us.pojo.silentauction.repository.ItemRepository;
import us.pojo.silentauction.repository.UserRepository;
import us.pojo.silentauction.service.BiddingService;
import us.pojo.silentauction.service.ImageService;

@Transactional
@Controller
public class AuctionController {

    @Autowired
    private BiddingService biddingService;
    
    @Autowired
    private ItemRepository items;
    
    @Autowired
    private UserRepository users;
    
    @Autowired 
    private BidRepository bids;
    
    @Autowired
    private ImageService images;
    
    private User currentUser;
    
    private User getCurrentUser() {
        if (currentUser == null) {
            currentUser = users.findUserByEmail("ben.lamonica@gmail.com");
            if (currentUser == null) {
                currentUser = new User(-1, "ben.lamonica@gmail.com", "Ben La Monica", "+16304539090", true, false);
                currentUser = users.save(currentUser);
            }
        }
        return currentUser;
    }

    @GetMapping(value="/items.html")
    public ModelAndView getItems(@RequestParam(name="filter", required=false, defaultValue="not_set") String filter) {
        ModelAndView model = new ModelAndView("items");
        List<Item> results;
        String viewName;
        switch (filter) {
        case "my_bids":
                results = items.findItemsByBidder(getCurrentUser());
                viewName = "Items that I've bid on";
                break;
        case "my_donations":
                viewName = "Items that I've donated";
                results = items.findItemsBySeller(getCurrentUser());
                break;
        default:
                viewName = "All Items";
                results = items.findAll();
                break;
        }
        model.addObject("viewName", viewName);
        model.addObject("currentUser", getCurrentUser());
        model.addObject("items", results);
        model.addObject("navUrl", "edit-item.html");
        model.addObject("navIcon", "glyphicon-plus");
        model.addObject("navText", "Add Item");
        return model;
    }
    
    @Transactional
    @PostMapping("/bid.html")
    public ModelAndView bid(@RequestParam(name="id", required=true) int id, @RequestParam(name="bidAmount", required=true) String bidAmountInput) {
        Double bidAmount = DoubleValidator.getInstance().validate(bidAmountInput);
        ModelAndView mav = new ModelAndView("item");
        mav.addObject("currentUser", getCurrentUser());
        mav.addObject("navUrl", "items.html");
        mav.addObject("navIcon", "glyphicon-menu-left");
        mav.addObject("navText", "Items");

        if (bidAmount == null) {
            mav.addObject("errorMessage", String.format("'%s' is not a number. Bids must be numbers.", bidAmountInput));
        } else {
            AtomicReference<String> error = new AtomicReference<>();
            Item item = biddingService.bid(id, bidAmount, error);
            mav.addObject("item", item);
            mav.addObject("errorMessage", error.get());
        }

        return mav;
    }
    
    @PostMapping(value="/delete-item.html")
    public String deleteItem(@RequestParam(name="id", required=true) int id) {
        items.delete(id);
        return "redirect:/items.html";
    }

    @GetMapping(value={"/","/index.html","/index.htm"})
    public String home() {
        return "redirect:/items.html";
    }

    
    @GetMapping(value="/item.html")
    public ModelAndView getItem(@RequestParam(name="id", required=true) int id) {
        ModelAndView mav = new ModelAndView("item");
        mav.addObject("currentUser", getCurrentUser());
        mav.addObject("item", items.findOne(id));
        mav.addObject("navUrl", "items.html");
        mav.addObject("navIcon", "glyphicon-menu-left");
        mav.addObject("navText", "Items");
        return mav;
    }
    
    @GetMapping(value="/winners.html")
    public ModelAndView winners() {
        ModelAndView mav = new ModelAndView("winners");
        List<Item> allItems = items.findAll();
        mav.addObject("currentUser", getCurrentUser());
        mav.addObject("items", allItems);
        mav.addObject("navUrl", "items.html");
        mav.addObject("navIcon", "glyphicon-menu-left");
        mav.addObject("navText", "Items");
        return mav;
    }
    
    @GetMapping(value="/report.html")
    public ModelAndView report() {
        ModelAndView mav = new ModelAndView("report");
        List<Item> allItems = items.findAll();
        Double total = allItems.stream().map(i->i.getHighBidAmount()).reduce(0.0, (a,b) -> a+b);
        mav.addObject("currentUser", getCurrentUser());
        mav.addObject("items", allItems);
        mav.addObject("totalAmount", total);
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
            item.setSeller(getCurrentUser());
        } else {
            item = items.findOne(id);
        }
        
        ModelAndView mav = new ModelAndView("edit-item");
        mav.addObject("currentUser", getCurrentUser());
        mav.addObject("item", item);
        mav.addObject("saveText", id == -1 ? "Add" : "Save");
        mav.addObject("showRemoveButton", true);
        mav.addObject("navUrl", "items.html");
        mav.addObject("navIcon", "glyphicon-menu-left");
        mav.addObject("navText", "Items");
        return mav;
    }
    
    @PostMapping(value="/edit-item.html")
    public String saveItem(@RequestParam("item_picture") MultipartFile picture, @RequestParam("id") int id, @RequestParam("name") String name, @RequestParam("description") String description, @RequestParam("donor") String donor) {
        Item item;
        if (id != -1) {
           item = items.findOne(id); 
        } else {
            item = new Item();
        }
        
        item.setName(name);
        item.setDescription(description);
        item.setDonor(donor);
        User user = getCurrentUser();
        
        item.setSeller(user);
        item = items.save(item);
        images.save(item.getId(), picture);
        return "redirect:/item.html?id="+item.getId();
    }

}
