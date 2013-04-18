# GEA Web Server / Client

##Prerequisites

In order to run the web server and client you should be running a machine with
the latest stable versions of [node.js](http://nodejs.org) and
[PostgreSQL](http://www.postgresql.org/) installed.

You will also need an `rdio.json` configuration file. This file is a simple
structure which contains an Rdio developer key and secret:

    {
      "key": "9d3ayynanambvwxq5wpnw82y",
      "secret": "CdNPjjcrPW"
    }

We did not commit our `rdio.json` file to this repository for security concerns.
You must create or otherwise obtain this file and place it in a new directory
called `config`.

##Installation Instructions

If you are running a clean installation of Ubuntu 12.10, we have an installation
script which will install all prerequisites and set everything up for you
automatically (including node.js, PostgreSQL, and all other dependencies). Run
`ubuntu-installer.sh` and skip ahead to the instructions on how to run the
server.

Otherwise, set up a database in PostgreSQL named `gea` with a user `gea` and
password `gea` (for development purposes, other settings may be set in
`db/database.json`, the user should have full permissions to the database)
please follow these directions in your terminal:

1. `npm install -g pg db-migrate`
2. `npm install`
3. `cd db`
4. `db-migrate`
5. `cd ..`

The database can be populated with sample data using the following command:

    psql -d gea -f db/sample-data.sql

##Running the Server

To run the server, run `node app` from this directory. The web client will be
accessible from `http://localhost:3000/`. You must have the Adobe Flash plugin
installed in your browser for the web client to function correctly. If you are
running Ubuntu 12.10, you can install the plugin with `sudo apt-get -f install
flashplugin-installer`. The current release of the web server and client are
running [here](http://gea.kenpowers.net) (April 2nd, 2013).

##BETA Functionality:

* Server
    * REST Endpoint: `GET /rdio/getPlaybackToken`
        * Gets a playback token from Rdio which allows the client to play music.
    * REST Endpoint: `POST /rate?from=rdio&id=<id>&verdict=<like|dislike>`
        * Saves a rating to the database. Only supports Rdio right now. `<id>` is a track id from Rdio. `<like|dislike>` must be set to either `like` or `dislike`.
    * REST Endpoint: `GET / recieve?[artist=<artist>[&album=<album>&title=<title>]][[&pastHours=<pastHours>]|[&timeStart=<timeStart>&timeEnd=<timeEnd>]][&limit=<limit>&offset=<offset>]`
        * Gets ratings from the database
        * `artist` is optional
        * If `artist` is specified, then `album || title` can be specified
        * `pastHours || (timeStart && timeEnd)` are optional. `pastHours` takes precedent -- default is 24 hours
        * `limit` will limit the number of results (defaults to 10, maximum 100)
        * `offset` will skip the first given number of results, useful for pagination
* Client
    * Allows user to search for songs.
    * When search results are clicked a thirty-second clip is played.
    * Users can like or dislike the currently playing song.
    * The top 10 songs are available through a button in the player.

##Release notes
See [`../release-notes_beta-binary.txt`](../release-notes_beta-binary.txt) and [`../release-notes_beta-search.txt`](../release-notes_beta-search.txt).
