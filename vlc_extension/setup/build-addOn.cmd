@echo off
luac53.exe -s -o ..\VLC\lua\meta\art\04_nfo.luac ..\lua\04_nfo.lua
luac53.exe -s -o ..\VLC\lua\intf\http.luac ..\lua\http.lua
E:\__Bin32\InnoSetup6\Compil32.exe /cc installAddOn.iss
