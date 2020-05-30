package ru.ps.vlcatv.utils.playlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ru.ps.vlcatv.utils.Text;
import ru.ps.vlcatv.utils.json.JSONArray;
import ru.ps.vlcatv.utils.json.JSONObject;
import ru.ps.vlcatv.utils.playlist.parse.ParseObject;

public class PlayListUtils {

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
    /// PlayList*Tables methods

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
    /// PlayListIds Interface

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

}
