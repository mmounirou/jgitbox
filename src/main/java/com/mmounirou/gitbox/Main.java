package com.mmounirou.gitbox;

import java.io.File;
import java.io.IOException;

import com.mmounirou.gitbox.exception.GitBoxException;

public class Main
{

	private Main()
	{
		throw new AssertionError();
	}

	public static void main(String[] args) throws GitBoxException, IOException
	{
		File gitDir = new File("");
		File workTree = new File("");


		final GitRepository gitRepository = new GitRepository(gitDir, workTree);

		final GitBoxConfiguration gitBoxConfiguration = new GitBoxConfiguration();
		final GitBox gitbox = new GitBox(gitBoxConfiguration, gitRepository);

		gitbox.start();

	}

}
