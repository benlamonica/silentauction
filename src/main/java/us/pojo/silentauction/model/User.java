package us.pojo.silentauction.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Cacheable
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "email" }) })
public class User implements UserDetails {
    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String email;
    private String passwordHash;

    @Column(length=60)
    private String name;
    private String phone;
    
    @Column(length=36)
    private String verifyToken;
    
    @Column(length=36, columnDefinition="VARCHAR(36) default null")
    private String resetPasswordToken;

    @Column(columnDefinition="BIGINT default null")
    private Long resetPasswordTokenExpiration;

    private boolean wantsSms;
    private boolean wantsEmail;
    private boolean emailVerified;
    private boolean admin = false;

    public User(int id, String email, String name, String phone, boolean wantsSMS, boolean wantsEmail, boolean isAdmin) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.passwordHash="";
        this.wantsEmail = wantsEmail;
        this.wantsSms = wantsSMS;
        this.admin = isAdmin;
    }
    
    public Long getResetPasswordTokenExpiration() {
		return resetPasswordTokenExpiration;
	}

	public void setResetPasswordTokenExpiration(Long resetPasswordTokenExpiration) {
		this.resetPasswordTokenExpiration = resetPasswordTokenExpiration;
	}



	public User() { }
    
    public boolean isAdmin() {
        return admin;
    }

    public String getResetPasswordToken() {
		return resetPasswordToken;
	}

	public void setResetPasswordToken(String resetPasswordToken) {
		this.resetPasswordToken = resetPasswordToken;
	}

	public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public int getId() {
        return id;
    }
    
    public String getVerifyToken() {
        return verifyToken;
    }

    public void setVerifyToken(String verifyToken) {
        this.verifyToken = verifyToken;
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
        
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean wantsSms() {
        return wantsSms;
    }
    
    private static final Pattern VALID_PHONE_NUMBER=Pattern.compile("\\+1\\d{10}");
    public boolean canSendSms() {
        return wantsSms && phone != null && VALID_PHONE_NUMBER.matcher(phone).matches();
    }

    public void setWantsSms(boolean wantsSMS) {
        this.wantsSms = wantsSMS;
    }

    public boolean wantsEmail() {
        return wantsEmail;
    }
    
    public boolean canSendEmail() {
        return wantsEmail && emailVerified;
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (isAdmin()) {
            return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return email;
    }
    
}
