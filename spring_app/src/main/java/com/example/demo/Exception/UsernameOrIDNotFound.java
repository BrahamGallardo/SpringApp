package com.example.demo.Exception;

public class UsernameOrIDNotFound extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3695278313314267768L;

	public UsernameOrIDNotFound() {
		super("Ususario no encontrado");
	}

	public UsernameOrIDNotFound(String msg) {
		super(msg);
	}
}
