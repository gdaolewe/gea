#! /usr/bin/env ruby
require 'uri'
require 'net/http'
require 'fileutils'

# Define getLib function
def getLib(to, from)
  dest = "public/#{from.split('.').pop}/lib/" + to
  unless File.exist? dest
    puts "Getting #{dest}"
    uri = URI(from)
    Net::HTTP.start uri.host, :use_ssl => uri.scheme == 'https' do |http|
      res = http.get(uri.path)
      open dest, 'wb' do |file|
        file.write res.body
      end
      puts 'Done.'
    end
  end
end

# Create lib directories
FileUtils.mkdir_p 'public/css/lib'
FileUtils.mkdir_p 'public/js/lib'

# Get JavaScript libraries
getLib 'require.min.js', 'http://cdnjs.cloudflare.com/ajax/libs/require.js/2.1.4/require.min.js'
getLib 'text.js', 'https://raw.github.com/requirejs/text/2.0.5/text.js'
getLib 'jquery.min.js', 'http://cdnjs.cloudflare.com/ajax/libs/jquery/1.9.1/jquery.min.js'
getLib 'lodash.min.js', 'http://cdnjs.cloudflare.com/ajax/libs/lodash.js/1.0.0-rc.3/lodash.min.js'
getLib 'backbone.min.js', 'http://cdnjs.cloudflare.com/ajax/libs/backbone.js/0.9.10/backbone-min.js'

# Get CSS libraries
getLib 'normalize.min.css', 'http://cdnjs.cloudflare.com/ajax/libs/normalize/2.1.0/normalize.min.css'
