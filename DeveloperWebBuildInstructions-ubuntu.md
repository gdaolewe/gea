#Web Build Instructions for Ubuntu (Developer)
####Tested with a clean install of Ubuntu 12.10

1. To download the package, you can use git. To install git, open a terminal window and run: 

		sudo apt-get install git

2. Set up your username, email, and password by following GitHub's set up guide: <https://help.github.com/articles/set-up-git>

3. Now you need to clone your fork. Go to <https://github.com/OpenGea/gea> and click the Fork button.

4. Once OpenGea is forked, open a terminal and `cd` into the folder you would like to use for your project workspace. Then clone the repository with:

		git clone https://github.com/<your_username>/gea.git 

5. Once the package has been cloned, run:
		
		cd gea/web

6. Set up the web system by running the installer script and enter your computer password when prompted:

		./ubuntu-installer.sh


11. Copy your rdio.json file containing your Rdio API key and secret in JSON format into the folder `gea/web/config/`.

12. Close all terminal windows to reload your bash environment OR run the following to initialize your bash environment **(NOTE: there is a space between `.` and `~`)**:

		. ~/.nvm/nvm.sh

13. To start the server, run the following command in a terminal from the `gea/web` folder:

		node app
 
#####Opening the Web Client

To see the web client, open a browser (currently only the latest versions of Firefox and Google Chrome are supported) and navigate to <http://localhost:3000>. 

**NOTE: In order to be able to use the web client, you must have the Adobe Flash plugin installed for your browser. The installer script will install this, if you choose to use the script.**

#####For More Information

For more information on using git with GitHub, visit <https://help.github.com/>

For more information on the Rdio API, visit <http://developer.rdio.com/docs>