package us.pojo.silentauction.repository;

import org.springframework.data.repository.CrudRepository;

import us.pojo.silentauction.model.User;

public interface UserRepository extends CrudRepository<User, Integer> {
    public User findUserByEmail(String email);
}
