# Web Instructions

To run the web server you should be running a \*nix system with
[`rvm`](http://rvm.io) installed. Once `rvm` is installed run `rvm install
1.9.3-p392`. You will also need a JavaScript environment installed, it is
recommended that you use [`nvm`](https://github.com/creationix/nvm) to install
Node v0.8.21 (`nvm install v0.8.21`). If `rvm` is set up correctly once you cd
into the `web` directory all remaining dependencies will be downloaded and
installed. Once everything is installed you can run the development server by
running `rails server` in the `web` directory. The server and web application
will be accessible through [`http://localhost:3000`](http://localhost:3000).
