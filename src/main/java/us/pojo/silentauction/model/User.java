package us.pojo.silentauction.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "email" }) })
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String email;
    private String passwordHash;
    private String name;
    private String phone;
    private boolean wantsSMS;
    private boolean wantsEmail;
    private boolean emailVerified;

    public User(int id, String email, String name, String phone, boolean wantsSMS, boolean wantsEmail) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.passwordHash="";
        this.wantsEmail = wantsEmail;
        this.wantsSMS = wantsSMS;
    }
    
    public User() { }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String password) {
        this.passwordHash = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    private static final Pattern SHORT_NAME_PATTERN = Pattern.compile("^(.+? .).+$");
    public String getShortName() {
        if (name != null) {
            Matcher m = SHORT_NAME_PATTERN.matcher(name);
            if (m.find()) {
                return m.group(1);
            }
        }
        
        return "";
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private static final Pattern VALID_PHONE_NUMBER=Pattern.compile("\\+1\\d{10}");
    public boolean wantsSMS() {
        return wantsSMS && phone != null && VALID_PHONE_NUMBER.matcher(phone).matches();
    }

    public void setWantsSMS(boolean wantsSMS) {
        this.wantsSMS = wantsSMS && email != null;
    }

    public boolean wantsEmail() {
        return wantsEmail;
    }

    public void setWantsEmail(boolean wantsEmail) {
        this.wantsEmail = wantsEmail;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
