package org.eclipse.e4.tapiji.git.ui.repository;

import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.ui.repository.RepositoryContract.View;

@Creatable
@Singleton
public class RepositoryPresenter implements RepositoryContract.Presenter {

	private View view;

	@Override
	public void init() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void setView(View view) {
		this.view = view;
	}
}
