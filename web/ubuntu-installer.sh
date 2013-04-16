#!/bin/bash

#Assumes a clean installation of Ubuntu 12.10

# Install:
#	git
#	curl
#	postgresql
#	libpq-dev
#	build-essential
#	flashplugin-installer

sudo apt-get -y install git curl postgresql postgresql-client-9.1 postgresql-common postgresql-9.1 libpq-dev build-essential

# Download and run 'nvm' to simplify setup process
wget -qO- https://raw.github.com/creationix/nvm/master/install.sh | sh

# Setup the bash environment so that it source's the nvm.sh script
echo ". ~/.nvm/nvm.sh" >> ~/.bashrc

# Source the nvm.sh script for this session manually
. ~/.nvm/nvm.sh

# Use nvm to install NodeJS v0.10.2 (current version)
nvm install v0.10.2

# Set v0.10.2 to be used
nvm use v0.10.2

# Let npm install all the necessary modules
npm install

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

# Install the npm packages to integrate with and intialize postgres
npm install -g pg db-migrate

# Initialize the database
cd db; db-migrate up

# Insert sample database data
psql -d gea -f sample-data.sql

# Create the config folder in which the rdio.json file will be stored
cd ..; mkdir config
