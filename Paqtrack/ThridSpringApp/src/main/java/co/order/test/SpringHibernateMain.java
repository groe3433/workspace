package co.order.test;

import java.util.List;

import org.springframework.context.support.FileSystemXmlApplicationContext;

import co.order.dao.Order_HeaderDAO;
import co.order.bean.Order_Header;

public class SpringHibernateMain {

	public static void main(String[] args) {

		FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext("beans.xml");
		Order_HeaderDAO orderheaderDAO = (Order_HeaderDAO) context.getBean("orderheaderDAO");
		
		Order_Header oh = new Order_Header();
		oh.setId(12345678);
		oh.setTotal_price(12.89);
		oh.setName("TestItem1");
		orderheaderDAO.save(oh);
		System.out.println("Order_Header::"+oh);
		
		Order_Header oh1 = new Order_Header();
		oh1.setId(87654321);
		oh1.setTotal_price(13.50);
		oh1.setName("TestItem2");
		orderheaderDAO.save(oh1);
		System.out.println("Order_Header::"+oh1);
		
		List<Order_Header> list1 = orderheaderDAO.list();
		for(Order_Header o : list1){
			System.out.println("Order_Header List::"+o);
		}
		
		//close resources
		context.close();	
	}
}
