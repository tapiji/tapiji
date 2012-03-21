package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.ui.SharedASTProvider;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.text.edits.TextEdit;
import org.eclipselabs.tapiji.tools.core.Logger;

import auditor.MethodParameterDescriptor;
import auditor.model.SLLocation;

public class ASTutils {
	
	private static MethodParameterDescriptor rbDefinition;
	private static MethodParameterDescriptor rbAccessor;
	
	public static MethodParameterDescriptor getRBDefinitionDesc () {
		if (rbDefinition == null) {
			// Init descriptor for Resource-Bundle-Definition
			List<String> definition = new ArrayList<String>();
			definition.add("getBundle");
			rbDefinition = new MethodParameterDescriptor(definition,
					"java.util.ResourceBundle", true, 0);
		}
		
		return rbDefinition;
	}
	
	public static MethodParameterDescriptor getRBAccessorDesc () {
		if (rbAccessor == null) {
			// Init descriptor for Resource-Bundle-Accessors
			List<String> accessors = new ArrayList<String>();
			accessors.add("getString");
			accessors.add("getStringArray");
			rbAccessor = new MethodParameterDescriptor(accessors,
					"java.util.ResourceBundle", true, 0);
		}
		
		return rbAccessor;
	}
	
	public static String insertExistingBundleRef (IDocument document,
												IResource resource,
												int offset,
												int length,
												String resourceBundleId,
												String key,
												Locale locale) {
		String reference = "";
		String newName = null;

		IJavaElement je = JavaCore.create(resource, JavaCore.create(resource.getProject()));

		// get the type of the currently loaded resource
		ITypeRoot typeRoot = ((ICompilationUnit) je);

		if (typeRoot == null)
			return null;

		// get a reference to the shared AST of the loaded CompilationUnit
		CompilationUnit cu = SharedASTProvider.getAST(typeRoot,
		// do not wait for AST creation
				SharedASTProvider.WAIT_YES, null);
		
		String variableName = ASTutils.resolveRBReferenceVar(document, resource, offset, resourceBundleId, cu);
		if (variableName == null)
			newName = ASTutils.getNonExistingRBRefName(resourceBundleId, document, cu);
		
		try {
			reference = ASTutils.createResourceReference(
					resourceBundleId, 
					key, 
					locale, 
					resource, 
					offset, 
					variableName == null ? newName : variableName, 
					document,
					cu);
			
			document.replace(offset, length, reference);
			
			// create non-internationalisation-comment
			createReplaceNonInternationalisationComment(cu, document, offset);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		// TODO retrieve cu in the same way as in createResourceReference 
		//      the current version does not parse method bodies
		
		if (variableName == null){
			ASTutils.createResourceBundleReference(resource, offset, document, resourceBundleId, locale, true, newName, cu);
//			createReplaceNonInternationalisationComment(cu, document, pos);
		}
		return reference;
	}
	
	public static String insertNewBundleRef (IDocument document,
										   IResource resource,
										   int startPos,
										   int endPos,
										   String resourceBundleId,
										   String key) {
		String newName = null;
		String reference = "";
		
		IJavaElement je = JavaCore.create(resource, JavaCore.create(resource.getProject()));

		// get the type of the currently loaded resource
		ITypeRoot typeRoot = ((ICompilationUnit) je);

		if (typeRoot == null)
			return null;

		// get a reference to the shared AST of the loaded CompilationUnit
		CompilationUnit cu = SharedASTProvider.getAST(typeRoot,
		// do not wait for AST creation
				SharedASTProvider.WAIT_YES, null);
		
		String variableName = ASTutils.resolveRBReferenceVar(document, resource, startPos, resourceBundleId, cu);
		if (variableName == null)
			newName = ASTutils.getNonExistingRBRefName(resourceBundleId, document, cu);
		
		try {
			reference = ASTutils.createResourceReference(
					resourceBundleId, 
					key, 
					null, 
					resource, 
					startPos, 
					variableName == null ? newName : variableName, 
					document,
					cu);
			
			if (startPos > 0 && document.get().charAt(startPos-1) == '\"') {
				startPos --;
				endPos ++;
			}
			
			if ((startPos + endPos) < document.getLength() && document.get().charAt(startPos + endPos) == '\"')
				endPos ++;
			
			if ((startPos + endPos) < document.getLength() && document.get().charAt(startPos + endPos-1) == ';')
				endPos --;
			
			document.replace(startPos, endPos, reference);
			
			// create non-internationalisation-comment
			createReplaceNonInternationalisationComment(cu, document, startPos);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		if (variableName == null) {
			// refresh reference to the shared AST of the loaded CompilationUnit
			cu = SharedASTProvider.getAST(typeRoot,
			// do not wait for AST creation
					SharedASTProvider.WAIT_YES, null);
			
			ASTutils.createResourceBundleReference(resource, startPos, document, resourceBundleId, null, true, newName, cu);
//			createReplaceNonInternationalisationComment(cu, document, pos);
		}
		
		
		return reference;
	}
	
	public static String resolveRBReferenceVar (IDocument document, IResource resource, int pos, final String bundleId, CompilationUnit cu) {
		String bundleVar;
		
		PositionalTypeFinder typeFinder = new PositionalTypeFinder(pos);
		cu.accept(typeFinder);
		AnonymousClassDeclaration atd = typeFinder.getEnclosingAnonymType();
		TypeDeclaration td = typeFinder.getEnclosingType();
		MethodDeclaration meth = typeFinder.getEnclosingMethod();
		
		if (atd == null) {
			BundleDeclarationFinder bdf = new BundleDeclarationFinder(bundleId, td,
					meth != null && (meth.getModifiers() & Modifier.STATIC) == Modifier.STATIC);
			td.accept(bdf);
			
			bundleVar = bdf.getVariableName();
		} else {
			BundleDeclarationFinder bdf = new BundleDeclarationFinder(bundleId, atd,
					meth != null && (meth.getModifiers() & Modifier.STATIC) == Modifier.STATIC);
			atd.accept(bdf);
			
			bundleVar = bdf.getVariableName();
		}
		
		// Check also method body
		if (meth != null) {
			try {
				InMethodBundleDeclFinder imbdf = new InMethodBundleDeclFinder(bundleId, 
						pos);
				typeFinder.getEnclosingMethod().accept (imbdf);
				bundleVar = imbdf.getVariableName() != null ? imbdf.getVariableName() : bundleVar;
			} catch (Exception e) {
				// ignore
			}
		}
		
		return bundleVar;
	}
	
	public static String getNonExistingRBRefName (String bundleId, IDocument document, CompilationUnit cu) {
		String referenceName = null;
		int i = 0;
		
		while (referenceName == null) {
			String actRef = bundleId.substring(bundleId.lastIndexOf(".") + 1) + "Ref" + (i == 0 ? "" : i);
			actRef = actRef.toLowerCase();
			
			VariableFinder vf = new VariableFinder(actRef);
			cu.accept(vf);
			
			if (!vf.isVariableFound()) {
				referenceName = actRef;
				break;
			}
			
			i++;
		}
		
		return referenceName;
	}
	
	@Deprecated
	public static String resolveResourceBundle(MethodInvocation methodInvocation,
			MethodParameterDescriptor rbDefinition,
			Map<IVariableBinding, VariableDeclarationFragment> variableBindingManagers) {
		String bundleName = null;

		if (methodInvocation.getExpression() instanceof SimpleName) {
			SimpleName vName = (SimpleName) methodInvocation.getExpression();
			IVariableBinding vBinding = (IVariableBinding) vName
					.resolveBinding();
			VariableDeclarationFragment dec = variableBindingManagers
					.get(vBinding);

			if (dec.getInitializer() instanceof MethodInvocation) {
				MethodInvocation init = (MethodInvocation) dec.getInitializer();

				// Check declaring class
				boolean isValidClass = false;
				ITypeBinding type = init.resolveMethodBinding()
						.getDeclaringClass();
				while (type != null) {
					if (type.getQualifiedName().equals(
							rbDefinition.getDeclaringClass())) {
						isValidClass = true;
						break;
					} else {
						if (rbDefinition.isConsiderSuperclass())
							type = type.getSuperclass();
						else
							type = null;
					}

				}
				if (!isValidClass)
					return null;

				boolean isValidMethod = false;
				for (String mn : rbDefinition.getMethodName()) {
					if (init.getName().getFullyQualifiedName().equals(mn)) {
						isValidMethod = true;
						break;
					}
				}
				if (!isValidMethod)
					return null;

				// retrieve bundlename
				if (init.arguments().size() < rbDefinition.getPosition() + 1)
					return null;

				bundleName = ((StringLiteral) init.arguments().get(
						rbDefinition.getPosition())).getLiteralValue();
			}
		}

		return bundleName;
	}
	
	public static SLLocation resolveResourceBundleLocation (MethodInvocation methodInvocation,
			MethodParameterDescriptor rbDefinition,
			Map<IVariableBinding, VariableDeclarationFragment> variableBindingManagers) {
		SLLocation bundleDesc = null;

		if (methodInvocation.getExpression() instanceof SimpleName) {
			SimpleName vName = (SimpleName) methodInvocation.getExpression();
			IVariableBinding vBinding = (IVariableBinding) vName
					.resolveBinding();
			VariableDeclarationFragment dec = variableBindingManagers
					.get(vBinding);

			if (dec.getInitializer() instanceof MethodInvocation) {
				MethodInvocation init = (MethodInvocation) dec.getInitializer();

				// Check declaring class
				boolean isValidClass = false;
				ITypeBinding type = init.resolveMethodBinding()
						.getDeclaringClass();
				while (type != null) {
					if (type.getQualifiedName().equals(
							rbDefinition.getDeclaringClass())) {
						isValidClass = true;
						break;
					} else {
						if (rbDefinition.isConsiderSuperclass())
							type = type.getSuperclass();
						else
							type = null;
					}

				}
				if (!isValidClass)
					return null;

				boolean isValidMethod = false;
				for (String mn : rbDefinition.getMethodName()) {
					if (init.getName().getFullyQualifiedName().equals(mn)) {
						isValidMethod = true;
						break;
					}
				}
				if (!isValidMethod)
					return null;

				// retrieve bundlename
				if (init.arguments().size() < rbDefinition.getPosition() + 1)
					return null;

				StringLiteral bundleLiteral = ((StringLiteral) init.arguments().get(
						rbDefinition.getPosition()));
				bundleDesc = new SLLocation (null, bundleLiteral.getStartPosition(), bundleLiteral.getLength() + bundleLiteral.getStartPosition(), bundleLiteral.getLiteralValue());
			}
		}

		return bundleDesc;
	}
	
	private static boolean isMatchingMethodDescriptor (
			MethodInvocation methodInvocation, 
			MethodParameterDescriptor desc) {
		boolean result = false;
		
		if (methodInvocation.resolveMethodBinding() == null)
			return false;
		
		String methodName = methodInvocation.resolveMethodBinding().getName();

		// Check declaring class
		ITypeBinding type = methodInvocation.resolveMethodBinding()
				.getDeclaringClass();
		while (type != null) {
			if (type.getQualifiedName().equals(desc.getDeclaringClass())) {
				result = true;
				break;
			} else {
				if (desc.isConsiderSuperclass())
					type = type.getSuperclass();
				else
					type = null;
			}

		}

		if (!result)
			return false;

		result = !result;

		// Check method name
		for (String method : desc.getMethodName()) {
			if (method.equals(methodName)) {
				result = true;
				break;
			}
		}
		
		return result;
	}
	
	public static boolean isMatchingMethodParamDesc(
			MethodInvocation methodInvocation, String literal,
			MethodParameterDescriptor desc) {
		boolean result = isMatchingMethodDescriptor (methodInvocation, desc);

		if (!result)
			return false;
		else
			result = false;
		
		if (methodInvocation.arguments().size() > desc.getPosition()) {
			if (methodInvocation.arguments().get(desc.getPosition()) instanceof StringLiteral) {
				StringLiteral sl = (StringLiteral) methodInvocation.arguments().get(desc.getPosition());
				if (sl.getLiteralValue().trim().toLowerCase().equals(literal.toLowerCase()))
					result = true;
			}	
		}
		
		return result;
	}
	
	public static boolean isMatchingMethodParamDesc(
			MethodInvocation methodInvocation, StringLiteral literal,
			MethodParameterDescriptor desc) {
		int keyParameter = desc.getPosition();
		boolean result = isMatchingMethodDescriptor(methodInvocation, desc);
		
		if (!result)
			return false;

		// Check position within method call
		StructuralPropertyDescriptor spd = literal.getLocationInParent();
		if (spd.isChildListProperty()) {
			List<ASTNode> arguments = (List<ASTNode>) methodInvocation
					.getStructuralProperty(spd);
			result = (arguments.size() > keyParameter && arguments
					.get(keyParameter) == literal);
		}

		return result;
	}
	
	public static ICompilationUnit createCompilationUnit (IResource resource) {
		// Instantiate a new AST parser
		ASTParser parser = ASTParser.newParser (AST.JLS3);
		parser.setResolveBindings(true);
		
		ICompilationUnit cu = 
			JavaCore.createCompilationUnitFrom(resource.getProject().getFile(resource.getRawLocation()));
		
		return cu;
	}
	
	public static CompilationUnit createCompilationUnit (IDocument document) {
		// Instantiate a new AST parser
		ASTParser parser = ASTParser.newParser (AST.JLS3);
		parser.setResolveBindings(true);
		
		parser.setSource(document.get().toCharArray());
		return (CompilationUnit) parser.createAST(null);
	}
	
	public static void createImport (IDocument doc, 
			IResource resource, 
			CompilationUnit cu, 
			AST ast, 
			ASTRewrite rewriter, 
			String qualifiedClassName)
		throws CoreException, BadLocationException {
		
		ImportFinder impFinder = new ImportFinder (qualifiedClassName);
		
		cu.accept(impFinder);
		
		if (!impFinder.isImportFound()) {
//			ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
//			IPath path = resource.getFullPath();
//			
//			bufferManager.connect(path, LocationKind.IFILE, null);
//			ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(doc);
			
			// TODO create new import
			ImportDeclaration id = ast.newImportDeclaration();
			id.setName(ast.newName(qualifiedClassName.split("\\.")));
			id.setStatic(false);
			
			ListRewrite lrw = rewriter.getListRewrite(cu, cu.IMPORTS_PROPERTY);
			lrw.insertFirst(id, null);
			
//			TextEdit te = rewriter.rewriteAST(doc, null);
//			te.apply(doc);
//			
//			if (textFileBuffer != null)
//				textFileBuffer.commit(null, false);
//			else
//				FileUtils.saveTextFile(resource.getProject().getFile(resource.getProjectRelativePath()), 
//						doc.get());
//			bufferManager.disconnect(path, LocationKind.IFILE, null);
		}
		
	}
	
	// TODO export initializer specification into a methodinvocationdefinition
	public static void createResourceBundleReference (
			IResource resource, 
			int typePos,
			IDocument doc, 
			String bundleId, 
			Locale locale, 
			boolean globalReference,
			String variableName,
			CompilationUnit cu) {

		try {
		
			if (globalReference) {
				
				// retrieve compilation unit from document
				PositionalTypeFinder typeFinder = new PositionalTypeFinder( typePos );
				cu.accept(typeFinder);
				ASTNode node = typeFinder.getEnclosingType();
				ASTNode anonymNode = typeFinder.getEnclosingAnonymType();
				if (anonymNode != null)
					node = anonymNode;
				
				MethodDeclaration meth = typeFinder.getEnclosingMethod();
				AST ast = node.getAST();
				
				VariableDeclarationFragment vdf = ast.newVariableDeclarationFragment();
				vdf.setName(ast.newSimpleName(variableName));
		
				// set initializer
				vdf.setInitializer(createResourceBundleGetter(ast, bundleId, locale));
				
				FieldDeclaration fd = ast.newFieldDeclaration(vdf);
				fd.setType(ast.newSimpleType(ast.newName(new String [] {"ResourceBundle"} )));
		
				if (meth != null && (meth.getModifiers() & Modifier.STATIC) == Modifier.STATIC)
					fd.modifiers().addAll(ast.newModifiers(Modifier.STATIC));
				
				
				// rewrite AST
				ASTRewrite rewriter = ASTRewrite.create(ast);
				ListRewrite lrw = rewriter.getListRewrite(node, 
						node instanceof TypeDeclaration ? TypeDeclaration.BODY_DECLARATIONS_PROPERTY :
							                              AnonymousClassDeclaration.BODY_DECLARATIONS_PROPERTY);
				lrw.insertAt(fd, /* findIndexOfLastField(node.bodyDeclarations())+1 */
						     0, null);
				
				// create import if required
				createImport(doc, resource, cu, ast, rewriter, getRBDefinitionDesc().getDeclaringClass());
				
				TextEdit te = rewriter.rewriteAST(doc, null);
				te.apply(doc);
			} else {
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static int findIndexOfLastField(List bodyDeclarations) {
        for (int i= bodyDeclarations.size() - 1; i >= 0; i--) {
            BodyDeclaration each= (BodyDeclaration)bodyDeclarations.get(i);
            if (each instanceof FieldDeclaration)
                return i;
        }
        return -1;
    }
	
	protected static MethodInvocation createResourceBundleGetter (AST ast, String bundleId, Locale locale) {
		MethodInvocation mi = ast.newMethodInvocation();
		
		mi.setName(ast.newSimpleName("getBundle"));
		mi.setExpression(ast.newName(new String[] { "ResourceBundle" }));
		
		// Add bundle argument
		StringLiteral sl = ast.newStringLiteral();
		sl.setLiteralValue(bundleId);
		mi.arguments().add(sl);
		
		// TODO Add Locale argument
				
		return mi;
	}
	
	public static ASTNode getEnclosingType (CompilationUnit cu, int pos) {
		PositionalTypeFinder typeFinder = new PositionalTypeFinder(pos);
		cu.accept(typeFinder);
		return (typeFinder.getEnclosingAnonymType() != null) ? typeFinder.getEnclosingAnonymType() : typeFinder.getEnclosingType();
	}
	
	public static ASTNode getEnclosingType (ASTNode cu, int pos) {
		PositionalTypeFinder typeFinder = new PositionalTypeFinder(pos);
		cu.accept(typeFinder);
		return (typeFinder.getEnclosingAnonymType() != null) ? typeFinder.getEnclosingAnonymType() : typeFinder.getEnclosingType();
	}
	
	protected static MethodInvocation referenceResource (AST ast, String accessorName, String key, Locale locale) {
		MethodParameterDescriptor accessorDesc = getRBAccessorDesc();
		MethodInvocation mi = ast.newMethodInvocation();
		
		mi.setName(ast.newSimpleName(accessorDesc.getMethodName().get(0)));

		// Declare expression
		StringLiteral sl = ast.newStringLiteral();
		sl.setLiteralValue(key);
		
		// TODO define locale expression
		if (mi.arguments().size() == accessorDesc.getPosition())
			mi.arguments().add(sl);
		
		SimpleName name = ast.newSimpleName(accessorName); 
		mi.setExpression(name);
		
		return mi;
	}
	
	public static String createResourceReference (String bundleId, 
			String key, 
			Locale locale,
			IResource resource, 
			int typePos,
			String accessorName,
			IDocument doc,
			CompilationUnit cu) {

		PositionalTypeFinder typeFinder = new PositionalTypeFinder(typePos);
		cu.accept(typeFinder);
		AnonymousClassDeclaration atd = typeFinder.getEnclosingAnonymType();
		TypeDeclaration td = typeFinder.getEnclosingType();
		
		// retrieve compilation unit from document
		ASTNode node = atd == null ? td : atd;
		AST ast = node.getAST();
		
		ExpressionStatement expressionStatement = ast  
				.newExpressionStatement(referenceResource(ast, accessorName, key, locale)); 
			
		String exp = expressionStatement.toString();
		
		// remove semicolon and line break at the end of this expression statement
		if (exp.endsWith(";\n"))
			exp = exp.substring(0, exp.length()-2);
		
		return exp;
	}
	
	private static int findNonInternationalisationPosition(CompilationUnit cu, IDocument doc, int offset){
		LinePreStringsFinder lsfinder = null;
		try {
			lsfinder = new LinePreStringsFinder(offset, doc);
			cu.accept(lsfinder);
		} catch (BadLocationException e) {
		}
		if (lsfinder == null) return 1;
		
		List<StringLiteral> strings = lsfinder.getStrings();
		
		return strings.size()+1;
	}
	
	private static void createReplaceNonInternationalisationComment(CompilationUnit cu, IDocument doc, int position) {
		int i = findNonInternationalisationPosition(cu, doc, position);
				
		IRegion reg;
		try {
			reg = doc.getLineInformationOfOffset(position);
			doc.replace(reg.getOffset()+reg.getLength(), 0, " //$NON-NLS-"+i+"$");
		} catch (BadLocationException e1) {
		}
	}
	
	private static void createASTNonInternationalisationComment(CompilationUnit cu, IDocument doc, ASTNode parent, ASTNode fd, ASTRewrite rewriter, ListRewrite lrw) {	
		int i = 1;
		
//		ListRewrite lrw2 = rewriter.getListRewrite(node, Block.STATEMENTS_PROPERTY);
		ASTNode placeHolder= rewriter.createStringPlaceholder("//$NON-NLS-"+i+"$", ASTNode.LINE_COMMENT);
		lrw.insertAfter(placeHolder, fd, null);
	}

	public static boolean existsNonInternationalisationComment(StringLiteral literal) throws BadLocationException {
		CompilationUnit cu = (CompilationUnit) literal.getRoot();
		ICompilationUnit icu = (ICompilationUnit) cu.getJavaElement();
		
		IDocument doc = null;
		try {
			doc = new Document(icu.getSource());
		} catch (JavaModelException e) {
			Logger.logError(e);
		}		
				
		int stringLine = doc.getLineOfOffset(literal.getStartPosition());
		List<Comment> comments = cu.getCommentList();		
		
		for (Comment comment : comments) {
			if (! (comment instanceof LineComment))
				continue;
			
			LineComment lineComment = (LineComment) comment;
			
			int startPos = lineComment.getStartPosition();
			int commentLine = doc.getLineOfOffset(startPos);
			int length = lineComment.getLength();
			
			if (stringLine != commentLine || comment.getStartPosition() < literal.getStartPosition())
				continue;
						
			String commentVal = doc.get(startPos, length);
			
			// remove first "//" of LineComment
			commentVal = commentVal.substring(2).toLowerCase();
			
			// split line comments, necessary if more NON-NLS comments exist in one line, eg.: $NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3
			String[] commentVals = commentVal.split("//");
			
			for (String commentStr : commentVals) {
				commentStr = commentStr.trim();
				
				// if comment match format: "$non-nls$" then ignore whole line
				if (commentStr.matches("^\\$non-nls\\$$")) {
					return true;
				
				// if comment match format: "$non-nls-{number}$" then only ignore string which is on given position
				} else if (commentStr.matches("^\\$non-nls-\\d+\\$$")) {
					int iString = findNonInternationalisationPosition(cu, doc, literal.getStartPosition());
					int iComment = new Integer(commentStr.substring(9,10));
					if (iString == iComment)
						return true;
				}
			}
		}		
		return false;
	}
	
	static class PositionalTypeFinder extends ASTVisitor {
		
		private int position;
		private TypeDeclaration enclosingType;
		private AnonymousClassDeclaration enclosingAnonymType;
		private MethodDeclaration enclosingMethod;
		
		public PositionalTypeFinder (int pos) {
			position = pos;
		}
		
		public TypeDeclaration getEnclosingType() {
			return enclosingType;
		}
		
		public AnonymousClassDeclaration getEnclosingAnonymType () {
			return enclosingAnonymType;
		}
		
		public MethodDeclaration getEnclosingMethod () {
			return enclosingMethod;
		}
		
		public boolean visit (MethodDeclaration node) {
			if (position >= node.getStartPosition() &&
					position <= (node.getStartPosition() + node.getLength())) {
					enclosingMethod = node;
					return true;
				} else
					return false;	
		}
		
		public boolean visit (TypeDeclaration node) {
			if (position >= node.getStartPosition() &&
				position <= (node.getStartPosition() + node.getLength())) {
				enclosingType = node;
				return true;
			} else
				return false;	
		}
		
		public boolean visit (AnonymousClassDeclaration node) {
			if (position >= node.getStartPosition() &&
				position <= (node.getStartPosition() + node.getLength())) {
				enclosingAnonymType = node;
				return true;
			} else
				return false;	
		}
	}
	
	static class ImportFinder extends ASTVisitor {
		
		String qName;
		boolean importFound = false;
		
		public ImportFinder (String qName) {
			this.qName = qName;
		}
		
		public boolean isImportFound () {
			return importFound;
		}
		
		public boolean visit (ImportDeclaration id) {
			if (id.getName().getFullyQualifiedName().equals(
					qName
			))
				importFound = true;
			
			return true;
		}
	}
	
	static class VariableFinder extends ASTVisitor {
		
		boolean found = false;
		String variableName;
		
		public boolean isVariableFound () {
			return found;
		}
		
		public VariableFinder (String variableName) {
			this.variableName = variableName;
		}
		
		public boolean visit (VariableDeclarationFragment vdf) {
			if (vdf.getName().getFullyQualifiedName().equals(variableName)) {
				found = true;
				return false;
			}
			
			return true;
		}
	}
	
	static class InMethodBundleDeclFinder extends ASTVisitor {
		String varName;
		String bundleId;
		int pos;
		
		public InMethodBundleDeclFinder (String bundleId, int pos) {
			this.bundleId = bundleId;
			this.pos = pos;
		}
		
		public String getVariableName () {
			return varName;
		}
		
		public boolean visit (VariableDeclarationFragment fdvd) {
			if (fdvd.getStartPosition() > pos)
				return false;

//			boolean bStatic = (fdvd.resolveBinding().getModifiers() & Modifier.STATIC) == Modifier.STATIC;
//			if (!bStatic && isStatic)
//				return true;
			
			String tmpVarName = fdvd.getName().getFullyQualifiedName();
			
			if (fdvd.getInitializer() instanceof MethodInvocation) {
				MethodInvocation fdi = (MethodInvocation) fdvd.getInitializer();
				if (isMatchingMethodParamDesc(
						fdi, bundleId, getRBDefinitionDesc()))
					varName = tmpVarName;
			}
			return true;
		}
	}
	
	static class BundleDeclarationFinder extends ASTVisitor {
		
		String varName;
		String bundleId;
		ASTNode typeDef;
		boolean isStatic;
		
		public BundleDeclarationFinder (String bundleId, ASTNode td, boolean isStatic) {
			this.bundleId = bundleId;
			this.typeDef = td;
			this.isStatic = isStatic;
		}

		public String getVariableName () {
			return varName;
		}
		
		public boolean visit (MethodDeclaration md) {
			return true;
		}
		
		public boolean visit (FieldDeclaration fd) {
			if (getEnclosingType(typeDef, fd.getStartPosition()) != typeDef)
				return false;
			
			boolean bStatic = (fd.getModifiers() & Modifier.STATIC) == Modifier.STATIC;
			if (!bStatic && isStatic)
				return true;
			
			if (fd.getType() instanceof SimpleType) {
				SimpleType fdType = (SimpleType) fd.getType();
				String typeName = fdType.getName().getFullyQualifiedName();
				String referenceName = getRBDefinitionDesc().getDeclaringClass();
				if (typeName.equals(referenceName) ||
					(referenceName.lastIndexOf(".") >= 0 && typeName.equals(referenceName.substring(referenceName.lastIndexOf(".")+1)))) {
					// Check VariableDeclarationFragment
					if (fd.fragments().size() == 1) {
						if (fd.fragments().get(0) instanceof VariableDeclarationFragment) {
							VariableDeclarationFragment fdvd = (VariableDeclarationFragment) fd.fragments().get(0);
							String tmpVarName = fdvd.getName().getFullyQualifiedName();
							
							if (fdvd.getInitializer() instanceof MethodInvocation) {
								MethodInvocation fdi = (MethodInvocation) fdvd.getInitializer();
								if (isMatchingMethodParamDesc(
										fdi, bundleId, getRBDefinitionDesc()))
									varName = tmpVarName;
							}
						}
					}
				}
			}
			return false;
		}
	
	}
	
	static class LinePreStringsFinder extends ASTVisitor{
		private int position;
		private int line;
		private List<StringLiteral> strings;
		private IDocument document;
		
		public LinePreStringsFinder(int position, IDocument document) throws BadLocationException{
			this.document=document;
			this.position = position;
			line =  document.getLineOfOffset(position);
			strings = new ArrayList<StringLiteral>();
		}
		
		public List<StringLiteral> getStrings(){
			return strings;
		}
		
		@Override
		public boolean visit (StringLiteral node){
			try{
				if (line == document.getLineOfOffset(node.getStartPosition()) && node.getStartPosition() < position){
					strings.add(node);
					return true;
				} 
			}catch(BadLocationException e){
			}
			return true;
		}
	}	
}
