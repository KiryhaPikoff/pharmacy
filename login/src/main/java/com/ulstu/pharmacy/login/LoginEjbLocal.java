package com.ulstu.pharmacy.login;

import com.ulstu.pharmacy.login.model.Authentication;

import javax.ejb.Local;

@Local
public interface LoginEjbLocal {

    boolean validate(Authentication auth);
}