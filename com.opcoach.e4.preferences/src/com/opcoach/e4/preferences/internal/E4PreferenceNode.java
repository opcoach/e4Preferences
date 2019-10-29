package com.opcoach.e4.preferences.internal;

import java.text.MessageFormat;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.preference.PreferencePage;
import org.osgi.framework.Bundle;

import com.opcoach.e4.preferences.IPreferenceStoreProvider;
import com.opcoach.e4.preferences.ScopedPreferenceStore;

public class E4PreferenceNode extends PreferenceNode
{
	private Class<? extends FieldEditorPreferencePage> clazz = null;

	// The bundle ID where this page is defined
	private String bundleID;

	// The class name in the bundle
	private String clazzname;

	private IEclipseContext context = null;

	public E4PreferenceNode(String id, String label, String bID, String classname, IEclipseContext ctx)
	{
		super(id, label, null, classname);
		context = ctx;
		clazzname = classname; // Private in ancestor, must store it locally...
		bundleID = bID;
	}

	@Override
	public void createPage()
	{
		if (clazz == null)
		{
			loadClass();
		}

		PreferencePage page = null;

		try
		{
			page = clazz.newInstance();
		} catch (InstantiationException  | IllegalAccessException e)
		{
			String explain = "";
			if (e instanceof IllegalAccessException)
			{
				explain="The class should have a PUBLIC default constructor (check its modifier)";

			}
			getLogger().error(e, MessageFormat.format("Unable to create the E4 Preference page with class name = {0} from bundle = {1} " + explain, clazz.getCanonicalName(), bundleID));
			

		}

		if (page != null && page instanceof PreferencePage)
		{
			if (getLabelImage() != null)
			{
				page.setImageDescriptor(getImageDescriptor());
			}
			page.setTitle(getLabelText());
			setPage(page);

			if (page.getPreferenceStore() == null)
				setPreferenceStore(page);

			// Must inject only when preferenceStore is set (or listening will not be done) !
			ContextInjectionFactory.inject(page, context);
		}
	}

	// Find the class instance from bundle and class name.
	@SuppressWarnings("unchecked")
	private void loadClass()
	{
		Bundle b = Platform.getBundle(bundleID);
		if (b == null)
		{
			getLogger().error(MessageFormat.format("The bundle with ID={0} is not found to create the E4 preference page with ID ={1} ", bundleID, getId()));
		} else
		{
			// Create the clazz instance
			try
			{
				clazz = (Class<? extends FieldEditorPreferencePage>) b.loadClass(clazzname);
			} catch (ClassNotFoundException e)
			{
				getLogger().error(e, MessageFormat.format("The class named {0} is not found to create the E4 preference page with ID ={1} ", clazzname, getId()));

			} catch (ClassCastException e)
			{
				getLogger().error(e, MessageFormat.format("The class named {0} is found but is not extending FieldEditorPreferencePage. Check definition of the E4 preference page with ID ={1} ",
						clazzname, getId()));

			}
		}

	}

	private Logger getLogger()
	{
		return context.get(Logger.class);
	}

	@Override
	public void disposeResources()
	{
		if (getPage() != null)
			ContextInjectionFactory.uninject(getPage(), context);
		super.disposeResources();
	}

	private void setPreferenceStore(PreferencePage page)
	{
		// Affect preference store to this page if this is a
		// PreferencePage, else, must manage it internally
		// Set the issue#1 on github :
		// https://github.com/opcoach/e4Preferences/issues/1
		// And manage the extensions of IP
		Map<String, Object> psProviders = (Map<String, Object>) context.get(E4PreferenceRegistry.KEY_PREF_STORE_PROVIDERS);
		IPreferenceStore store = null;

		// Get the preference store according to policy.
		Object data = psProviders.get(bundleID);
		if (data != null)
		{
			if (data instanceof IPreferenceStore)
				store = (IPreferenceStore) data;
			else if (data instanceof IPreferenceStoreProvider)
				store = ((IPreferenceStoreProvider) data).getPreferenceStore();
			else if (data instanceof String)
				store = (IPreferenceStore) context.get((String) data);

		} else
		{
			// Default behavior : create a preference store for this bundle and
			// remember of it
			store = new ScopedPreferenceStore(InstanceScope.INSTANCE, bundleID);
			psProviders.put(bundleID, store);
		}

		if (store != null)
			page.setPreferenceStore(store);
		else
		{
			getLogger().warn("Unable to set the preferenceStore for page " + page.getTitle() + " defined in bundle " + bundleID);
		}

	}

}
