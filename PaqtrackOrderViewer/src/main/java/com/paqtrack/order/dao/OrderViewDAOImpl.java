package com.paqtrack.order.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.paqtrack.order.model.OrderView;

/**
 * This is the DAO Implementation for accessing our Order table. 
 * 
 * @author Andrew Groenewold
 * @version 1.0.0
 * @Date: February 15, 2017
 * @Copyright: Copyright © 2017 by Andrew Groenewold. All Rights Reserved.
 * @category Order DAO Class
 */
@Repository
public class OrderViewDAOImpl implements OrderViewDAO {
	
	private static final Logger logger = LoggerFactory.getLogger(OrderViewDAOImpl.class);

	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sf){
		this.sessionFactory = sf;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<OrderView> listOrders() {
		Session session = this.sessionFactory.getCurrentSession();
		List<OrderView> ordersList = session.createQuery("from OrderView").list();
		return ordersList;
	}
}