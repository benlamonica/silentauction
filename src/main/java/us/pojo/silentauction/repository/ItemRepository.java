package us.pojo.silentauction.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import us.pojo.silentauction.model.Item;
import us.pojo.silentauction.model.User;

public interface ItemRepository extends CrudRepository<Item, Integer> {
    List<Item> findAll();

    @Query("select distinct i from Item i left join fetch i.bids b where b.user.id = :#{#user.id}")
    List<Item> findItemsByBidder(@Param("user") User user);
    
    List<Item> findItemsBySeller(User currentUser);
}
