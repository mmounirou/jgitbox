package com.mmounirou.gitbox;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Nonnull;

import org.eclipse.jgit.api.FetchMergeCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;

public class GitRepository extends GitRepositoryObservable
{
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();
	private final ExecutorService remoteExecutorService = Executors.newSingleThreadExecutor();

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
					// TODO make a commit before deletion if the file is already tracked

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

	public void push(String strRemote)
	{

		//don't execute an push and a pull //
		remoteExecutorService.execute(new Runnable()
		{

			public void run()
			{
				//don't execute a push and a add/remote/delete operation
				executorService.execute(new Runnable()
				{

					public void run()
					{
						try
						{
							git.push().setForce(true).call();
							firePush();
						} catch (RuntimeException e)
						{
							throw e;
						} catch (Exception e)
						{
							fireErrorDuringPush(e);
						}
					}

				});
			}
		});

	}

	public void pull()
	{
		//Use an another thread to fetch the remote refs .
		//this operation can be made // to the local add/update/delete operations
		remoteExecutorService.execute(new Runnable()
		{
			public void run()
			{
				try
				{
					//TODO manage error during merge

					final FetchMergeCommand fetchMergeCommand = new FetchMergeCommand(git.getRepository());
					fetchMergeCommand.call();

					//apply the remotes changes to the local branch .
					//this operation cannot be made concurrently with add/update/delete operations so
					//use the localExecutorService to schedule this merge
					executorService.execute(new Runnable()
					{

						public void run()
						{
							try
							{
								fetchMergeCommand.applyLocally();

								//TODO provide more information about pull result : or use local notif to provide info on remote modification
								firePull();
							} catch (RefNotFoundException e)
							{
								fireErrorPull(e);
							}
						}

					});

				} catch (RuntimeException e)
				{
					throw e;
				} catch (Exception e)
				{
					fireErrorPull(e);
				}
			}
		});
	}
}
