package com.mmounirou.gitbox.core;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.FetchMergeCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;

import com.mmounirou.gitbox.observers.GitRepositoryObservable;
import com.mmounirou.gitbox.utils.GitBoxUtils;
import com.mmounirou.gitbox.utils.GitUtils;

public class GitRepository extends GitRepositoryObservable
{
	private Logger logger = Logger.getLogger(getClass());

	private final ExecutorService executorService = Executors.newSingleThreadExecutor();
	private final ExecutorService remoteExecutorService = Executors.newSingleThreadExecutor();

	private final Git git;
	private final File gitDirectory;
	private final File workTree;

	public GitRepository(@Nonnull File gitDirectory, @Nonnull File workTree) throws IOException
	{

		logger.debug(String.format("Load the git repository %s:%s ...", workTree.getAbsolutePath(), gitDirectory.getAbsolutePath()));
		Repository repository = new RepositoryBuilder().setGitDir(gitDirectory).setWorkTree(workTree).readEnvironment().findGitDir().build();
		createRepositoryIfNotExist(gitDirectory, repository);
		logger.debug(String.format("the git repository loaded", workTree.getAbsolutePath(), gitDirectory.getAbsolutePath()));

		this.git = new Git(repository);
		this.gitDirectory = gitDirectory;
		this.workTree = workTree;

	}

	private void createRepositoryIfNotExist(File gitDir, Repository repo) throws IOException
	{
		if (!gitDir.exists() || gitDir.list().length == 0)
		{
			logger.info("create the git repository ...");
			repo.create();
			logger.info("the repository created");
		}
	}

	public void addFile(@Nonnull final File file)
	{
		logger.debug(String.format("schedule add of %s", file.getAbsolutePath()));
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

					logger.debug(String.format("start add of %s ...", file.getAbsolutePath()));

					String strRelativePath = GitBoxUtils.getRelativePath(file, workTree);
					git.add().addFilepattern(strRelativePath).call();
					git.commit().setMessage(String.format("auto add %s ", strRelativePath)).call();

					fireFileAdded(file);

					logger.debug(String.format("%s added", file.getAbsolutePath()));

				} catch (RuntimeException e)
				{
					throw e;
				} catch (Exception e)
				{
					logger.error(String.format("%s add fail", file.getAbsolutePath()));

					fireErrorDuringFileAdd(file, e);
				}
			}
		});
	}

	public void updateFile(@Nonnull final File file)
	{
		logger.debug(String.format("schedule update of %s", file.getAbsolutePath()));
		executorService.execute(new Runnable()
		{

			public void run()
			{
				try
				{
					// TODO check that the file is in the workTree;
					// TODO check that the file is not too big for auto-commit
					// TODO check if the file is a directory

					logger.debug(String.format("start update of %s ...", file.getAbsolutePath()));

					String strRelativePath = GitBoxUtils.getRelativePath(file, workTree);
					git.add().addFilepattern(strRelativePath).setUpdate(true).call();
					git.commit().setMessage(String.format("auto add %s ", strRelativePath)).call();

					fireFileUpdated(file);

					logger.debug(String.format("%s updated", file.getAbsolutePath()));

				} catch (RuntimeException e)
				{
					throw e;
				} catch (Exception e)
				{
					logger.error(String.format("%s delete fail", file.getAbsolutePath()));

					fireErrorDuringFileUpdate(file, e);
				}
			}
		});
	}

	public void deleteFile(@Nonnull final File file)
	{
		logger.debug(String.format("schedule delete of %s", file.getAbsolutePath()));
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

					logger.debug(String.format("start delete of %s ...", file.getAbsolutePath()));

					String strRelativePath = GitBoxUtils.getRelativePath(file, workTree);
					git.rm().addFilepattern(strRelativePath).call();
					git.commit().setMessage(String.format("auto remove %s ", strRelativePath)).call();

					fireFileDeleted(file);

					logger.debug(String.format("%s deleted", file.getAbsolutePath()));

				} catch (RuntimeException e)
				{
					throw e;
				} catch (Exception e)
				{
					logger.error(String.format("%s deleted fail", file.getAbsolutePath()));

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

	public void push()
	{
		//Run only if a remote is configured
		if (!haveRemote())
		{
			logger.debug(String.format("cancel the push : there are no remote configured"));
			return;
		}

		logger.debug(String.format("schedule push"));
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
							logger.debug(String.format("start push ..."));

							git.push().setForce(true).call();
							firePush();
						} catch (RuntimeException e)
						{
							throw e;
						} catch (Exception e)
						{
							logger.error(String.format("push failed"));
							fireErrorDuringPush(e);
						}
					}

				});
			}
		});

	}

	private boolean haveRemote()
	{
		return GitUtils.haveRemote(git.getRepository());
	}

	public void pull()
	{
		//Run only if a remote is configured
		if (!haveRemote())
		{
			logger.debug(String.format("cancel the pull : there are no remote configured"));
			return;
		}

		logger.debug(String.format("schedule pull"));

		//Use an another thread to fetch the remote refs .
		//this operation can be made // to the local add/update/delete operations
		remoteExecutorService.execute(new Runnable()
		{
			public void run()
			{
				try
				{
					//TODO manage error during merge
					logger.debug(String.format("start fetch ..."));

					final FetchMergeCommand fetchMergeCommand = new FetchMergeCommand(git.getRepository());
					fetchMergeCommand.fetch();

					logger.debug(String.format("remote fetched"));

					//apply the remotes changes to the local branch .
					//this operation cannot be made concurrently with add/update/delete operations so
					//use the localExecutorService to schedule this merge

					logger.debug(String.format("schedule merge/rebase"));

					executorService.execute(new Runnable()
					{

						public void run()
						{
							try
							{
								logger.debug(String.format("start merge/rebase ..."));

								fetchMergeCommand.applyLocally();

								//TODO provide more information about pull result : or use local notif to provide info on remote modification
								firePull();

								logger.debug(String.format("merged"));

							} catch (RefNotFoundException e)
							{
								logger.error(String.format("merge failed"));

								fireErrorPull(e);
							}
						}

					});

				} catch (RuntimeException e)
				{
					throw e;
				} catch (Exception e)
				{
					logger.error(String.format("pull failed"));
					fireErrorPull(e);
				}
			}
		});
	}
}
