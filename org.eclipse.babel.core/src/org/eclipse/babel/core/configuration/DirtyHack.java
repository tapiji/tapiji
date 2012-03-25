package org.eclipse.babel.core.configuration;

public final class DirtyHack {

	private static boolean fireEnabled = true; // no property-fire calls in in AbstractMessageModel
	
	private static boolean editorModificationEnabled = true; // no setText in EclipsePropertiesEditorResource
												// set this, if you serialize!

	public static boolean isFireEnabled() {
		return fireEnabled;
	}

	public static void setFireEnabled(boolean fireEnabled) {
		DirtyHack.fireEnabled = fireEnabled;
	}

	public static boolean isEditorModificationEnabled() {
		return editorModificationEnabled;
	}

	public static void setEditorModificationEnabled(
			boolean editorModificationEnabled) {
		DirtyHack.editorModificationEnabled = editorModificationEnabled;
	}
	
}
