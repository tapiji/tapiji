package org.eclipse.jface.text.source;


/**
 * Interface for objects interested in getting informed about annotation model
 * changes. Changes are the addition or removal of annotations managed by the
 * model. Clients may implement this interface.
 *
 * In order to provided backward compatibility for clients of
 * <code>IAnnotationModelListener</code>, extension interfaces are used to
 * provide a means of evolution. The following extension interfaces exist:
 * <ul>
 * <li>{@link org.eclipse.jface.text.source.IAnnotationModelListenerExtension}
 *     since version 2.0 replacing the change notification mechanisms.</li>
 * </ul>
 *
 * @see org.eclipse.jface.text.source.IAnnotationModel
 */
public interface IAnnotationModelListener {

	/**
	 * Called if a model change occurred on the given model.<p>
	 * Replaced by {@link IAnnotationModelListenerExtension#modelChanged(AnnotationModelEvent)}.
	 *
	 * @param model the changed annotation model
	 */
	void modelChanged(IAnnotationModel model);
}
