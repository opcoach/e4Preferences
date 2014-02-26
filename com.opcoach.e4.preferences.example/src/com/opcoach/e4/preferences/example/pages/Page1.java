package com.opcoach.e4.preferences.example.pages;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;

/** A sample preference page to show how it works */
public class Page1 extends FieldEditorPreferencePage
{
	
	public Page1()
	{
		super(GRID);
	}

	@Override
	protected void createFieldEditors()
	{
	
		addField(new StringFieldEditor("page1", "Page1 value : ", getFieldEditorParent()));
	}

}
