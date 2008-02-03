/*******************************************************************************
 * Copyright (c) 2007 Pascal Essiembre.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Pascal Essiembre - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.editor.bundle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.babel.core.message.MessagesBundle;
import org.eclipse.babel.core.util.BabelUtils;
import org.eclipse.babel.editor.util.UIUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorSite;


/**
 * MessagesBundle group strategies for dealing with Eclipse "NL"
 * directory structure within a plugin.
 * <p>
 * This class also falls back to the usual locales as suffixes
 * by calling the methods defined in DefaultBundleGroupStrategy.
 * It enables us to re-use directly this class to support loading resources located
 * inside a fragment. In other words:
 * this class is extended by {@link NLFragmentBundleGroupStrategy}.
 * </p>
 * <p>
 * Note it is unclear how 
 * <p>
 * 
 * 
 * @author Pascal Essiembre
 * @author Hugues Malphettes
 */
public class NLPluginBundleGroupStrategy extends DefaultBundleGroupStrategy {
	
	private static Set ISO_LANG_CODES = new HashSet();
	private static Set ISO_COUNTRY_CODES = new HashSet();
	static {
		String[] isos = Locale.getISOLanguages();
		for (int i = 0; i < isos.length; i++) {
			ISO_LANG_CODES.add(isos[i]);
		}
		String[] isoc = Locale.getISOCountries();
		for (int i = 0; i < isoc.length; i++) {
			ISO_COUNTRY_CODES.add(isoc[i]);
		}
	}

	
	protected IFolder nlFolder;
	protected String basePathInsideNL;
	
    /**
     * @param nlFolder when null, this strategy behaves just like
     * DefaultBundleGroupStrategy. Otherwise it is a localized file
     * using the "nl" folder. Most complete example found so far:
     * http://dev.eclipse.org/mhonarc/lists/babel-dev/msg00111.html
     * Although it applies to properties files too:
     * See figure 1 of:
     * http://www.eclipse.org/articles/Article-Speak-The-Local-Language/article.html
     */
    public NLPluginBundleGroupStrategy(IEditorSite site, IFile file,
    		IFolder nlFolder) {
        super(site, file);
        this.nlFolder = nlFolder;
    }

    /**
     * @see org.eclipse.babel.core.bundle.IBundleGroupStrategy#loadBundles()
     */
    public MessagesBundle[] loadMessagesBundles() {
        if (nlFolder == null) {
        	return super.loadMessagesBundles();
        }
        //get the nl directory.
        //navigate the entire directory from there
        //and look for the file with the same file names.
        if (nlFolder == null) {
        	return super.loadMessagesBundles();
        }
        final String name = getOpenedFile().getName();
        final Collection bundles = new ArrayList();
        IResourceVisitor visitor = new IResourceVisitor() {
			public boolean visit(IResource resource) throws CoreException {
				if (resource.getType() == IResource.FILE
						&& resource.getName().equals(name)
						&& !getOpenedFile().equals(resource)) {
	                Locale locale = extractLocale((IFile)resource, false);
	                if (locale != null && UIUtils.isDisplayed(locale)) {
	                	bundles.add(createBundle(locale, resource));
	                }
				}
				return true;
			}
        };
        try {
        	Locale locale = extractLocale(getOpenedFile(), true);
            if (UIUtils.isDisplayed(locale)) {
            	bundles.add(createBundle(locale, getOpenedFile()));
            }
        	nlFolder.accept(visitor);
		} catch (CoreException e) {
			e.printStackTrace();
		}
        return (MessagesBundle[]) bundles.toArray(EMPTY_BUNDLES);
    }

    /**
     * Tries to parse a locale directly from the file.
     * Support the locale as a string suffix and the locale as part of a path
     * inside an nl folder.
     * @param file
     * @return The parsed locale or null if no locale could be parsed.
     * If the locale is the root locale UIBableUtils.ROOT_LOCALE is returned.
     */
    private Locale extractLocale(IFile file, boolean docomputeBasePath) {
    	IFolder nl = MessagesBundleGroupFactory.getNLFolder(file);
    	String path = file.getFullPath().removeFileExtension().toString();
    	if (nl == null) {
	    	int ind = path.indexOf('_');
	    	int maxInd = path.length()-1;
	    	while (ind != -1 && ind < maxInd) {
	    		String possibleLocale = path.substring(ind+1);
	    		Locale res = BabelUtils.parseLocale(possibleLocale);
	    		if (res != null) {
	    			return res;
	    		}
	    		ind = path.indexOf('_', ind+1);
	    	}
	    	return null;
    	}
    	//the locale is not in the suffix.
    	//let's look into the nl folder:
    	int ind = path.lastIndexOf("/nl/");
    	//so the remaining String is a composition of the base path of
    	//the default properties and the path that reflects the locale.
    	//for example:
    	//if the default file is /theproject/icons/img.gif
    	//then the french localized file is /theproject/nl/FR/icons/img.gif
    	//so we need to separate fr from icons/img.gif to locate the base file.
    	//unfortunately we need to look into the values of the tokens
    	//to guess whether they are part of the path leading to the default file
    	//or part of the path that reflects the locale.
    	//we simply look whether 'icons' exist.
    	
    	// in other words: using folders is risky and users could
    	//crash eclipse using locales that conflict with pathes to resources.
    	
    	//so we must verify that the first 2 or 3 tokens after nl are valid ISO codes.
    	//the variant is the most problematic issue
    	//as it is not standardized.
    	
    	//we rely on finding the base properties
    	//to decide whether 'icons' is a variant or a folder.
    	
    	
    	
    	if (ind != -1) {
    		ind = ind + "/nl/".length();
    		int lastFolder = path.lastIndexOf('/');
    		if (lastFolder == ind) {
    			return UIUtils.ROOT_LOCALE;
    		}
    		path = path.substring(ind, lastFolder);
    		StringTokenizer tokens = new StringTokenizer(path, "/", false);
    		switch (tokens.countTokens()) {
    		case 0:
    			return null;
    		case 1:
            	String lang = tokens.nextToken();
                if (!ISO_LANG_CODES.contains(lang)) {
            		return null;
            	}
                if (docomputeBasePath) {
                	basePathInsideNL = "";
                	return new Locale(lang);
                } else if (basePathInsideNL.equals("")) {
                	return new Locale(lang);
                } else {
                	return null;
                }
            case 2:
            	lang = tokens.nextToken();
            	if (!ISO_LANG_CODES.contains(lang)) {
            		return null;
            	}
            	String country = tokens.nextToken();
            	if (!ISO_COUNTRY_CODES.contains(country)) {
            		//in this case, this might be the beginning
            		//of the base path.
	        		if (isExistingFirstFolderForDefaultLocale(country)) {
	                    if (docomputeBasePath) {
	                    	basePathInsideNL = country;
	                    	return new Locale(lang);
	                    } else if (basePathInsideNL.equals(country)) {
	                    	return new Locale(lang);
	                    } else {
	                    	return null;
	                    }
	        		}
            	}
                if (docomputeBasePath) {
                	basePathInsideNL = "";
                	return new Locale(lang, country);
                } else if (basePathInsideNL.equals(country)) {
                	return new Locale(lang, country);
                } else {
                	return null;
                }
            default:
            	lang = tokens.nextToken();
	        	if (!ISO_LANG_CODES.contains(lang)) {
	        		return null;
	        	}
	        	country = tokens.nextToken();
	        	if (!ISO_COUNTRY_CODES.contains(country)) {
	        		if (isExistingFirstFolderForDefaultLocale(country)) {
	        			StringBuffer b = new StringBuffer(country);
                    	while (tokens.hasMoreTokens()) {
                    		b.append("/" + tokens.nextToken());
                    	}
	                    if (docomputeBasePath) {
	                    	basePathInsideNL = b.toString();
	                    	return new Locale(lang);
	                    } else if (basePathInsideNL.equals(b.toString())) {
	                    	return new Locale(lang);
	                    } else {
	                    	return null;
	                    }
	        		}
            	}
	        	String variant = tokens.nextToken();
        		if (isExistingFirstFolderForDefaultLocale(variant)) {
        		    StringBuffer b = new StringBuffer(variant);
                	while (tokens.hasMoreTokens()) {
                		b.append("/" + tokens.nextToken());
                	}
                    if (docomputeBasePath) {
                    	basePathInsideNL = b.toString();
                    	return new Locale(lang, country);
                    } else if (basePathInsideNL.equals(b.toString())) {
                    	return new Locale(lang);
                    } else {
                    	return null;
                    }
        		}
        		StringBuffer b = new StringBuffer();
            	while (tokens.hasMoreTokens()) {
            		b.append("/" + tokens.nextToken());
            	}
                if (docomputeBasePath) {
                	basePathInsideNL = b.toString();
                	return new Locale(lang, country, variant);
                } else if (basePathInsideNL.equals(b.toString())) {
                	return new Locale(lang);
                } else {
                	return null;
                }
    		}
    	}
    	return UIUtils.ROOT_LOCALE;
    }
    
    /**
     * Called when using an nl structure.
     * We need to find out whether the variant is in fact a folder.
     * If we locate a folder inside the project with this name we assume it is not a variant.
     * <p>
     * This method is overridden inside the NLFragment thing as we need to check 2 projects over there:
     * the host-plugin project and the current project.
     * </p>
     * @param possibleVariant
     * @return
     */
    protected boolean isExistingFirstFolderForDefaultLocale(String folderName) {
		return getOpenedFile().getProject().getFolder(folderName).exists();
    }
    
//    protected boolean isExistingFirstTwoFoldersForDefaultLocale(String firstFolderName,
//    		String secondFolderName) {
//    	return getOpenedFile().getProject().getFolder(
//				new Path(firstFolderName + "/" + secondFolderName)).exists();
//    }
}
