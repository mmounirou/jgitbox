package com.mmounirou.jgitbox.main;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.eclipse.jgit.lib.Constants;

import com.google.common.util.concurrent.Monitor;
import com.mmounirou.jgitbox.JGitBox;
import com.mmounirou.jgitbox.core.GitBoxConfiguration;
import com.mmounirou.jgitbox.core.GitRepository;
import com.mmounirou.jgitbox.exception.GitBoxException;
import com.mmounirou.jgitbox.observers.LogObserver;

@SuppressWarnings("static-access")
public class Main
{
	private static Logger logger = Logger.getLogger(Main.class);

	//@formatter:off
	private static final Option WORK_TREE = OptionBuilder.withLongOpt("work-tree").isRequired(true).hasArg(true).withType(File.class).withDescription("git working directory").create("w");
	private static final Option GIT_DIR = OptionBuilder.withLongOpt("git-dir").isRequired(false).hasArg(true).withType(File.class).withDescription("git directory").create("g");
	private static final Option CONFIG_FILE = OptionBuilder.withLongOpt("properties").isRequired(false).hasArg(true).withType(File.class).withDescription("gitbox properties file").create("p");
	//@formatter:on

	private static final Options OPTIONS = buildOptions();

	private Main()
	{
		throw new AssertionError();
	}

	private static Options buildOptions()
	{
		Options result = new Options();

		result.addOption(WORK_TREE);
		result.addOption(GIT_DIR);
		result.addOption(CONFIG_FILE);

		return result;
	}

	public static void main(String[] args) throws GitBoxException, IOException, ConfigurationException
	{

		try
		{
			CommandLine commandLine = new PosixParser().parse(OPTIONS, args);
			File workTree = (File) commandLine.getParsedOptionValue(WORK_TREE.getOpt());

			File gitDir;
			GitBoxConfiguration gitBoxConfiguration;

			if (commandLine.hasOption(GIT_DIR.getOpt()))
			{
				gitDir = (File) commandLine.getParsedOptionValue(WORK_TREE.getOpt());
			} else
			{
				gitDir = new File(workTree, Constants.DOT_GIT);
			}

			if (commandLine.hasOption(CONFIG_FILE.getOpt()))
			{
				gitBoxConfiguration = new GitBoxConfiguration((File) commandLine.getParsedOptionValue(CONFIG_FILE.getOpt()));
			} else
			{
				gitBoxConfiguration = new GitBoxConfiguration();
			}

			final GitRepository gitRepository = new GitRepository(gitDir, workTree);
			gitRepository.addObserver(new LogObserver());

			final JGitBox gitbox = new JGitBox(gitBoxConfiguration, gitRepository);
			gitbox.start();
			
			new Monitor().enter();

		} catch (ParseException e)
		{
			logger.error(e.getMessage());
			printUsage();
		}

	}

	private static void printUsage()
	{
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("gitbox", OPTIONS);
	}

}
