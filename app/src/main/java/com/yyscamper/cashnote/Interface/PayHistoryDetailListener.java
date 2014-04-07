package com.yyscamper.cashnote.Interface;

import com.yyscamper.cashnote.PayType.PayHistory;

/**
 * Created by yuanf on 2014-04-05.
 */
public interface PayHistoryDetailListener {
    public static final int ACTION_NONE = 0;
    public static final int ACTION_ADDE = 1;
    public static final int ACTION_MODIFIED = 2;
    public static final int ACTION_DELETED = 3;
    public void OnPayHistoryChanged(int action, PayHistory oldHistory, PayHistory newHistory);
}
