#Web Build Instructions for Ubuntu (Administrator)
####Tested with a clean install of Ubuntu 12.10


Admin just has to: real stuf

1. To download the package, go to <http://github.com/OpenGea/gea> and click on the Zip button on the left and unzip the archive to the desktop.

2. In a terminal window, run the following: 
		
		cd Desktop/gea-master/web
		./ubuntu-installer.sh 

3. To apply for an Rdio API key, go to <http://developer.rdio.com/> and follow the section labeled "How to Get Started".

4. Copy your rdio.json file containing your Rdio API key and secret in JSON format into the folder `gea/web/config/`.

5. In a new terminal window, run the following to start the server

		cd Desktop/gea-master/web
		. ~/.nvm/nvm.sh
		nvm run v0.10.2 app

**NOTE: In order to be able to use the web client, you must have the Adobe Flash plugin installed for your browser. The installer script will install this, if you choose to use the script.**





