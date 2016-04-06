package br.ufrn.cloudbox.server.dao;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;

import br.ufrn.cloudbox.model.User;

public class HibernateUserDao implements IUserDao {

	private static HibernateUserDao hibernateUserDao;
	private SessionFactory sessionFactory;

	public static HibernateUserDao getInstance() {
		if (hibernateUserDao == null)
			hibernateUserDao = new HibernateUserDao();
		return hibernateUserDao;
	}

	private HibernateUserDao() {
		this.sessionFactory = new Configuration().configure().buildSessionFactory();
	}
	
	public Long register(User user) {
		Session session = sessionFactory.openSession();
		Long userId = null;
		Transaction transaction = null;
		
		try {
			transaction = session.beginTransaction();
			userId = (Long) session.save(user);
			transaction.commit();
		} catch (HibernateException e) {
			if (transaction != null) {
				transaction.rollback();
			}
		} finally {
			session.close();
		}
		
		return userId;
	}
	
	public User findByEmail(String email) {
		Session session = sessionFactory.openSession();
		Criteria criteria = session.createCriteria(User.class);
		criteria.add(Restrictions.eq("email", email));
		
		User foundUser = (User) criteria.uniqueResult();
		
		session.close();
		
		return foundUser;
	}

	public User findByEmailAndPassword(String email, String password) {
		Session session = sessionFactory.openSession();
		Criteria criteria = session.createCriteria(User.class);
		criteria.add(Restrictions.eq("email", email));
		criteria.add(Restrictions.eq("password", password));
		
		User foundUser = (User) criteria.uniqueResult();
		
		session.close();
		
		return foundUser;
	}

}
