package com.mmounirou.gitbox.core;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;

import com.mmounirou.gitbox.exception.WrappedGitBoxException;

public class LocalGitRepositoryWatcher
{

	private static class GitListener implements FileListener
	{

		private final GitRepository repository;

		public GitListener(GitRepository gitRepository)
		{
			repository = gitRepository;
		}

		public void fileCreated(FileChangeEvent event) throws Exception
		{
			repository.addFile(toFile(event));
		}

		public void fileDeleted(FileChangeEvent event) throws Exception
		{
			repository.deleteFile(toFile(event));
		}

		public void fileChanged(FileChangeEvent event) throws Exception
		{
			repository.updateFile(toFile(event));
		}

		private File toFile(@Nonnull FileChangeEvent event) throws FileSystemException
		{
			FileObject fileObject = event.getFile();
			File file = new File(fileObject.getName().getPathDecoded());
			return file;
		}

	}

	private final GitBoxConfiguration gitBoxConfiguration;
	private final GitRepository gitRepository;

	public LocalGitRepositoryWatcher(@Nonnull GitBoxConfiguration gitBoxConfiguration, GitRepository gitRepository)
	{
		this.gitBoxConfiguration = gitBoxConfiguration;
		this.gitRepository = gitRepository;
	}

	public void start() throws WrappedGitBoxException
	{
		//TODO exclude git dir from monitoring
		//TODO the delay gived to the monitor is : 1 second for every 1000 files processed ; so convert the user delay to this unit

		try
		{
			FileSystemManager fsManager = VFS.getManager();
			FileObject listendir = fsManager.resolveFile(gitRepository.getWorkTree().getAbsolutePath());

			DefaultFileMonitor fm = new DefaultFileMonitor(new GitListener(gitRepository));
			fm.setRecursive(true);
			long checkPeriod = gitBoxConfiguration.getDelayForLocalChangeCheckInSeconds();
			fm.setDelay(TimeUnit.MILLISECONDS.convert(checkPeriod, TimeUnit.SECONDS));
			fm.addFile(listendir);
			fm.start();

		} catch (FileSystemException e)
		{
			throw new WrappedGitBoxException(e);
		}

	}

}