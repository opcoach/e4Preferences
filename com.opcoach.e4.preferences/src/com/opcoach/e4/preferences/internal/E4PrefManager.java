/*******************************************************************************
 * Copyright (c) 2019 OPCoach.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     OPCoach - initial API and implementation
 *******************************************************************************/
package com.opcoach.e4.preferences.internal;

import org.eclipse.jface.preference.PreferenceManager;

import com.opcoach.e4.preferences.E4PreferencesAddon;

/** This is only a specific E4 Pref Manager to be stored in the context 
 * @see E4PreferencesAddon
 */
public class E4PrefManager extends PreferenceManager
{
    // Nothing special except marking it as local for E4. 
}
