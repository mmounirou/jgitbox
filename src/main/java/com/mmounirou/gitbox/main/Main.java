package com.mmounirou.gitbox.main;

import java.io.File;
import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;

import com.mmounirou.gitbox.GitBox;
import com.mmounirou.gitbox.core.GitBoxConfiguration;
import com.mmounirou.gitbox.core.GitRepository;
import com.mmounirou.gitbox.exception.GitBoxException;

public class Main
{

	private Main()
	{
		throw new AssertionError();
	}

	public static void main(String[] args) throws GitBoxException, IOException, ConfigurationException
	{
		File gitDir = new File("");
		File workTree = new File("");
		File configurationFile = new File("");

		final GitRepository gitRepository = new GitRepository(gitDir, workTree);
		gitRepository.addObserver(new LogObserver());

		final GitBoxConfiguration gitBoxConfiguration = new GitBoxConfiguration(configurationFile);
		final GitBox gitbox = new GitBox(gitBoxConfiguration, gitRepository);

		gitbox.start();

	}

}
