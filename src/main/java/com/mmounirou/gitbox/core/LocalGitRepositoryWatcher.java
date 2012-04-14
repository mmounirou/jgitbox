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
import com.mmounirou.gitbox.utils.FilterableFileMonitor;

public class LocalGitRepositoryWatcher
{

	private static class GitListener implements FileListener
	{

		private final GitRepository repository;

		public GitListener(GitRepository gitRepository)
		{
			this.repository = gitRepository;
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
		//TODO make a first scan of the work tree to add non comitted files
		//TODO the delay gived to the monitor is : 1 second for every 1000 files processed ; so convert the user delay to this unit

		try
		{
			FileSystemManager fsManager = VFS.getManager();
			FileObject workTreeFolder = fsManager.resolveFile(gitRepository.getWorkTree().getAbsolutePath());
			FileObject gitFolder = fsManager.resolveFile(gitRepository.getGitDirectory().getAbsolutePath());

			FilterableFileMonitor fm = new FilterableFileMonitor(new GitListener(gitRepository), gitFolder);
			fm.setRecursive(true);
			long checkPeriod = gitBoxConfiguration.getDelayForLocalChangeCheckInSeconds();
			fm.setDelay(TimeUnit.MILLISECONDS.convert(checkPeriod, TimeUnit.SECONDS));
			fm.addFile(workTreeFolder);
			fm.start();

		} catch (FileSystemException e)
		{
			throw new WrappedGitBoxException(e);
		}

	}

}
