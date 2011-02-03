package at.ac.tuwien.inso.eclipse.i18n.model.exception;

public class ResourceBundleException extends Exception {

	private static final long serialVersionUID = -2039182473628481126L;

	public ResourceBundleException (String msg) {
		super (msg);
	}
	
	public ResourceBundleException () {
		super();
	}
	
}
