package com.paqtrack.order.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * This is the hibernate entity bean with JPA annotations for our Order table. 
 * 
 * @author Andrew Groenewold
 * @version 1.0.0
 * @Date: February 15, 2017
 * @Copyright: Copyright © 2017 by Andrew Groenewold. All Rights Reserved.
 * @category Order Hibernate Entity Bean
 */
@Entity
@Table(name="ORDERVIEW")
public class OrderView {

	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	private String orderNo;
	
	private String totalCost;
	
	private String customerName;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(String totalCost) {
		this.totalCost = totalCost;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	@Override
	public String toString() {
		return "OrderView [id=" + id + ", orderNo=" + orderNo + ", totalCost=" + totalCost + ", customerName="
				+ customerName + "]";
	}		
}