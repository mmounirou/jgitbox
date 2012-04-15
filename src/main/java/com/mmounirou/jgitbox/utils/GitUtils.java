package com.mmounirou.jgitbox.utils;

import java.io.IOException;

import org.eclipse.jgit.JGitText;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;

public class GitUtils
{

	public static boolean haveRemote(Repository repo)
	{
		String branchName;
		try
		{
			String fullBranch = repo.getFullBranch();
			if (fullBranch == null)
				return false;

			if (!fullBranch.startsWith(Constants.R_HEADS))
			{
				return false;
			}
			branchName = fullBranch.substring(Constants.R_HEADS.length());
		} catch (IOException e)
		{
			throw new JGitInternalException(JGitText.get().exceptionCaughtDuringExecutionOfPullCommand, e);
		}

		// get the configured remote for the currently checked out branch
		// stored in configuration key branch.<branch name>.remote
		StoredConfig repoConfig = repo.getConfig();
		String remote = repoConfig.getString(ConfigConstants.CONFIG_BRANCH_SECTION, branchName, ConfigConstants.CONFIG_KEY_REMOTE);
		if (remote == null)
			// fall back to default remote
			remote = Constants.DEFAULT_REMOTE_NAME;

		// get the name of the branch in the remote repository
		// stored in configuration key branch.<branch name>.merge
		String remoteBranchName = repoConfig.getString(ConfigConstants.CONFIG_BRANCH_SECTION, branchName, ConfigConstants.CONFIG_KEY_MERGE);

		if (remoteBranchName == null)
		{
			return false;
		}
		return true;
	}
}
