package com.opcoach.e4.preferences.provider.example;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;

import com.opcoach.e4.preferences.IPreferenceStoreProvider;
import com.opcoach.e4.preferences.ScopedPreferenceStore;

public class MyPStoreProvider implements IPreferenceStoreProvider
{

	public MyPStoreProvider()
	{
	}

	@Override
	public IPreferenceStore getPreferenceStore()
	{
		System.out.println("Use my preference Store for this plugin");
		return new ScopedPreferenceStore(InstanceScope.INSTANCE, "com.opcoach.e4.preferences.provider.example");
	}

}
