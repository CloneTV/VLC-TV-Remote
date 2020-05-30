package ru.ps.vlcatv.utils.playlist.parse;

import java.util.Scanner;

import ru.ps.vlcatv.utils.BuildConfig;
import ru.ps.vlcatv.utils.Log;
import ru.ps.vlcatv.utils.Text;
import ru.ps.vlcatv.utils.playlist.PlayList;
import ru.ps.vlcatv.utils.playlist.PlayListConstant;
import ru.ps.vlcatv.utils.playlist.PlayListEpg;
import ru.ps.vlcatv.utils.playlist.PlayListGroup;
import ru.ps.vlcatv.utils.playlist.PlayListItem;
import ru.ps.vlcatv.utils.playlist.PlayListItemUrls;

public class ParseM3UList {

    public static void parseM3u8(PlayList playList, String text, int idx) {
        Scanner scanner = null;
        try {
            if (Text.isempty(text))
                return;

            try {
                int cnt = 0;
                ParseObject po = null;
                scanner = new Scanner(text);
                String[] array = null;
                PlayListEpg epgList = new PlayListEpg(playList.getDbManager());

                while (scanner.hasNextLine()) {
                    String s = scanner.nextLine();
                    if (Text.isempty(s))
                        continue;
                    if ((array == null) || (cnt >= 5)) {
                        cnt = 0;
                        array = new String[5];
                    }
                    array[cnt++] = s;
                    if (s.startsWith("http")) {
                        po = new ParseObject(array, epgList);
                        if (po.itemType == PlayListConstant.TYPE_ONLINE) {

                            PlayListGroup grp = null;
                            switch (idx) {
                                case PlayList.IDX_ONLINE_TV: {
                                    if (po.playListType == PlayList.IDX_ONLINE_RADIO)
                                        grp = playList.groups.get(PlayList.IDX_ONLINE_RADIO);
                                    else if (po.playListType == PlayList.IDX_ONLINE_TV)
                                        grp = playList.groups.get(PlayList.IDX_ONLINE_TV);
                                    break;
                                }
                                case PlayList.IDX_ONLINE_RADIO: {
                                    grp = playList.groups.get(PlayList.IDX_ONLINE_RADIO);
                                    break;
                                }
                                case PlayList.IDX_ONLINE_FILMS: {
                                    grp = playList.groups.get(PlayList.IDX_ONLINE_FILMS);
                                    break;
                                }
                            }
                            if (grp != null) {
                                PlayListItem item = playList.findItemByTitle(po.itemTitle, grp);
                                if (item != null) {
                                    boolean b = false;
                                    for (PlayListItemUrls purl : item.urls)
                                        if (purl.url.equals(po.itemUri)) {
                                            b = true;
                                            break;
                                        }
                                    if (!b)
                                        item.urls.add(new PlayListItemUrls(po.itemUri));
                                } else {
                                    item = new PlayListItem();
                                    item.copy(po);
                                    grp.items.add(item);
                                }
                            }
                        }
                        cnt = 0;
                        array = null;
                    }
                }
            } finally {
                if (scanner != null)
                    scanner.close();
            }

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("-- m3u8 parser", Text.requireString(e.getLocalizedMessage()), e);
        }
    }
}
