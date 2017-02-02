package org.eclipse.e4.tapiji.git.ui.unstaged;

import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.ui.unstaged.UnstagedContract.View;

@Creatable
@Singleton
public class UnstagedPresenter implements UnstagedContract.Presenter {

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
	public void setView(UnstagedContract.View view) {
		this.view = view;
	}


}
