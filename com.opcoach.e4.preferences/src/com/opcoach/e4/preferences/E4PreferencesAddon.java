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

package com.opcoach.e4.preferences;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.preference.PreferenceManager;

import com.opcoach.e4.preferences.internal.E4PrefManager;
import com.opcoach.e4.preferences.internal.E4PreferenceRegistry;

/** This addon is used to ensure that current PreferenceManager in the context is updated with both : 
 * 
 *   - pure e4 preference pages
 *   - legacy E3 preference pages
 *   
 *   With this addon it will be possible to use the E4 preferences either in a pure E4 application or in a 
 *   mixed E3/E4 application without any change. 
 *   
 * @author olivier
 *
 */
public class E4PreferencesAddon {

//	/** 
//	 * Here we must ensure that PreferenceManager is initialized correctly (see comment inside). 
//	 * after this method, the PreferenceManager can contain both E3 and E4 pref page or only E4 pages.
//	 * @param ctx current application context.
//	 */
//	@PostConstruct
//	public void initializePreferenceManager(IEclipseContext ctx) {
//		
//		// First of all ... is there a PreferenceManager in the context ? 
//		//  Yes -> we are in a E3 application that initialized it from class org.eclipse.ui.internal.WorkbenchPlugin (around line 1356)
//		//       in this case we must complete this existing preference manager with addition pure E4 pref page. 
//		//  No -> we are running in a pure E4 application... let's manage ours
//		
//		PreferenceManager pm = ctx.get(PreferenceManager.class);
//		if (pm == null)
//		{
//			// we are in a pure E4 application without any initialization... Must init it using E4PreferenceRegistry
//			pm = new PreferenceManager();
//			ctx.set(PreferenceManager.class, pm);
//		}
//		
//			
//	}
//	
	/**
	 * This method is invoked when the preference manager changes in the context. 
	 * It can be possible in 2 cases : 
	 *   <ul>
	 *   
	 *   <li> E3 mixed mode with E4 pref plugins -> the org.eclipse.ui.internal.WorkbenchPlugin set it after the creation of this addon. 
	 *        In this case we must complete the E3 init with the E4 preferences extensions.  </li>
	 *   <li> pure E4 mode : in this case, the com.opcoach.e4.preferences.handlers.E4PreferencesHandler was called to initialize the Preference manager if it is not preset. In this case
	 *   nothing special to do...</li> 
	 *    </ul>
	 * @param ctx
	 * @param pm
	 */
	@Inject @Optional
	public void initializePreferenceManager(IEclipseContext ctx, PreferenceManager pm) {
		// First time it can be null because init is not finished but we register that we are interesting in
		if (pm == null || pm instanceof E4PrefManager)
			return;
		
		// Here we got a E3 preference manager, we must complete its content. 
		// If we enter here, it is because Fill this context with the additional E4 pref pages. 
		E4PreferenceRegistry registry = new E4PreferenceRegistry();
		registry.populatePrefManagerWithE4Extensions(pm, ctx);	
		
	}
		
	

}
