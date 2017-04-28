package us.pojo.silentauction.repository;

import org.springframework.data.repository.CrudRepository;

import us.pojo.silentauction.model.Auction;

public interface AuctionRepository extends CrudRepository<Auction, Integer> {

}
