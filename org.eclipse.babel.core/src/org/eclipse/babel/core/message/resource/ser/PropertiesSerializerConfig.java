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
package org.eclipse.babel.core.message.resource.ser;

/**
 * Properties serialization configuration options.
 * @author Pascal Essiembre (pascal@essiembre.com)
 */
public class PropertiesSerializerConfig {

    //TODO extend Model and fire property change events
	//TODO re-design in order to closer integrate with Eclipse Preferences

    /** New Line Type: Default. */
    public static final int NEW_LINE_DEFAULT = 0;
    /** New Line Type: UNIX. */
    public static final int NEW_LINE_UNIX = 1;
    /** New Line Type: Windows. */
    public static final int NEW_LINE_WIN = 2;
    /** New Line Type: Mac. */
    public static final int NEW_LINE_MAC = 3;

    private boolean unicodeEscapeEnabled = true;
    private boolean unicodeEscapeUppercase = true;
    private int newLineStyle = NEW_LINE_DEFAULT;
    private boolean showSupportEnabled = true;
    private boolean spacesAroundEqualsEnabled = true;
    private boolean newLineNice;
    private boolean alignEqualsEnabled = true;
    
    // Key grouping options
    private int groupSepBlankLineCount = 1;
    private boolean groupKeysEnabled = true;
    private int groupLevelDeepness = 1;
    private String groupLevelSeparator = "."; //$NON-NLS-1$
    private boolean groupAlignEqualsEnabled = true;
    
    // Line wrapping options
    private boolean wrapLinesEnabled;
    private int wrapLineLength = 80;
    private boolean wrapAlignEqualsEnabled;
    private int wrapIndentLength = 8;
    
    /**
     * Constructor.
     */
    public PropertiesSerializerConfig() {
        super();
    }

    /**
     * Default true.
     * @return Returns the unicodeEscapeEnabled.
     */
    public boolean isUnicodeEscapeEnabled() {
        return unicodeEscapeEnabled;
    }
    /**
     * @param unicodeEscapeEnabled The unicodeEscapeEnabled to set.
     */
    public void setUnicodeEscapeEnabled(boolean unicodeEscapeEnabled) {
        this.unicodeEscapeEnabled = unicodeEscapeEnabled;
    }

    /**
     * Default to "NEW_LINE_DEFAULT".
     * @return Returns the newLineStyle.
     */
    public int getNewLineStyle() {
        return newLineStyle;
    }
    /**
     * @param newLineStyle The newLineStyle to set.
     */
    public void setNewLineStyle(int newLineStyle) {
        this.newLineStyle = newLineStyle;
    }

    /**
     * Default is 1.
     * @return Returns the groupSepBlankLineCount.
     */
    public int getGroupSepBlankLineCount() {
        return groupSepBlankLineCount;
    }
    /**
     * @param groupSepBlankLineCount The groupSepBlankLineCount to set.
     */
    public void setGroupSepBlankLineCount(int groupSepBlankLineCount) {
        this.groupSepBlankLineCount = groupSepBlankLineCount;
    }

    /**
     * Defaults to true.
     * @return Returns the showSupportEnabled.
     */
    public boolean isShowSupportEnabled() {
        return showSupportEnabled;
    }
    /**
     * @param showSupportEnabled The showSupportEnabled to set.
     */
    public void setShowSupportEnabled(boolean showSupportEnabled) {
        this.showSupportEnabled = showSupportEnabled;
    }

    /**
     * Defaults to true.
     * @return Returns the groupKeysEnabled.
     */
    public boolean isGroupKeysEnabled() {
        return groupKeysEnabled;
    }
    /**
     * @param groupKeysEnabled The groupKeysEnabled to set.
     */
    public void setGroupKeysEnabled(boolean groupKeysEnabled) {
        this.groupKeysEnabled = groupKeysEnabled;
    }

    /**
     * Defaults to true.
     * @return Returns the unicodeEscapeUppercase.
     */
    public boolean isUnicodeEscapeUppercase() {
        return unicodeEscapeUppercase;
    }
    /**
     * @param unicodeEscapeUppercase The unicodeEscapeUppercase to set.
     */
    public void setUnicodeEscapeUppercase(boolean unicodeEscapeUppercase) {
        this.unicodeEscapeUppercase = unicodeEscapeUppercase;
    }

    /**
     * Defaults to 80.
     * @return Returns the wrapLineLength.
     */
    public int getWrapLineLength() {
        return wrapLineLength;
    }

    /**
     * @param wrapLineLength The wrapLineLength to set.
     */
    public void setWrapLineLength(int wrapLineLength) {
        this.wrapLineLength = wrapLineLength;
    }

    /**
     * @return Returns the wrapLinesEnabled.
     */
    public boolean isWrapLinesEnabled() {
        return wrapLinesEnabled;
    }
    /**
     * @param wrapLinesEnabled The wrapLinesEnabled to set.
     */
    public void setWrapLinesEnabled(boolean wrapLinesEnabled) {
        this.wrapLinesEnabled = wrapLinesEnabled;
    }

    /**
     * @return Returns the wrapAlignEqualsEnabled.
     */
    public boolean isWrapAlignEqualsEnabled() {
        return wrapAlignEqualsEnabled;
    }
    /**
     * @param wrapAlignEqualsEnabled The wrapAlignEqualsEnabled to set.
     */
    public void setWrapAlignEqualsEnabled(boolean wrapAlignsEquals) {
        this.wrapAlignEqualsEnabled = wrapAlignsEquals;
    }

    /**
     * Defaults to 8.
     * @return Returns the wrapIndentLength.
     */
    public int getWrapIndentLength() {
        return wrapIndentLength;
    }
    /**
     * @param wrapIndentLength The wrapIndentLength to set.
     */
    public void setWrapIndentLength(int wrapIndentLength) {
        this.wrapIndentLength = wrapIndentLength;
    }

    /**
     * Defaults to true.
     * @return Returns the spacesAroundEqualsEnabled.
     */
    public boolean isSpacesAroundEqualsEnabled() {
        return spacesAroundEqualsEnabled;
    }
    /**
     * @param spacesAroundEqualsEnabled The spacesAroundEqualsEnabled to set.
     */
    public void setSpacesAroundEqualsEnabled(
           boolean spacesAroundEqualsEnabled) {
        this.spacesAroundEqualsEnabled = spacesAroundEqualsEnabled;
    }

    /**
     * @return Returns the newLineNice.
     */
    public boolean isNewLineNice() {
        return newLineNice;
    }
    /**
     * @param newLineNice The newLineNice to set.
     */
    public void setNewLineNice(boolean newLineNice) {
        this.newLineNice = newLineNice;
    }

    /**
     * @return Returns the groupLevelDeepness.
     */
    public int getGroupLevelDeepness() {
        return groupLevelDeepness;
    }
    /**
     * @param groupLevelDeepness The groupLevelDeepness to set.
     */
    public void setGroupLevelDeepness(int groupLevelDeepness) {
        this.groupLevelDeepness = groupLevelDeepness;
    }

    /**
     * @return Returns the groupLevelSeparator.
     */
    public String getGroupLevelSeparator() {
        return groupLevelSeparator;
    }
    /**
     * @param groupLevelSeparator The groupLevelSeparator to set.
     */
    public void setGroupLevelSeparator(String groupLevelSeparator) {
        this.groupLevelSeparator = groupLevelSeparator;
    }

    /**
     * @return Returns the alignEqualsEnabled.
     */
    public boolean isAlignEqualsEnabled() {
        return alignEqualsEnabled;
    }
    /**
     * @param alignEqualsEnabled The alignEqualsEnabled to set.
     */
    public void setAlignEqualsEnabled(boolean alignEqualsEnabled) {
        this.alignEqualsEnabled = alignEqualsEnabled;
    }

    /**
     * Defaults to true.
     * @return Returns the groupAlignEqualsEnabled.
     */
    public boolean isGroupAlignEqualsEnabled() {
        return groupAlignEqualsEnabled;
    }
    /**
     * @param groupAlignEqualsEnabled The groupAlignEqualsEnabled to set.
     */
    public void setGroupAlignEqualsEnabled(boolean groupAlignEqualsEnabled) {
        this.groupAlignEqualsEnabled = groupAlignEqualsEnabled;
    }
}
