#!/usr/bin/env bash

# Function which gets a library if it doesn't exist
function getLib {
  if [ ! -f $LIB_DIR/$1 ]; then
    echo "Getting $1"
    curl $2 > $LIB_DIR/$1
  fi
}

# Create lib directories if necessary
mkdir -p public/js/lib
mkdir -p assets/styl/lib

# Get libraries
LIB_DIR='public/js/lib'
getLib require.min.js http://cdnjs.cloudflare.com/ajax/libs/require.js/2.1.4/require.min.js
getLib jquery.min.js http://cdnjs.cloudflare.com/ajax/libs/jquery/2.0.0/jquery.min.js
getLib jquery.joyride.js https://raw.github.com/zurb/joyride/master/jquery.joyride-2.0.3.js
getLib jquery.cookie.js https://raw.github.com/zurb/joyride/master/jquery.cookie.js
getLib lodash.min.js http://cdnjs.cloudflare.com/ajax/libs/lodash.js/1.0.0-rc.3/lodash.min.js
getLib backbone.min.js http://cdnjs.cloudflare.com/ajax/libs/backbone.js/0.9.10/backbone-min.js
getLib jquery.rdio.min.js https://raw.github.com/rdio/jquery.rdio.js/master/jquery.rdio.min.js
getLib async.js https://raw.github.com/millermedeiros/requirejs-plugins/master/src/async.js
getLib promise.js https://raw.github.com/stackp/promisejs/master/promise.js
getLib text.js https://raw.github.com/requirejs/text/master/text.js
getLib oms.min.js http://jawj.github.io/OverlappingMarkerSpiderfier/bin/oms.min.js
getLib keymaster.min.js https://raw.github.com/madrobby/keymaster/master/keymaster.min.js
getLib backbone.shortcuts.min.js https://raw.github.com/bry4n/backbone-shortcuts/master/backbone.shortcuts.min.js
getLib markerclusterer_packed.js http://google-maps-utility-library-v3.googlecode.com/svn/tags/markerclustererplus/2.0.9/src/markerclusterer_packed.js
LIB_DIR='assets/styl/lib'
getLib normalize.styl https://raw.github.com/KenPowers/normalize.styl/master/normalize.styl
getLib joyride.css https://raw.github.com/zurb/joyride/master/joyride-2.0.3.css
