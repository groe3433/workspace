package com.paqtrack.order.service;

import java.util.List;

import com.paqtrack.order.model.OrderView;

/**
 * This is the hibernate service interface that will be implemented by the hibernate service class
 * which will use the Order object for querying our Order table. 
 * 
 * @author Andrew Groenewold
 * @version 1.0.0
 * @Date: February 15, 2017
 * @Copyright: Copyright © 2017 by Andrew Groenewold. All Rights Reserved.
 * @category Order Hibernate Service Interface
 */
public interface OrderViewService {
	
	public List<OrderView> listOrders();
}