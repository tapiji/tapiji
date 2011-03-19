package validator;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ISourceValidator;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipselabs.tapiji.tools.core.extensions.IMarkerConstants;
import org.eclipselabs.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipselabs.tapiji.tools.core.util.EditorUtils;

import auditor.JSFResourceBundleDetector;
import auditor.model.SLLocation;

public class JSFInternationalizationValidator implements IValidator, ISourceValidator {

	private IDocument document;

	@Override
	public void cleanup(IReporter reporter) {
	}

	@Override
	public void validate(IValidationContext context, IReporter reporter)
			throws ValidationException {
		if (context.getURIs().length > 0) {
			IFile file = ResourcesPlugin.getWorkspace().getRoot()
					.getFile(new Path(context.getURIs()[0]));

			// full document validation
			EditorUtils.deleteAuditMarkersForResource(file.getProject()
					.findMember(file.getProjectRelativePath()));

			// validate all bundle definitions
			int pos = document.get().indexOf("loadBundle", 0);
			while (pos >= 0) {
				validateRegion(new Region(pos, 1), context, reporter);
				pos = document.get().indexOf("loadBundle", pos + 1);
			}

			// iterate all value definitions
			pos = document.get().indexOf(" value", 0);
			while (pos >= 0) {
				validateRegion(new Region(pos, 1), context, reporter);
				pos = document.get().indexOf(" value", pos + 1);
			}
		}
	}

	@Override
	public void connect(IDocument doc) {
		document = doc;
	}

	@Override
	public void disconnect(IDocument arg0) {
		document = null;
	}

	public void validateRegion(IRegion dirtyRegion, IValidationContext context,
			IReporter reporter) {
		int startPos = dirtyRegion.getOffset();
		int endPos = dirtyRegion.getOffset() + dirtyRegion.getLength();

		if (context.getURIs().length > 0) {
			IFile file = ResourcesPlugin.getWorkspace().getRoot()
					.getFile(new Path(context.getURIs()[0]));
			ResourceBundleManager manager = ResourceBundleManager
					.getManager(file.getProject());

			String bundleName = JSFResourceBundleDetector
					.resolveResourceBundleRefIdentifier(document, startPos);
			if (bundleName != null
					&& !manager.getResourceBundleIdentifiers().contains(
							bundleName)) {
				IRegion reg = JSFResourceBundleDetector.getBasenameRegion(
						document, startPos);
				String ref = document.get().substring(reg.getOffset(),
						reg.getOffset() + reg.getLength());

				EditorUtils
						.reportToMarker(
								EditorUtils
										.getFormattedMessage(
												EditorUtils.MESSAGE_BROKEN_RESOURCE_BUNDLE_REFERENCE,
												new String[] { ref }),
								new SLLocation(file, reg.getOffset(), reg
										.getOffset() + reg.getLength(), ref),
								IMarkerConstants.CAUSE_BROKEN_RB_REFERENCE,
								ref, null, "jsf");
				return;
			}

			IRegion evr = JSFResourceBundleDetector.getElementAttrValueRegion(
					document, "value", startPos);
			if (evr != null) {
				String elementValue = document.get().substring(evr.getOffset(),
						evr.getOffset() + evr.getLength());

				// check all constant string expressions
				List<IRegion> regions = JSFResourceBundleDetector
						.getNonELValueRegions(elementValue);

				for (IRegion region : regions) {
					// report constant string literals
					String constantLiteral = elementValue.substring(
							region.getOffset(),
							region.getOffset() + region.getLength());

					EditorUtils
							.reportToMarker(
									EditorUtils
											.getFormattedMessage(
													EditorUtils.MESSAGE_NON_LOCALIZED_LITERAL,
													new String[] { constantLiteral }),
									new SLLocation(file, region.getOffset()
											+ evr.getOffset(), evr.getOffset()
											+ region.getOffset()
											+ region.getLength(),
											constantLiteral),
									IMarkerConstants.CAUSE_CONSTANT_LITERAL,
									constantLiteral, null,
									"jsf");
				}

				// check el expressions
				int start = document.get().indexOf("#{", evr.getOffset());

				while (start >= 0 && start < evr.getOffset() + evr.getLength()) {
					int end = document.get().indexOf("}", start);
					end = Math.min(end, evr.getOffset() + evr.getLength());

					if ((end - start) > 6) {
						String def = document.get().substring(start + 2, end);
						String varName = JSFResourceBundleDetector
								.getBundleVariableName(def);
						String key = JSFResourceBundleDetector
								.getResourceKey(def);
						if (varName != null && key != null) {
							if (varName.length() > 0) {
								IRegion refReg = JSFResourceBundleDetector
										.resolveResourceBundleRefPos(document,
												varName);

								if (refReg == null) {
									start = document.get().indexOf("#{", end);
									continue;
								}

								int bundleStart = refReg.getOffset();
								int bundleEnd = refReg.getOffset()
										+ refReg.getLength();

								if (manager.isKeyBroken(
										document.get().substring(
												refReg.getOffset(),
												refReg.getOffset()
														+ refReg.getLength()),
										key)) {
									SLLocation subMarker = new SLLocation(file,
											bundleStart, bundleEnd, document
													.get().substring(
															bundleStart,
															bundleEnd));
									EditorUtils
											.reportToMarker(
													EditorUtils
														.getFormattedMessage(
															EditorUtils.MESSAGE_BROKEN_RESOURCE_REFERENCE,
															new String[] { key, subMarker.getLiteral() }),
													new SLLocation(file,
															start+2, end, key),
													IMarkerConstants.CAUSE_BROKEN_REFERENCE,
													key,
													subMarker,
													"jsf");
								}
							}
						}
					}

					start = document.get().indexOf("#{", end);
				}
			}
		}
	}

	@Override
	public void validate(IRegion arg0, IValidationContext arg1, IReporter arg2) {}

}
