package com.mmounirou.gitbox.utils;

import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;

public class FilterableFileMonitor extends DefaultFileMonitor
{

	private final FileObject[] excluded;

	public FilterableFileMonitor(FileListener listener, FileObject... excludeds)
	{
		super(listener);
		this.excluded = excludeds;
	}

	@Override
	public void addFile(FileObject file)
	{
		if (isExcluded(file))
		{
			return;
		} else
		{
			super.addFile(file);
		}

	}

	private boolean isExcluded(FileObject file)
	{
		FileName fileName = file.getName();
		for (FileObject excludedPath : excluded)
		{
			FileName name = excludedPath.getName();
			if (name.compareTo(fileName) == 0 || name.isDescendent(fileName))
			{
				return true;
			}
		}
		return false;
	}
}
