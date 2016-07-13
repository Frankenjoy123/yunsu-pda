package com.yunsoo.exception;

@SuppressWarnings("serial")
public class ServerAuthException extends BaseException {
	
	public ServerAuthException()
	{
		super("PDA is not authorized");
	}

	boolean loginRequired;
	public ServerAuthException(String msg, boolean loginRequired)
	{
		super(msg);
		this.loginRequired = loginRequired;
	}

	public boolean isLoginRequired() {
		return loginRequired;
	}
}
