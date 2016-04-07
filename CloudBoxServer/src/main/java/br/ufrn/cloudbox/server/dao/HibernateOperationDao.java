package br.ufrn.cloudbox.server.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import br.ufrn.cloudbox.model.User;
import br.ufrn.cloudbox.server.model.Operation;

public class HibernateOperationDao implements IOperationDao {

	private static HibernateOperationDao hibernateOperationDao;
	private SessionFactory sessionFactory;

	public static HibernateOperationDao getInstance() {
		if (hibernateOperationDao == null)
			hibernateOperationDao = new HibernateOperationDao();
		return hibernateOperationDao;
	}

	private HibernateOperationDao() {
		this.sessionFactory = new Configuration().configure().buildSessionFactory();
	}

	public Long registerOperation(Operation operation) {
		Session session = sessionFactory.openSession();
		Long operationId = null;
		Transaction transaction = null;

		try {
			transaction = session.beginTransaction();
			operationId = (Long) session.save(operation);
			transaction.commit();
		} catch (HibernateException e) {
			if (transaction != null) {
				transaction.rollback();
			}
		} finally {
			session.close();
		}

		return operationId;
	}

//	public Operation getLastOperation(User user, String relativeFilePath) {
//		Session session = sessionFactory.openSession();
//		DetachedCriteria maxDatetimeQuery = DetachedCriteria.forClass(Operation.class);
//		maxDatetimeQuery.add(Restrictions.eq("user.id", user.getId()));
//		maxDatetimeQuery.add(Restrictions.eq("relativeFilePath", relativeFilePath));
//		maxDatetimeQuery.setProjection(Projections.max("datetime"));
//
//		Criteria criteria = session.createCriteria(Operation.class);
//		criteria.add(Restrictions.eq("user.id", user.getId()));
//		criteria.add(Restrictions.eq("relativeFilePath", relativeFilePath));
//		criteria.add(Property.forName("datetime").eq(maxDatetimeQuery));
//
//		Operation foundOperation = (Operation) criteria.uniqueResult();
//
//		session.close();
//
//		return foundOperation;
//	}

	public Map<String, Operation> getFilesOperations(User user) {
		Session session = sessionFactory.openSession();
		Query query = session
				.createSQLQuery("SELECT op.operation_id, op.datetime, op.type, op.relative_file_path, op.user_id"
						+ " FROM operation AS op"
						+ " INNER JOIN ( SELECT relative_file_path, user_id, MAX(datetime) datetime" 
						+ " FROM operation"
						+ " WHERE user_id = :user_id"
						+ " GROUP BY relative_file_path, user_id"
						+ " ORDER BY relative_file_path) op2"
						+ " ON op.relative_file_path = op2.relative_file_path AND op.user_id = op2.user_id AND op.datetime = op2.datetime ORDER BY op.relative_file_path")
				.addEntity(Operation.class)
				.setParameter("user_id", user.getId());

		@SuppressWarnings("unchecked")
		List<Operation> operationList = (List<Operation>) query.list();
		session.close();

		HashMap<String, Operation> operationHashMap = new HashMap<String, Operation>();
		for (Operation operation : operationList) {
			operationHashMap.put(operation.getRelativeFilePath(), operation);
		}
		return operationHashMap;
	}
	
}
