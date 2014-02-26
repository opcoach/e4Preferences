package com.opcoach.e4.preferences.example.pages;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;

/** A sample preference page to show how it works */
public class RootPage extends FieldEditorPreferencePage
{
	
	public RootPage()
	{
		super(GRID);
	}

	@Override
	protected void createFieldEditors()
	{
	
		addField(new StringFieldEditor("rootPageValue", "Root page value : ", getFieldEditorParent()));
	}

}
