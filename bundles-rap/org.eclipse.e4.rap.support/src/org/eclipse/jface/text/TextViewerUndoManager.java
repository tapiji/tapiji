package org.eclipse.jface.text;

import org.eclipse.core.commands.operations.IUndoContext;

public class TextViewerUndoManager implements IUndoManager, IUndoManagerExtension {

	private int fUndoLevel;

	/**
	 * Creates a new undo manager who remembers the specified number of edit commands.
	 *
	 * @param undoLevel the length of this manager's history
	 */
	public TextViewerUndoManager(int undoLevel) {
		fUndoLevel= undoLevel;
	}
	
	@Override
	public IUndoContext getUndoContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beginCompoundChange() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endCompoundChange() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMaximalUndoLevel(int undoLevel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean undoable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean redoable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void undo() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void redo() {
		// TODO Auto-generated method stub
		
	}

}
