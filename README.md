[![Build Status](https://travis-ci.org/opcoach/e4Preferences.svg?branch=master)](https://travis-ci.org/opcoach/e4Preferences)

e4Preferences
=============

Some code to manage preferencePage with an extension point (as in Eclipse 3).

This new release is updated **to mix both E3 and E4 preference pages.** So you can define pure E4 preference pages in a plug-in, and launch your plugin either in a pure E4 application or in a compatibility runtime.

You can get some explainations <a href="http://www.opcoach.com/en/managing-preference-pages-with-eclipse-4/">on my blog</a>

If you want to add the 'Window -> Preferences' menu automatically in your **pure** e4 application, use the **com.opcoach.e4.preferences.mainmenu** plugin fragment in your launch configuration. To get the menu, it assumes that your main menu has the regular ID : 'org.eclipse.ui.main.menu'. 

If you are running with the compatibility layer, do not use the *com.opcoach.e4.preferences.mainmenu* plugin fragment, and prefer to add the Preference Action in your ActionBarAdvisor. 

This project is available on this p2 repository : https://www.opcoach.com/repository/latest (use the com.opcoach.e4.preferences.feature.feature.group) 

