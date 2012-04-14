package com.mmounirou.gitbox.observers;

import java.io.File;

import javax.annotation.Nonnull;

public interface GitRepositoryObserver
{

	void onFileAdded(@Nonnull File file);

	void onErrorDuringFileAdd(@Nonnull File file, @Nonnull Exception e);

	void onFileUpdated(@Nonnull File file);

	void onErrorDuringFileUpdate(@Nonnull File file, @Nonnull Exception e);

	void onFileDeleted(@Nonnull File file);

	void onErrorDuringFileDelete(@Nonnull File file, @Nonnull Exception e);

	void onPull();

	void onErrorDuringPull(@Nonnull Exception exception);

	void onPush();

	void onErrorDuringPush(@Nonnull Exception exception);

}
