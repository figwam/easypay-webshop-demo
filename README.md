refimpl
=======

reference implementation for checkout-page, easypay


		
Eclipse
===========
prerequisites: 
 - git plugin
 - maven plugin

Howto
===========
1. clone the project from git repository
2. import it as maven project
3. download and install an jboss 7 server in eclipse
4a. deploy to jboss 7 via eclipse 
4b. you can also deploy the war file directly to jboss 7 deployment folder

Deployment
=========== 
	Tested on JBoss 7.1
	
	//*****************************
	// Note
	//*****************************
	Set following system properties:
		sec.key
		merchant.id