package com.example.demo.Exception;

public class CantChangePass extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6744781581184275583L;
	
	public CantChangePass() {
		super ("Can't do this action"); 
	}
	public CantChangePass(String msg) {
		super(msg);
	}
}
