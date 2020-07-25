require "common"

vlc.msg.info("Lua HTTP interface")

open_tag = "<?vlc"
close_tag = "?>"

mimes = {
    txt = "text/plain",
    json = "text/plain",
    html = "text/html",
    xml = "text/xml",
    js = "text/javascript",
    css = "text/css",
    png = "image/png",
    jpg = "image/jpeg",
    jpeg = "image/jpeg",
    ico = "image/x-icon",
    nfo = "text/x-nfo",
    m3u8 = "application/x-mpegURL",
    mp4 = "video/mp4",
    mov = "video/quicktime",
    mkv = "video/x-matroska",
    wmv = "video/x-ms-wmv",
    avi = "video/x-msvideo",
    flv = "video/x-flv",
    ts  = "video/MP2T",
    m3u  = "application/mpegurl",
    m3u8  = "application/x-mpegurl",
}
check_ext = {
    "mp4",
    "mov",
    "mkv",
    "wmv",
    "avi",
}

function content_return(ext, size, raw)
  return [[Content-Type: ]]..mimes[ext]..[[

Content-Length: ]]..size..[[


]]..raw..[[

]]
end

function e404_return()
    return [[Status: 404
Content-Type: text/plain
Content-Length: 9

Error 404
]]
end

function e500_return()
    return [[Status: 500
Content-Type: text/plain
Content-Length: 9

Error 500
]]
end

function json_not_found_return()
    return [[Status: 200
Content-Type: text/json
Content-Length: 15

{"found":false}
]]
end

function json_ok_return(name, size)
    local json = "{\"found\":true,\"uri\":\"" .. name .. "\",\"size\":" .. size .. "}"
    return [[Status: 200
Content-Type: text/json
Content-Length: ]] .. string.len(json) .. [[


]] .. json
end


function escape(s)
    return (string.gsub(s,"([%^%$%%%.%[%]%*%+%-%?])","%%%1"))
end

function my_vlc_load(code, filename)
    if _VERSION == "Lua 5.1" then
        return loadstring(code, filename)
    else
        return load(code, filename)
    end
end

function process_raw(filename)
    local input = io.open(filename):read("*a")
    -- find the longest [===[ or ]=====] type sequence and make sure that
    -- we use one that's longer.
    local str="X"
    for str2 in string.gmatch(input,"[%[%]]=*[%[%]]") do
        if #str < #str2 then str = str2 end
    end
    str=string.rep("=",#str-1)

    --[[ FIXME:
    <?xml version="1.0" encoding="charset" standalone="yes" ?> is still a problem. The closing '?>' needs to be printed using '?<?vlc print ">" ?>' to prevent a parse error.
    --]]
    local code0 = string.gsub(input,escape(close_tag)," print(["..str.."[")
    local code1 = string.gsub(code0,escape(open_tag),"]"..str.."]) ")
    local code = "print(["..str.."["..code1.."]"..str.."])"
    --[[ Uncomment to debug
    if string.match(filename,"vlm_cmd.xml$") then
    io.write(code)
    io.write("\n")
    end
    --]]
    return assert(my_vlc_load(code,filename))
end

function process(filename)
    local mtime = 0    -- vlc.net.stat(filename).modification_time
    local func = false -- process_raw(filename)
    return function(...)
        local new_mtime = vlc.net.stat(filename).modification_time
        if func == false or new_mtime ~= mtime then
            -- Re-read the file if it changed
            if func == false then
                vlc.msg.dbg("Loading `"..filename.."'")
            else
                vlc.msg.dbg("Reloading `"..filename.."'")
            end
            func = process_raw(filename)
            mtime = new_mtime
        end
        return func(...)
    end
end

function callback_error(path,url,msg)
    local url = url or "&lt;page unknown&gt;"
    return  [[<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Error loading ]]..url..[[</title>
</head>
<body>
<h1>Error loading ]]..url..[[</h1><pre>]]..(config.no_error_detail and "Remove configuration option `no_error_detail' on the server to get more information."
or vlc.strings.convert_xml_special_chars(tostring(msg)))..[[</pre>
<p>
<a href="http://www.videolan.org/">VideoLAN</a><br/>
<a href="http://www.lua.org/manual/5.1/">Lua 5.1 Reference Manual</a>
</p>
</body>
</html>]]
end

function dirlisting(url,listing)
    local list = {}
    for _,f in ipairs(listing) do
        if not string.match(f,"^%.") then
            table.insert(list,"<li><a href='"..f.."'>"..f.."</a></li>")
        end
    end
    list = table.concat(list)
    local function callback()
        return [[<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Directory listing ]]..url..[[</title>
</head>
<body>
<h1>Directory listing ]]..url..[[</h1><ul>]]..list..[[</ul>
</body>
</html>]]
    end
    return h:file(url,"text/html",nil,password,callback,nil)
end

function callback_check(data, request, args)
  local checkfile = function(data, request)
    
    local filename = nil
    if (args ~= nil) then
      local uri = args:match("^uri=file:///(.+)$")
      if (uri ~= nil) then
        filename = vlc.strings.decode_uri(uri):gsub("/", "\\")
      end
    end
    if (filename == nil) then
      return json_not_found_return()
    end
    for i, ext in ipairs(check_ext) do
      local path = filename .. "-trailer." .. ext
      local stat = vlc.net.stat(path)
      if (stat == nil) then
         vlc.msg.err("!) open error: " .. path)
      elseif (stat.size > 0) then
         return json_ok_return("file:///" .. path:gsub("\\", "/"), stat.size)
      end
    end
    return json_not_found_return()
  end

  local ok, content = pcall(checkfile, data, request)
  if not ok then
    return json_not_found_return()
  end
  return content
end

function callback_art(data, request, args)
    local getart = function(data, request)
        local num = nil
        if args ~= nil then
            num = string.gmatch(args, "item=(.*)")
            if num ~= nil then
                num = num()
            end
        end
        local item
        if num == nil then
            item = vlc.input.item()
        else
            item = vlc.playlist.get(num).item
        end
        local metas = item:metas()
        local filename = vlc.strings.decode_uri(string.gsub(metas["artwork_url"],"file://",""))
        local windowsdrive = string.match(filename, "^/%a:/.+$")  --match windows drive letter
        if windowsdrive then
            filename = string.sub(filename, 2)  --remove starting forward slash before the drive letter
        end
        local size = vlc.net.stat(filename).size
        local ext = string.match(filename,"%.([^%.]-)$")
        local raw = io.open(filename, 'rb'):read("*a")
        return content_return(ext, size, raw)
    end

    local ok, content = pcall(getart, data, request)
    if not ok then
      return e404_return()
    end
    return content
end

function callback_get(data, request, args)
  local getfile = function(data, request)
    
    local filename = nil
    if (args ~= nil) then
      local uri = args:match("^uri=file:///(.+)$")
      if (uri ~= nil) then
        filename = vlc.strings.decode_uri(uri):gsub("/", "\\")
      end
    end
    if (filename == nil) then
      return e500_return()
    end

    local size = vlc.net.stat(filename).size
    local ext = filename:match("%.([^%.]-)$")
    local f, msg = vlc.io.open(filename, 'rb')
    if (f == nil) then
      vlc.msg.err("open error: " .. msg)
      return e500_return()
    end

    local raw = ""
    while true do
       local line = f:read(65536)
       if (line == nil) then
           break
       end
       raw = raw .. line
    end
    f:close()

    if (raw == nil) then
      vlc.msg.err("read error: " .. filename)
      return e500_return()
    end
    return content_return(ext, size, raw)
  end

  local ok, content = pcall(getfile, data, request)
  if not ok then
    return e404_return()
  end
  return content
end

function file(h,path,url,mime)
    local generate_page = process(path)
    local callback = function(data,request)
        -- FIXME: I'm sure that we could define a real sandbox
        -- redefine print
        local page = {}
        local function pageprint(...)
            for i=1,select("#",...) do
                if i== 1 then
                    table.insert(page,tostring(select(i,...)))
                else
                    table.insert(page," "..tostring(select(i,...)))
                end
            end
        end
        _G._GET = parse_url_request(request)
        local oldprint = print
        print = pageprint
        local ok, msg = pcall(generate_page)
        -- reset
        print = oldprint
        if not ok then
            return callback_error(path,url,msg)
        end
        return table.concat(page)
    end
    return h:file(url or path,mime,nil,password,callback,nil)
end

function rawfile(h,path,url)
    local filename = path
    local mtime = 0    -- vlc.net.stat(filename).modification_time
    local page = false -- io.open(filename):read("*a")
    local callback = function(data,request)
        local new_mtime = vlc.net.stat(filename).modification_time
        if page == false or new_mtime ~= mtime then
            -- Re-read the file if it changed
            if page == false then
                vlc.msg.dbg("Loading `"..filename.."'")
            else
                vlc.msg.dbg("Reloading `"..filename.."'")
            end
            page = io.open(filename,"rb"):read("*a")
            mtime = new_mtime
        end
        return page
    end
    return h:file(url or path,nil,nil,password,callback,nil)
end

function parse_url_request(request)
    if not request then return {} end
    local t = {}
    for k,v in string.gmatch(request,"([^=&]+)=?([^=&]*)") do
        local k_ = vlc.strings.decode_uri(k)
        local v_ = vlc.strings.decode_uri(v)
        if t[k_] ~= nil then
            local t2
            if type(t[k_]) ~= "table" then
                t2 = {}
                table.insert(t2,t[k_])
                t[k_] = t2
            else
                t2 = t[k_]
            end
            table.insert(t2,v_)
        else
            t[k_] = v_
        end
    end
    return t
end

local function find_datadir(name)
    local list = vlc.config.datadir_list(name)
    for _, l in ipairs(list) do
        local s = vlc.net.stat(l)
        if s then
            return l
        end
    end
    error("Unable to find the `"..name.."' directory.")
end
http_dir = config.dir or find_datadir("http")

do
    local oldpath = package.path
    package.path = http_dir.."/?.lua"
    local ok, err = pcall(require,"custom")
    if not ok then
        vlc.msg.warn("Couldn't load "..http_dir.."/custom.lua",err)
    else
        vlc.msg.dbg("Loaded "..http_dir.."/custom.lua")
    end
    package.path = oldpath
end
files = {}
local function load_dir(dir,root)
    local root = root or "/"
    local has_index = false
    local d = vlc.net.opendir(dir)
    for _,f in ipairs(d) do
        if not string.match(f,"^%.") then
            local s = vlc.net.stat(dir.."/"..f)
            if s.type == "file" then
                local url
                if f == "index.html" then
                    url = root
                    has_index = true
                else
                    url = root..f
                end
                local ext = string.match(f,"%.([^%.]-)$")
                local mime = mimes[ext]
                -- print(url,mime)
                if mime and string.match(mime,"^text/") then
                    table.insert(files,file(h,dir.."/"..f,url,mime))
                else
                    table.insert(files,rawfile(h,dir.."/"..f,url))
                end
            elseif s.type == "dir" then
                load_dir(dir.."/"..f,root..f.."/")
            end
        end
    end
    if not has_index and not config.no_index then
        -- print("Adding index for", root)
        table.insert(files,dirlisting(root,d))
    end
end

if config.host then
    vlc.msg.err("\""..config.host.."\" HTTP host ignored")
    local port = string.match(config.host, ":(%d+)[^]]*$")
    vlc.msg.info("Pass --http-host=IP "..(port and "and --http-port="..port.." " or "").."on the command line instead.")
end

password = vlc.var.inherit(nil,"http-password")

h = vlc.httpd()
load_dir( http_dir )
a = h:handler("/art",nil,password,callback_art,nil)
c = h:handler("/check",nil,password,callback_check,nil)
m = h:handler("/get",nil,password,callback_get,nil)
