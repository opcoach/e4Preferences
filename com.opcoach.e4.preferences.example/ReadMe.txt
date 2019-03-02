
Some information about this plugin.
===================================

It contains an application model to be a pure E4 application containing pure E4 preferences. 

The launch configuration should be set with : 

com.opcoach.e4.preferences
com.opcoach.e4.preferences.example
com.opcoach.e4.preferences.mainmenu
com.opcoach.e4.preferences.provider.example

 and select the com.opcoach.e4.preferences.example.product   in the main launch tab. 
 
 Ensure that these plugins are not in the launch configuration : 
     com.opcoach.e4.preferences.example.e3  :  the fragment which is used to launch the example in the ide for instance. 
     org.eclipse.ui
     org.eclipse.ui.*
  
  
     
