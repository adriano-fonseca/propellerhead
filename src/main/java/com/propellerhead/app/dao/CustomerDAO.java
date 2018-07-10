package com.propellerhead.app.dao;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.propellerhead.app.exception.DAOException;
import com.propellerhead.app.exception.RecordNotFoundException;
import com.propellerhead.app.model.Customer;
import com.propellerhead.app.util.UtilMessages;
import com.propellerhead.app.util.UtilMessages.Messages;

@Stateless
public class CustomerDAO {

	@PersistenceContext
	private EntityManager entityManager;
	
	public Customer add(Customer t) {
			if (entityManager.contains(t)) {
				throw new RuntimeException("Trying add an object already menaged.");
			}
			entityManager.persist(t);
			entityManager.flush();
			return t;
	}

	public Customer remove(Customer Customer) {
		Customer = this.find(Customer);
		if (Customer == null) {
			throw new RecordNotFoundException(UtilMessages.getMessage("message", UtilMessages.Messages.RECORD_NOT_FOUND.getMsg()));
		} else {
			entityManager.remove(Customer);
			entityManager.flush();
		}
		return Customer;
	}
	
	public Customer change(Customer t) {
		HashMap<String, String> msg = null;
		try {
			Customer queryBean = find(t);
			if (queryBean == null) {
				msg = new HashMap<String, String>();
				msg.put("message", "Record not found!");
				throw new RecordNotFoundException(msg);
			}
		} catch (DAOException e) {
			msg.put("message", "Record not found!");
			throw new RecordNotFoundException(msg);
		}

		Customer managed = entityManager.merge(t);
		entityManager.flush();
		return managed;
	}
	
	
	/**
	 * 
	 * @param Customer
	 * @return find Customer by id
	 */
	public Customer find(Customer Customer) {
		Customer entityReturned = entityManager.find(Customer.class, Customer.getId());
		HashMap<String, String> msg = null;

		if (entityReturned == null) {
			msg = new HashMap<String, String>();
			msg.put("message", "Record not found!");
			if (msg != null) {
				throw new RecordNotFoundException(msg);
			}
		}
		return entityReturned;
	}
	/**
	 * 
	 * @param Customer
	 * @return return all Customers
	 */
  final public List<Customer> list(Customer Customer) {
  	TypedQuery<Customer> q =  entityManager.createQuery("SELECT t FROM SimpleEntity t", Customer.class);
  	return q.getResultList();
  }


	public Customer findByIdFetchingNotes(Long id){
		try{
			Query query = entityManager.createQuery("SELECT c FROM Customer c LEFT JOIN FETCH c.listNote n WHERE c.id=:id");
			query.setParameter("id", id);
			return (Customer) query.getSingleResult();
		}catch (NoResultException e){
			throw new RecordNotFoundException(UtilMessages.getMessage("message", Messages.RECORD_NOT_FOUND.getMsg()));
		}
	}
	
	/**
	 * 
	 * @param idCustomer
	 * @return
	 * 
	 * Package Scope for the Tests
	 */
	Customer getDataForCustomer(Long idCustomer) {
		try {
			return this.findByIdFetchingNotes(idCustomer);
		} catch (Exception e) {
			throw new RecordNotFoundException(UtilMessages.getMessage("message",Messages.RECORD_NOT_FOUND.getMsg()));
		}
	}
	
	


	@SuppressWarnings("unchecked")
	public List<Customer> listCustomerWithNotesLazzy(Customer Customer) {
		List<Customer> list = this.entityManager.createQuery("SELECT c FROM Customer c LEFT JOIN FETCH c.listNote n ").getResultList();
		return list;
	}
}