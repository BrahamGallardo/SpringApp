package com.example.demo.Exception;

public class UsernameOrIDNotFound extends Exception {

	public UsernameOrIDNotFound() {
		super("Ususario no encontrado");
	}

	public UsernameOrIDNotFound(String msg) {
		super(msg);
	}
}
