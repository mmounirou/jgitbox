package com.mmounirou.gitbox.core;

import java.io.File;

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
		try
		{
			FileSystemManager fsManager = VFS.getManager();
			FileObject listendir = fsManager.resolveFile(gitRepository.getWorkTree().getAbsolutePath());

			DefaultFileMonitor fm = new DefaultFileMonitor(new GitListener(gitRepository));
			fm.setRecursive(true);
			fm.setDelay(gitBoxConfiguration.getDelayForLocalChangeCheck());
			fm.addFile(listendir);
			fm.start();

		} catch (FileSystemException e)
		{
			throw new WrappedGitBoxException(e);
		}

	}

}
