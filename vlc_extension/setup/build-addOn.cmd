@echo off
luac51-32.exe -s -o ..\VLC\lua\intf\http.luac ..\lua\http.lua
E:\__Bin32\InnoSetup6\Compil32.exe /cc installAddOn.iss
