package co.order.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Order_Header")
public class Order_Header {
	@Id
	@Column(name="Order_Header_Key")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int Order_Header_Key;
	
	private int id;
	
	private double total_price;
	
	private String name;

	public int getOrder_Header_Key() {
		return Order_Header_Key;
	}

	public void setOrder_Header_Key(int order_Header_Key) {
		Order_Header_Key = order_Header_Key;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getTotal_price() {
		return total_price;
	}

	public void setTotal_price(double total_price) {
		this.total_price = total_price;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Order_Header [Order_Header_Key=" + Order_Header_Key + ", id=" + id + ", total_price=" + total_price
				+ ", name=" + name + "]";
	}	
}
