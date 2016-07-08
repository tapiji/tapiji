package org.eclipselabs.e4.tapiji.translator.model.filter;

import java.io.Serializable;
import org.eclipse.jface.text.IRegion;

public class FilterRegion implements Serializable {
    /** The region offset */
    private int fOffset;
    /** The region length */
    private int fLength;

    /**
     * Create a new region.
     *
     * @param offset the offset of the region
     * @param length the length of the region
     */
    public FilterRegion(int offset, int length) {
        fOffset= offset;
        fLength= length;
    }

    public int getLength() {
        return fLength;
    }

    public int getOffset() {
        return fOffset;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof IRegion) {
            IRegion r= (IRegion) o;
            return r.getOffset() == fOffset && r.getLength() == fLength;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (fOffset << 24) | (fLength << 16);
    }

    @Override
    public String toString() {
        return "offset: " + fOffset + ", length: " + fLength; //$NON-NLS-1$ //$NON-NLS-2$;
    }
}
