package com.opcoach.e4.preference.example.e3;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class SampleE3PrefPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{

	public SampleE3PrefPage()
	{
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void init(IWorkbench workbench)
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void createFieldEditors()
	{
		addField(new StringFieldEditor("PREF_STRING", "A pref string", getFieldEditorParent()));

	}

}
