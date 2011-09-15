package org.eclipselabs.tapiji.tools.rbmanager.viewer.actions.hoverinformants;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipselabs.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipselabs.tapiji.tools.core.util.FragmentProjectUtils;
import org.eclipselabs.tapiji.tools.rbmanager.ImageUtils;
import org.eclipselabs.tapiji.tools.rbmanager.ui.hover.HoverInformant;

public class I18NProjectInformant implements HoverInformant {
	private String locales;
	private String fragments;
	private boolean show = false;
	
	private Composite infoComposite;
	private Label localeLabel;
	private Composite localeGroup;
	private Label fragmentsLabel;
	private Composite fragmentsGroup;
	
	private GridData infoData;
	private GridData showLocalesData;
	private GridData showFragmentsData;
	
	@Override
	public Composite getInfoComposite(Object data, Composite parent) {
		show = false;
		
		if (infoComposite == null){
			infoComposite = new Composite(parent, SWT.NONE);
			GridLayout layout =new GridLayout(1,false);
			layout.verticalSpacing = 1;
			layout.horizontalSpacing = 0;
			infoComposite.setLayout(layout);
			
			infoData = new GridData(SWT.LEFT, SWT.TOP, true, true);
			infoComposite.setLayoutData(infoData);
		}
		
		if (data instanceof IProject) {					
			addLocale(infoComposite, data);
			addFragments(infoComposite, data);
		} 
		
		if (show) {
			infoData.heightHint = -1;
			infoData.widthHint = -1;
		} else {
			infoData.heightHint = 0;
			infoData.widthHint = 0;
		}
		
		infoComposite.layout();
		infoComposite.pack();
		sinkColor(infoComposite);
		
		return infoComposite;
	}

	@Override
	public boolean show() {
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

	private void addLocale(Composite parent, Object data){
		if (localeGroup == null) {
			localeGroup =  new Composite(parent, SWT.NONE);
			localeLabel = new Label(localeGroup, SWT.SINGLE);
			
			showLocalesData = new GridData(SWT.LEFT, SWT.TOP, true, true);
			localeGroup.setLayoutData(showLocalesData);
		}
		
		locales = getProvidedLocales(data);
		
		if (locales.length() != 0) {
			localeLabel.setText(locales);
			localeLabel.pack();	
			show = true;
//			showLocalesData.heightHint = -1;
//			showLocalesData.widthHint=-1;
		} else {
			localeLabel.setText("No Language Provided");
			localeLabel.pack();
			show = true;
//			showLocalesData.heightHint = 0;
//			showLocalesData.widthHint = 0;
		}
		
//		localeGroup.layout();
		localeGroup.pack();
	}
	
	private void addFragments(Composite parent, Object data){
		if (fragmentsGroup == null) {
			fragmentsGroup = new Composite(parent, SWT.NONE);
			GridLayout layout = new GridLayout(1, false);
			layout.verticalSpacing = 0;
			layout.horizontalSpacing = 0;
			fragmentsGroup.setLayout(layout);

			showFragmentsData = new GridData(SWT.LEFT, SWT.TOP, true, true);
			fragmentsGroup.setLayoutData(showFragmentsData);

			Composite fragmentTitleGroup = new Composite(fragmentsGroup,
					SWT.NONE);
			layout = new GridLayout(2, false);
			layout.verticalSpacing = 0;
			layout.horizontalSpacing = 5;
			fragmentTitleGroup.setLayout(layout);

			Label fragmentImageLabel = new Label(fragmentTitleGroup, SWT.NONE);
			fragmentImageLabel.setImage(ImageUtils
					.getBaseImage(ImageUtils.FRAGMENT_PROJECT_IMAGE));
			fragmentImageLabel.pack();

			Label fragementTitleLabel = new Label(fragmentTitleGroup, SWT.NONE);
			fragementTitleLabel.setText("Project Fragments:");
			fragementTitleLabel.pack();
			fragmentsLabel = new Label(fragmentsGroup, SWT.SINGLE);
		}
		
		fragments = getFragmentProjects(data);
		
		if (fragments.length() != 0) {
			fragmentsLabel.setText(fragments);
			show = true;
			showFragmentsData.heightHint = -1;
			showFragmentsData.widthHint= -1;
			fragmentsLabel.pack();
		} else {
			showFragmentsData.heightHint = 0;
			showFragmentsData.widthHint = 0;
		}
		
		fragmentsGroup.layout();
		fragmentsGroup.pack();
	}
	
	private String getProvidedLocales(Object data) {
		if (data instanceof IProject) {
			ResourceBundleManager rbmanger = ResourceBundleManager.getManager((IProject) data);
			Set<Locale> ls = rbmanger.getProjectProvidedLocales();

			if (ls.size() > 0) {
				StringBuilder sb = new StringBuilder();
				sb.append("Provided Languages:\n");

				int i = 0;
				for (Locale l : ls) {
					if (!l.toString().equals(""))
						sb.append(l.getDisplayName());
					else
						sb.append("[Default]");

					if (++i != ls.size()) {
						sb.append(",");
						if (i % 5 == 0)
							sb.append("\n");
						else
							sb.append(" ");
					}

				}
				return sb.toString();
			}
		}
		
		
		return "";
	}

	private String getFragmentProjects(Object data) {
		if (data instanceof IProject){
			List<IProject> fragments = FragmentProjectUtils.getFragments((IProject) data);
			if (fragments.size() > 0) {
				StringBuilder sb = new StringBuilder();
				
				int i = 0;
				for (IProject f : fragments){
					sb.append(f.getName());
					if (++i != fragments.size()) sb.append("\n");
				}
				return sb.toString();
			}
		}		
		return "";
	}
}
