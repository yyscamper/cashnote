package com.yyscamper.cashnote.Storage;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by yuanf on 2014-04-07.
 */
public class TeamStoreManager {
    private Hashtable<String, TeamStore> mAllTeamStores;
    private TeamStoreManager mInstance;
    private TeamStore mCurrTeamStore;

    private TeamStoreManager() {
        mAllTeamStores = new Hashtable<String, TeamStore>();
    }

    public TeamStoreManager getInstance() {
        if (mInstance ==  null) {
            mInstance = new TeamStoreManager();
        }
        return mInstance;
    }

    public void init() {
        TeamStore ts1 = new TeamStore("sandbox");
        TeamStore ts2 = new TeamStore("UFE");
        TeamStore ts3 = new TeamStore("GPS");
        TeamStore ts4 = new TeamStore("GHTS");

        add(ts1);
        add(ts2);
        add(ts3);
        add(ts4);
    }

    public void sync() {
        ArrayList<TeamStore> syncResult = null;

        syncResult.add(new TeamStore("sandbox"));
        syncResult.add(new TeamStore("GPS"));
        syncResult.add(new TeamStore("UFE"));
        syncResult.add(new TeamStore("GHTS"));
        syncResult.add(new TeamStore("GTAT"));
        syncResult.add(new TeamStore("Logic"));
        syncResult.add(new TeamStore("USD"));

        mAllTeamStores.clear();
        for (TeamStore ts : syncResult) {
            mAllTeamStores.put(ts.getName(), ts);
        }
    }

    public void add(TeamStore ts) {
        if (ts != null && !mAllTeamStores.containsKey(ts.getName()))
            mAllTeamStores.put(ts.getName(), ts);
    }

    public void add(String name) {
        if (name != null && !mAllTeamStores.containsKey(name)) {
            mAllTeamStores.put(name, new TeamStore(name));
        }
    }

    public void remove(String name) {
        if (name != null && mAllTeamStores.containsKey(name)) {
            mAllTeamStores.remove(name);
        }
    }

    public String[] getAllTeamStoreNames() {
        String[] arr = new String[mAllTeamStores.size()];
        int j = 0;
        for (String str : mAllTeamStores.keySet()) {
            arr[j++] = str;
        }
        return arr;
    }

    public TeamStore getCurrTeamStore() {
        return mCurrTeamStore;
    }

    public String getCurrTeamStoreName() {
        return mCurrTeamStore.getName();
    }

    public void selectTeamStore(String name) {
        if (name != null && mAllTeamStores.containsKey(name)) {
            mCurrTeamStore = mAllTeamStores.get(name);
        }
    }



}
