package org.apache.commons.vfs2.impl;

import java.util.List;

import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.events.CreateEvent;

import com.google.common.collect.Lists;

public class FilterableFileMonitor extends DefaultFileMonitor
{

	private final List<FileObject> excludedFromLocalCheck = Lists.newArrayList();
	private final List<FileObject> excludeFromInitialScanNotif = Lists.newArrayList();

	public FilterableFileMonitor(FileListener listener)
	{
		super(listener);
	}

	@Override
	public void addFile(FileObject file)
	{
		if (isExcluded(file))
		{
			return;
		} else
		{
			fireCreated(file);
			super.addFile(file);
		}

	}

	private void fireCreated(FileObject file)
	{
		if (excludeFromInitialScanNotif(file))
		{
			return;
		}
		try
		{
			new CreateEvent(file).notify(getFileListener());

		} catch (Exception e)
		{
			//Quiet
		}
	}

	/**
	 * The exclusion if not recursive
	 * @param file
	 * @return
	 */
	private boolean excludeFromInitialScanNotif(FileObject file)
	{
		FileName fileName = file.getName();
		for (FileObject excludedPath : excludeFromInitialScanNotif)
		{
			FileName name = excludedPath.getName();
			if (name.compareTo(fileName) == 0)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * The exclusion if recursive
	 * @param file
	 * @return
	 */
	private boolean isExcluded(FileObject file)
	{
		FileName fileName = file.getName();
		for (FileObject excludedPath : excludedFromLocalCheck)
		{
			FileName name = excludedPath.getName();
			if (name.compareTo(fileName) == 0 || name.isDescendent(fileName))
			{
				return true;
			}
		}
		return false;
	}

	public void exclude(FileObject folder)
	{
		excludedFromLocalCheck.add(folder);
	}

	public void excludeInInitialScanNotif(FileObject folder)
	{
		//TODO add a possibility to choose if the exclusion if recursive or not
		excludeFromInitialScanNotif.add(folder);
	}

}
