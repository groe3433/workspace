package co.order.dao;

import java.util.List;

import co.order.bean.Order_Header;

public interface Order_HeaderDAO {

	public void save(Order_Header o);
	
	public List<Order_Header> list();
	
}