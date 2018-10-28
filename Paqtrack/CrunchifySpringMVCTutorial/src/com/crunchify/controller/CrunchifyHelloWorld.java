package com.crunchify.controller;
 
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.ModelAndView;

import co.order.bean.Order_Header;
import co.order.dao.Order_HeaderDAO;
 
@Controller
public class CrunchifyHelloWorld {
 
	@RequestMapping("/welcome")
	public ModelAndView helloWorld() {
 		
		//ApplicationContext context = new ClassPathXmlApplicationContext("**/applicationContext.xml");
		//FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext("**/WEB-INF/applicationContext.xml");
		//Order_HeaderDAO orderheaderDAO = (Order_HeaderDAO) context.getBean("orderheaderDAO");
		
		//Order_Header oh = new Order_Header();
		//oh.setId(12345678);
		//oh.setTotal_price(12.89);
		//oh.setName("TestItem1");
		//orderheaderDAO.save(oh);
		//System.out.println("Order_Header::"+oh);
		
		//Order_Header oh1 = new Order_Header();
		//oh1.setId(87654321);
		//oh1.setTotal_price(13.50);
		//oh1.setName("TestItem2");
		//orderheaderDAO.save(oh1);
		//System.out.println("Order_Header::"+oh1);
		
		//String temp = "";
		//List<Order_Header> list1 = orderheaderDAO.list();
		//for(Order_Header o : list1){
		//	temp = temp + o;
		//	System.out.println(temp);
		//}
		
		String message = "<br><div style='text-align:center;'>"
				+ "<h3>********** Hello World, Spring MVC Tutorial</h3>This message is coming from CrunchifyHelloWorld.java **********</div><br><br>";
		return new ModelAndView("welcome", "message", message);
	}
}