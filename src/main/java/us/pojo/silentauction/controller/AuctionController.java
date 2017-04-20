package us.pojo.silentauction.controller;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.validator.routines.DoubleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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
import us.pojo.silentauction.service.BiddingService;
import us.pojo.silentauction.service.ImageService;
import us.pojo.silentauction.service.UserDetailService;

@Transactional
@Controller
public class AuctionController {

    @Autowired
    private BiddingService biddingService;
    
    @Autowired
    private ItemRepository items;
    
    @Autowired
    private UserDetailService userService;
    
    @Autowired
    private AuthenticationManager authManager;
    
    @Autowired 
    private BidRepository bids;
    
    @Autowired
    private ImageService images;
    
    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value="/delete-bid.html")
    public String deleteBid(@RequestParam(name="itemId") int itemId, @RequestParam("bidId") int bidId) {
        Item item = items.findOne(itemId);
        item.getBids().removeIf(b->b.getId() == bidId);
        bids.delete(bidId);
        return "redirect:/item.html?id="+itemId;
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
        Item item = items.findOne(id);
        User user = getCurrentUser();
        mav.addObject("currentUser", user);
        mav.addObject("navUrl", "items.html");
        mav.addObject("navIcon", "glyphicon-menu-left");
        mav.addObject("navText", "Items");
        mav.addObject("item", item);
        if (bidAmount == null) {
            mav.addObject("errorMessage", String.format("'%s' is not a number. Bids must be numbers.", bidAmountInput));
        } else {
            AtomicReference<String> error = new AtomicReference<>();
            biddingService.bid(user, item, bidAmount, error);
            mav.addObject("errorMessage", error.get());
        }

        return mav;
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value="/delete-item.html")
    public String deleteItem(@RequestParam(name="id", required=true) int id) {
        items.delete(id);
        return "redirect:/items.html";
    }

    @GetMapping(value={"/","/index.html","/index.htm"})
    public String home() {
        return "redirect:/items.html";
    }

    @GetMapping("/create-account.html")
    public String showCreateAccountPage() {
        return "create-account";
    }
    
    @PostMapping(value="/create-account.html")
    public String createUser(@RequestParam("email") String email, @RequestParam("name") String name, 
            @RequestParam("password") String password, @RequestParam("phone") String phone, 
            @RequestParam(value="wantsEmail", required=false, defaultValue="false") boolean wantsEmail, 
            @RequestParam(value="wantsSMS", required=false, defaultValue="false") boolean wantsSMS) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPasswordHash(password);
        user.setPhone(phone);
        user.setWantsEmail(wantsEmail);
        user.setWantsSms(wantsSMS);
        user = userService.registerNewUser(user);
        
        // automatically log the user in after they've registered.
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
        authManager.authenticate(auth);
        if(auth.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

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
    
    @PreAuthorize("hasRole('ADMIN')")
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
    
    private Optional<String> enforcePermission(User currentUser, Item item) {
        if (!currentUser.isAdmin() && !item.getSeller().equals(currentUser)) {
            return Optional.of("redirect:item.html?id=" + item.getId());
        }
        return Optional.empty();
    }
    
    @GetMapping(value="/edit-item.html")
    public ModelAndView editItem(@RequestParam(name="id", defaultValue="-1", required=false) int id) {
        User currentUser = getCurrentUser();
        Optional<String> view = Optional.empty();
        Item item;
        if (id == -1) {
            item = new Item();
            item.setId(-1);
            item.setSeller(currentUser);
        } else {
            item = items.findOne(id);
            view = enforcePermission(currentUser, item);
        }
        
        ModelAndView mav = new ModelAndView(view.orElse("edit-item"));
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
        User user = getCurrentUser();
        Optional<String> view = Optional.empty();
        if (id != -1) {
           item = items.findOne(id); 
           view = enforcePermission(getCurrentUser(), item);
        } else {
            item = new Item();
        }

        if (!view.isPresent()) {
            item.setName(name);
            item.setDescription(description);
            item.setDonor(donor);
            item.setSeller(user);
            item = items.save(item);
            images.save(item.getId(), picture);
        }
        return view.orElse("redirect:/item.html?id="+item.getId());
    }

}
