package us.pojo.silentauction.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsync;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsyncClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.GetIdentityVerificationAttributesRequest;
import com.amazonaws.services.simpleemail.model.GetIdentityVerificationAttributesResult;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import com.amazonaws.services.simpleemail.model.VerifyEmailIdentityRequest;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

import us.pojo.silentauction.model.Bid;
import us.pojo.silentauction.model.Item;
import us.pojo.silentauction.model.User;

@Service
public class NotificationService {
    
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private AmazonSNS snsClient = AmazonSNSClientBuilder.defaultClient();
    private AmazonSimpleEmailServiceAsync sesClient = AmazonSimpleEmailServiceAsyncClientBuilder.defaultClient();
    private Map<String, MessageAttributeValue> smsAttributes = new HashMap<String, MessageAttributeValue>();

    public NotificationService() {
        smsAttributes.put("AWS.SNS.SMS.MaxPrice", new MessageAttributeValue()
                .withStringValue("0.01") //Sets the max price to 0.50 USD.
                .withDataType("Number"));
        smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
                .withStringValue("Transactional")
                .withDataType("String"));
    }

    private double getDiff(Bid oldHighBid, Bid newHighBid) {
       return newHighBid.getBid() - oldHighBid.getBid();
    }
    
    public void verifyAddress(String email) {
        if ("missing".equals(getEmailVerificationStatus(email))) {
            VerifyEmailIdentityRequest request = new VerifyEmailIdentityRequest();
            request.withEmailAddress(email);
            sesClient.verifyEmailIdentityAsync(request);
        }
    }

    private String getEmailVerificationStatus(String email) {
        GetIdentityVerificationAttributesRequest request = new GetIdentityVerificationAttributesRequest();
        request.withIdentities(email);
        GetIdentityVerificationAttributesResult result = sesClient.getIdentityVerificationAttributes(request);
        return Optional.ofNullable(result.getVerificationAttributes().get(email)).map(a->a.getVerificationStatus()).orElse("missing");
    }
    
    public boolean isEmailVerified(String email) {
        return "success".equals(getEmailVerificationStatus(email));
    }
    
    public void outbid(Item item, Bid oldHighBid, Bid newHighBid) {
        if (oldHighBid != null && !oldHighBid.getUser().equals(newHighBid.getUser())) {
            User oldHighBidder = oldHighBid.getUser();
            
            if (oldHighBidder.wantsSMS()) {
                sendSMS(item, oldHighBid, newHighBid);
            } else if (oldHighBidder.wantsEmail()) {
                sendEmail(item, oldHighBid, newHighBid);
            } else {
                log.info("Outbid notification not requested by user.");
            }
        }
    }
    
    private void sendEmail(Item item, Bid oldHighBid, Bid newHighBid) {
        ForkJoinPool.commonPool().submit(()->{
            // Construct an object to contain the recipient address.
            if (!oldHighBid.getUser().isEmailVerified()) {
                if (isEmailVerified(newHighBid.getUser().getEmail())) {
                    newHighBid.getUser().setEmailVerified(true);
                } else {
                    log.warn("User has not verified their e-mail yet, so can't send a message.");
                    return;
                }
            }
            
            Destination destination = new Destination().withToAddresses(oldHighBid.getUser().getEmail());
            
            double diff = getDiff(oldHighBid, newHighBid);

            // Create the subject and body of the message.
            Content subject = new Content().withData(String.format("You were outbid by $%.2f on %s", diff, item.getName()));
            Content textBody = new Content().withData(String.format("You were outbid by $%.2f on %s.\nIncrease your bid at http://auction.pojo.us:11111/item.html?id=%d", diff, item.getName(), item.getId())); 
            Content htmlBody = new Content().withData(String.format("<h1>You were outbid by $%.2f on %s.<br/><a href=\"http://auction.pojo.us:11111/item.html?id=%d\">Increase Your Bid</a></h1>", diff, item.getName(), item.getId())); 
            Body body = new Body().withText(textBody).withHtml(htmlBody);
            
            // Create a message with the specified subject and body.
            Message message = new Message().withSubject(subject).withBody(body);
            
            // Assemble the email.
            SendEmailRequest request = new SendEmailRequest().withSource("ben.lamonica@icloud.com").withDestination(destination).withMessage(message);
            SendEmailResult result = sesClient.sendEmail(request);
            log.info("Email message was sent to SES {}", result);
        });
        
    }
    
    private void sendSMS(Item item, Bid oldHighBid, Bid newHighBid) {
        double diff = getDiff(oldHighBid, newHighBid);
        String message = String.format("You've been outbid by $%.2f on %s! Bid again http://auction.pojo.us:11111/item.html?id=%d", diff, item.getName(), item.getId());

        ForkJoinPool.commonPool().submit(()->{
        PublishResult result = snsClient.publish(new PublishRequest()
                .withMessage(message)
                .withPhoneNumber(oldHighBid.getUser().getPhone())
                .withMessageAttributes(smsAttributes));
        log.info("SMS message was sent to SNS {}", result);
        });
    }
}
