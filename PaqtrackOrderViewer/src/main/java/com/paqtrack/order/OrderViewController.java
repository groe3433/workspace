package com.paqtrack.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.paqtrack.order.model.OrderView;
import com.paqtrack.order.service.OrderViewService;

/**
 * This class will take in requests, return database results, and send them to the view. 
 * 
 * @author Andrew Groenewold
 * @version 1.0.0
 * @Date: February 15, 2017
 * @Copyright: Copyright © 2017 by Andrew Groenewold. All Rights Reserved.
 * @category Order Controller Class
 */
@Controller
public class OrderViewController {
	
	private OrderViewService orderviewService;
	
	@Autowired(required=true)
	@Qualifier(value="orderviewService")
	public void setOrderViewService(OrderViewService ps){
		this.orderviewService = ps;
	}
	
	// route http requests from: http://localhost:8080/PaqtrackOrderViewer/orderview
	@RequestMapping(value = "/orderview", method = RequestMethod.GET)
	public String listOrders(Model model) {
		model.addAttribute("orderview", new OrderView());
		model.addAttribute("listOrders", this.orderviewService.listOrders());
		return "orderview";
	}
}