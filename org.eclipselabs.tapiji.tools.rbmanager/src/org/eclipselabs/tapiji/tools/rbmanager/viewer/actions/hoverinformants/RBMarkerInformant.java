package org.eclipselabs.tapiji.tools.rbmanager.viewer.actions.hoverinformants;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipselabs.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipselabs.tapiji.tools.core.util.EditorUtils;
import org.eclipselabs.tapiji.tools.core.util.FragmentProjectUtils;
import org.eclipselabs.tapiji.tools.core.util.ResourceUtils;
import org.eclipselabs.tapiji.tools.rbmanager.ImageUtils;
import org.eclipselabs.tapiji.tools.rbmanager.model.VirtualResourceBundle;
import org.eclipselabs.tapiji.tools.rbmanager.ui.hover.HoverInformant;


public class RBMarkerInformant implements HoverInformant {
	private int MAX_PROBLEMS = 20;
	private boolean show = false;
	
	private String title;
	private String problems;
	
	private Composite infoComposite;
	private Composite titleGroup;
	private Label titleLabel;
	private Composite problemGroup;
	private Label problemLabel;
	
	private GridData infoData;
	private GridData showTitleData;
	private GridData showProblemsData;
	
	
	@Override
	public Composite getInfoComposite(Object data, Composite parent) {
		show = false;
		
		if (infoComposite == null){
			infoComposite = new Composite(parent, SWT.NONE);
			GridLayout layout =new GridLayout(1,false);
			layout.verticalSpacing = 0;
			layout.horizontalSpacing = 0;
			infoComposite.setLayout(layout);
			
			infoData = new GridData(SWT.LEFT, SWT.TOP, true, true);
			infoComposite.setLayoutData(infoData);
		}
		
		if (data instanceof VirtualResourceBundle || data instanceof IResource) {			
			addTitle(infoComposite, data);
			addProblems(infoComposite, data);
		}
		
		if (show){
			infoData.heightHint=-1;
			infoData.widthHint=-1;
		} else {
			infoData.heightHint=0;
			infoData.widthHint=0;
		}
		
		infoComposite.layout();
		sinkColor(infoComposite);
		infoComposite.pack();
		
		return infoComposite;
	}

	@Override
	public boolean show(){		
		return show;
	}
	
	private void setColor(Control control){
		Display display = control.getParent().getDisplay();
		
		control.setForeground(display
				.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		control.setBackground(display
				.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
	}
	
	private void sinkColor(Composite composite){
		setColor(composite);
		
		for(Control c : composite.getChildren()){
			setColor(c);			
			if (c instanceof Composite) sinkColor((Composite) c);
		}
	}
	
	private void addTitle(Composite parent, Object data){		
		if (titleGroup == null) {
			titleGroup = new Composite(parent, SWT.NONE);
			titleLabel = new Label(titleGroup, SWT.SINGLE);
			
			showTitleData = new GridData(SWT.LEFT, SWT.TOP, true, true);
			titleGroup.setLayoutData(showTitleData);
		}
		title = getTitel(data);
		
		if (title.length() != 0) {
			titleLabel.setText(title);
			show = true;
			showTitleData.heightHint=-1;
			showTitleData.widthHint=-1;
			titleLabel.pack();
		} else {
			showTitleData.heightHint=0;
			showTitleData.widthHint=0;
		}
		
		titleGroup.layout();
		titleGroup.pack();
	}
	
	
	private void addProblems(Composite parent, Object data){
		if (problemGroup == null) {
			problemGroup = new Composite(parent, SWT.NONE);
			GridLayout layout = new GridLayout(1, false);
			layout.verticalSpacing = 0;
			layout.horizontalSpacing = 0;
			problemGroup.setLayout(layout);

			showProblemsData = new GridData(SWT.LEFT, SWT.TOP, true, true);
			problemGroup.setLayoutData(showProblemsData);
			
			Composite problemTitleGroup = new Composite(problemGroup, SWT.NONE);
			layout = new GridLayout(2, false);
			layout.verticalSpacing = 0;
			layout.horizontalSpacing = 5;
			problemTitleGroup.setLayout(layout);

			Label warningImageLabel = new Label(problemTitleGroup, SWT.NONE);
			warningImageLabel.setImage(ImageUtils
					.getBaseImage(ImageUtils.WARNING_IMAGE));
			warningImageLabel.pack();

			Label waringTitleLabel = new Label(problemTitleGroup, SWT.NONE);
			waringTitleLabel.setText("ResourceBundle-Problems:");
			waringTitleLabel.pack();

			problemLabel = new Label(problemGroup, SWT.SINGLE);
		}
		
		problems = getProblems(data);
		
		if (problems.length() != 0) {
			problemLabel.setText(problems);
			show = true;
			showProblemsData.heightHint=-1;
			showProblemsData.widthHint=-1;
			problemLabel.pack();
		} else {
			showProblemsData.heightHint=0;
			showProblemsData.widthHint=0;
		}
		
		problemGroup.layout();
		problemGroup.pack();
	}	
	
	
	private String getTitel(Object data) {
		if (data instanceof IFile){
			return ((IResource)data).getFullPath().toString();
		}
		if (data instanceof VirtualResourceBundle){
			return ((VirtualResourceBundle)data).getResourceBundleId();
		}
		
		return "";
	}
	
	private String getProblems(Object data){
		IMarker[] ms = null;
		
		if (data instanceof IResource){
			IResource res = (IResource) data;
			try {
				if (res.exists())
					ms = res.findMarkers(EditorUtils.RB_MARKER_ID, false,
						IResource.DEPTH_INFINITE);
				else ms = new IMarker[0];
			} catch (CoreException e) {
				e.printStackTrace();
			}
			if (data instanceof IContainer){
				//add problem of same folder in the fragment-project
				List<IContainer> fragmentContainer = ResourceUtils.getCorrespondingFolders((IContainer) res,
								FragmentProjectUtils.getFragments(res.getProject()));
				
				IMarker[] fragment_ms;
				for (IContainer c : fragmentContainer){
					try {
						if (c.exists()) {
							fragment_ms = c.findMarkers(EditorUtils.RB_MARKER_ID, false,
									IResource.DEPTH_INFINITE);

							ms = EditorUtils.concatMarkerArray(ms, fragment_ms);
						}
					} catch (CoreException e) {
					}
				}
			}
		}
		
		if (data instanceof VirtualResourceBundle){
			VirtualResourceBundle vRB = (VirtualResourceBundle) data;
			
			ResourceBundleManager rbmanager = vRB.getResourceBundleManager();
			IMarker[] file_ms;
			
			Collection<IResource> rBundles = rbmanager.getResourceBundles(vRB.getResourceBundleId());
			if (!rBundles.isEmpty())
				for (IResource r : rBundles){
					try {
						file_ms = r.findMarkers(EditorUtils.RB_MARKER_ID, false, IResource.DEPTH_INFINITE);
						if (ms != null) {
							ms = EditorUtils.concatMarkerArray(ms, file_ms);
						}else{
							ms = file_ms;
						}
					} catch (Exception e) {
					}
				}
		}
		
		
		StringBuilder sb = new StringBuilder();
		int count=0;
		
		if (ms != null && ms.length!=0){
			for (IMarker m : ms) {
				try {
					sb.append(m.getAttribute(IMarker.MESSAGE));
					sb.append("\n");
					count++;
					if (count == MAX_PROBLEMS && ms.length-count!=0){
						sb.append(" ... and ");
						sb.append(ms.length-count);
						sb.append(" other problems");
						break;
					}
				} catch (CoreException e) {
				}
				;
			}
			return sb.toString();
		}
		
		return "";
	}
}
