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
package org.eclipse.babel.tapiji.tools.java.visitor;

import java.util.List;

public class MethodParameterDescriptor {

    private List<String> methodName;
    private String declaringClass;
    private boolean considerSuperclass;
    private int position;

    public MethodParameterDescriptor(List<String> methodName,
            String declaringClass, boolean considerSuperclass, int position) {
        super();
        this.setMethodName(methodName);
        this.declaringClass = declaringClass;
        this.considerSuperclass = considerSuperclass;
        this.position = position;
    }

    public String getDeclaringClass() {
        return declaringClass;
    }

    public void setDeclaringClass(String declaringClass) {
        this.declaringClass = declaringClass;
    }

    public boolean isConsiderSuperclass() {
        return considerSuperclass;
    }

    public void setConsiderSuperclass(boolean considerSuperclass) {
        this.considerSuperclass = considerSuperclass;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setMethodName(List<String> methodName) {
        this.methodName = methodName;
    }

    public List<String> getMethodName() {
        return methodName;
    }

}
