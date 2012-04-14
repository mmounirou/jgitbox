package com.mmounirou.gitbox;

import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Nonnull;

public class RemoteGitRepositoryWatcher
{

	public class PushTask extends TimerTask
	{

		private final GitRepository repository;

		public PushTask(GitRepository gitRepository)
		{
			// TODO send remotely only one commit

			repository = gitRepository;
		}

		@Override
		public void run()
		{
			repository.push();
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

	public RemoteGitRepositoryWatcher(@Nonnull GitBoxConfiguration gitBoxConfiguration, GitRepository gitRepository)
	{
		// TODO ensure that the specified remote is well configured

		this.gitBoxConfiguration = gitBoxConfiguration;
		this.gitRepository = gitRepository;
	}

	public void start()
	{
		Timer pullTimer = new Timer(true);
		TimerTask pullTask = new PullTask(gitRepository);
		long pullPeriod = gitBoxConfiguration.getPullPeriod();
		pullTimer.scheduleAtFixedRate(pullTask, 0, pullPeriod);

		Timer pushTimer = new Timer(true);
		TimerTask pushTask = new PushTask(gitRepository);
		long pushPeriod = gitBoxConfiguration.getPushPeriod();
		pushTimer.scheduleAtFixedRate(pushTask, 0, pushPeriod);

	}

}
