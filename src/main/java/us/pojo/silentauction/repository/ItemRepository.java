package us.pojo.silentauction.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import us.pojo.silentauction.model.Item;

public interface ItemRepository extends CrudRepository<Item, Integer> {
    List<Item> findAll();
}
