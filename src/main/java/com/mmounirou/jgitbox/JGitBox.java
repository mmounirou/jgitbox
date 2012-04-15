package com.mmounirou.jgitbox;

import javax.annotation.Nonnull;

import com.mmounirou.jgitbox.core.GitBoxConfiguration;
import com.mmounirou.jgitbox.core.GitRepository;
import com.mmounirou.jgitbox.core.LocalGitRepositoryWatcher;
import com.mmounirou.jgitbox.core.RemoteGitRepositoryWatcher;
import com.mmounirou.jgitbox.exception.GitBoxException;

public class JGitBox
{

	private final LocalGitRepositoryWatcher localGitWatcher;
	private final RemoteGitRepositoryWatcher remoteGitWatcher;

	public JGitBox(@Nonnull GitBoxConfiguration gitBoxConfiguration, @Nonnull GitRepository gitRepository)
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
