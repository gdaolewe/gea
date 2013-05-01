#Administrator Web Build Instructions for Ubuntu
####Tested with a clean install of Ubuntu 12.10


1. To download the package, go to <http://github.com/OpenGea/gea> and click on the Zip button on the left and unzip the archive to the desktop.

2. In a terminal window, run the following:

		cd Desktop/gea-master/web
		./ubuntu-installer.sh

3. If you do not have an Rdio API key, go to <http://developer.rdio.com/> and follow the section labeled "How to Get Started". Then create a text file named `rdio.json` with the following content:

		{
  			"key": "YOUR_RDIO_KEY",
  			"secret": "YOUR_RDIO_SECRET"
		}

4. Copy your rdio.json file containing your Rdio API key and secret in JSON format into the folder `gea/web/config/`.

5. In a new terminal window, run the following to start the server

		cd Desktop/gea-master/web
		source ~/.nvm/nvm.sh
		nvm run v0.10.2 app

#####Opening the Web Client

To see the web client, open a browser (currently only the latest versions of Firefox (19.0+) and Google Chrome (26+) are supported) and navigate to <http://localhost:3000>.

**NOTE: In order to be able to use the web client, you must have the Adobe Flash plugin installed for your browser. The installer script will install the Flash plugin.**

#####More Information

For more information on using:
* git with GitHub, visit <https://help.github.com/>
* the Rdio API, visit <http://developer.rdio.com/docs>
