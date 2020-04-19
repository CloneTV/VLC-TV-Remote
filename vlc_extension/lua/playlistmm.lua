<?vlc

--package.loaded.httprequests = nil --uncomment to debug changes
require "httprequests"
local nodes = {}

local pathFormat = function(fmt, paths, val)
  local d = tonumber(val)
  if (d > 9) then
    return string.format(fmt, paths, "", val)
  else
    return string.format(fmt, paths, "0", val)
  end
end

local findLastOf = function(s)
  local n = (s:reverse()):find("%\\")
  if (n ~= nil) and (n > 0) then
    n = s:len() - n
    if (n <= 0) then
      return null
    end
  else
      return null
  end
  s = s:sub(1, n)
  if (s == nil) then
     return null
  end
  return s
end


local setNode = function(paths)
  if #nodes == 0 then
    return
  end
  
  local s = paths

  for i=1, #nodes do
    s = findLastOf(s)
    if (s == nil) then
      break
    end
    local item = nodes[#nodes + 1 - i]
    local res = string.format("%s\\tvshow.nfo", s)
    local f = vlc.io.open(res, "r")
    if f ~= nil then
      item.nfo = vlc.strings.make_uri(res)
    else
      local d = s:match("(%d+)$")
      if d ~= nil then
        res = pathFormat("%s\\season%s%s-poster.jpg", s, d)
        f = vlc.io.open(res, "r")
        if f ~= nil then
          item.arturl = vlc.strings.make_uri(pathFormat("%s\\season%s%s-", s, d))
        else
          local ss = findLastOf(s)
          if (ss == nil) then
            break
          end
          res = pathFormat("%s\\season%s%s-poster.jpg", ss, d)
          f = vlc.io.open(res, "r")
          if f ~= nil then
            item.arturl = vlc.strings.make_uri(pathFormat("%s\\season%s%s-", ss, d))
          end
        end
      end
    end
  end
  nodes = {}
  
end

local setItem = function(item)
  if (item ~= nil) and (item.uri ~= nil) and (item.uri:find("file:///") == 1) then
    local t = vlc.strings.decode_uri(item.uri):sub(9):match("(.+)%..+$"):gsub("/", "\\")
    local s = t .. ".nfo"
    local f = vlc.io.open(s, "r")
    if f ~= nil then
      f:seek(0)
      local txt = f:read("*all")
      f:close()
      f = nil
      item.nfo = vlc.strings.make_uri(s)
      item.rating = txt:match("<rating>([^<]+)") .. "/" .. txt:match("<votes>([^<]+)")
      item.title = txt:match("<title>([^<]+)")
      item.description = txt:match("<plot>([^<]+)")
      item.posterurl = txt:match("<thumb>([^<]+)")
      --item.date = txt:match("<premiered>([^<]+)")

      s = t .. "-thumb.jpg"
      f = vlc.io.open(s, "r")
      if f ~= nil then
        f:close()
        item.posterfile = vlc.strings.make_uri(s)
      end
    end
    setNode(t);
  end
end

local testnode = function(item)
  local children=NULL
  for k,v in pairs(item) do
    if (k == "type") then
    elseif (k == "children") then
      children = v._array
    end
  end
  return children
end

testitems = function(item)
  local children = NULL

  if (item.type == "node") then
    table.insert(nodes, item)
    children = testnode(item)

    if (children) then
      for i,v in ipairs(children) do
        testitems(v)
      end
    end
  else
    setItem(item)
  end

end

httprequests.processcommands()

local pt = httprequests.playlisttable()

testitems(pt)
httprequests.printTableAsJson(pt)


?>
