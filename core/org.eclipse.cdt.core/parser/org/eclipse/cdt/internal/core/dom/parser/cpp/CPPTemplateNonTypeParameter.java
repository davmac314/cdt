/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrew Niefer (IBM Corporation) - initial API and implementation
 *     Markus Schorn (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.internal.core.dom.parser.cpp;

import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.IValue;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameterPackType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateArgument;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateNonTypeParameter;
import org.eclipse.cdt.internal.core.dom.parser.Value;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor;

/**
 * Binding for a non-type template parameter.
 */
public class CPPTemplateNonTypeParameter extends CPPTemplateParameter implements
		ICPPTemplateNonTypeParameter {

	private IType type = null;
	
	public CPPTemplateNonTypeParameter(IASTName name) {
		super(name);
	}

	public IASTExpression getDefault() {
		IASTName[] nds = getDeclarations();
		if (nds == null || nds.length == 0)
		    return null;
		
		for (IASTName name : nds) {
			if (name != null) {
				IASTNode parent = name.getParent();
				assert parent instanceof IASTDeclarator;
				if (parent instanceof IASTDeclarator) {
					IASTDeclarator dtor = (IASTDeclarator) parent;
					IASTInitializer initializer = dtor.getInitializer();
					if (initializer instanceof IASTEqualsInitializer) {
						IASTInitializerClause clause= ((IASTEqualsInitializer) initializer).getInitializerClause();
						if (clause instanceof IASTExpression)
							return (IASTExpression) clause;
						if (clause instanceof IASTInitializerList) {
							// mstodo handle braced init list
						}
					}
				}
			}
		}
		return null;
	}
	
	public ICPPTemplateArgument getDefaultValue() {
		IASTExpression d= getDefault();
		if (d == null)
			return null;
		
		IValue val= Value.create(d, Value.MAX_RECURSION_DEPTH);
		IType t= getType();
		return new CPPTemplateArgument(val, t);
	}

	public IType getType() {
		if (type == null) {
			IASTNode parent= getPrimaryDeclaration().getParent();
			while (parent != null) {
				if (parent instanceof ICPPASTParameterDeclaration) {
					type= CPPVisitor.createParameterType((ICPPASTParameterDeclaration) parent, true);
					break;
				}
				parent= parent.getParent();
			}
		}
		return type;
	}

	public boolean isParameterPack() {
		return getType() instanceof ICPPParameterPackType;
	}

	public boolean isStatic() throws DOMException {
		return false;
	}
	public boolean isExtern() throws DOMException {
		return false;
	}
	public boolean isAuto() throws DOMException {
		return false;
	}
	public boolean isRegister() throws DOMException {
		return false;
	}
	public IValue getInitialValue() {
		return null;
	}
	public boolean isExternC() {
		return false;
	}
	public boolean isMutable() {
		return false;
	}
}
