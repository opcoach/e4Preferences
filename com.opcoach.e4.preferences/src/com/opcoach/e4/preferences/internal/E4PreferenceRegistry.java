/*******************************************************************************
 * Copyright (c) 2014 OPCoach.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     OPCoach - initial API and implementation
 *******************************************************************************/
package com.opcoach.e4.preferences.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.contributions.IContributionFactory;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceManager;

import com.opcoach.e4.preferences.IPreferenceStoreProvider;

/**
 * This class is used to read the com.opcoach.e4.preferences.e4PreferencePages
 * extensions if any It will complete a possible existing preference manager
 * previously initialized by E3 (see E4PreferencesAddon)
 */
public class E4PreferenceRegistry
{

	private static final String PREFS_PAGE_XP = "com.opcoach.e4.preferences.e4PreferencePages"; // $NON-NLS-1$
	private static final String PREF_STORE_PROVIDER = "com.opcoach.e4.preferences.e4PreferenceStoreProvider"; // $NON-NLS-1$
	private static final String ELMT_PAGE = "page"; // $NON-NLS-1$
	private static final String ATTR_ID = "id"; // $NON-NLS-1$
	private static final String ATTR_CATEGORY = "category"; // $NON-NLS-1$
	private static final String ATTR_CLASS = "class"; // $NON-NLS-1$
	private static final String ATTR_NAME = "name"; // $NON-NLS-1$

	protected static final String ATTR_PLUGIN_ID = "pluginId"; // $NON-NLS-1$
	protected static final String ATTR_ID_IN_WBCONTEXT = "idInWorkbenchContext"; // $NON-NLS-1$

	// A map of (pluginId, { IPreferenceStoreProvider, or key in wbcontext }
	private Map<String, Object> psProviders;
	// The key to find the psProviders map in the context (if not empty)
	static final String KEY_PREF_STORE_PROVIDERS = "com.opcoach.e4.preferences.e4PreferenceStoreProviders"; // $NON-NLS-1$

	private Logger logger;

	public void populatePrefManagerWithE4Extensions(PreferenceManager pm, IEclipseContext context)
	{

		// Remember of the unbounded nodes to order parent pages.
		// Map<category, list of children> (all nodes except root nodes)
		Map<String, Collection<IPreferenceNode>> childrenNodes = new HashMap<String, Collection<IPreferenceNode>>();

		IExtensionRegistry registry = Platform.getExtensionRegistry(); // context.get(IExtensionRegistry.class);

		logger = context.get(Logger.class);

		for (IConfigurationElement elmt : registry.getConfigurationElementsFor(PREFS_PAGE_XP))
		{
			String bundleId = elmt.getNamespaceIdentifier();
			String pageID = elmt.getAttribute(ATTR_ID);
			String pageName = elmt.getAttribute(ATTR_NAME);
			String pageClass = elmt.getAttribute(ATTR_CLASS);
			if (!elmt.getName().equals(ELMT_PAGE))
			{
				logger.warn("unexpected element: {0}", elmt.getName());
				continue;
			} else if (isEmpty(pageID) || isEmpty(pageName))
			{
				logger.warn("missing id and/or name: {0}", bundleId);
				continue;
			}
			E4PreferenceNode pn = null;
			if (isEmpty(pageClass))
			{
				logger.warn("The class defined in E4 Preference page '{0}' is empty. Cannot create it", elmt.getName());
				continue;
			}

			pn = new E4PreferenceNode(pageID, pageName, bundleId, pageClass, context);

			// Issue 2 : Fix bug on order (see :
			// https://github.com/opcoach/e4Preferences/issues/2)
			// Add only pages at root level and remember of child pages for
			// categories
			String category = elmt.getAttribute(ATTR_CATEGORY);
			if (isEmpty(category))
			{
				pm.addToRoot(pn);
			} else
			{
				/*
				 * IPreferenceNode parent = findNode(pm, category); if (parent
				 * == null) { // No parent found, but may be the extension has
				 * not been read yet. So remember of it unboundedNodes.put(pn,
				 * category); } else { parent.add(pn); }
				 */
				// Check if this category is already registered.
				Collection<IPreferenceNode> children = childrenNodes.get(category);
				if (children == null)
				{
					children = new ArrayList<IPreferenceNode>();
					childrenNodes.put(category, children);
				}
				children.add(pn);
			}
		}

		// Must now bind pages that has not been added in nodes (depends on the
		// preference page read order)
		// Iterate on all possible categories
		Collection<String> categoriesDone = new ArrayList<String>();

		while (!childrenNodes.isEmpty())
		{
			for (String cat : Collections.unmodifiableSet(childrenNodes.keySet()))
			{
				// Is this category already in preference manager ? If not add
				// it later...
				IPreferenceNode parent = findNode(pm, cat);
				if (parent != null)
				{
					// Can add the list of children to this parent page...
					for (IPreferenceNode pn : childrenNodes.get(cat))
					{
						parent.add(pn);
					}
					// Ok This parent page is done. Can remove it from map
					// outside of this loop
					categoriesDone.add(cat);
				}
			}

			for (String keyToRemove : categoriesDone)
				childrenNodes.remove(keyToRemove);
			categoriesDone.clear();

		}
		
		// read also pref store providers
		initialisePreferenceStoreProviders(context);

	}



	/** Read the e4PreferenceStoreProvider extension point */
	private void initialisePreferenceStoreProviders(IEclipseContext context)
	{
		if (psProviders == null)
		{
			IContributionFactory factory = context.get(IContributionFactory.class);

			psProviders = new HashMap<String, Object>();
			IExtensionRegistry registry = context.get(IExtensionRegistry.class);

			// Read extensions and fill the map...
			for (IConfigurationElement elmt : registry.getConfigurationElementsFor(PREF_STORE_PROVIDER))
			{
				String declaringBundle = elmt.getNamespaceIdentifier();
				String pluginId = elmt.getAttribute(ATTR_PLUGIN_ID);
				if (isEmpty(pluginId))
				{
					logger.warn("missing plugin Id in extension " + PREF_STORE_PROVIDER + " check the plugin " + declaringBundle);
					continue;
				}

				String classname = elmt.getAttribute(ATTR_CLASS);
				String objectId = elmt.getAttribute(ATTR_ID_IN_WBCONTEXT);

				if ((isEmpty(classname) && isEmpty(objectId)) || (((classname != null) && classname.length() > 0) && ((objectId != null) && objectId.length() > 0)))
				{
					logger.warn("In extension " + PREF_STORE_PROVIDER + " only one of the two attributes (pluginId or idInWorkbenchContext) must be set. Check the plugin "
							+ declaringBundle);
					continue;
				}

				// Ok can now work with data...
				Object data = objectId;
				if (classname != null)
				{
					data = factory.create(classname, context);
					if (!(data instanceof IPreferenceStoreProvider))
					{
						logger.warn("In extension " + PREF_STORE_PROVIDER + " the class must implements IPreferenceStoreProvider. Check the plugin " + declaringBundle);
						continue;
					}
				}

				psProviders.put(pluginId, data);

			}
			
			context.set(KEY_PREF_STORE_PROVIDERS, psProviders);
		}
	}

	private IPreferenceNode findNode(PreferenceManager pm, String categoryId)
	{
		for (Object o : pm.getElements(PreferenceManager.POST_ORDER))
		{
			if (o instanceof IPreferenceNode && ((IPreferenceNode) o).getId().equals(categoryId))
			{
				return (IPreferenceNode) o;
			}
		}
		return null;
	}


	private boolean isEmpty(String value)
	{
		return value == null || value.trim().isEmpty();
	}


}
