package com.mmounirou.gitbox;

import javax.annotation.Nonnull;

public class RemoteGitRepositoryWatcher
{

	@SuppressWarnings("unused")
	private final GitBoxConfiguration gitBoxConfiguration;

	public RemoteGitRepositoryWatcher(@Nonnull GitBoxConfiguration gitBoxConfiguration, GitRepository gitRepository)
	{
		this.gitBoxConfiguration = gitBoxConfiguration;
	}

	public void start()
	{
		throw new UnsupportedOperationException();
	}

}
