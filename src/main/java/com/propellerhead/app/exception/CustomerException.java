package com.propellerhead.app.exception;

import java.util.HashMap;


/**
 * 
 * Exception launched when something unexpected happens
 * 
 * @author adriano-fonseca
 *
 */
public class CustomerException extends DAOException {

  private static final long serialVersionUID = 1L;

  public CustomerException(HashMap<String, String> msg) {
    super(msg.get("message"));
  }
}