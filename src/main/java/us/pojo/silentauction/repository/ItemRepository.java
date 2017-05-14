package us.pojo.silentauction.repository;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import us.pojo.silentauction.model.Item;
import us.pojo.silentauction.model.User;

public interface ItemRepository extends CrudRepository<Item, Integer> {
    
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    List<Item> findAll();

    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    List<Item> findAllByOrderByIdDesc();

    @Query("select item from Item item where item.id in (select distinct i.id from Item i left join i.bids b where b.user.id = :#{#user.id})")
    List<Item> findItemsByBidder(@Param("user") User user);
    
    List<Item> findItemsBySeller(User currentUser);
    
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    @Query("select distinct i from Item i left join fetch i.bids b where i.bids IS EMPTY order by i.id desc")
    List<Item> findItemsWithNoBid();
}
