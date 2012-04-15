package com.mmounirou.jgitbox.exception;

public class WrappedGitBoxException extends GitBoxException
{

	private static final long serialVersionUID = -6904316431823931526L;

	public WrappedGitBoxException(Throwable e)
	{
		super(e);
	}

}
