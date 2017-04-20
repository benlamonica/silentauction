package us.pojo.silentauction.service;

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
    private PasswordEncoder pwEncoder;
    
    @Autowired
    public UserDetailService(UserRepository users, PasswordEncoder pwEncoder) {
        this.users = users;
        this.pwEncoder = pwEncoder;
    }

    public User modifyUser(User user) {
        User existingUser = users.findUserByEmail(user.getEmail());
        if (existingUser == null) {
            throw new RuntimeException("Unable to find user!");
        }
        existingUser.setEmailVerified(user.isEmailVerified());
        existingUser.setName(user.getName());
        existingUser.setPhone(formatPhone(user.getPhone()));
        existingUser.setWantsEmail(user.wantsEmail());
        existingUser.setWantsSms(user.wantsSms());
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
        user.setPhone(formatPhone(user.getPhone()));
        user.setPasswordHash(pwEncoder.encode(password));
        return users.save(user);
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = users.findUserByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException(username + " was not found.");
        }
        
        return user;
    }

}
