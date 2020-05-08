package ru.ps.vlcatv.remote.gui.activity;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

import ru.ps.vlcatv.remote.AppMain;
import ru.ps.vlcatv.remote.BuildConfig;
import ru.ps.vlcatv.remote.R;
import ru.ps.vlcatv.remote.data.SettingsInterface;
import ru.ps.vlcatv.remote.Utils;
import ru.ps.vlcatv.remote.data.DataSharedControl;
import ru.ps.vlcatv.remote.databinding.ActivityMainBinding;
import ru.ps.vlcatv.remote.gui.FragmentManageAdapter;
import ru.ps.vlcatv.remote.gui.fragment.ErrorFragment;
import ru.ps.vlcatv.remote.gui.fragment.PlayHistoryFragment;
import ru.ps.vlcatv.remote.gui.fragment.PlayInfoFragment;
import ru.ps.vlcatv.remote.gui.fragment.PlayTitleFragment;
import ru.ps.vlcatv.remote.gui.fragment.SettingsFragment;

public class AppMainActivity extends FragmentActivity implements SettingsInterface {

    private static final String TAG = AppMainActivity.class.getSimpleName();

    private FragmentManageAdapter m_pagerAdapter;
    private static Timer m_timer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            AppMain.setActivity(this);
            ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
            binding.setStatus(AppMain.getStatus());

            AppMain.getSettings().setCallbackChanged(this);
            AppMain.getStatus().setCallbackPlayStateChanged(this);

            m_pagerAdapter = new FragmentManageAdapter(getSupportFragmentManager());
            m_pagerAdapter.add(new ErrorFragment(), getResources().getString(R.string.fragment_title_0));
            m_pagerAdapter.add(new PlayTitleFragment(), getResources().getString(R.string.fragment_title_1));
            m_pagerAdapter.add(new PlayInfoFragment(), getResources().getString(R.string.fragment_title_2));
            m_pagerAdapter.add(new SettingsFragment(), getResources().getString(R.string.fragment_title_3));
            m_pagerAdapter.add(new PlayHistoryFragment(), getResources().getString(R.string.fragment_title_4));

            m_timer = new Timer();
            m_timer.scheduleAtFixedRate(new statusTask(), 0, 5000);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e(TAG, e.getLocalizedMessage(), e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppMain.setActivity(this);

        if (m_timer != null) {
            try {
                m_timer.cancel();
                m_timer.purge();
                m_timer = null;
            } catch (Exception ignored) {}
        }
        try {
            m_timer = new Timer();
            m_timer.scheduleAtFixedRate(new statusTask(), 0, 5000);
        } catch (Exception ignored) {}
        AppMain.getRequestStatus();
    }
    @Override
    protected void onPause() {
        try {
            m_timer.cancel();
        } catch (Exception ignored) {}
        try {
            m_timer.purge();
        } catch (Exception ignored) {}
        m_timer = null;

        AppMain.setActivity(null);
        super.onPause();
    }
    @Override
    protected void onStop() {
        AppMain.setActivity(null);
        super.onStop();
    }
    @Override
    public void onSettingsChange() {
        event_FRAGMENT_TITLE();
    }
    @Override
    public void onPlayChange() {
        event_FRAGMENT_TITLE();
    }

    @Override
    public void onHistoryChange() {}

    private void event_FRAGMENT_TITLE() {

        if ((AppMain.getStatus().AppInfo.get()) ||
                (AppMain.getStatus().AppHistory.get()))
            return;

        boolean b = ((Utils.isempty(AppMain.getStatus().Title.get())) ||
                (AppMain.getStatus().PlayId.get() == -1));

        if (AppMain.getStatus().AppTitle.get()) {
            if (b) {
                m_pagerAdapter.removePageFragments(
                        new int[] { 1, 2 },
                        FragmentManageAdapter.TOP,
                        FragmentManageAdapter.UP);
            }
        } else if (!b) {
            m_pagerAdapter.replaceSinglePageFragment(
                    new int[]{1},
                    FragmentManageAdapter.TOP,
                    FragmentManageAdapter.DOWN);
        }
    }

    private void event_BTN_TITLE() {

        if ((Utils.isempty(AppMain.getStatus().Title.get())) ||
                (AppMain.getStatus().PlayId.get() == -1)) {

            m_pagerAdapter.removePageFragments(
                    new int[] { 1, 2 },
                    FragmentManageAdapter.TOP,
                    FragmentManageAdapter.UP);
            return;
        }
        if (!AppMain.getStatus().AppInfo.get()) {
            m_pagerAdapter.replaceClearPageFragments(
                    new int[] { 1, 2 },
                    FragmentManageAdapter.TOP,
                    FragmentManageAdapter.UP);
        } else {
            m_pagerAdapter.replaceClearPageFragments(
                    new int[] { 2, 1 },
                    FragmentManageAdapter.TOP,
                    FragmentManageAdapter.DOWN);
        }
    }

    private void event_BTN_SETUP() {

        if (!AppMain.getStatus().AppSetup.get()) {
            m_pagerAdapter.replaceSinglePageFragment(
                    new int[] { 3 },
                    FragmentManageAdapter.BOTTOM,
                    FragmentManageAdapter.UP);
        } else {
            m_pagerAdapter.removePageFragments(
                    new int[] { 3 },
                    FragmentManageAdapter.BOTTOM,
                    FragmentManageAdapter.DOWN);
        }
    }

    private void event_BTN_HISTORY() {

        if (AppMain.getStatus().AppHistory.get()) {

            m_pagerAdapter.removePageFragments(
                    new int[] { 4 },
                    FragmentManageAdapter.TOP,
                    FragmentManageAdapter.UP);
        } else {
            m_pagerAdapter.replaceSinglePageFragment(
                    new int[]{ 4 },
                    FragmentManageAdapter.TOP,
                    FragmentManageAdapter.DOWN);
        }
    }

    private void event_EVT_ERROR(String s) {

        if (Utils.isempty(s))
            return;

        if (!AppMain.getStatus().AppError.get()) {

            m_pagerAdapter.setFragmentTitle(0, s);
            m_pagerAdapter.replaceSinglePageFragment(
                    new int[]{0},
                    FragmentManageAdapter.BOTTOM,
                    FragmentManageAdapter.UP);
            m_pagerAdapter.removePageFragments(
                    new int[]{0},
                    FragmentManageAdapter.BOTTOM,
                    FragmentManageAdapter.DOWN,
                    true);
            AppMain.getStatus().AppError.set(true);

        } else {

            m_pagerAdapter.setFragmentTitle(0, s);
            m_pagerAdapter.removePageFragments(
                    new int[]{0},
                    FragmentManageAdapter.BOTTOM,
                    FragmentManageAdapter.DOWN,
                    true);
            AppMain.getStatus().AppError.set(true);
        }
    }

    private void event_BTN_ERROR(String s) {
        if (AppMain.getStatus().AppError.get()) {
            m_pagerAdapter.removePageFragments(
                    new int[] { 0 },
                    FragmentManageAdapter.BOTTOM,
                    FragmentManageAdapter.DOWN);
            AppMain.getStatus().AppError.set(false);
        }
    }

    public void OnClickBtn(View view) {
        OnClickBtn(view.getId(), null);
    }
    public void OnClickBtn(int id, String s) {
        if (BuildConfig.DEBUG) Log.e("AppMainActivity","id=" + id);
        switch (id)
        {
            case DataSharedControl.GET_STAT: {
                AppMain.getRequestStatus();
                break;
            }
            case DataSharedControl.BTN_SETUP:
            case DataSharedControl.BTN_SETUPD: {
                event_BTN_SETUP();
                break;
            }
            case DataSharedControl.BTN_ERROR:
            case DataSharedControl.BTN_ERRORD: {
                event_BTN_ERROR(s);
                break;
            }
            case DataSharedControl.BTN_ERRORE: {
                event_EVT_ERROR(s);
                break;
            }
            case DataSharedControl.BTN_TITLE: {
                event_BTN_TITLE();
                break;
            }
            case DataSharedControl.BTN_HISTORY_BACK:
            case DataSharedControl.BTN_HISTORY: {
                event_BTN_HISTORY();
                break;
            }
            default: {
                if (id == DataSharedControl.BTN_HOME) {
                    AppMain.getStatus().AppRun.set(
                            !AppMain.getStatus().AppRun.get()
                    );

                } else if (id == DataSharedControl.BTN_PLAY) {
                    if (AppMain.getStatus().checkPlayState(KeyEvent.KEYCODE_MEDIA_PLAY))
                        AppMain.getStatus().PlayState.set(KeyEvent.KEYCODE_MEDIA_PAUSE);

                } else if (id == DataSharedControl.BTN_PAUSE) {
                    if (AppMain.getStatus().checkPlayState(KeyEvent.KEYCODE_MEDIA_PAUSE))
                        AppMain.getStatus().PlayState.set(KeyEvent.KEYCODE_MEDIA_PLAY);

                } else if (id == DataSharedControl.BTN_STOP) {
                    AppMain.getStatus().PlayState.set(KeyEvent.KEYCODE_MEDIA_STOP);
                }
                String cmd = AppMain.getStatus().getCtrlCmd(id);
                if (!Utils.isempty(cmd)) {
                    AppMain.getRequest(cmd);
                    AppMain.getStatus().clickStateChange();
                }
                break;
            }
        }
    }

    private static final class statusTask extends TimerTask {

        @Override
        public void run() {
            try {
                if (!AppMain.getSettings().isempty()) {
                    AppMain.getRequestStatus();
                    AppMain.getStatus().clickStateChange();
                }

            } catch (Exception e) {
                if (BuildConfig.DEBUG) AppMain.printError(e.getLocalizedMessage());
            }
        }
    }

}