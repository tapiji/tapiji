/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 ******************************************************************************/
package auditor;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jst.jsf.context.resolver.structureddocument.IDOMContextResolver;
import org.eclipse.jst.jsf.context.resolver.structureddocument.IStructuredDocumentContextResolverFactory;
import org.eclipse.jst.jsf.context.resolver.structureddocument.ITaglibContextResolver;
import org.eclipse.jst.jsf.context.structureddocument.IStructuredDocumentContext;
import org.eclipse.jst.jsf.context.structureddocument.IStructuredDocumentContextFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class JSFResourceBundleDetector {

    public static List<IRegion> getNonELValueRegions(String elExpression) {
	List<IRegion> stringRegions = new ArrayList<IRegion>();
	int pos = -1;

	do {
	    int start = pos + 1;
	    int end = elExpression.indexOf("#{", start);
	    end = end >= 0 ? end : elExpression.length();

	    if (elExpression.substring(start, end).trim().length() > 0) {
		IRegion region = new Region(start, end - start);
		stringRegions.add(region);
	    }

	    if (elExpression.substring(end).startsWith("#{"))
		pos = elExpression.indexOf("}", end);
	    else
		pos = end;
	} while (pos >= 0 && pos < elExpression.length());

	return stringRegions;
    }

    public static String getBundleVariableName(String elExpression) {
	String bundleVarName = null;
	String[] delimitors = new String[] { ".", "[" };

	int startPos = elExpression.indexOf(delimitors[0]);

	for (String del : delimitors) {
	    if ((startPos > elExpression.indexOf(del) && elExpression
		    .indexOf(del) >= 0)
		    || (startPos == -1 && elExpression.indexOf(del) >= 0))
		startPos = elExpression.indexOf(del);
	}

	if (startPos >= 0)
	    bundleVarName = elExpression.substring(0, startPos);

	return bundleVarName;
    }

    public static String getResourceKey(String elExpression) {
	String key = null;

	if (elExpression.indexOf("[") == -1
		|| (elExpression.indexOf(".") < elExpression.indexOf("[") && elExpression
			.indexOf(".") >= 0)) {
	    // Separation by dot
	    key = elExpression.substring(elExpression.indexOf(".") + 1);
	} else {
	    // Separation by '[' and ']'
	    if (elExpression.indexOf("\"") >= 0
		    || elExpression.indexOf("'") >= 0) {
		int startPos = elExpression.indexOf("\"") >= 0 ? elExpression
			.indexOf("\"") : elExpression.indexOf("'");
		int endPos = elExpression.indexOf("\"", startPos + 1) >= 0 ? elExpression
			.indexOf("\"", startPos + 1) : elExpression.indexOf(
			"'", startPos + 1);
		if (startPos < endPos) {
		    key = elExpression.substring(startPos + 1, endPos);
		}
	    }
	}

	return key;
    }

    public static String resolveResourceBundleRefIdentifier(IDocument document,
	    int startPos) {
	String result = null;

	final IStructuredDocumentContext context = IStructuredDocumentContextFactory.INSTANCE
		.getContext(document, startPos);
	final IDOMContextResolver domResolver = IStructuredDocumentContextResolverFactory.INSTANCE
		.getDOMContextResolver(context);

	if (domResolver != null) {
	    final Node curNode = domResolver.getNode();

	    // node must be an XML attribute
	    if (curNode instanceof Attr) {
		final Attr attr = (Attr) curNode;
		if (attr.getNodeName().toLowerCase().equals("basename")) {
		    final Element owner = attr.getOwnerElement();
		    if (isBundleElement(owner, context))
			result = attr.getValue();
		}
	    } else if (curNode instanceof Element) {
		final Element elem = (Element) curNode;
		if (isBundleElement(elem, context))
		    result = elem.getAttribute("basename");
	    }
	}

	return result;
    }

    public static String resolveResourceBundleId(IDocument document,
	    String varName) {
	String content = document.get();
	String bundleId = "";
	int offset = 0;

	while (content.indexOf("\"" + varName + "\"", offset + 1) >= 0
		|| content.indexOf("'" + varName + "'", offset + 1) >= 0) {
	    offset = content.indexOf("\"" + varName + "\"") >= 0 ? content
		    .indexOf("\"" + varName + "\"") : content.indexOf("'"
		    + varName + "'");

	    final IStructuredDocumentContext context = IStructuredDocumentContextFactory.INSTANCE
		    .getContext(document, offset);
	    final IDOMContextResolver domResolver = IStructuredDocumentContextResolverFactory.INSTANCE
		    .getDOMContextResolver(context);

	    if (domResolver != null) {
		final Node curNode = domResolver.getNode();

		// node must be an XML attribute
		Attr attr = null;
		if (curNode instanceof Attr) {
		    attr = (Attr) curNode;
		    if (!attr.getNodeName().toLowerCase().equals("var"))
			continue;
		}

		// Retrieve parent node
		Element parentElement = (Element) attr.getOwnerElement();

		if (!isBundleElement(parentElement, context))
		    continue;

		bundleId = parentElement.getAttribute("basename");
		break;
	    }
	}

	return bundleId;
    }

    public static boolean isBundleElement(Element element,
	    IStructuredDocumentContext context) {
	String bName = element.getTagName().substring(
		element.getTagName().indexOf(":") + 1);

	if (bName.equals("loadBundle")) {
	    final ITaglibContextResolver tlResolver = IStructuredDocumentContextResolverFactory.INSTANCE
		    .getTaglibContextResolver(context);
	    if (tlResolver.getTagURIForNodeName(element).trim()
		    .equalsIgnoreCase("http://java.sun.com/jsf/core")) {
		return true;
	    }
	}

	return false;
    }

    public static boolean isJSFElement(Element element,
	    IStructuredDocumentContext context) {
	try {
	    final ITaglibContextResolver tlResolver = IStructuredDocumentContextResolverFactory.INSTANCE
		    .getTaglibContextResolver(context);
	    if (tlResolver.getTagURIForNodeName(element).trim()
		    .equalsIgnoreCase("http://java.sun.com/jsf/core")
		    || tlResolver.getTagURIForNodeName(element).trim()
			    .equalsIgnoreCase("http://java.sun.com/jsf/html")) {
		return true;
	    }
	} catch (Exception e) {
	}
	return false;
    }

    public static IRegion getBasenameRegion(IDocument document, int startPos) {
	IRegion result = null;

	final IStructuredDocumentContext context = IStructuredDocumentContextFactory.INSTANCE
		.getContext(document, startPos);
	final IDOMContextResolver domResolver = IStructuredDocumentContextResolverFactory.INSTANCE
		.getDOMContextResolver(context);

	if (domResolver != null) {
	    final Node curNode = domResolver.getNode();

	    // node must be an XML attribute
	    if (curNode instanceof Element) {
		final Element elem = (Element) curNode;
		if (isBundleElement(elem, context)) {
		    Attr na = elem.getAttributeNode("basename");

		    if (na != null) {
			int attrStart = document.get().indexOf("basename",
				startPos);
			result = new Region(document.get().indexOf(
				na.getValue(), attrStart), na.getValue()
				.length());
		    }
		}
	    }
	}

	return result;
    }

    public static IRegion getElementAttrValueRegion(IDocument document,
	    String attribute, int startPos) {
	IRegion result = null;

	final IStructuredDocumentContext context = IStructuredDocumentContextFactory.INSTANCE
		.getContext(document, startPos);
	final IDOMContextResolver domResolver = IStructuredDocumentContextResolverFactory.INSTANCE
		.getDOMContextResolver(context);

	if (domResolver != null) {
	    Node curNode = domResolver.getNode();
	    if (curNode instanceof Attr)
		curNode = ((Attr) curNode).getOwnerElement();

	    // node must be an XML attribute
	    if (curNode instanceof Element) {
		final Element elem = (Element) curNode;
		Attr na = elem.getAttributeNode(attribute);

		if (na != null && isJSFElement(elem, context)) {
		    int attrStart = document.get().indexOf(attribute, startPos);
		    result = new Region(document.get().indexOf(
			    na.getValue().trim(), attrStart), na.getValue()
			    .trim().length());
		}
	    }
	}

	return result;
    }

    public static IRegion resolveResourceBundleRefPos(IDocument document,
	    String varName) {
	String content = document.get();
	IRegion region = null;
	int offset = 0;

	while (content.indexOf("\"" + varName + "\"", offset + 1) >= 0
		|| content.indexOf("'" + varName + "'", offset + 1) >= 0) {
	    offset = content.indexOf("\"" + varName + "\"") >= 0 ? content
		    .indexOf("\"" + varName + "\"") : content.indexOf("'"
		    + varName + "'");

	    final IStructuredDocumentContext context = IStructuredDocumentContextFactory.INSTANCE
		    .getContext(document, offset);
	    final IDOMContextResolver domResolver = IStructuredDocumentContextResolverFactory.INSTANCE
		    .getDOMContextResolver(context);

	    if (domResolver != null) {
		final Node curNode = domResolver.getNode();

		// node must be an XML attribute
		Attr attr = null;
		if (curNode instanceof Attr) {
		    attr = (Attr) curNode;
		    if (!attr.getNodeName().toLowerCase().equals("var"))
			continue;
		}

		// Retrieve parent node
		Element parentElement = (Element) attr.getOwnerElement();

		if (!isBundleElement(parentElement, context))
		    continue;

		String bundleId = parentElement.getAttribute("basename");

		if (bundleId != null && bundleId.trim().length() > 0) {

		    while (region == null) {
			int basename = content.indexOf("basename", offset);
			int value = content.indexOf(bundleId,
				content.indexOf("=", basename));
			if (value > basename) {
			    region = new Region(value, bundleId.length());
			}
		    }

		}
		break;
	    }
	}

	return region;
    }

    public static String resolveResourceBundleVariable(IDocument document,
	    String selectedResourceBundle) {
	String content = document.get();
	String variableName = null;
	int offset = 0;

	while (content
		.indexOf("\"" + selectedResourceBundle + "\"", offset + 1) >= 0
		|| content.indexOf("'" + selectedResourceBundle + "'",
			offset + 1) >= 0) {
	    offset = content.indexOf("\"" + selectedResourceBundle + "\"") >= 0 ? content
		    .indexOf("\"" + selectedResourceBundle + "\"") : content
		    .indexOf("'" + selectedResourceBundle + "'");

	    final IStructuredDocumentContext context = IStructuredDocumentContextFactory.INSTANCE
		    .getContext(document, offset);
	    final IDOMContextResolver domResolver = IStructuredDocumentContextResolverFactory.INSTANCE
		    .getDOMContextResolver(context);

	    if (domResolver != null) {
		final Node curNode = domResolver.getNode();

		// node must be an XML attribute
		Attr attr = null;
		if (curNode instanceof Attr) {
		    attr = (Attr) curNode;
		    if (!attr.getNodeName().toLowerCase().equals("basename"))
			continue;
		}

		// Retrieve parent node
		Element parentElement = (Element) attr.getOwnerElement();

		if (!isBundleElement(parentElement, context))
		    continue;

		variableName = parentElement.getAttribute("var");
		break;
	    }
	}

	return variableName;
    }

    public static String resolveNewVariableName(IDocument document,
	    String selectedResourceBundle) {
	String variableName = "";
	int i = 0;
	variableName = selectedResourceBundle.replace(".", "");
	while (resolveResourceBundleId(document, variableName).trim().length() > 0) {
	    variableName = selectedResourceBundle + (i++);
	}
	return variableName;
    }

    public static void createResourceBundleRef(IDocument document,
	    String selectedResourceBundle, String variableName) {
	String content = document.get();
	int headInsertPos = -1;
	int offset = 0;

	while (content.indexOf("head", offset + 1) >= 0) {
	    offset = content.indexOf("head", offset + 1);

	    final IStructuredDocumentContext context = IStructuredDocumentContextFactory.INSTANCE
		    .getContext(document, offset);
	    final IDOMContextResolver domResolver = IStructuredDocumentContextResolverFactory.INSTANCE
		    .getDOMContextResolver(context);

	    if (domResolver != null) {
		final Node curNode = domResolver.getNode();

		if (!(curNode instanceof Element)) {
		    continue;
		}

		// Retrieve parent node
		Element parentElement = (Element) curNode;

		if (parentElement.getNodeName().equalsIgnoreCase("head")) {
		    do {
			headInsertPos = content.indexOf("head", offset + 5);

			final IStructuredDocumentContext contextHeadClose = IStructuredDocumentContextFactory.INSTANCE
				.getContext(document, offset);
			final IDOMContextResolver domResolverHeadClose = IStructuredDocumentContextResolverFactory.INSTANCE
				.getDOMContextResolver(contextHeadClose);
			if (domResolverHeadClose.getNode() instanceof Element
				&& domResolverHeadClose.getNode().getNodeName()
					.equalsIgnoreCase("head")) {
			    headInsertPos = content.substring(0, headInsertPos)
				    .lastIndexOf("<");
			    break;
			}
		    } while (headInsertPos >= 0);

		    if (headInsertPos < 0) {
			headInsertPos = content.indexOf(">", offset) + 1;
		    }

		    break;
		}
	    }
	}

	// resolve the taglib
	try {
	    int taglibPos = content.lastIndexOf("taglib");
	    if (taglibPos > 0) {
		final IStructuredDocumentContext taglibContext = IStructuredDocumentContextFactory.INSTANCE
			.getContext(document, taglibPos);
		taglibPos = content.indexOf("%>", taglibPos);
		if (taglibPos > 0) {
		    String decl = createLoadBundleDeclaration(document,
			    taglibContext, variableName, selectedResourceBundle);
		    if (headInsertPos > taglibPos)
			document.replace(headInsertPos, 0, decl);
		    else
			document.replace(taglibPos, 0, decl);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    private static String createLoadBundleDeclaration(IDocument document,
	    IStructuredDocumentContext context, String variableName,
	    String selectedResourceBundle) {
	String bundleDecl = "";

	// Retrieve jsf core namespace prefix
	final ITaglibContextResolver tlResolver = IStructuredDocumentContextResolverFactory.INSTANCE
		.getTaglibContextResolver(context);
	String bundlePrefix = tlResolver
		.getTagPrefixForURI("http://java.sun.com/jsf/core");

	MessageFormat tlFormatter = new MessageFormat(
		"<{0}:loadBundle var=\"{1}\" basename=\"{2}\" />\r\n");
	bundleDecl = tlFormatter.format(new String[] { bundlePrefix,
		variableName, selectedResourceBundle });

	return bundleDecl;
    }
}
