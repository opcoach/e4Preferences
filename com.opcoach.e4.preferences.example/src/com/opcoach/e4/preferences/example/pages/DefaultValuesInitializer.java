package com.opcoach.e4.preferences.example.pages;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.RGB;

import com.opcoach.e4.preferences.ScopedPreferenceStore;

public class DefaultValuesInitializer extends AbstractPreferenceInitializer
{

	public DefaultValuesInitializer()
	{
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initializeDefaultPreferences()
	{
		IPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE, "com.opcoach.e4.preferences.example");	
		
		System.out.println("Enter in default Preference Initializer");
	
		store.setDefault("rootPageValue", "DEFAULT ROOT PAGE VALUE");
		store.setDefault("page1", "DEFAULT PAGE 1 VALUE");
		store.setDefault("page2", "DEFAULT PAGE 2 VALUE");
		
		store.setDefault("prefCombo", "value2");
		store.setDefault("prefColor", StringConverter.asString(new RGB(0,255,0)));
		store.setDefault("prefBoolean",true);
		store.setDefault("prefString","Default string value");
				
		
		
		
	}

}
