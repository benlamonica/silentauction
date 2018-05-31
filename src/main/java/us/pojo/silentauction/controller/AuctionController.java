package us.pojo.silentauction.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.DoubleValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.PropertyResolver;
import org.springframework.format.annotation.DateTimeFormat;
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

import scala.sys.process.processInternal;
import us.pojo.silentauction.model.Auction;
import us.pojo.silentauction.model.Item;
import us.pojo.silentauction.model.User;
import us.pojo.silentauction.repository.AuctionRepository;
import us.pojo.silentauction.repository.BidRepository;
import us.pojo.silentauction.repository.ItemRepository;
import us.pojo.silentauction.service.BidQueryService;
import us.pojo.silentauction.service.BiddingService;
import us.pojo.silentauction.service.ImageService;
import us.pojo.silentauction.service.NotificationService;
import us.pojo.silentauction.service.UserDetailService;

@Transactional
@Controller
public class AuctionController {

    private static final Logger log = LoggerFactory.getLogger(AuctionController.class);
    
    @Autowired
    private BiddingService biddingService;
    
    @Autowired
    private ItemRepository items;
    
    @Autowired
    private UserDetailService userService;
    
    @Autowired
    private NotificationService notificationSerivce;;

    @Autowired
    private AuctionRepository auctions;
    
    @Autowired
    private AuthenticationManager authManager;
    
    @Autowired
    private BidQueryService bidQueryService;
    
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
        item.getBids().removeIf(bid->{
            log.info("{} deleted {}", getCurrentUser(), bid);
            return bid.getId() == bidId;   
        });
        bids.delete(bidId);
        return "redirect:/item.html?id="+itemId;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value="/end-of-auction.html")
    public ModelAndView endOfAuction(@RequestParam(name="userId") int userId) {
        ModelAndView mav = new ModelAndView("end-of-auction");
        bidQueryService.populateEndOfAuctionModel(userId, mav::addObject);
        return mav;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value="/users.html")
    public ModelAndView endOfAuction() {
        ModelAndView mav = new ModelAndView("users");
        defaultNav(mav);
        mav.addObject("users", userService.getAllUsers());
        return mav;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value="/send-all-eoa-email.html")
    public String sendEndOfAuctionEmail() {
        userService.getAllUsers().forEach(u->{
            if (u != null) {
                try {
                    notificationSerivce.sendEndOfAuctionEmail(u.getId());
                } catch (Exception e) {
                    log.error("Unable to send e-mail for {}", u, e);
                }
            }
        });
        return "redirect:/users.html";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value="/send-eoa-email.html")
    public String sendEndOfAuctionEmail(@RequestParam("userId") Integer userId) {
        notificationSerivce.sendEndOfAuctionEmail(userId);
        return "redirect:/users.html";
    }
    
    @GetMapping(value="/items.html")
    public ModelAndView getItems(@RequestParam(name="filter", required=false, defaultValue="not_set") String filter) {
        ModelAndView model = new ModelAndView("items");
        List<Item> results;
        String viewName;
        String view = "items";
        User currentUser = getCurrentUser();
        switch (filter) {
        case "no_bids":
            results = items.findItemsWithNoBid();
            viewName = "Items with no bids yet";
            break;
        case "my_bids":
                results = bidQueryService.getItemsUserBidOn(currentUser);
                viewName = "Items that I've bid on";
                view = "my-bids";
                break;
        case "my_donations":
                viewName = "Items that I've donated";
                results = items.findItemsBySeller(currentUser);
                break;
        default:
                viewName = "All Items";
                results = items.findAllByOrderByIdDesc();
                break;
        }
        model = new ModelAndView(view);
        model.addObject("viewName", viewName);
        model.addObject("items", results);
        defaultNav(model);

        if (currentUser.isAdmin()) {
            model.addObject("navUrl", "edit-item.html");
            model.addObject("navIcon", "glyphicon-plus");
            model.addObject("navText", "Add Item");
            model.addObject("currentUser", getCurrentUser());
            model.addObject("auction", auctions.findOne(1));
        }
        
        return model;
    }

    private void defaultNav(ModelAndView model) {
        List<Item> allItems = items.findAllByOrderByIdDesc();
        User currentUser = getCurrentUser();
        model.addObject("currentDonation", allItems.stream()
                .filter(item->currentUser.equals(item.getHighBidder()))
                .mapToDouble(Item::getHighBidAmount)
                .sum());
        model.addObject("currentUser", currentUser);
        model.addObject("navUrl", "items.html");
        model.addObject("navIcon", "glyphicon-th");
        model.addObject("navText", "Items");
        model.addObject("auction", auctions.findOne(1));
    }
    
    @Transactional
    @PostMapping("/bid.html")
    public ModelAndView bid(@RequestParam(name="id", required=true) int id, @RequestParam(name="bidAmount", required=true) String bidAmountInput) {
        Double bidAmount = DoubleValidator.getInstance().validate(bidAmountInput);
        ModelAndView mav = new ModelAndView("item");
        Item item = items.findOne(id);
        User user = getCurrentUser();
        mav.addObject("item", item);
        if (bidAmount == null) {
            mav.addObject("errMsg", String.format("'%s' is not a number. Bids must be numbers.", bidAmountInput));
        } else {
            AtomicReference<String> error = new AtomicReference<>();
            biddingService.bid(user, item, bidAmount, error);
            mav.addObject("errMsg", error.get());
        }

        defaultNav(mav);

        return mav;
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value="/delete-item.html")
    public String deleteItem(@RequestParam(name="id", required=true) int id) {
        Item item = items.findOne(id);
        log.info("{} deleted {}", getCurrentUser(), item);
        items.delete(item);
        return "redirect:/items.html";
    }

    @GetMapping(value={"/","/index.html","/index.htm"})
    public String home() {
        return "redirect:/items.html";
    }

    @GetMapping("/create-account.html")
    public String showCreateAccountPage() {
        return "edit-account";
    }

    @GetMapping("/edit-account.html")
    public ModelAndView editAccountPage() {
        ModelAndView mav = new ModelAndView("edit-account");
        mav.addObject("action", "edit");
        mav.addObject("currentUser", getCurrentUser());
        return mav;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/add-admin.html")
    public String addAdmin(@RequestParam("userId") int userId) {
        userService.setUserAsAdmin(userId, true);
        return "redirect:users.html";
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/remove-admin.html")
    public String removeAdmin(@RequestParam("userId") int userId) {
        userService.setUserAsAdmin(userId, false);
        return "redirect:users.html";
    }
    
    @GetMapping("/validate-email.html")
    public ModelAndView validateEmailAddress(@RequestParam(name="token") String token) {
        ModelAndView mav = getItems("not_set");
        User user = getCurrentUser();
        if (userService.markUserAsVerified(user, token)) {
            mav.addObject("msg", "Email address verified.");
        } else {
            mav.addObject("errMsg", "Invalid Token, e-mail address not verified.");
        }
        refreshUserInSecurityContext(userService.getUser(user.getId()));
        return mav;
    }
    
    @GetMapping("/reset-password.html")
    public String changePassword(@RequestParam(name="email") String email, @RequestParam(name="token") String token) {
    	User user = userService.getUserByPasswordToken(email, token);
    	if (user != null) {
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, token, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        	return "redirect:/edit-account.html";
    	} else {
    		return "redirect:/login.html";
    	}
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/send-reset-password-link.html")
    public String changePassword(@RequestParam(name="userId") int userId) {
    	User user = userService.getUser(userId);
    	userService.updatePasswordResetToken(user);
    	notificationSerivce.sendPasswordResetEmail(user);
    	return "redirect:users.html";
    }

    @GetMapping("/resend-validation-email.html")
    public ModelAndView resendValidation() {
        User user = getCurrentUser();
        userService.updateVerifyToken(user);
        notificationSerivce.sendVerificationEmail(user);
        ModelAndView mav = editAccountPage();
        mav.addObject("msg", "Verification Email Re-sent");
        return mav;
    }

    @GetMapping(value={"/auction.html", "/about.html"})
    public ModelAndView getAuction() {
        ModelAndView mav = new ModelAndView("auction");
        defaultNav(mav);
        return mav;
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value="/edit-auction.html")
    public ModelAndView editAuction() {
        ModelAndView mav = new ModelAndView("edit-auction");
        defaultNav(mav);
        return mav;
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value="/edit-auction.html")
    public String saveAuction(@RequestParam("auction_picture") MultipartFile picture, @RequestParam("name") String name, @RequestParam("description") String description, @DateTimeFormat(pattern="MM/dd/yyyy h:mm a") @RequestParam("endsAt") LocalDateTime endsAt, @RequestParam("organizer") String organizer, @RequestParam("organizerEmail") String organizerEmail, @RequestParam("endOfAuctionInstructions") String endOfAuctionInstructions, @RequestParam("endOfAuctionInstructionsText") String endOfAuctionInstructionsText) {
        Auction auction = auctions.findOne(1);
        if (auction == null) {
            auction = new Auction();
        }
        
        auction.setDescription(description);
        auction.setEndOfAuctionInstructions(endOfAuctionInstructions);
        auction.setEndOfAuctionInstructionsText(endOfAuctionInstructionsText);
        auction.setName(name);
        auction.setEnds(endsAt);
        auction.setOrganizer(organizer);
        auction.setOrganizerEmail(organizerEmail);
        images.save("auction", picture);
        auctions.save(auction);

        return "redirect:/auction.html";
    }
    
    @PostMapping(value="/edit-account.html")
    public String editUser(@RequestParam("email") String email, @RequestParam("name") String name, 
            @RequestParam("phone") String phone, @RequestParam("password") String password,
            @RequestParam(value="wantsEmail", required=false, defaultValue="false") boolean wantsEmail, 
            @RequestParam(value="wantsSMS", required=false, defaultValue="false") boolean wantsSMS) {

        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPhone(phone);
        user.setPasswordHash(password);
        user.setWantsEmail(wantsEmail);
        user.setWantsSms(wantsSMS);
        user = userService.modifyUser(getCurrentUser(), user);

        refreshUserInSecurityContext(user);

        return "redirect:/items.html?msg=User profile saved.";
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

        addUserToSecurityContext(user, password);

        return "redirect:/about.html";
    }
    
    
    
    private void refreshUserInSecurityContext(User user) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
    
    private void addUserToSecurityContext(User user, String password) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
        authManager.authenticate(auth);
        if(auth.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
    }
    
    @GetMapping(value="/item.html")
    public ModelAndView getItem(@RequestParam(name="id", required=true) int id, @RequestParam(name="err-msg", required=false) String errMsg) {
        ModelAndView mav = new ModelAndView("item");
        defaultNav(mav);
        mav.addObject("item", items.findOne(id));
        if (StringUtils.isNotBlank(errMsg)) {
            mav.addObject("errMsg", errMsg);
        }
        return mav;
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value="/winners.html")
    public ModelAndView winners() {
        ModelAndView mav = new ModelAndView("winners");
        List<Item> allItems = items.findAll();
        mav.addObject("items", allItems);
        defaultNav(mav);
        return mav;
    }

    @GetMapping(value="/report.html")
    public ModelAndView report() {
        ModelAndView mav = new ModelAndView("report");
        List<Item> allItems = items.findAll();
        Double total = allItems.stream().map(Item::getHighBidAmount).reduce(0.0, (a,b) -> a+b);
        mav.addObject("items", allItems);
        mav.addObject("totalAmount", total);
        defaultNav(mav);
        return mav;
    }
    
    private Optional<String> enforcePermission(User currentUser, Item item) {
        if (!currentUser.isAdmin() && !item.getSeller().equals(currentUser)) {
            return Optional.of("redirect:item.html?id=" + item.getId());
        }
        return Optional.empty();
    }
    
    @PreAuthorize("hasRole('ADMIN')")
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
        mav.addObject("item", item);
        mav.addObject("saveText", id == -1 ? "Add" : "Save");
        mav.addObject("showRemoveButton", true);
        defaultNav(mav);
        return mav;
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value="/edit-item.html")
    public String saveItem(@RequestParam("item_picture") MultipartFile picture, @RequestParam("id") int id, @RequestParam("name") String name, @RequestParam("description") String description, @RequestParam("donor") String donor, @RequestParam("minimum_bid") Double minimumBid) {
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
            item.setMinimumBid(Optional.ofNullable(minimumBid).orElse(0d));
            item = items.save(item);
            images.save(String.valueOf(item.getId()), picture);
        }
        return view.orElse("redirect:/item.html?id="+item.getId());
    }
    
    
}
