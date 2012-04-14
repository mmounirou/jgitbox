package com.mmounirou.gitbox;

import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Nonnull;

public class RemoteGitRepositoryWatcher
{

	public class PushTask extends TimerTask
	{

		private final GitRepository repository;
		private final String strRemote;

		public PushTask(GitRepository gitRepository, String remoteName)
		{
			// TODO send remotely only one commit

			repository = gitRepository;
			strRemote = remoteName;
		}

		@Override
		public void run()
		{
			repository.push(strRemote);
		}

	}

	public class PullTask extends TimerTask
	{

		private final GitRepository repository;

		public PullTask(GitRepository gitRepository)
		{
			repository = gitRepository;
		}

		@Override
		public void run()
		{
			repository.pull();
		}

	}

	private final GitBoxConfiguration gitBoxConfiguration;
	private final GitRepository gitRepository;
	private final String remoteName;

	public RemoteGitRepositoryWatcher(@Nonnull GitBoxConfiguration gitBoxConfiguration, GitRepository gitRepository, @Nonnull String remoteName)
	{
		// TODO ensure that the specified remote is well configured

		this.gitBoxConfiguration = gitBoxConfiguration;
		this.gitRepository = gitRepository;
		this.remoteName = remoteName;
	}

	public void start()
	{
		Timer pullTimer = new Timer(true);
		TimerTask pullTask = new PullTask(gitRepository);
		long pullPeriod = gitBoxConfiguration.getPullPeriod();
		pullTimer.scheduleAtFixedRate(pullTask, 0, pullPeriod);

		Timer pushTimer = new Timer(true);
		TimerTask pushTask = new PushTask(gitRepository, remoteName);
		long pushPeriod = gitBoxConfiguration.getPushPeriod();
		pushTimer.scheduleAtFixedRate(pushTask, 0, pushPeriod);

	}

}
