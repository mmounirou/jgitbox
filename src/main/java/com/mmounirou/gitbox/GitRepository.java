package com.mmounirou.gitbox;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Nonnull;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;

public class GitRepository extends GitRepositoryObservable
{
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	private final Git git;
	private final File gitDirectory;
	private final File workTree;

	public GitRepository(@Nonnull File gitDirectory, @Nonnull File workTree) throws IOException
	{
		Repository repository = new RepositoryBuilder().setGitDir(gitDirectory).setWorkTree(workTree).readEnvironment().findGitDir().build();

		this.git = new Git(repository);
		this.gitDirectory = gitDirectory;
		this.workTree = workTree;
	}

	public void addFile(final File file)
	{
		executorService.execute(new Runnable()
		{

			public void run()
			{
				try
				{
					// TODO check that the file is in the workTree;
					// TODO check that the file is not still added
					// TODO check that the file is not too big for auto-commit
					// TODO check if the file is a directory

					String strRelativePath = GitBoxUtils.getRelativePath(file, workTree);
					git.add().addFilepattern(strRelativePath).call();
					git.commit().setMessage(String.format("auto add %s ", strRelativePath)).call();

					fireFileAdded(file);

				} catch (RuntimeException e)
				{
					throw e;
				} catch (Exception e)
				{
					fireErrorDuringFileAdd(file, e);
				}
			}
		});
	}

	public void updateFile(final File file)
	{
		executorService.execute(new Runnable()
		{

			public void run()
			{
				try
				{
					// TODO check that the file is in the workTree;
					// TODO check that the file is not too big for auto-commit
					// TODO check if the file is a directory

					String strRelativePath = GitBoxUtils.getRelativePath(file, workTree);
					git.add().addFilepattern(strRelativePath).setUpdate(true).call();
					git.commit().setMessage(String.format("auto add %s ", strRelativePath)).call();

					fireFileUpdated(file);

				} catch (RuntimeException e)
				{
					throw e;
				} catch (Exception e)
				{
					fireErrorDuringFileUpdate(file, e);
				}
			}
		});
	}

	public void deleteFile(final File file)
	{
		executorService.execute(new Runnable()
		{

			public void run()
			{
				try
				{
					// TODO check that the file is in the workTree;
					// TODO check that the file is not too big for auto-commit
					// TODO check if the file is a directory

					String strRelativePath = GitBoxUtils.getRelativePath(file, workTree);
					git.rm().addFilepattern(strRelativePath).call();
					git.commit().setMessage(String.format("auto remove %s ", strRelativePath)).call();

					fireFileDeleted(file);

				} catch (RuntimeException e)
				{
					throw e;
				} catch (Exception e)
				{
					fireErrorDuringFileDelete(file, e);
				}
			}
		});
	}

	public File getWorkTree()
	{
		return workTree;
	}

	public File getGitDirectory()
	{
		return gitDirectory;
	}

}
