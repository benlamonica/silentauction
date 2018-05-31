package us.pojo.silentauction.repository;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

import us.pojo.silentauction.model.User;

public interface UserRepository extends CrudRepository<User, Integer> {
    
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    public User findUserByEmail(String email);
    
    public User findUserByVerifyToken(String verifyToken);

    public User findUserByResetPasswordToken(String passwordResetToken);

}
