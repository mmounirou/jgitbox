package com.mmounirou.gitbox.exception;

public class GitBoxException extends Exception
{
	private static final long serialVersionUID = 957440422250726391L;

	public GitBoxException()
	{
		super();
	}

	public GitBoxException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public GitBoxException(String message)
	{
		super(message);
	}

	public GitBoxException(Throwable cause)
	{
		super(cause);
	}

}
