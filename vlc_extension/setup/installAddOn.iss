#pragma include __INCLUDE__ + ";" + ReadReg(HKLM, "Software\Mitrich Software\Inno Download Plugin", "InstallDir")

#include <idp.iss>
#include <idplang/Russian.iss>

[Setup]
AppId={{DEF043E7-2A46-40C7-B41B-524D921030F8}
AppName=VlcAaddOnAnroidTvRemote
AppVersion=1.2.0
AppVerName="VLC AddOn Anroid-TV remote - Play List media extension"
AppPublisher=PS
AppPublisherURL=https://clonetv.github.io/VLC-TV-Remote/
AppSupportURL=https://clonetv.github.io/VLC-TV-Remote/
AppUpdatesURL=https://clonetv.github.io/VLC-TV-Remote/
WizardStyle=modern
ShowLanguageDialog=auto
CreateAppDir=no
DisableDirPage=yes
DefaultDirName={code:GetVlcIntallationPath}

DefaultGroupName=VLC PlayList AddOn
AlwaysShowDirOnReadyPage=yes
Compression=lzma
SolidCompression=yes
ArchitecturesInstallIn64BitMode=x64
OutputDir=..\..\docs\release
OutputBaseFilename=VLCAddOnMediaPlayList

[Languages]
Name: "en"; MessagesFile: "compiler:Default.isl"
Name: "ru"; MessagesFile: "compiler:Languages\Russian.isl"

[CustomMessages]
notVlcDescription = VLC installation path not found! Exit
addOnDescription = VLC PlayList AddOn
avlcrTaskDescription = VLC Android-TV remote (APK)
tinymmDescription = Tiny Media Manager (3.1.4)
ru.notVlcDescription = путь к установленному VLC плееру не найден! инсалл€ци€ невозможна
ru.addOnDescription = VLC —писок воспроизведени€ (AddOn)
ru.avlcrTaskDescription = VLC Android-TV пульт (APK)

[Types]
Name: "full"; Description: "{cm:addOnDescription} installation"
Name: "custom"; Description: "{cm:addOnDescription} + {cm:avlcrTaskDescription} installation"
Name: "extended"; Description: "{cm:addOnDescription} + {cm:avlcrTaskDescription} + {cm:tinymmDescription} installation";

[Components]
Name: "luascript"; Description: "{cm:addOnDescription}";  Types: full custom extended; Flags: fixed
Name: "apkfile"; Description: "{cm:avlcrTaskDescription}";  Types: custom extended;
Name: "tinymm"; Description: "{cm:tinymmDescription}"; Types: extended;

[Files]
Source: "..\VLC\*"; Check: CheckVlcIntallationPath; DestDir: "{code:GetVlcIntallationPath}"; Flags: ignoreversion recursesubdirs createallsubdirs; Components: luascript;
Source: "..\avlctv-remote-install.cmd"; Check: CheckVlcIntallationPath; DestDir: "{code:GetVlcIntallationPath}\AVLCTVR"; Flags: ignoreversion; Components: apkfile;
Source: "{tmp}\avlctv-remote-release.apk"; Check: GetAvlctvRemote; DestDir: "{tmp}"; Flags: external deleteafterinstall; Components: apkfile;
Source: "{tmp}\tmm_3.1.4_win.zip"; Check: GetTinymediamanager; DestDir: "{tmp}"; Flags: external deleteafterinstall; Components: tinymm;

[Run]
Filename: "cmd.exe"; Parameters: "/C move ""{tmp}\avlctv-remote-release.apk"" ""{code:GetVlcIntallationPath}\AVLCTVR"""; WorkingDir: {tmp}; Components: apkfile;
Filename: "cmd.exe"; Parameters: "/C move ""{tmp}\tmm_3.1.4_win.zip"" ""{code:GetVlcIntallationPath}\AVLCTVR"""; WorkingDir: {tmp}; Components: tinymm;


[Code]
var
  vlcDir: string;

function GetVlcIntallationPath(Param: string): string;
begin
  Result := vlcDir;
end;

function CheckVlcIntallationPath(): Boolean;
var
  len: Integer;

  begin
  len := Length(vlcDir);
  Result := len > 0;
end;

function InitializeSetup: Boolean;
var
  valDir: string;

begin

  Result := False;
  if RegQueryStringValue(HKLM, 'SOFTWARE\VideoLAN\VLC', 'InstallDir', valDir) then
  begin
      Result := True;
      vlcDir := valDir;
  end;

  if not Result then
  begin
    if RegQueryStringValue(HKLM, 'SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\VLC media player', 'InstallLocation', valDir) then
    begin
      Result := True;
      vlcDir := valDir;
    end;
  end;

  if not Result then
  begin
    MsgBox(ExpandConstant('{cm:notVlcDescription}'), mbCriticalError, MB_OK);
  end;

end;

function GetAvlctvRemote(): Boolean;
begin
  Result := idpDownloadFile('https://github.com/CloneTV/VLC-TV-Remote/releases/download/1.0/avlctv-remote-release.apk', ExpandConstant('{tmp}\avlctv-remote-release.apk'));
end;

function GetTinymediamanager(): Boolean;
begin
  Result := idpDownloadFile('https://release.tinymediamanager.org/v3/dist/tmm_3.1.4_win.zip', ExpandConstant('{tmp}\tmm_3.1.4_win.zip'));
end;

