class RestEndpointController < ApplicationController

  #GET /artist/:id?params=
  def getArtist

    #INIT VARS
    db_return = nil
    query_type = 'artistID'

    #GET SEARCH PARAM
    if !params[:type].nil? && params[:type] == 'rdio_artistID'
      query_type = 'rdio_artistID'
      #elsif !params[:type].nil? && params[:type] == 'EXPAND_STREAM_SERVICE'
      #query_type = 'EXPAND_STREAM_SERVICE'
    end

    #CHECK OUR UNIQUE artistID
    if query_type == 'artistID' && !params[:id].nil? && params[:id].to_i > 0

      #TODO: ACTIVATE DATABASE CALL HERE
      db_return = dummy #ActiveRecord::Base.connection.select_all("SELECT * FROM Artists WHERE artistID=#{ActiveRecord::Base.sanitize(params[:id])}")

      #CHECK LINKED rdio_artistID
    elsif query_type == 'rdio_artistID' && !params[:id].nil? && params[:id].to_s != ''

      #TODO: ACTIVATE DATABASE CALL HERE
      db_return = dummy #ActiveRecord::Base.connection.select_all("SELECT * FROM Artists WHERE rdio_artistID=#{ActiveRecord::Base.sanitize(params[:id])}")

    end

    #VALIDATE RETURNED DATA.
    #ENSURE THE DATABASE CALL WENT THROUGH
    if !db_return.nil?

      #TODO: Change hash order to match database
      #ENSURE A RECORD WAS RETURNED THAT MATCHES
      if !db_return['Artist'][''+params[:id]].nil?

        #SEND ARTIST JSON TO CLIENT
        render :json => JSON.parse(db_return['Artist'][''+params[:id]].to_json), :status => 200
        return true
      end
    end
    render :text => 'Record not found!', :status => 404
  end

  #GET /album/:id?params=
  def getAlbum

    #INIT VARS
    db_return = nil
    query_type = 'albumID'

    #GET SEARCH PARAM
    if !params[:type].nil? && params[:type] == 'rdio_albumID'
      query_type = 'rdio_albumID'
      #elsif !params[:type].nil? && params[:type] == 'EXPAND_STREAM_SERVICE'
      #query_type = 'EXPAND_STREAM_SERVICE'
    end

    #CHECK OUR UNIQUE albumID
    if query_type == 'albumID' && !params[:id].nil? && params[:id].to_i > 0

      #TODO: ACTIVATE DATABASE CALL HERE
      db_return = dummy #ActiveRecord::Base.connection.select_all("SELECT * FROM Albums WHERE albumID=#{ActiveRecord::Base.sanitize(params[:id])}")

      #CHECK LINKED rdio_albumID
    elsif query_type == 'rdio_albumID' && !params[:id].nil? && params[:id].to_s != ''

      #TODO: ACTIVATE DATABASE CALL HERE
      db_return = dummy #ActiveRecord::Base.connection.select_all("SELECT * FROM Albums WHERE rdio_albumID=#{ActiveRecord::Base.sanitize(params[:id])}")

    end

    #VALIDATE RETURNED DATA.
    #ENSURE THE DATABASE CALL WENT THROUGH
    if !db_return.nil?

      #TODO: Change hash order to match database
      #ENSURE A RECORD WAS RETURNED THAT MATCHES
      if !db_return['Album'][''+params[:id]].nil?

        #SEND ARTIST JSON TO CLIENT
        render :json => JSON.parse(db_return['Album'][''+params[:id]].to_json), :status => 200
        return true
      end
    end
    render :text => 'Record not found!', :status => 404
  end

  #GET /song/:id?params=
  def getSong

    #INIT VARS
    db_return = nil
    query_type = 'songID'

    #GET SEARCH PARAM
    if !params[:type].nil? && params[:type] == 'rdio_songID'
      query_type = 'rdio_songID'
      #elsif !params[:type].nil? && params[:type] == 'EXPAND_STREAM_SERVICE'
      #query_type = 'EXPAND_STREAM_SERVICE'
    end

    #CHECK OUR UNIQUE songID
    if query_type == 'songID' && !params[:id].nil? && params[:id].to_i > 0

      #TODO: ACTIVATE DATABASE CALL HERE
      db_return = dummy #ActiveRecord::Base.connection.select_all("SELECT * FROM Songs WHERE songID=#{ActiveRecord::Base.sanitize(params[:id])}")

      #CHECK LINKED rdio_songID
    elsif query_type == 'rdio_songID' && !params[:id].nil? && params[:id].to_s != ''

      #TODO: ACTIVATE DATABASE CALL HERE
      db_return = dummy #ActiveRecord::Base.connection.select_all("SELECT * FROM Songs WHERE rdio_songID=#{ActiveRecord::Base.sanitize(params[:id])}")

    end

    #VALIDATE RETURNED DATA.
    #ENSURE THE DATABASE CALL WENT THROUGH
    if !db_return.nil?

      #TODO: Change hash order to match database
      #ENSURE A RECORD WAS RETURNED THAT MATCHES
      if !db_return['Song'][''+params[:id]].nil?

        #SEND song JSON TO CLIENT
        render :json => JSON.parse(db_return['Song'][''+params[:id]].to_json), :status => 200
        return true
      end
    end
    render :text => 'Record not found!', :status => 404
  end
end
