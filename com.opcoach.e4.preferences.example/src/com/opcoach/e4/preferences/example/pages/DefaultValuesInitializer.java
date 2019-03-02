package com.opcoach.e4.preferences.example.pages;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.RGB;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.prefs.BackingStoreException;

public class DefaultValuesInitializer extends AbstractPreferenceInitializer
{

	public DefaultValuesInitializer()
	{
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences node = DefaultScope.INSTANCE.getNode(FrameworkUtil.getBundle(getClass()).getSymbolicName());
	
		if (node != null)
		{
			node.put("rootPageValue", "DEFAULT ROOT PAGE VALUE");
			node.put("page1", "DEFAULT PAGE 1 VALUE");
			node.put("page2", "DEFAULT PAGE 2 VALUE");
		
			node.put("prefCombo", "value2");
			node.put("prefColor", StringConverter.asString(new RGB(0,255,0)));
			node.putBoolean("prefBoolean",true);
			node.put("prefString","Default string value");
			
			try { node.flush();  }  catch (BackingStoreException e) { }
		}		
		
		
		
	}

}
