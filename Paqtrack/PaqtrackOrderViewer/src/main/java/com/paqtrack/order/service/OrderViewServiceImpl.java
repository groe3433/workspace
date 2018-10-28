package com.paqtrack.order.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paqtrack.order.dao.OrderViewDAO;
import com.paqtrack.order.model.OrderView;

/**
 * This is the hibernate service class that will use the Order object for querying our Order table. 
 * 
 * @author Andrew Groenewold
 * @version 1.0.0
 * @Date: February 15, 2017
 * @Copyright: Copyright © 2017 by Andrew Groenewold. All Rights Reserved.
 * @category Order Hibernate Service Class
 */
@Service
public class OrderViewServiceImpl implements OrderViewService {
	
	private OrderViewDAO orderviewDAO;

	public void setOrderviewDAO(OrderViewDAO orderviewDAO) {
		this.orderviewDAO = orderviewDAO;
	}

	@Override
	@Transactional
	public List<OrderView> listOrders() {
		return this.orderviewDAO.listOrders();
	}
}