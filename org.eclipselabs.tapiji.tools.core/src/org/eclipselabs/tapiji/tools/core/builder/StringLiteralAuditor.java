package org.eclipselabs.tapiji.tools.core.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipselabs.tapiji.tools.core.Activator;
import org.eclipselabs.tapiji.tools.core.Logger;
import org.eclipselabs.tapiji.tools.core.builder.analyzer.RBAuditor;
import org.eclipselabs.tapiji.tools.core.builder.analyzer.ResourceFinder;
import org.eclipselabs.tapiji.tools.core.extensions.I18nAuditor;
import org.eclipselabs.tapiji.tools.core.extensions.I18nRBAuditor;
import org.eclipselabs.tapiji.tools.core.extensions.I18nResourceAuditor;
import org.eclipselabs.tapiji.tools.core.extensions.ILocation;
import org.eclipselabs.tapiji.tools.core.extensions.IMarkerConstants;
import org.eclipselabs.tapiji.tools.core.model.exception.NoSuchResourceAuditorException;
import org.eclipselabs.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipselabs.tapiji.tools.core.model.preferences.TapiJIPreferences;
import org.eclipselabs.tapiji.tools.core.util.EditorUtils;
import org.eclipselabs.tapiji.tools.core.util.RBFileUtils;


public class StringLiteralAuditor extends IncrementalProjectBuilder{

	public static final String BUILDER_ID = Activator.PLUGIN_ID
			+ ".I18NBuilder";

	private static I18nAuditor[] resourceAuditors;
	private static Set<String> supportedFileEndings;
	private static IPropertyChangeListener listner;
	

	static {
		List<I18nAuditor> auditors = new ArrayList<I18nAuditor>();
		supportedFileEndings = new HashSet<String>();
		
		// init default auditors
		auditors.add(new RBAuditor());

		// lookup registered auditor extensions
		IConfigurationElement[] config = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(Activator.BUILDER_EXTENSION_ID);

		try {
			for (IConfigurationElement e : config) {
				I18nAuditor a = (I18nAuditor) e.createExecutableExtension("class");
				auditors.add(a);
				supportedFileEndings.addAll(Arrays.asList(a.getFileEndings()));
			}
		} catch (CoreException ex) {
			Logger.logError(ex);
		}

		resourceAuditors = auditors.toArray(new I18nAuditor[auditors.size()]);
		
		listner = new BuilderPropertyChangeListener();
		TapiJIPreferences.addPropertyChangeListener(listner);
	}

	public StringLiteralAuditor() {
	}

	public static I18nAuditor getI18nAuditorByContext(String contextId)
			throws NoSuchResourceAuditorException {
		for (I18nAuditor auditor : resourceAuditors) {
			if (auditor.getContextId().equals(contextId))
				return auditor;
		}
		throw new NoSuchResourceAuditorException();
	}

	private Set<String> getSupportedFileExt() {
		return supportedFileEndings;
	}

	public static boolean isResourceAuditable(IResource resource,
			Set<String> supportedExtensions) {
		for (String ext : supportedExtensions) {
			if (resource.getType() == IResource.FILE && !resource.isDerived() && resource.getFileExtension() != null
					&& (resource.getFileExtension().equalsIgnoreCase(ext))) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected IProject[] build(final int kind, Map args, IProgressMonitor monitor)
			throws CoreException {

		ResourcesPlugin.getWorkspace().run(
				new IWorkspaceRunnable() {
					public void run(IProgressMonitor monitor) throws CoreException {
						if (kind == FULL_BUILD) {
							fullBuild(monitor);
						} else {
							// only perform audit if the resource delta is not empty
							IResourceDelta resDelta = getDelta(getProject());

							if (resDelta == null)
								return;

							if (resDelta.getAffectedChildren() == null)
								return;

							incrementalBuild(monitor, resDelta);
						}
					}
				}, 
				monitor);

		return null;
	}

	private void incrementalBuild(IProgressMonitor monitor,
			IResourceDelta resDelta) throws CoreException {
		try {
			// inspect resource delta
			ResourceFinder csrav = new ResourceFinder(getSupportedFileExt());
			resDelta.accept(csrav);
			auditResources(csrav.getResources(), monitor, getProject());
		} catch (CoreException e) {
			Logger.logError(e);
		}
	}

	public void buildResource(IResource resource, IProgressMonitor monitor) {
		if (isResourceAuditable(resource, getSupportedFileExt())) {
			List<IResource> resources = new ArrayList<IResource>();
			resources.add(resource);
			// TODO: create instance of progressmonitor and hand it over to
			// auditResources
			try {
				auditResources(resources, monitor, getProject());
			} catch (Exception e) {
				Logger.logError(e);
			}
		}
	}

	public void buildProject(IProgressMonitor monitor, IProject proj) {
		try {
			ResourceFinder csrav = new ResourceFinder(getSupportedFileExt());
			proj.accept(csrav);
			auditResources(csrav.getResources(), monitor, proj);
		} catch (CoreException e) {
			Logger.logError(e);
		}
	}

	private void fullBuild(IProgressMonitor monitor) {
		buildProject(monitor, getProject());
	}

	private void auditResources(List<IResource> resources,
			IProgressMonitor monitor, IProject project) {
		int work = resources.size();
		int actWork = 0;
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		monitor.beginTask("Audit resource file for Internationalization problems",
				work);

		for (IResource resource : resources) {
			monitor.subTask("'" + resource.getFullPath().toOSString() + "'");
			if (monitor.isCanceled())
				throw new OperationCanceledException();

			if (!EditorUtils.deleteAuditMarkersForResource(resource))
				continue;

			if (ResourceBundleManager.isResourceExcluded(resource))
				continue;
			
			if (!resource.exists())
				continue;
			
			for (I18nAuditor ra : resourceAuditors) {
				if (ra instanceof I18nResourceAuditor && !(TapiJIPreferences.getAuditResource()))
					continue;
				if (ra instanceof I18nRBAuditor && !(TapiJIPreferences.getAuditRb()))
					continue;
				
				try {
					if (monitor.isCanceled()) {
						monitor.done();
						break;
					}
					
					if (ra.isResourceOfType(resource)) {
						ra.audit(resource);
					}
				} catch (Exception e) {
					Logger.logError("Error during auditing '" + resource.getFullPath() + "'", e);
				}
			}

			if (monitor != null)
				monitor.worked(1);
		}
		
		for (I18nAuditor a : resourceAuditors) {
			if (a instanceof I18nResourceAuditor){
				handleI18NAuditorMarkers((I18nResourceAuditor)a);
			}
			if (a instanceof I18nRBAuditor){
				handleI18NAuditorMarkers((I18nRBAuditor)a);
				((I18nRBAuditor)a).resetProblems();
			}
		}

		monitor.done();
	}
	
	private void handleI18NAuditorMarkers(I18nResourceAuditor ra){
		try {
			for (ILocation problem : ra.getConstantStringLiterals()) {
				EditorUtils
						.reportToMarker(
								EditorUtils
										.getFormattedMessage(
												EditorUtils.MESSAGE_NON_LOCALIZED_LITERAL,
												new String[] { problem
														.getLiteral() }),
								problem,
								IMarkerConstants.CAUSE_CONSTANT_LITERAL,
								"", (ILocation) problem.getData(),
								ra.getContextId());
			}
	
			// Report all broken Resource-Bundle references
			for (ILocation brokenLiteral : ra
					.getBrokenResourceReferences()) {
				EditorUtils
						.reportToMarker(
								EditorUtils
										.getFormattedMessage(
												EditorUtils.MESSAGE_BROKEN_RESOURCE_REFERENCE,
												new String[] {
														brokenLiteral
																.getLiteral(),
														((ILocation) brokenLiteral
																.getData())
																.getLiteral() }),
								brokenLiteral,
								IMarkerConstants.CAUSE_BROKEN_REFERENCE,
								brokenLiteral.getLiteral(),
								(ILocation) brokenLiteral.getData(),
								ra.getContextId());
			}
	
			// Report all broken definitions to Resource-Bundle
			// references
			for (ILocation brokenLiteral : ra
					.getBrokenBundleReferences()) {
				EditorUtils
						.reportToMarker(
								EditorUtils
										.getFormattedMessage(
												EditorUtils.MESSAGE_BROKEN_RESOURCE_BUNDLE_REFERENCE,
												new String[] { brokenLiteral
														.getLiteral() }),
								brokenLiteral,
								IMarkerConstants.CAUSE_BROKEN_RB_REFERENCE,
								brokenLiteral.getLiteral(),
								(ILocation) brokenLiteral.getData(),
								ra.getContextId());
			}
		} catch (Exception e) {
			Logger.logError("Exception during reporting of Internationalization errors", e);
		}
	}
	
	private void handleI18NAuditorMarkers(I18nRBAuditor ra){
		try {
			//Report all unspecified keys
			if (TapiJIPreferences.getAuditMissingValue())
				for (ILocation problem : ra.getUnspecifiedKeyReferences()) {
					EditorUtils.reportToRBMarker(
							EditorUtils.getFormattedMessage(
									EditorUtils.MESSAGE_UNSPECIFIED_KEYS,
									new String[] {problem.getLiteral(), problem.getFile().getName()}),
							problem,
							IMarkerConstants.CAUSE_UNSPEZIFIED_KEY,
							problem.getLiteral(),"",
							(ILocation) problem.getData(),
							ra.getContextId());
				}
	
			//Report all same values
			if(TapiJIPreferences.getAuditSameValue()){
				Map<ILocation,ILocation> sameValues = ra.getSameValuesReferences();			
				for (ILocation problem : sameValues.keySet()) {
					EditorUtils.reportToRBMarker(
							EditorUtils.getFormattedMessage(
									EditorUtils.MESSAGE_SAME_VALUE, 
									new String[] {problem.getFile().getName(),
									sameValues.get(problem).getFile().getName(),
									problem.getLiteral()}),
							problem,
							IMarkerConstants.CAUSE_SAME_VALUE,
							problem.getLiteral(),
							sameValues.get(problem).getFile().getName(),
							(ILocation) problem.getData(),
							ra.getContextId());
				}
			}
			// Report all missing languages
			if (TapiJIPreferences.getAuditMissingLanguage())
				for (ILocation problem : ra.getMissingLanguageReferences()) {
					EditorUtils.reportToRBMarker(
							EditorUtils.getFormattedMessage(
									EditorUtils.MESSAGE_MISSING_LANGUAGE, 
									new String[] {RBFileUtils.getCorrespondingResourceBundleId(problem.getFile()),
									problem.getLiteral()}),
							problem,
							IMarkerConstants.CAUSE_MISSING_LANGUAGE,
							problem.getLiteral(),"",
							(ILocation) problem.getData(),
							ra.getContextId());
				}
		} catch (Exception e) {
			Logger.logError("Exception during reporting of Internationalization errors", e);
		}
	}
	

	
	@SuppressWarnings("unused")
	private void setProgress(IProgressMonitor monitor, int progress)
			throws InterruptedException {
		monitor.worked(progress);

		if (monitor.isCanceled())
			throw new OperationCanceledException();

		if (isInterrupted())
			throw new InterruptedException();
	}

	@Override
	protected void clean(IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		super.clean(monitor);
	}

	public static void addBuilderToProject(IProject project) {
		Logger.logInfo("Internationalization-Builder registered for '" + project.getName() + "'");
		
		// Only for open projects
		if (!project.isOpen())
			return;

		IProjectDescription description = null;
		try {
			description = project.getDescription();
		} catch (CoreException e) {
			Logger.logError(e);
			return;
		}

		// Check if the builder is already associated to the specified project
		ICommand[] commands = description.getBuildSpec();
		for (ICommand command : commands) {
			if (command.getBuilderName().equals(BUILDER_ID))
				return;
		}

		// Associate the builder with the project
		ICommand builderCmd = description.newCommand();
		builderCmd.setBuilderName(BUILDER_ID);
		List<ICommand> newCommands = new ArrayList<ICommand>();
		newCommands.addAll(Arrays.asList(commands));
		newCommands.add(builderCmd);
		description.setBuildSpec((ICommand[]) newCommands
				.toArray(new ICommand[newCommands.size()]));

		try {
			project.setDescription(description, null);
		} catch (CoreException e) {
			Logger.logError(e);
		}
	}

	public static void removeBuilderFromProject(IProject project) {
		// Only for open projects
		if (!project.isOpen())
			return;

		try {
			project.deleteMarkers(EditorUtils.MARKER_ID, false,
					IResource.DEPTH_INFINITE);
			project.deleteMarkers(EditorUtils.RB_MARKER_ID, false,
					IResource.DEPTH_INFINITE);
		} catch (CoreException e1) {
			Logger.logError(e1);
		}

		IProjectDescription description = null;
		try {
			description = project.getDescription();
		} catch (CoreException e) {
			Logger.logError(e);
			return;
		}

		// remove builder from project
		int idx = -1;
		ICommand[] commands = description.getBuildSpec();
		for (int i = 0; i < commands.length; i++) {
			if (commands[i].getBuilderName().equals(BUILDER_ID)) {
				idx = i;
				break;
			}
		}
		if (idx == -1)
			return;

		List<ICommand> newCommands = new ArrayList<ICommand>();
		newCommands.addAll(Arrays.asList(commands));
		newCommands.remove(idx);
		description.setBuildSpec((ICommand[]) newCommands
				.toArray(new ICommand[newCommands.size()]));

		try {
			project.setDescription(description, null);
		} catch (CoreException e) {
			Logger.logError(e);
		}

	}

}
