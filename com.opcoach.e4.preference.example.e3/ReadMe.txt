Some information about this plugin fragment.
============================================

This plugin fragment is used to check that we can mix both e3 and E4 preferences.

This fragment contains : 
   - a model fragment that defines a perspective in snippet (available in the perspective switcher)
   - an Eclipse 3 preferences page.
 

The launch configuration should be set with : 

com.opcoach.e4.preferences
com.opcoach.e4.preferences.example
com.opcoach.e4.preferences.example.e3 
com.opcoach.e4.preferences.provider.example

 and select the org.eclipse.platform.ide as a product in the main launch tab (it will be possible to switch to the perspective). 
 
 Ensure that this plugin is not in the launch configuration : 
     com.opcoach.e4.preferences.mainmenu  (it will add an additional preference command in the window menu, this is useless...)
 
 
 
  
  
     
