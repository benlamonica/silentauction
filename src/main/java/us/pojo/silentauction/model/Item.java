package us.pojo.silentauction.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Item {
	@Id
	private int id;
	
	private String name;
	
	private String description;
	
	private String image;
	
	@ManyToOne
	private User seller;
	
	private Double minimumBid;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public User getSeller() {
		return seller;
	}

	public void setSeller(User seller) {
		this.seller = seller;
	}

	public Double getMinimumBid() {
		return minimumBid;
	}

	public void setMinimumBid(Double minimumBid) {
		this.minimumBid = minimumBid;
	}
}
