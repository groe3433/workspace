package co.order.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import co.order.bean.Order_Header;

public class Order_HeaderDAOImpl implements Order_HeaderDAO {

	private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
	@Override
	public void save(Order_Header o) {
		Session session = this.sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		session.persist(o);
		tx.commit();
		session.close();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Order_Header> list() {
		Session session = this.sessionFactory.openSession();
		List<Order_Header> personList = session.createQuery("from Order_Header").list();
		session.close();
		return personList;
	}

}
