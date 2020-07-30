package ru.ps.vlcatv.utils.playlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ru.ps.vlcatv.utils.Text;
import ru.ps.vlcatv.utils.json.JSONArray;
import ru.ps.vlcatv.utils.json.JSONObject;
import ru.ps.vlcatv.utils.playlist.parse.ParseObject;

public class PlayListUtils {

    ///
    /// SET ITEM LIST

    private static <M> void idsInterfaceToList_(PlayListIdsInterface pif, List<M> dst, Class<M> clazz) {
        try {
            M clz = clazz.newInstance();
            PlayListIdsInterface plz = (PlayListIdsInterface) clz;
            plz.setValId(pif.getValId());
            plz.setTypeId(pif.getTypeId());
            dst.add(clz);
        } catch (Exception ignore) {}
    }

    public static <T> void setItemListSkip(T s, List<T> dst) {
        if (Text.isempty(
                ((PlayListParentTableInterface)s).getName()))
            return;

        boolean b = false;
        if (dst.size() > 0) {
            for (T ts : dst) {
                if (((PlayListParentTableInterface)ts).getName()
                        .equals(((PlayListParentTableInterface)s).getName())) {
                    b = true;
                    break;
                }
            }
        }
        if (!b)
            dst.add(s);
    }
    public static <T> void setItemListSkip(List<T> src, List<T> dst) {
        if (src.size() == 0) {
            return;
        }
        if (dst.size() == 0) {
            dst = src;
            return;
        }
        for (T ts1 : src) {
            boolean b = false;
            for (T ts2 : dst) {
                if (((PlayListParentTableInterface)ts2).getName()
                                .equals(((PlayListParentTableInterface)ts1).getName())) {
                    b = true;
                    break;
                }
            }
            if (!b)
                dst.add(ts1);
        }
    }
    public static <T> String getListRandom(List<T> list) {
        if (list.size() == 0)
            return "";
        try {
            Random rand = new Random();
            PlayListParentTableInterface ti = (PlayListParentTableInterface) list.get(
                    rand.nextInt(list.size())
            );
            return ti.getName();
        } catch (Exception ignore) {}
        return "";
    }
    public static String getStringRandom(List<String> list) {
        if (list.size() == 0)
            return "";
        try {
            Random rand = new Random();
            return list.get(
                    rand.nextInt(list.size())
            );
        } catch (Exception ignore) {}
        return "";
    }

    ///
    /// PlayList*Tables methods SET NEW

    public static List<PlayListActorsIndex> setNewActors(List<PlayListActors> src, List<PlayListActors> dst) {
        List<PlayListActorsIndex> list = new ArrayList<>();
        for (PlayListActors act1 : src) {
            boolean b = false;
            for (PlayListActors act2 : dst)
                if (act2.Name.equals(act1.Name)) {
                    list.add(new PlayListActorsIndex(act2.id));
                    b = true;
                    break;
                }
            if (!b) {
                act1.id = dst.size();
                dst.add(act1);
                list.add(new PlayListActorsIndex(act1.id));
            }
        }
        return list;
    }

    public static <T,M> List<T> setNewIndexList(List<String> src, List<M> dst, Class<M> clazz) {
        List<T> list = new ArrayList<>();
        for (String s : src) {
            boolean b = false;
            for (M d : dst) {
                try {
                    PlayListParentTableInterface ti = (PlayListParentTableInterface) d;
                    if (ti.equalsName(s)) {
                        list.add(ti.getNewClassIndex());
                        b = true;
                        break;
                    }
                } catch (Exception ignore) {}
            }
            if (!b) {
                try {
                    M clz = clazz.newInstance();
                    PlayListParentTableInterface ti = (PlayListParentTableInterface) clz;
                    long id = dst.size();
                    dst.add(ti.getNewClassInstance(s, id));
                    list.add(ti.getNewClassIndex(id));
                } catch (Exception ignore) {}
            }
        }
        return list;
    }

    ///
    /// PlayListIds Interface IDS

    public static <T> void setIdsReplace(T obj, List<T> dst) {
        if (dst.size() == 0) {
            dst.add(obj);
            return;
        }
        boolean b = false;
        for (T pi : dst) {
            if (((PlayListIdsInterface) pi).getTypeId().equals(
                    ((PlayListIdsInterface) obj).getTypeId())) {
                ((PlayListIdsInterface) pi).setValId(((PlayListIdsInterface) obj).getValId());
                b = true;
                break;
            }
        }
        if (!b)
            dst.add(obj);
    }
    public static <T> void setIdsSkip(T obj, List<T> dst) {
        if (dst.size() == 0) {
            dst.add(obj);
            return;
        }
        boolean b = false;
        for (T pi : dst) {
            if (((PlayListIdsInterface) pi).getTypeId().equals(
                    ((PlayListIdsInterface) obj).getTypeId())) {
                b = true;
                break;
            }
        }
        if (!b)
            dst.add(obj);
    }
    public static <T,M> void setIdsReplace(List<T> src, List<M> dst, Class<M> clazz) {
        if (src.size() == 0) {
            return;
        }
        if (dst.size() == 0) {
            for (T ids : src)
                idsInterfaceToList_((PlayListIdsInterface) ids, dst, clazz);
            return;
        }
        for (T ids1 : src) {
            boolean b = false;
            for (M ids2 : dst) {
                PlayListIdsInterface pif1 = (PlayListIdsInterface)ids1,
                                     pif2 = (PlayListIdsInterface)ids2;
                if (pif2.getTypeId().equals(pif1.getTypeId())) {
                    pif2.setValId(pif1.getValId());
                    b = true;
                    break;
                }
            }
            if (!b)
                idsInterfaceToList_((PlayListIdsInterface) ids1, dst, clazz);
        }
    }
    public static <T,M> void setIdsSkip(List<T> src, List<M> dst, Class<M> clazz) {
        if (src.size() == 0) {
            return;
        }
        if (dst.size() == 0) {
            for (T ids : src)
                idsInterfaceToList_((PlayListIdsInterface) ids, dst, clazz);
            return;
        }
        for (T ids1 : src) {
            boolean b = false;
            for (M ids2 : dst)
                if (((PlayListIdsInterface) ids2).getTypeId().equals(
                        ((PlayListIdsInterface) ids1).getTypeId())) {
                    b = true;
                    break;
                }
            if (!b)
                idsInterfaceToList_((PlayListIdsInterface) ids1, dst, clazz);
        }
    }
    public static <T> String getIdsString(String s, List<T> list) {
        for (T pi : list) {
            if (((PlayListIdsInterface) pi).getTypeId().equals(s)) {
                return ((PlayListIdsInterface) pi).getValId();
            }
        }
        return null;
    }
    public static <T> int getIdsInt(String s, List<T> list) {
        String ss = getIdsString(s, list);
        if (Text.isempty(ss))
            return -1;
        try {
            return Integer.parseInt(ss);
        } catch (Exception ignore) {}
        return -1;
    }
    public static <T> long getIdsLong(String s, List<T> list) {
        String ss = getIdsString(s, list);
        if (Text.isempty(ss))
            return -1L;
        try {
            return Long.parseLong(ss);
        } catch (Exception ignore) {}
        return -1L;
    }

    ////
    /// TO EDIT ITEMS

    public static void addEditList(JSONArray arr, PlayList list, PlayListObjectInterface plo) {
        if (arr == null)
            return;
        for (int i = 0; i < arr.length(); i++)
            addEditList_(arr.optJSONObject(i), list, plo);
    }
    private static void addEditList_(JSONObject obj, PlayList list, PlayListObjectInterface plo) {
        ParseObject po = new ParseObject(obj);
        if (po.itemType == PlayListConstant.TYPE_NONE)
            return;
        PlayListItemEdit ite = new PlayListItemEdit(
                plo.getDbIndex(), plo.getVlcId(), po.itemTitle, po.itemPremiered
        );
        list.itemsEdit.add(ite);
    }

    ////
    /// FIND

    public static PlayListItem findItemByVlcId(PlayListGroup group, long id) {
        if (group == null)
            return null;

        PlayListItem item;
        if ((item = findItemByVlcId__(group, id)) != null)
            return item;
        for (PlayListGroup grp : group.groups) {
            if ((item = findItemByVlcId__(grp, id)) != null)
                return item;
        }
        return null;
    }
    public static PlayListItem findItemByUrl(PlayListGroup group, String url) {
        if (group == null)
            return null;

        PlayListItem item;
        if ((item = findItemByUrl__(group, url)) != null)
            return item;
        for (PlayListGroup grp : group.groups) {
            if ((item = findItemByUrl__(grp, url)) != null)
                return item;
        }
        return null;
    }
    public static PlayListItem findItemByTitle(PlayListGroup group, String title) {
        if (group == null)
            return null;

        for (PlayListItem item : group.items) {
            String s = item.title.get();
            if ((!Text.isempty(s)) && (s.equals(title)))
                return item;
        }
        return null;
    }
    public static PlayListItem findItemByDbId(PlayListGroup group, long id) {
        if (group == null)
            return null;

        PlayListItem item;
        if ((item = findItemByDbId__(group, id)) != null)
            return item;
        for (PlayListGroup grp : group.groups) {
            if ((item = findItemByDbId__(grp, id)) != null)
                return item;
        }
        return null;
    }
    public static PlayListGroup findGroupByVlcId(PlayListGroup group, long id) {
        if (group == null)
            return null;

        PlayListGroup gr;
        if ((gr = findGroupByVlcId__(group, id)) != null)
            return gr;
        for (PlayListGroup grp : group.groups) {
            if ((gr = findGroupByVlcId__(grp, id)) != null)
                return gr;
        }
        return null;
    }
    public static PlayListGroup findGroupByItemVlcId(PlayListGroup group, long id) {
        if (group == null)
            return null;

        PlayListGroup gr;
        if ((gr = findGroupByItemVlcId_(group, id)) != null)
            return gr;
        for (PlayListGroup grp : group.groups) {
            if ((gr = findGroupByItemVlcId_(grp, id)) != null)
                return gr;
        }
        return null;
    }

    /// FIND private

    private static PlayListItem findItemByVlcId__(PlayListGroup grp, long id) {

        for (PlayListItem pli : grp.items)
            if (pli.getVlcId() == id)
                return pli;

        for (PlayListGroup gr : grp.groups) {
            PlayListItem pli;
            if ((pli = findItemByVlcId__(gr, id)) != null)
                return pli;
        }
        return null;
    }
    private static PlayListItem findItemByDbId__(PlayListGroup grp, long id) {

        for (PlayListItem pli : grp.items)
            if (pli.dbIndex == id)
                return pli;

        for (PlayListGroup gr : grp.groups) {
            PlayListItem pli;
            if ((pli = findItemByVlcId__(gr, id)) != null)
                return pli;
        }
        return null;
    }
    private static PlayListItem findItemByUrl__(PlayListGroup grp, String url) {
        for (PlayListItem pli : grp.items)
            if (pli.uri.equals(url))
                return pli;

        for (PlayListGroup gr : grp.groups) {
            PlayListItem pli;
            if ((pli = findItemByUrl__(gr, url)) != null)
                return pli;
        }
        return null;
    }
    private static PlayListGroup findGroupByVlcId__(PlayListGroup grp, long id) {

        if (grp.getVlcId() == id)
            return grp;

        for (PlayListGroup gr : grp.groups) {
            if (gr.getVlcId() == id)
                return gr;
            PlayListGroup grs;
            if ((grs = findGroupByVlcId__(gr, id)) != null)
                return grs;
        }
        return null;
    }
    private static PlayListGroup findGroupByItemVlcId_(PlayListGroup grp, long id) {
        if (grp == null)
            return null;

        if (grp.items.size() > 0)
            for (PlayListItem item : grp.items)
                if (item.getVlcId() == id)
                    return grp;

        for (PlayListGroup gr : grp.groups) {
            PlayListGroup grr;
            if ((grr = findGroupByItemVlcId_(gr, id)) != null)
                return grr;
        }
        return null;
    }

    ////
    /// UTILS

    public static ParseObject parseObject(final JSONObject obj) {
        if (obj == null)
            return null;
        final ParseObject po = new ParseObject(obj);
        if (po.itemType == PlayListConstant.TYPE_NONE)
            return null;
        return po;
    }

}
