package org.eclipse.e4.tapiji.git.ui.filediff;

import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.ui.filediff.FileDiffContract.View;

@Creatable
@Singleton
public class FileDiffPresenter implements FileDiffContract.Presenter {

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
	public void setView(FileDiffContract.View view) {
		this.view = view;
	}


}
