package at.ac.tuwien.inso.eclipse.i18n.ui.widgets.event;

public class ResourceSelectionEvent {

	private String selectionSummary;
	private String selectedKey;
	
	public ResourceSelectionEvent (String selectedKey, String selectionSummary) {
		this.setSelectionSummary(selectionSummary);
		this.setSelectedKey(selectedKey);
	}

	public void setSelectedKey (String key) {
		selectedKey = key;
	}
	
	public void setSelectionSummary(String selectionSummary) {
		this.selectionSummary = selectionSummary;
	}

	public String getSelectionSummary() {
		return selectionSummary;
	}

	public String getSelectedKey() {
		return selectedKey;
	}
	
	
	
}
