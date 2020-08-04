package ru.ps.vlcatv.utils.playlist;

import androidx.annotation.Keep;

import ru.ps.vlcatv.constanttag.DataTagParse;
import ru.ps.vlcatv.constanttag.DataTagVlcStatus;
import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.annotation.IFieldReflect;

@Keep
public class PlayListExportStatus extends ReflectAttribute {

    PlayListExportStatus() {}
    public PlayListExportStatus(PlayStatusInterface psi) {
        playId = psi.getPlayId();
        title = psi.getTitle();
        duration = psi.getPlayTotal();
        lastPos = psi.getPlayPosition();
        audioVolume = psi.getAudioVolume();
        playState = psi.getPlayState();
        isVlc = psi.isVlcEnable();
        playIsRepeat = psi.getPlayIsRepeat();
        playIsLoop = psi.getPlayIsLoop();
        playIsRandom = psi.getPlayIsRandom();
        playIsFullscreen = psi.getPlayIsFullscreen();
        type = psi.getPlayType();
    }

    @IFieldReflect(DataTagParse.TAG_TITLE)
    public String title = null;

    @IFieldReflect(DataTagVlcStatus.TAG_PLAYID)
    public long playId = -1L;
    @IFieldReflect(DataTagParse.TAG_DURATION)
    public long duration = -1;
    @IFieldReflect(DataTagParse.TAG_POSITION)
    public long lastPos = -1;

    @IFieldReflect(DataTagParse.TAG_TYPE)
    public int type = -1;
    @IFieldReflect(DataTagVlcStatus.TAG_VOLUME)
    public int audioVolume = 0;
    @IFieldReflect(DataTagVlcStatus.TAG_STATE)
    public int playState = -1;
    @IFieldReflect(DataTagVlcStatus.TAG_VLCVER)
    public boolean isVlc = false;
    @IFieldReflect(DataTagVlcStatus.TAG_REPEAT)
    public boolean playIsRepeat = false;
    @IFieldReflect(DataTagVlcStatus.TAG_LOOP)
    public boolean playIsLoop = false;
    @IFieldReflect(DataTagVlcStatus.TAG_RANDOM)
    public boolean playIsRandom = false;
    @IFieldReflect(DataTagVlcStatus.TAG_FULLSCREEN)
    public boolean playIsFullscreen = false;
}
