#!/usr/bin/env bash

# Assumes a clean installation of Ubuntu 12.10
### WARNING: THIS WILL UPDATE ALL PACKAGES AND INSTALL THE PACKAGES LISTED BELOW ###

echo "

WARNING: This will update *all* packages and install the following packages and their dependencies:
  git
  curl
  postgresql
  libpq-dev
  build-essential
  memcached

"

read -p "Press [Enter] key to begin installation, or [Ctrl] + [c] to exit
"

echo "Updating and installing packages..."
echo

# First update the package listings
sudo apt-get update

# Then make sure the system is up to date
sudo apt-get -y upgrade

# Install:
#	git
#	curl
#	postgresql
#	libpq-dev
#	build-essential
#	memcached
# flashplugin-installer is required if viewing the webapp in a browser

sudo apt-get -y install git curl postgresql postgresql-client-9.1 postgresql-common postgresql-9.1 libpq-dev build-essential memcached

echo "Downloading and installing 'nvm'..."
echo

# Download and run 'nvm' to simplify setup process
wget -qO- https://raw.github.com/creationix/nvm/master/install.sh | sh

# Setup the bash environment so that it source's the nvm.sh script
echo "source ~/.nvm/nvm.sh" >> ~/.profile

# Source the nvm.sh script for this session manually
source ~/.nvm/nvm.sh

# Use nvm to install NodeJS v0.10.2 (current version)
nvm install v0.10.2

# Make the default v0.10.2
nvm alias default v0.10.2

# Set v0.10.2 to be used
nvm use v0.10.2

# Let npm install all the necessary modules
npm install

echo "Setting up the postgres database..."
echo

# Set up postgres with the current user as a superuser
sudo -u postgres createuser --superuser $USER

# Create a database for the current user (required so that they can login to Postgres)
createdb $USER

# Create the database for the app
createdb gea

# Create a user 'gea' for the app with password 'gea'
psql -c "CREATE USER gea WITH PASSWORD 'gea';"

# Grant privileges to the user on the gea database
psql -c "GRANT ALL PRIVILEGES ON DATABASE gea TO gea;"

# Install the npm packages to integrate with and intialize postgres, keep the server running, and setup tests
npm install -g pg db-migrate forever mocha

# Initialize the database
cd db; db-migrate up

# Insert sample database data
node refreshDb.js

# Create the config folder in which the rdio.json file will be stored
cd ..; mkdir config

# Make sure the crontab script is executable
chmod +x gea-server.sh

# Add the job to a crontab file so that the server will be started on boot
echo "Adding a job to the current user's crontab file to automatically start the server on boot..."
echo

crontab -l > tempfile
echo "@reboot $(pwd)/gea-server.sh" >> tempfile
crontab tempfile
rm tempfile

# Create a template rdio.json file in preparation for the key and secret
echo '{
  "key": "YOUR_RDIO_KEY",
  "secret": "YOUR_RDIO_SECRET"
}
' > config/rdio.json

# We should also print a message about editing database.json with accurate production settings.

echo "


Installation complete.

"
echo '
If you do not have an Rdio API key, go to http://developer.rdio.com/
and follow the section labeled "How to Get Started".

Once you have received your Rdio API key and secret, save them in
web/config/rdio.json file where specified.
'

echo 'To run the server in production mode, you must edit the settings
in db/database.json where specified. Use the development configuration
as an example.
'
