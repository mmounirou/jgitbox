package com.mmounirou.gitbox.main;

import java.io.File;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;

import com.mmounirou.gitbox.observers.GitRepositoryObserver;

public class LogObserver implements GitRepositoryObserver
{
	private Logger logger = Logger.getLogger(getClass());

	public void onFileAdded(@Nonnull File file)
	{
		logger.info(String.format("%s added", file.getAbsolutePath()));
	}

	public void onErrorDuringFileAdd(@Nonnull File file, @Nonnull Exception e)
	{
		logger.error(String.format("%s adding failed", file.getAbsolutePath()), e);
	}

	public void onFileUpdated(@Nonnull File file)
	{
		logger.info(String.format("%s updated", file.getAbsolutePath()));
	}

	public void onErrorDuringFileUpdate(@Nonnull File file, @Nonnull Exception e)
	{
		logger.error(String.format("%s updating  failed", file.getAbsolutePath()), e);
	}

	public void onFileDeleted(@Nonnull File file)
	{
		logger.info(String.format("%s deleted", file.getAbsolutePath()));
	}

	public void onErrorDuringFileDelete(@Nonnull File file, @Nonnull Exception e)
	{
		logger.error(String.format("%s deleting failed", file.getAbsolutePath()), e);
	}

	public void onPull()
	{
		logger.info(String.format("pull done"));
	}

	public void onErrorDuringPull(@Nonnull Exception exception)
	{
		logger.error(String.format("pull failed"), exception);
	}

	public void onPush()
	{
		logger.info(String.format("push done"));
	}

	public void onErrorDuringPush(@Nonnull Exception exception)
	{
		logger.error(String.format("push failed"), exception);
	}

	public void setLogger(Logger logger)
	{
		this.logger = logger;
	}

}
