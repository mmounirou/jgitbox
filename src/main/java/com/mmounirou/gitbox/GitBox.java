package com.mmounirou.gitbox;

import javax.annotation.Nonnull;

import com.mmounirou.gitbox.exception.GitBoxException;

public class GitBox
{

	private final LocalGitRepositoryWatcher localGitWatcher;
	private final RemoteGitRepositoryWatcher remoteGitWatcher;

	public GitBox(@Nonnull GitBoxConfiguration gitBoxConfiguration, @Nonnull GitRepository gitRepository)
	{
		this.localGitWatcher = new LocalGitRepositoryWatcher(gitBoxConfiguration, gitRepository);
		this.remoteGitWatcher = new RemoteGitRepositoryWatcher(gitBoxConfiguration, gitRepository);
	}

	public void start() throws GitBoxException
	{
		localGitWatcher.start();
		remoteGitWatcher.start();
	}

}
