
function trim (s)
  return (string.gsub(s, "^%s*(.-)%s*$", "%1"))
end

function descriptor()
    return { scope="local" }
end

function parse_filename(meta)

    if meta["title"] then
        return
    end

    local name = meta["filename"];
    if not name then
        return
    end

    local title, seasonNumber
    _, _, showName, seasonNumber, episodeNumber = string.find(name, "(.+)S(%d+)E(%d+).*")
    if not showName then
        return
    end

    showName = trim(string.gsub(showName, "%.", " "))
    vlc.item:set_meta("title", showName.." S"..seasonNumber.."E"..episodeNumber)
    vlc.item:set_meta("show_name", showName)
    vlc.item:set_meta("episode", episodeNumber)
    vlc.item:set_meta("season", seasonNumber)
end

function parse_nfo(s)

	local ss, msg = io.open(s, "r")
    
	if ss == nil then
	   vlc.msg.err(msg)
	   return nil
	end

	local txt = ss:read(200000)
	ss = nil
	if txt == nil then
	   return nil
	end

	vlc.msg.err(txt)
	local arturl = txt:match("<thumb>([^<]+)")
	vlc.item:set_meta("url", vlc.strings.make_uri(s))
	vlc.item:set_meta("title", txt:match("<title>([^<]+)"))
	vlc.item:set_meta("season", txt:match("<season>([^<]+)"))
	vlc.item:set_meta("episode", txt:match("<episode>([^<]+)"))
	vlc.item:set_meta("date", txt:match("<premiered>([^<]+)"))
	vlc.item:set_meta("rating", txt:match("<rating>([^<]+)"))
	vlc.item:set_meta("director", txt:match("<votes>([^<]+)"))
	vlc.item:set_meta("description", txt:match("<plot>([^<]+)"))
	vlc.item:set_meta("arturl", arturl)
	return arturl;
end

-- for META ART
function fetch_art()

	if vlc.item == nil then return nil end
	local meta = vlc.item:metas()
	local uri = vlc.item:uri()

	if (uri:find("directory:///") == 1) then
            return nil
	elseif (uri:find("file:///") ~= 1) then
            return nil
	end

	--vlc.msg.err(uri)
        local s = vlc.strings.decode_uri(uri):sub(9):gsub("/", "\\")
	--vlc.msg.err(s)
        local ss = s:match("(.+)%..+$")
        if ss ~= nil then
           s = ss .. ".nfo"
	   return parse_nfo(s)
        else
	    parse_filename(meta)
            return nil
        end
end
