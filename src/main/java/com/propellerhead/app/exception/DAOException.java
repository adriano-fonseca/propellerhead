package com.propellerhead.app.exception;

import javax.ejb.ApplicationException;

@ApplicationException
public class DAOException extends RuntimeException {
	
	public DAOException(String mensagem) {
		super(mensagem);
	}

}
