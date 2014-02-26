/*******************************************************************************
 * Copyright (c) 2010 - 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Lars Vogel <lars.Vogel@gmail.com> - Bug 419770
 *******************************************************************************/
package com.opcoach.e4.preferences.example.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class SamplePart
{

	private Text txtInput;
	private TableViewer tableViewer;

	@Inject
	private MDirtyable dirty;

	@PostConstruct
	public void createComposite(Composite parent, @Preference(value = "prefColor") String colorKey)
	{
		parent.setLayout(new GridLayout(1, false));

		txtInput = new Text(parent, SWT.BORDER);
		txtInput.setMessage("Enter text to mark part as dirty");
		txtInput.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e)
			{
				dirty.setDirty(true);
			}
		});
		txtInput.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		tableViewer = new TableViewer(parent);

		tableViewer.add("Sample item 1");
		tableViewer.add("Sample item 2");
		tableViewer.add("Sample item 3");
		tableViewer.add("Sample item 4");
		tableViewer.add("Sample item 5");
		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		reactOnPrefColorChange(colorKey);
	}

	@Focus
	public void setFocus()
	{
		tableViewer.getTable().setFocus();
	}

	@Persist
	public void save()
	{
		dirty.setDirty(false);
	}

	private ColorRegistry creg = new ColorRegistry();

	@Inject
	@Optional
	public void reactOnPrefColorChange(@Preference(value = "prefColor") String colorKey)
	{
		System.out.println("React on a change in preferences with colorkey = " + colorKey);
		if ((colorKey != null) && (tableViewer != null) && !tableViewer.getControl().isDisposed())
		{
			Color c = getColorFromPref(colorKey);

			tableViewer.getTable().setForeground(c);
			tableViewer.getTable().redraw();
		}
	}

	private Color getColorFromPref(String colorKey)
	{
		Color c = creg.get(colorKey);
		if (c == null)
		{
			creg.put(colorKey, StringConverter.asRGB(colorKey));
			c = creg.get(colorKey);
		}
		return c;
	}
}