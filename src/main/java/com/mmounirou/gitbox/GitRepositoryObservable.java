package com.mmounirou.gitbox;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.collect.Maps;

public class GitRepositoryObservable
{
	private static interface CustomRunnable
	{
		void run(GitRepositoryObserver listener);
	}

	// Notify each listener sequentially but in a separate thread
	private final Map<GitRepositoryObserver, ExecutorService> listeners = Maps.newLinkedHashMap();

	public void addListener(GitRepositoryObserver observer)
	{
		listeners.put(observer, Executors.newSingleThreadExecutor());
	}

	public void removeListener(GitRepositoryObserver observer)
	{
		ExecutorService executorService = listeners.remove(observer);
		executorService.shutdown();
	}

	protected void fireFileAdded(final File file)
	{
		notifyListeners(new CustomRunnable()
		{

			public void run(GitRepositoryObserver listener)
			{
				listener.onFileAdded(file);
			}
		});
	}

	protected void fireErrorDuringFileAdd(final File file, final Exception e)
	{
		notifyListeners(new CustomRunnable()
		{

			public void run(GitRepositoryObserver listener)
			{
				listener.onErrorDuringFileAdd(file, e);
			}
		});

	}

	protected void fireFileUpdated(final File file)
	{
		notifyListeners(new CustomRunnable()
		{

			public void run(GitRepositoryObserver listener)
			{
				listener.onFileUpdated(file);
			}
		});
	}

	protected void fireErrorDuringFileUpdate(final File file, final Exception e)
	{
		notifyListeners(new CustomRunnable()
		{

			public void run(GitRepositoryObserver listener)
			{
				listener.onErrorDuringFileUpdate(file, e);
			}
		});
	}

	protected void fireFileDeleted(final File file)
	{
		notifyListeners(new CustomRunnable()
		{

			public void run(GitRepositoryObserver listener)
			{
				listener.onFileDeleted(file);
			}
		});
	}

	protected void fireErrorDuringFileDelete(final File file, final Exception e)
	{
		notifyListeners(new CustomRunnable()
		{

			public void run(GitRepositoryObserver listener)
			{
				listener.onErrorDuringFileDelete(file, e);
			}
		});
	}

	protected void firePull()
	{
		notifyListeners(new CustomRunnable()
		{

			public void run(GitRepositoryObserver listener)
			{
				listener.onPull();
			}
		});
	}

	protected void fireErrorPull(Exception e)
	{
		notifyListeners(new CustomRunnable()
		{

			public void run(GitRepositoryObserver listener)
			{
				listener.onErrorDuringPull();
			}
		});
	}

	private void notifyListeners(final CustomRunnable command)
	{
		for (final GitRepositoryObserver listener : listeners.keySet())
		{
			ExecutorService executorService = listeners.get(listener);
			executorService.execute(new Runnable()
			{

				public void run()
				{
					command.run(listener);
				}
			});
		}
	}

}
