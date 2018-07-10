package com.propellerhead.app.resources;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.propellerhead.app.exception.RecordNotFoundException;
import com.propellerhead.app.model.Customer;
import com.propellerhead.app.model.Note;
import com.propellerhead.app.services.CustomerService;
import com.propellerhead.app.util.UtilMessages;
import com.propellerhead.app.util.UtilMessages.Messages;

/*
 * To compile angular
 * https://stackoverflow.com/questions/39498978/cannot-find-module-models-config */

@Path("customer/")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class CustomerResource {

	@Inject
	CustomerService customerService;

	@GET
	@Path("{idCustomer}")
	public Response findCustomer(@PathParam("idCustomer") Long idCustomer) {
		Response response = null;
		try {
			Customer customer = customerService.find(idCustomer);
			response = Response.ok().entity(customer).build();
		} catch (Exception e) {
			response = Response.status(Response.Status.NOT_FOUND)
					.entity(UtilMessages.getMessage("message", Messages.RECORD_NOT_FOUND.getMsg())).build();
		}
		return response;
	}
	

	@GET
	@Path("{idCustomer}/notes")
	public Response getCustomerNotes(@PathParam("idCustomer") Long idCustomer) {
		Response response = null;
		try {
			List<Note> customer = customerService.getCustomerNotes(idCustomer);
			response = Response.ok().entity(customer).build();
		} catch (Exception e) {
			response = Response.status(Response.Status.NOT_FOUND)
					.entity(UtilMessages.getMessage("message", Messages.RECORD_NOT_FOUND.getMsg())).build();
		}
		return response;
	}

	@GET
	public Response list() {
		List<Customer> clients = customerService.listCustomerWithDataLazzy();
		return Response.ok().entity(clients).build();
	}

	@DELETE
	@Path("{idCustomer}")
	public Response remove(@PathParam("idCustomer") Long idCustomer) {
		Response response = null;
		try {
			customerService.remove(idCustomer);
			response = Response.ok().entity(UtilMessages.getMessage("message", Messages.RECORD_REMOVED.getMsg()))
					.build();
		} catch (RecordNotFoundException e) {
			response = Response.status(Response.Status.NOT_FOUND)
					.entity(UtilMessages.getMessage("message", Messages.RECORD_NOT_FOUND.getMsg())).build();
		}
		return response;
	}

	@PUT
	@Path("{idCustomer}")
	public Response addCustomer(Customer customer, @PathParam("idCustomer") Long idCustomer) {
		customer.setId(idCustomer);
		customer = customerService.change(customer);
		return Response.ok().entity(customer).build();
	}

	@POST
	public Response add(Customer customer) {
		customer = customerService.add(customer);
		return Response.ok().entity(customer).build();
	}
	
}