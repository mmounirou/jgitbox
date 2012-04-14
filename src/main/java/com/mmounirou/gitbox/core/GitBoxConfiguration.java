package com.mmounirou.gitbox.core;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;
import javax.swing.filechooser.FileSystemView;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

import com.google.common.io.Files;

public class GitBoxConfiguration
{

	private PropertiesConfiguration configuration;

	public GitBoxConfiguration(@Nonnull File configurationFile) throws ConfigurationException
	{
		configuration = new PropertiesConfiguration(configurationFile);
		configuration.setAutoSave(true);
		configuration.setReloadingStrategy(new FileChangedReloadingStrategy());
	}

	public GitBoxConfiguration() throws ConfigurationException
	{
		this(getGitBoxPropertyFile());
	}

	private static File getGitBoxPropertyFile() throws ConfigurationException
	{
		try
		{

			File homeDirectory = FileSystemView.getFileSystemView().getHomeDirectory();
			File gitBoxDirectory = new File(homeDirectory, ".gitbox");
			gitBoxDirectory.mkdirs();

			File gitBoxPropertyFile = new File(homeDirectory, "gitbox.properties");
			if (!gitBoxDirectory.exists())
			{
				Files.touch(gitBoxPropertyFile);
			}
			return gitBoxPropertyFile;

		} catch (IOException e)
		{
			throw new ConfigurationException(e);
		}

	}

	public long getDelayForLocalChangeCheckInSeconds()
	{
		return configuration.getLong(Constants.DELAY_BETWEEN_LOCAL_CHECK_IN_SECONDS, Constants.DEFAULT_DELAY_BETWEEN_LOCAL_CHECK_IN_SECONDS);
	}

	public long getPullPeriodInSeconds()
	{
		return configuration.getLong(Constants.DELAY_BETWEEN_PULL_IN_SECONDS, Constants.DEFAULT_DELAY_BETWEEN_PULL_IN_SECONDS);

	}

	public long getPushPeriodInSeconds()
	{
		return configuration.getLong(Constants.DELAY_BETWEEN_PUSH_IN_SECONDS, Constants.DEFAULT_DELAY_BETWEEN_PUSH_IN_SECONDS);
	}

}
