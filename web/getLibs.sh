# Function which gets a library if it doesn't exist
function getLib {
  if [ ! -f $LIB_DIR/$1 ]; then
    echo "Getting $1"
    curl $2 > $LIB_DIR/$1
  fi
}

# Create lib directories if necessary
mkdir -p public/js/lib
mkdir -p public/css/lib

# Get libraries
LIB_DIR='public/js/lib'
getLib require.min.js http://cdnjs.cloudflare.com/ajax/libs/require.js/2.1.4/require.min.js
getLib jquery.min.js http://cdnjs.cloudflare.com/ajax/libs/jquery/1.9.1/jquery.min.js
getLib lodash.min.js http://cdnjs.cloudflare.com/ajax/libs/lodash.js/1.0.0-rc.3/lodash.min.js
getLib backbone.min.js http://cdnjs.cloudflare.com/ajax/libs/backbone.js/0.9.10/backbone-min.js
LIB_DIR='public/css/lib'
getLib normalize.min.css http://cdnjs.cloudflare.com/ajax/libs/normalize/2.1.0/normalize.min.css
