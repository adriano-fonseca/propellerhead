package com.propellerhead.app.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.http.HttpEntity;
import org.apache.log4j.Logger;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.Type;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.propellerhead.app.exception.RecordNotFoundException;
import com.propellerhead.app.model.Customer;
import com.propellerhead.app.model.Note;

@RunWith(Arquillian.class)
public class CustomerDaoIT {

	@Deployment
	public static WebArchive createDeployment() {
		return ShrinkWrap.create(WebArchive.class).addClass(Type.class).addClass(HttpEntity.class)
				.addClass(ResultTransformer.class).addPackages(true, "com.propellerhead.app")
				.addAsResource("h2-test-persistence.xml", "META-INF/persistence.xml");
	}

	private static final Logger LOGGER = Logger.getLogger(CustomerDaoIT.class);

	@Inject
	CustomerDAO customerDAO;

	@Test
	public void shouldFailCustomerNotPersisted() {
		Customer customer = new Customer();
		customer.setName("Adriano Fonseca");
		customerDAO.add(customer);

		Customer customer2 = new Customer();
		customer2.setName("Regina Soares");
		customerDAO.add(customer2);

		List<Customer> list = customerDAO.list(new Customer());
		Assert.assertEquals(list.size(), 2);

	}

	@Test(expected = RecordNotFoundException.class)
	public void shouldFailIfCustomerWasFound() {
		Customer customer = new Customer();
		customer.setId(1000L);
		customer = customerDAO.find(customer);
	}

	@Test(expected = RecordNotFoundException.class)
	public void shouldFailIfCustomerWasDeleted() {
		Customer customer = new Customer();
		customer.setId(1000L);
		customer = customerDAO.remove(customer);
	}

	@Test
	public void shouldFailIfCustomerNotRemoved() {
		List<Customer> list = customerDAO.list(new Customer());
		Iterator<Customer> it = list.iterator();

		while (it.hasNext()) {
			Customer Customer = it.next();
			customerDAO.remove(Customer);
		}
		List<Customer> list2 = customerDAO.list(new Customer());
		Assert.assertEquals(list2.size(), 0);
	}

	@Test
	public void shouldFailIfCustomerNotChanged() {
		List<Customer> list = customerDAO.list(new Customer());
		Iterator<Customer> it = list.iterator();
		Customer customer = new Customer();
		String newName = "Adriano da Silva Fonseca";

		customer.setName("Adriano Fonseca");
		Customer customer2 = customerDAO.add(customer);

		customer2.setName(newName);
		customerDAO.change(customer2);

		Customer customerChanged = new Customer();
		customerChanged.setId(customer2.getId());
		customerChanged = customerDAO.find(customerChanged);
		Assert.assertEquals(newName, customerChanged.getName());
	}

	@Test
	public void shouldFailIfCustomerDontHaveTwoNotes() {
		Customer customer = new Customer();
		customer.setName("Adriano Fonseca");

		Customer customer2 = customerDAO.add(customer);
		List<Note> newList = new ArrayList<Note>();
		newList.add(new Note("Call to him!", customer2.getId()));
		newList.add(new Note("Send him an email!", customer2.getId()));

		customer2.setListNote(newList);
		customer = customerDAO.change(customer2);
		Assert.assertEquals(2, customer.getListNote().size());
	}

	@Test
	public void shouldFailIfCustomerDontHaveOneNoteAfterChange() {
		Customer customer = new Customer();
		customer.setName("Adriano Fonseca");

		Customer customer2 = customerDAO.add(customer);
		List<Note> newList = new ArrayList<Note>();
		newList.add(new Note("Call to him!", customer2.getId()));
		newList.add(new Note("Send him an email!", customer2.getId()));
		customer2.setListNote(newList);
		customer = customerDAO.change(customer2);
		customer.setListNote(null);
		Assert.assertEquals(0, customerDAO.change(customer).getListNote().size());
	}
}