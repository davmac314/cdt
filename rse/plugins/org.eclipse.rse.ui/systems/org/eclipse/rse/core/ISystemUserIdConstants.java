/********************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * The following IBM employees contributed to the Remote System Explorer
 * component that contains this file: David McKnight, Kushal Munir, 
 * Michael Berger, David Dykstal, Phil Coulthard, Don Yantzi, Eric Simpson, 
 * Emily Bruner, Mazen Faraj, Adrian Storisteanu, Li Ding, and Kent Hawley.
 * 
 * Contributors:
 * {Name} (company) - description of contribution.
 ********************************************************************************/

package org.eclipse.rse.core;
/**
 * Constants for user Id management
 * @deprecated use {@link IRSEUserIdConstants} instead.
 */
public interface ISystemUserIdConstants
{
	
	public static final int USERID_LOCATION_NOTSET = 0;	
	public static final int USERID_LOCATION_SUBSYSTEM = 1;
	public static final int USERID_LOCATION_CONNECTION = 2;
	public static final int USERID_LOCATION_DEFAULT_SYSTEMTYPE = 3;
	public static final int USERID_LOCATION_DEFAULT_OVERALL= 4;			
}