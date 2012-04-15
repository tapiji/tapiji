package org.eclipse.babel.tapiji.tools.java.auditor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.babel.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipse.babel.tapiji.tools.java.auditor.model.SLLocation;
import org.eclipse.babel.tapiji.tools.java.util.ASTutils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

/**
 * @author Martin
 * 
 */
public class ResourceAuditVisitor extends ASTVisitor implements
		IResourceVisitor {

	private List<SLLocation> constants;
	private List<SLLocation> brokenStrings;
	private List<SLLocation> brokenRBReferences;
	private SortedMap<Long, IRegion> rbDefReferences = new TreeMap<Long, IRegion>();
	private SortedMap<Long, IRegion> keyPositions = new TreeMap<Long, IRegion>();
	private Map<IRegion, String> bundleKeys = new HashMap<IRegion, String>();
	private Map<IRegion, String> bundleReferences = new HashMap<IRegion, String>();
	private IFile file;
	private Map<IVariableBinding, VariableDeclarationFragment> variableBindingManagers = new HashMap<IVariableBinding, VariableDeclarationFragment>();
	private String projectName;
	
	
	public ResourceAuditVisitor(IFile file, String projectName) {
		constants = new ArrayList<SLLocation>();
		brokenStrings = new ArrayList<SLLocation>();
		brokenRBReferences = new ArrayList<SLLocation>();
		this.file = file;
		this.projectName = projectName;
	}

	@Override
	public boolean visit(VariableDeclarationStatement varDeclaration) {
		for (Iterator<VariableDeclarationFragment> itFrag = varDeclaration
				.fragments().iterator(); itFrag.hasNext();) {
			VariableDeclarationFragment fragment = itFrag.next();
			parseVariableDeclarationFragment(fragment);
		}
		return true;
	}

	@Override
	public boolean visit(FieldDeclaration fieldDeclaration) {
		for (Iterator<VariableDeclarationFragment> itFrag = fieldDeclaration
				.fragments().iterator(); itFrag.hasNext();) {
			VariableDeclarationFragment fragment = itFrag.next();
			parseVariableDeclarationFragment(fragment);
		}
		return true;
	}

	protected void parseVariableDeclarationFragment(
			VariableDeclarationFragment fragment) {
		IVariableBinding vBinding = fragment.resolveBinding();
		this.variableBindingManagers.put(vBinding, fragment);
	}

	@Override
	public boolean visit(StringLiteral stringLiteral) {
		try {
			ASTNode parent = stringLiteral.getParent();
			ResourceBundleManager manager = ResourceBundleManager.getManager(projectName);

			if (parent instanceof MethodInvocation) {
				MethodInvocation methodInvocation = (MethodInvocation) parent;

				IRegion region = new Region(stringLiteral.getStartPosition(),
						stringLiteral.getLength());

				// Check if this method invokes the getString-Method on a
				// ResourceBundle Implementation
				if (ASTutils.isMatchingMethodParamDesc(methodInvocation,
						stringLiteral, ASTutils.getRBAccessorDesc())) {
					// Check if the given Resource-Bundle reference is broken
					SLLocation rbName = ASTutils.resolveResourceBundleLocation(
							methodInvocation, ASTutils.getRBDefinitionDesc(),
							variableBindingManagers);
					if (rbName == null
							|| manager.isKeyBroken(rbName.getLiteral(),
									stringLiteral.getLiteralValue())) {
						// report new problem
						SLLocation desc = new SLLocation(file,
								stringLiteral.getStartPosition(),
								stringLiteral.getStartPosition()
										+ stringLiteral.getLength(),
								stringLiteral.getLiteralValue());
						desc.setData(rbName);
						brokenStrings.add(desc);
					}

					// store position of resource-bundle access
					keyPositions.put(
							Long.valueOf(stringLiteral.getStartPosition()),
							region);
					bundleKeys.put(region, stringLiteral.getLiteralValue());
					bundleReferences.put(region, rbName.getLiteral());
					return false;
				} else if (ASTutils.isMatchingMethodParamDesc(methodInvocation,
						stringLiteral, ASTutils.getRBDefinitionDesc())) {
					rbDefReferences.put(
							Long.valueOf(stringLiteral.getStartPosition()),
							region);
					boolean referenceBroken = true;
					for (String bundle : manager.getResourceBundleIdentifiers()) {
						if (bundle.trim().equals(
								stringLiteral.getLiteralValue())) {
							referenceBroken = false;
						}
					}
					if (referenceBroken) {
						this.brokenRBReferences.add(new SLLocation(file,
								stringLiteral.getStartPosition(), stringLiteral
										.getStartPosition()
										+ stringLiteral.getLength(),
								stringLiteral.getLiteralValue()));
					}

					return false;
				}
			}

			// check if string is followed by a "$NON-NLS$" line comment
			if (ASTutils.existsNonInternationalisationComment(stringLiteral)) {
				return false;
			}

			// constant string literal found
			constants.add(new SLLocation(file,
					stringLiteral.getStartPosition(), stringLiteral
							.getStartPosition() + stringLiteral.getLength(),
					stringLiteral.getLiteralValue()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public List<SLLocation> getConstantStringLiterals() {
		return constants;
	}

	public List<SLLocation> getBrokenResourceReferences() {
		return brokenStrings;
	}

	public List<SLLocation> getBrokenRBReferences() {
		return this.brokenRBReferences;
	}

	public IRegion getKeyAt(Long position) {
		IRegion reg = null;

		Iterator<Long> keys = keyPositions.keySet().iterator();
		while (keys.hasNext()) {
			Long startPos = keys.next();
			if (startPos > position)
				break;

			IRegion region = keyPositions.get(startPos);
			if (region.getOffset() <= position
					&& (region.getOffset() + region.getLength()) >= position) {
				reg = region;
				break;
			}
		}

		return reg;
	}

	public String getKeyAt(IRegion region) {
		if (bundleKeys.containsKey(region))
			return bundleKeys.get(region);
		else
			return "";
	}

	public String getBundleReference(IRegion region) {
		return bundleReferences.get(region);
	}

	@Override
	public boolean visit(IResource resource) throws CoreException {
		// TODO Auto-generated method stub
		return false;
	}

	public Collection<String> getDefinedResourceBundles(int offset) {
		Collection<String> result = new HashSet<String>();
		for (String s : bundleReferences.values()) {
			if (s != null)
				result.add(s);
		}
		return result;
	}

	public IRegion getRBReferenceAt(Long offset) {
		IRegion reg = null;

		Iterator<Long> keys = rbDefReferences.keySet().iterator();
		while (keys.hasNext()) {
			Long startPos = keys.next();
			if (startPos > offset)
				break;

			IRegion region = rbDefReferences.get(startPos);
			if (region != null && region.getOffset() <= offset
					&& (region.getOffset() + region.getLength()) >= offset) {
				reg = region;
				break;
			}
		}

		return reg;
	}
}
