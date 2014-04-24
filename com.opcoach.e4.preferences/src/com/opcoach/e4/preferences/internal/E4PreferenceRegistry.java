package com.opcoach.e4.preferences.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.contributions.IContributionFactory;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.opcoach.e4.preferences.ScopedPreferenceStore;

@Creatable
public class E4PreferenceRegistry
{

	public static final String PREFS_PAGE_XP = "com.opcoach.e4.preferences.e4PreferencePages";
	protected static final String ELMT_PAGE = "page"; // $NON-NLS-1$
	protected static final String ATTR_ID = "id"; // $NON-NLS-1$
	protected static final String ATTR_CATEGORY = "category"; // $NON-NLS-1$
	protected static final String ATTR_CLASS = "class"; // $NON-NLS-1$
	protected static final String ATTR_NAME = "name"; // $NON-NLS-1$

	@Inject
	protected Logger logger;

	@Inject
	protected IEclipseContext context;

	@Inject
	protected IExtensionRegistry registry;

	private PreferenceManager pm = null;

	public PreferenceManager getPreferenceManager()
	{

		// Remember of the unbounded nodes to order parent pages.
		// Map<category, list of children> (all nodes except root nodes)
		Map<String, Collection<IPreferenceNode>> childrenNodes = new HashMap<String, Collection<IPreferenceNode>>();

		if (pm != null)
			return pm;

		pm = new PreferenceManager();
		IContributionFactory factory = context.get(IContributionFactory.class);

		for (IConfigurationElement elmt : registry.getConfigurationElementsFor(PREFS_PAGE_XP))
		{
			String bundleId = elmt.getNamespaceIdentifier();
			if (!elmt.getName().equals(ELMT_PAGE))
			{
				logger.warn("unexpected element: {0}", elmt.getName());
				continue;
			} else if (isEmpty(elmt.getAttribute(ATTR_ID)) || isEmpty(elmt.getAttribute(ATTR_NAME)))
			{
				logger.warn("missing id and/or name: {}", bundleId);
				continue;
			}
			PreferenceNode pn = null;
			if (elmt.getAttribute(ATTR_CLASS) != null)
			{
				IPreferencePage page = null;
				try
				{
					String prefPageURI = getClassURI(bundleId, elmt.getAttribute(ATTR_CLASS));
					Object object = factory.create(prefPageURI, context);
					if (!(object instanceof IPreferencePage))
					{
						logger.error("Expected instance of IPreferencePage: {0}", elmt.getAttribute(ATTR_CLASS));
						continue;
					}
					page = (IPreferencePage) object;
					// Affect preference store to this page if this is a
					// FieldEditorPreferencePage, else, must manage it
					// internally
					if (page instanceof FieldEditorPreferencePage)
					{
						IPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE, bundleId);
						((FieldEditorPreferencePage) page).setPreferenceStore(store);
					}

				} catch (ClassNotFoundException e)
				{
					logger.error(e);
					continue;
				}
				ContextInjectionFactory.inject(page, context);
				if ((page.getTitle() == null || page.getTitle().isEmpty()) && elmt.getAttribute(ATTR_NAME) != null)
				{
					page.setTitle(elmt.getAttribute(ATTR_NAME));
				}

				pn = new PreferenceNode(elmt.getAttribute(ATTR_ID), page);
			} else
			{
				pn = new PreferenceNode(elmt.getAttribute(ATTR_ID), new EmptyPreferencePage(elmt.getAttribute(ATTR_NAME)));
			}

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
				// Is this category already in preference manager ? If not add it later...
				IPreferenceNode parent = findNode(pm, cat);
				if (parent != null)
				{
					// Can add the list of children to this parent page...
					for (IPreferenceNode pn : childrenNodes.get(cat))
					{
						parent.add(pn);
					}
					// Ok This parent page is done. Can remove it from map outside of this loop
					categoriesDone.add(cat);
				}
			}
			
			for (String keyToRemove : categoriesDone)
				childrenNodes.remove(keyToRemove);
			categoriesDone.clear();

		}
		
		return pm;
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

	private String getClassURI(String definingBundleId, String spec) throws ClassNotFoundException
	{
		if (spec.startsWith("platform:"))
		{
			return spec;
		} // $NON-NLS-1$
		return "bundleclass://" + definingBundleId + '/' + spec;
	}

	private boolean isEmpty(String value)
	{
		return value == null || value.trim().isEmpty();
	}

	static class EmptyPreferencePage extends PreferencePage
	{

		public EmptyPreferencePage(String title)
		{
			setTitle(title);
			noDefaultAndApplyButton();
		}

		@Override
		protected Control createContents(Composite parent)
		{
			return new Label(parent, SWT.NONE);
		}

	}

}
