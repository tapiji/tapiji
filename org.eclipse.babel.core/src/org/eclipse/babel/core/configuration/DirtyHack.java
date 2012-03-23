package org.eclipse.babel.core.configuration;

public final class DirtyHack {

	private static boolean fireEnabled; // no property-fire calls in in AbstractMessageModel
	
	private static boolean editorModificationEnabled; // no setText in EclipsePropertiesEditorResource
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
