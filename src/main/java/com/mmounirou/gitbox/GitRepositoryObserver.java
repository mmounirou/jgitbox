package com.mmounirou.gitbox;

import java.io.File;

public interface GitRepositoryObserver
{

	void onFileAdded(File file);

	void onErrorDuringFileAdd(File file, Exception e);

	void onFileUpdated(File file);

	void onErrorDuringFileUpdate(File file, Exception e);

	void onFileDeleted(File file);

	void onErrorDuringFileDelete(File file, Exception e);

}
