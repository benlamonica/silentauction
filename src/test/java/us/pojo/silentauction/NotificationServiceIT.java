package us.pojo.silentauction;

import java.util.Arrays;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import freemarker.template.Configuration;
import freemarker.template.Version;
import us.pojo.silentauction.model.Bid;
import us.pojo.silentauction.model.Item;
import us.pojo.silentauction.model.User;
import us.pojo.silentauction.service.BidQueryService;
import us.pojo.silentauction.service.NotificationService;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class NotificationServiceIT {
    
    @Mock
    private BidQueryService bidQueryService;
    
    private NotificationService target;
    
    @Before
    public void setup() {
        target = new NotificationService("http://auction.pojo.us:11111", "ben.lamonica@icloud.com", new Configuration(new Version(1)), bidQueryService);
    }
    
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
