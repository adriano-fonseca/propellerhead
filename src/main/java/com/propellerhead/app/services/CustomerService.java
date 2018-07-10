package com.propellerhead.app.services;

import java.util.List;

import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import com.propellerhead.app.dao.CustomerDAO;
import com.propellerhead.app.exception.MethodNotAllowedException;
import com.propellerhead.app.exception.RecordNotFoundException;
import com.propellerhead.app.model.Customer;
import com.propellerhead.app.model.Note;
import com.propellerhead.app.util.UtilMessages;
import com.propellerhead.app.util.UtilMessages.Messages;

@Model
public class CustomerService {

	@Inject
	CustomerDAO customerDAO;

	public Customer find(Long idCustomer) {
		return customerDAO.findByIdFetchingNotes(idCustomer);
	}

	public Response remove(Long idCustomer) {
		Response response = null;
		try {
			Customer client = new Customer();
			client.setId(idCustomer);
			customerDAO.remove(client);
			response = Response.ok().entity(UtilMessages.getMessage("message", Messages.RECORD_REMOVED.getMsg())).build();
		} catch (RecordNotFoundException e) {
			response = Response.status(Response.Status.BAD_REQUEST).entity(UtilMessages.getMessage("message", Messages.RECORD_NOT_FOUND.getMsg())).build();

		}
		return response;
	}

	public Customer add(Customer client) {
		client = customerDAO.add(client);
		return client;
	}

	public Customer change(Customer client) {
		return customerDAO.change(client);
	}

	public List<Customer> list() {
		return customerDAO.list(new Customer());
	}

	public List<Customer> listCustomerWithDataLazzy() {
		return customerDAO.listCustomerWithNotesLazzy(new Customer());
	}
	
	/**
	 * Try to get a Customer from database, if there is not a Customer persisted, 
	 * it will persist the client and Notes
	 * 
	 * Case the Customer is already persisted, we need to check if there is already
	 * a left data persisted, it there is we need to return HTTP Error 405 (Method Not Allowed), 
	 * otherwise we will persist the left data.
	 * 
	 * PS: Second the HATEOS (Level 2) guidelines the Post Verb should be use to create
	 * and PUT should be used to Update Information
	 * 
	 * @param note
	 * @param idCustomer
	 * @param side
	 * @return
	 */
	public Response addNoteForCustomer(Note note, Long idCustomer) {
		Response response = null;
		
		try{
			//Can launch RecordNotFoundException
			Customer customer = customerDAO.findByIdFetchingNotes(idCustomer);
			//Can lauch MethodNotAllowedException
			customer = changeCustomerNote(customer, note);
			//If no Exceptions HTTP Status 200
			response = Response.ok().entity(customer).build();
		} catch (RecordNotFoundException e){
			Customer client = addCustomerAndData(idCustomer, note);
			response = Response.ok().entity(client).build();
		} catch (MethodNotAllowedException e){
			response = Response.status(Response.Status.METHOD_NOT_ALLOWED).entity(UtilMessages.getMessage("message", Messages.PUT_TO_UPDATE.getMsg())).build();
		}
		
		return response;
	}
	
	/**
	 * Basically checks if this note of Customer has already been persisted. If was launch
	 * an MethodNotAllowedException, because is an update (Should use PUT), or else change
	 * client adding this side data.
	 * 
	 * @param customer
	 * @param note 
	 * @param side
	 * @return
	 */
	private Customer changeCustomerNote(Customer customer, Note note) {
		customer.getListNote().add(note);
		customer = customerDAO.change(customer);
		return customer;
	}

	private Customer addCustomerAndData(Long idCustomer, Note data) {
		Customer client = new Customer(idCustomer);
		client.getListNote().add(data);
		client = customerDAO.add(client);
		return client;
	}

	public List<Note> getCustomerNotes(Long idCustomer) {
		return customerDAO.findByIdFetchingNotes(idCustomer).getListNote();
	}

}
