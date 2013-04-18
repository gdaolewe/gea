#!/usr/bin/env bash

# Variables
NODE_ENV=production
PORT=8080

# Source nvm
source $HOME/.nvm/nvm.sh

# Start application (forever requires .js extension)
forever start ./app.js
