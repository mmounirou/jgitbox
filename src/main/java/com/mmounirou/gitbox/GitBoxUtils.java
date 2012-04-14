package com.mmounirou.gitbox;

import java.io.File;

public class GitBoxUtils
{

	/**
	 * Note : that method will work only if file to relativize is child of file to relativize to
	 * @param file
	 * @param parent
	 * @return
	 */
	public static String getRelativePath(File file, File parent)
	{
		return parent.toURI().relativize(file.toURI()).getPath();
	}

}
