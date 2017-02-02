package org.eclipse.e4.tapiji.git.ui.staged;

import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.ui.staged.StagedContract.View;

@Creatable
@Singleton
public class StagedPresenter implements StagedContract.Presenter {

	private View view;

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setView(StagedContract.View view) {
		this.view = view;
	}
}
