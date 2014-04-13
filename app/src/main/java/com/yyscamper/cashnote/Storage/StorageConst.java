package com.yyscamper.cashnote.Storage;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by yuanf on 2014-04-07.
 */
public class StorageConst {
    public static final String TABLE_HISTORY = "histories";
    public static final String TABLE_PERSON = "persons";
    public static final String TABLE_LOCATION = "locations";
    public static final String TABLE_ACCOUNT_BOOK = "account_book";
    public static final String TABLE_LOCATION_GROUP = "location_groups";

    /** KEY DEFINITION **/
    //KEYs that common for all data types
    public static final String KEY_KEY = "key";
    public static final String KEY_DATA_TYPE = "data_type";
    public static final String KEY_VERSION = "version";
    public static final String KEY_ACCOUNT_BOOK = "account_book";
    public static final String KEY_LAST_MODIFIY_TIME = "last_modify_time";
    public static final String KEY_CLOUD_STATUS = "cloud_status";
    public static final String KEY_CREATED_TIME = "created_time";
    public static final String KEY_LAST_MODIFY_USER_ID = "last_modify_user_id";
    public static final String KEY_CREATED_USER_ID = "created_user_id";
    public static final String KEY_LAST_SYNC_TIME = "last_sync_time";

    //KEYs for Pay History
    public static final String KEY_HISTORY_MONEY = "money";
    public static final String KEY_HISTORY_PAYER_NAME = "payer_name";
    public static final String KEY_HISTORY_ATTENDS = "attends";
    public static final String KEY_HISTORY_LOCATION = "location";
    public static final String KEY_HISTORY_TIME = "time";
    public static final String KEY_HISTORY_DESCRIPTION = "description";
    public static final String KEY_HISTORY_TYPE = "history_type";

    //KEYs for Pay Persons
    public static final String KEY_PERSON_ATTEND_COUNT = "attend_count";
    public static final String KEY_PERSON_PAY_COUNT = "pay_count";
    public static final String KEY_PERSON_BALANCE = "blance";
    public static final String KEY_PERSON_EMAIL = "email";
    public static final String KEY_PERSON_PHONE ="phone";

    //KEYs for Pay Location
    public static final String KEY_LOCATION_ATTEND_COUNT = "attend_account";
    public static final String KEY_LOCATION_LAST_ATTEND_TIME = "last_attend_time";

    //KEYs for selected DB parameters

    //KEYs for global all DBs
    public static final String KEY_GLOBAL_ALL_DB_NAMES = "all_db_names";

    //KEYs for location groups
    public static final String KEY_LOCATION_GROUP_CHILDREN = "children";
}
