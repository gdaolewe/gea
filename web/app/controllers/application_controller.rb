#Application Controller
require 'json'

class ApplicationController < ActionController::Base
  protect_from_forgery
  helper_method :dummy

  def dummy
    @dummy_data = {
      'Song' => {
        '1' => {
          'title' => '99 Red Balloons',
          'songID' => 1,
          'rdio_songID' => '',
          'artistID' => 2,
          'albumID' => 2
        },
        '2' => {
          'title' => 'You Think It\'s a Joke',
          'songID' => 2,
          'rdio_songID' => '',
          'artistID' => 2,
          'albumID' => 2
        },
        '3' => {
          'title' => 'Breaking Down',
          'songID' => 3,
          'rdio_songID' => '',
          'artistID' => 1,
          'albumID' => 1
        },
        '4' => {
          'title' => 'Islands',
          'songID' => 4,
          'rdio_songID' => '',
          'artistID' => 3,
          'albumID' => 3
        },
        '5' => {
          'title' => 'Shelter',
          'songID' => 5,
          'rdio_songID' => '',
          'artistID' => 3,
          'albumID' => 3
        }
      },
      'Artist' => {
        '1' => {
          'name' => 'Florence + the Machine',
          'artistID' => 1,
          'rdio_artistID' => ''
        },
        '2' => {
          'name' => 'Goldfinger',
          'artistID' => 2,
          'rdio_artistID' => ''
        },
        '3' => {
          'name' => 'The xx',
          'artistID' => 3,
          'rdio_artistID' => ''
        }
      },
      'Album' => {
        '1' => {
          'title' => 'Ceremonials',
          'albumID' => 1,
          'rdio_albumID' => ''
        },
        '2' => {
          'title' => 'Stomping Ground',
          'albumID' => 2,
          'rdio_albumID' => ''
        },
        '3' => {
          'title' => 'xx',
          'albumID' => 3,
          'rdio_albumID' => ''
        }
      }
    }
  end
end

#BEGIN
#Handle global DB API calls

#Get a track based on unique song ID in our system, or any match from streaming service identifiers
#OPTIONAL: Restrict fields returned to reduce query time

#RETURNS: Associative hash containing all song fields if found
#         nil if not found
def getSongFromID(songID = nil, streamingUniqueIDs = nil, restrictFields = nil)

  #INIT VARS
  db_return = nil

  #CHECK OUR UNIQUE songID
  if !songID.nil? && songID.to_i > 0

    #TODO: ACTIVATE DATABASE CALL HERE
    db_return = dummy_data['Song'][''+songID] #ActiveRecord::Base.connection.select_all("SELECT * FROM Songs WHERE songID=#{ActiveRecord::Base.sanitize(songID)}")

  end

  #CHECK LINKED any streaming ID
  if !streamingUniqueIDs.nil? && (db_return.nil? )#TODO:REMOVETHIS#|| db_return.count == 0)

    #Loop over all streaming types
    streamingUniqueIDs.each_with_index do |id,streamName|

      #Make sure no track was found
      if db_return.nil? #TODO:REMOVETHIS#|| db_return.count == 0

        if streamName == 'rdio'

          db_return = dummy_data['Song']['rdio_songID'] #ActiveRecord::Base.connection.select_all("SELECT * FROM Songs WHERE rdio_songID=#{ActiveRecord::Base.sanitize(id)}")

          #elsif streamName == 'EXPAND_STREAM_SERVICE'

          #db_return = dummy_data['Song']['EXPAND_STREAM_SERVICE_songID'] #ActiveRecord::Base.connection.select_all("SELECT * FROM Songs WHERE EXPAND_STREAM_SERVICE_songID=#{ActiveRecord::Base.sanitize(id)}")

        end
        #END STREAM SELECTOR

      end
      #END

    end
    #END STREAM SERVICE LOOP

  end
  #END SEARCHING FOR SONG VIA STREAMING SERVICE

  #VALIDATE RETURNED DATA.
  #ENSURE THE DATABASE CALL WENT THROUGH
  if !db_return.nil? #TODO:REMOVETHIS#&& db_return.count > 0

    #SEND album to calling method
    return db_return
  end

  #Not found, return nil
  return nil

end
#END GET SONG

#Get an album based on unique album ID in our system, or any match from streaming service identifiers
#OPTIONAL: Restrict fields returned to reduce query time

#RETURNS: Associative hash containing all album fields if found
#         nil if not found
def getAlbumFromID(albumID = nil, streamingUniqueIDs = nil, restrictFields = nil)

  #INIT VARS
  db_return = nil

  #CHECK OUR UNIQUE albumID
  if !albumID.nil? && albumID.to_i > 0

    #TODO: ACTIVATE DATABASE CALL HERE
    db_return = dummy_data['Album'][''+albumID] #ActiveRecord::Base.connection.select_all("SELECT * FROM Albums WHERE albumID=#{ActiveRecord::Base.sanitize(albumID)}")

  end

  #CHECK LINKED any streaming ID
  if !streamingUniqueIDs.nil? && (db_return.nil? )#TODO:REMOVETHIS#|| db_return.count == 0)

    #Loop over all streaming types
    streamingUniqueIDs.each_with_index do |id,streamName|

      #Make sure no track was found
      if db_return.nil? #TODO:REMOVETHIS#|| db_return.count == 0

        if streamName == 'rdio'

          db_return = dummy_data['Album']['rdio_albumID'] #ActiveRecord::Base.connection.select_all("SELECT * FROM Albums WHERE rdio_albumID=#{ActiveRecord::Base.sanitize(id)}")

          #elsif streamName == 'EXPAND_STREAM_SERVICE'

          #db_return = dummy_data['Album']['EXPAND_STREAM_SERVICE_albumID'] #ActiveRecord::Base.connection.select_all("SELECT * FROM Albums WHERE EXPAND_STREAM_SERVICE_albumID=#{ActiveRecord::Base.sanitize(id)}")

        end
        #END STREAM SELECTOR

      end
      #END

    end
    #END STREAM SERVICE LOOP

  end
  #END SEARCHING FOR album VIA STREAMING SERVICE

  #VALIDATE RETURNED DATA.
  #ENSURE THE DATABASE CALL WENT THROUGH
  if !db_return.nil? #TODO:REMOVETHIS#&& db_return.count > 0

    return db_return

  end

  #Not found, return nil
  return nil

end
#END getAlbumByID

#Get an artist based on unique artist ID in our system, or any match from streaming service identifiers
#OPTIONAL: Restrict fields returned to reduce query time

#RETURNS: Associative hash containing all artist fields if found
#         nil if not found
def getArtistFromID(artistID = nil, streamingUniqueIDs = nil, restrictFields = nil)

  #INIT VARS
  db_return = nil

  #CHECK OUR UNIQUE songID
  if !artistID.nil? && artistID.to_i > 0

    #TODO: ACTIVATE DATABASE CALL HERE
    db_return = dummy_data['Artist'][''+artistID] #ActiveRecord::Base.connection.select_all("SELECT * FROM Songs WHERE artistID=#{ActiveRecord::Base.sanitize(artistID)}")

  end

  #CHECK LINKED any streaming ID
  if !streamingUniqueIDs.nil? && (db_return.nil? )#TODO:REMOVETHIS#|| db_return.count == 0)

    #Loop over all streaming types
    streamingUniqueIDs.each_with_index do |id,streamName|

      #Make sure no track was found
      if db_return.nil? #TODO:REMOVETHIS#|| db_return.count == 0

        if streamName == 'rdio'

          db_return = dummy_data['Artist']['rdio_artistID'] #ActiveRecord::Base.connection.select_all("SELECT * FROM Artists WHERE rdio_artistID=#{ActiveRecord::Base.sanitize(id)}")

          #elsif streamName == 'EXPAND_STREAM_SERVICE'

          #db_return = dummy_data['Artist']['EXPAND_STREAM_SERVICE_artistID'] #ActiveRecord::Base.connection.select_all("SELECT * FROM Artists WHERE EXPAND_STREAM_SERVICE_artistID=#{ActiveRecord::Base.sanitize(id)}")

        end
        #END STREAM SELECTOR

      end
      #END

    end
    #END STREAM SERVICE LOOP

  end
  #END SEARCHING FOR SONG VIA STREAMING SERVICE

  #VALIDATE RETURNED DATA.
  #ENSURE THE DATABASE CALL WENT THROUGH
  if !db_return.nil? #TODO:REMOVETHIS#&& db_return.count > 0

    #SEND album to calling method
    return db_return
  end

  #Not found, return nil
  return nil

end
#END getArtistByID

#END GLOBAL DATABASE CALLS

#END
