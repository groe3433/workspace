package com.paqtrack.order.dao;

import java.util.List;

import com.paqtrack.order.model.OrderView;

/**
 * This is the DAO Interface for the DAO Implementation for accessing our Order table. 
 * 
 * @author Andrew Groenewold
 * @version 1.0.0
 * @Date: February 15, 2017
 * @Copyright: Copyright © 2017 by Andrew Groenewold. All Rights Reserved.
 * @category Order DAO Interface
 */
public interface OrderViewDAO {
	
	public List<OrderView> listOrders();
}