package com.mmounirou.jgitbox.utils;

import java.io.File;

import javax.annotation.Nonnull;

public final class GitBoxUtils
{

	private GitBoxUtils()
	{
		throw new AssertionError();
	}

	/**
	 * Note : that method will work only if file to relativize is child of file to relativize to
	 * @param file
	 * @param parent
	 * @return
	 */
	public static String getRelativePath(@Nonnull File file, @Nonnull File parent)
	{
		return parent.toURI().relativize(file.toURI()).getPath();
	}

}
