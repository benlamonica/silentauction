package us.pojo.silentauction.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import us.pojo.silentauction.model.User;
import us.pojo.silentauction.repository.UserRepository;

@Service
public class UserDetailService implements UserDetailsService {

    private final UserRepository users;
    private final NotificationService notification;
    private PasswordEncoder pwEncoder;
    
    @Autowired
    public UserDetailService(UserRepository users, NotificationService notification, PasswordEncoder pwEncoder) {
        this.users = users;
        this.pwEncoder = pwEncoder;
        this.notification = notification;
    }

    public User getUser(int id) {
        return users.findOne(id);
    }
    
    public void updateVerifyToken(User user) {
        if (user.getVerifyToken() == null) {
            User dbUser = users.findOne(user.getId());
            dbUser.setVerifyToken(UUID.randomUUID().toString());
            user.setVerifyToken(dbUser.getVerifyToken());
        }
    }
    
    public User modifyUser(User currentUser, User user) {
        User existingUser = users.findUserByEmail(user.getEmail());
        boolean emailChange = !user.getEmail().equals(currentUser.getEmail());
        if (existingUser != null && emailChange) {
            throw new RuntimeException("Email already used by a different user");
        }
        existingUser.setEmail(user.getEmail());
        if (StringUtils.isNotBlank(user.getPassword())) {
            existingUser.setPasswordHash(pwEncoder.encode(user.getPassword()));
        }
        existingUser.setEmailVerified(user.isEmailVerified() && !emailChange);
        existingUser.setName(user.getName());
        existingUser.setPhone(formatPhone(user.getPhone()));
        existingUser.setWantsEmail(user.wantsEmail());
        existingUser.setWantsSms(user.wantsSms());
        
        if (emailChange) {
            notification.sendVerificationEmail(existingUser);
        }
        return existingUser;
    }
    
    private Pattern PHONE_REGEX = Pattern.compile("^\\s*(\\+\\d+)?\\s*\\(?\\s*(\\d+)\\s*\\)?\\-?(\\d+)\\-?(\\d+)\\s*$");
    private String formatPhone(String phone) {
        Matcher m = PHONE_REGEX.matcher(phone);
        if (!StringUtils.isBlank(phone) && m.find()) {
            String countryCode = m.group(1);
            String areaCode = m.group(2);
            String exchangeCode = m.group(3);
            String number = m.group(4);
            
            if (StringUtils.isBlank(countryCode)) {
                countryCode = "+1";
            }
            return countryCode+areaCode+exchangeCode+number;
        }
        
        return null;
    }
    
    public User registerNewUser(User user) {
        User existingUser = users.findUserByEmail(user.getEmail());
        if (existingUser != null) {
            throw new RuntimeException("User already exists! Unable to create new user. Please log in instead.");
        }
        
        String password = user.getPassword();
        user.setVerifyToken(UUID.randomUUID().toString());
        user.setPhone(formatPhone(user.getPhone()));
        user.setPasswordHash(pwEncoder.encode(password));
        User savedUser = users.save(user);
        notification.sendVerificationEmail(savedUser);
        return savedUser;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = users.findUserByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException(username + " was not found.");
        }
        
        return user;
    }

    public User getUserByPasswordToken(String email, String token) {
    	if (StringUtils.isNotBlank(token)) {
            User user = users.findUserByResetPasswordToken(token);
            if (user != null && email.equalsIgnoreCase(user.getEmail()) && user.getResetPasswordTokenExpiration() > System.currentTimeMillis()) {
            	return user;
            }	
    	}
    	return null;
    }
    
    public void updatePasswordResetToken(User user) {
        User dbUser = users.findOne(user.getId());
        dbUser.setResetPasswordToken(UUID.randomUUID().toString());
        dbUser.setResetPasswordTokenExpiration(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30));
        user.setResetPasswordToken(dbUser.getResetPasswordToken());
    }

    
    public boolean markUserAsVerified(User currentUser, String token) {
        User user = users.findUserByVerifyToken(token);
        if (currentUser.equals(user)) {
            user.setEmailVerified(true);
            return true;
        }
        return false;
    }
    
    public boolean setUserAsAdmin(int userId, boolean isAdmin) {
        User user = users.findOne(userId);
        if (user != null) {
            user.setAdmin(isAdmin);
            return true;
        }
        return false;
    }

    public Iterable<User> getAllUsers() {
        return users.findAll();
    }
}
