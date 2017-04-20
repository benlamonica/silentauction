package us.pojo.silentauction;

import java.util.Arrays;
import java.util.TreeSet;

import org.junit.Ignore;
import org.junit.Test;

import us.pojo.silentauction.model.Bid;
import us.pojo.silentauction.model.Item;
import us.pojo.silentauction.model.User;
import us.pojo.silentauction.service.NotificationService;

@Ignore
public class NotificationServiceIT {
    
    public NotificationService target = new NotificationService();
    
    @Test
    public void shouldSendEmailVerificationRequest() {
        target.verifyAddress("ben.lamonica@morningstar.com");
    }
    
    @Test
    public void shouldSendEmailIfUserWantsEmail() {
        User laura = new User(1, "lb.lamonica@gmail.com", "Laura La Monica", "+16302008751", false, true, true);
        User ben = new User(2, "ben.lamonica@morningstar.com", "Ben La Monica", "+16304539090", false, true, false);
        Bid oldBid = new Bid(ben, 100.0);
        Bid newBid = new Bid(laura, 105.0);
        Item garageDoor = new Item();
        garageDoor.setId(1);
        garageDoor.setName("Automated Garage Door Opener");
        garageDoor.setSeller(laura);
        garageDoor.setDescription("An automated garage door!");
        garageDoor.setBids(new TreeSet<>(Arrays.asList(newBid, oldBid)));
        target.outbid(garageDoor, oldBid, newBid);
    }
    
    @Test
    public void shouldSendSMSOnLostBidIfUserWantsSMS() {
        User laura = new User(1, "lb.lamonica@gmail.com", "Laura La Monica", "+16302008751", true, true, true);
        User ben = new User(2, "ben.lamonica@gmail.com", "Ben La Monica", "+16304539090", true, true, false);
        Bid oldBid = new Bid(ben, 100.0);
        Bid newBid = new Bid(laura, 105.0);
        Item garageDoor = new Item();
        garageDoor.setId(1);
        garageDoor.setName("Automated Garage Door Opener");
        garageDoor.setSeller(laura);
        garageDoor.setDescription("An automated garage door!");
        garageDoor.setBids(new TreeSet<>(Arrays.asList(newBid, oldBid)));
        target.outbid(garageDoor, oldBid, newBid);
    }
}
