package ru.ps.vlcatv.remote.gui;

import android.os.Handler;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import java.util.ArrayList;

import ru.ps.vlcatv.remote.AppMain;
import ru.ps.vlcatv.remote.BuildConfig;
import ru.ps.vlcatv.remote.R;
import ru.ps.vlcatv.remote.gui.fragment.FragmentInterface;

public class FragmentManageAdapter {

    private static final String TAG = FragmentManageAdapter.class.getSimpleName();
    public static final int TOP = 10001;
    public static final int BOTTOM = 10002;
    public static final int UP = 10001;
    public static final int DOWN = 10002;

    private static final class DataFragment {
        String   title;
        Fragment fragment;
        DataFragment(Fragment f, String s) {
            fragment = f;
            title = s;
        }
    }
    private ArrayList<DataFragment> m_fragments = new ArrayList<DataFragment>();
    private FragmentManager m_fragmentMgr;
    private Handler m_handler = new Handler();
    private Runnable m_runnable = null;



    public FragmentManageAdapter(FragmentManager m) {
        m_fragmentMgr = m;
    }

    public void add(Fragment f, String s)
    {
        for (DataFragment data : m_fragments)
            if (data.fragment.equals(f))
                return;
        DataFragment d = new DataFragment(f, s);
        m_fragments.add(d);
    }
    public void clean()
    {
        m_fragments.clear();
    }
    public int getCount() {
        return m_fragments.size();
    }
    public Fragment getItem(final int pos) {
        if (pos >= m_fragments.size())
            return null;
        return m_fragments.get(pos).fragment;
    }
    public String getPageTitle(final int pos) {
        if (pos >= m_fragments.size())
            return null;
        return m_fragments.get(pos).title;
    }
    public void setFragmentTitle(final int pos, String s) {
        if (pos >= m_fragments.size())
            return;
        FragmentInterface fi = (FragmentInterface) m_fragments.get(pos).fragment;
        if (fi != null)
            fi.setTitle(s);
    }

    private int[] directionFragment(int orientation, int direction) {
        switch (orientation) {
            case TOP: {
                return new int[] {
                        R.id.fragment_top,
                        (direction == UP) ? R.anim.slide_out_down : R.anim.slide_in_up,
                        (direction == UP) ? R.anim.slide_in_down : R.anim.slide_out_up,
                        (direction == UP) ? R.anim.slide_in_up : R.anim.slide_out_down,
                        (direction == UP) ? R.anim.slide_out_up : R.anim.slide_in_down
                };
            }
            case BOTTOM: {
                return new int[] {
                        R.id.fragment_bottom,
                        (direction == UP) ? R.anim.slide_in_up : R.anim.slide_out_down,
                        (direction == UP) ? R.anim.slide_out_up : R.anim.slide_in_down,
                        (direction == UP) ? R.anim.slide_in_up : R.anim.slide_in_up,
                        (direction == UP) ? R.anim.slide_out_up : R.anim.slide_out_up
                };
            }
            default:
                return null;
        }
    }

    public void replaceSinglePageFragment(final int[] idx, int orientation, int direction) {
        try {
            if (idx[0] >= m_fragments.size())
                return;

            int[] id = directionFragment(orientation, direction);
            if (id == null)
                return;

            m_fragmentMgr
                    .beginTransaction()
                    .setCustomAnimations(id[1], id[2], id[3], id[4])
                    .replace(id[0], m_fragments.get(idx[0]).fragment, m_fragments.get(idx[0]).title)
                    .commit();

        } catch (Exception e) {
            if (BuildConfig.DEBUG) AppMain.printError(e.getLocalizedMessage());
        }
    }
    public void replaceClearPageFragments(final int[] idx, int orientation, int direction) {
        try {

            if ((idx[0] >= m_fragments.size()) || (idx[1] >= m_fragments.size()))
                return;

            int[] id = directionFragment(orientation, direction);
            if (id == null)
                return;

            m_fragmentMgr
                    .beginTransaction()
                    .remove(m_fragments.get(idx[0]).fragment)
                    .runOnCommit(new Runnable() {
                        @Override
                        public void run() {
                            m_fragmentMgr
                                    .beginTransaction()
                                    .setCustomAnimations(id[1], id[2], id[3], id[4])
                                    .replace(id[0], m_fragments.get(idx[1]).fragment, m_fragments.get(idx[1]).title)
                                    .commit();
                        }
                    })
                    .commit();

        } catch (Exception e) {
            if (BuildConfig.DEBUG) AppMain.printError(e.getLocalizedMessage());
        }
    }
    public void removePageFragments(final int[] idx, final int orientation, final int direction) {
        removePageFragments(idx, orientation, direction, false);
    }
    public void removePageFragments(final int[] idx, final int orientation, final int direction, boolean isClear) {

        try {
            if ((idx.length == 1) && (isClear))
            {
                if (m_runnable != null)
                    m_handler.removeCallbacks(m_runnable);
                m_runnable = new Runnable() {
                    @Override
                    public void run() {
                        removePageFragments(
                                new int[]{ idx[0] },
                                orientation,
                                direction);
                        if (idx[0] == 0)
                            AppMain.getStatus().AppError.set(false);
                        m_runnable = null;
                    }
                };
                m_handler.postDelayed(m_runnable, 15000);
                return;
            }

            for (int pos : idx) {
                if (pos >= m_fragments.size())
                    continue;
                int[] id = directionFragment(orientation, direction);
                if (id == null)
                    continue;
                m_fragmentMgr
                        .beginTransaction()
                        .remove(m_fragments.get(pos).fragment)
                        .commit();
            }

        } catch (Exception e) {
            if (BuildConfig.DEBUG) AppMain.printError(e.getLocalizedMessage());
        }
    }
}

