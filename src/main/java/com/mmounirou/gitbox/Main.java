package com.mmounirou.gitbox;

import com.mmounirou.gitbox.exception.GitBoxException;

public class Main
{

	private Main()
	{
		throw new AssertionError();
	}

	public static void main(String[] args) throws GitBoxException
	{
		final GitRepository gitRepository = new GitRepository();

		final GitBoxConfiguration gitBoxConfiguration = new GitBoxConfiguration();
		final GitBox gitbox = new GitBox(gitBoxConfiguration, gitRepository);

		gitbox.start();

	}

}
