package com.mmounirou.gitbox.core;

import java.io.File;

import javax.annotation.Nonnull;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

public class GitBoxConfiguration
{

	private PropertiesConfiguration configuration;

	public GitBoxConfiguration(@Nonnull File configurationFile) throws ConfigurationException
	{
		configuration = new PropertiesConfiguration(configurationFile);
		configuration.setAutoSave(true);
		configuration.setReloadingStrategy(new FileChangedReloadingStrategy());
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
