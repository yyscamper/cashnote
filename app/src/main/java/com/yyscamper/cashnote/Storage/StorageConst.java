package com.yyscamper.cashnote.Storage;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by yuanf on 2014-04-07.
 */
public class StorageConst {
    /** KEY DEFINITION **/
    //Data type
    public static final int DATA_TYPE_PERSON = 1;
    public static final int DATA_TYPE_HISTORY = 2;
    public static final int DATA_TYPE_LOCATION = 3;
    public static final int DATA_TYPE_LOCATION_GROUP = 4;
    public static final int DATA_TYPE_DB_PARAM = 5;
    public static final int DATA_TYPE_GLOBAL_PARAM = 6;

    //KEYs that common for all data types
    public static final String KEY_DATA_TYPE = "data_type";
    public static final String KEY_DB_NAME = "db_name";
    public static final String KEY_LAST_MODIFIY_TIME = "last_modify_time";
    public static final String KEY_STATUS = "status";

    //KEYs for Pay History
    public static final String KEY_PAY_MONEY = "money";
    public static final String KEY_PAY_PAYER_NAME = "payer_name";
    public static final String KEY_PAY_ATTENDS = "attends";
    public static final String KEY_PAY_LOCATION = "location";
    public static final String KEY_PAY_TIME = "time";
    public static final String KEY_PAY_DESCRIPTION = "description";
    public static final String KEY_PAY_UUID = "uuid";
    public static final String KEY_PAY_TYPE = "type";

    //KEYs for Pay Persons
    public static final String KEY_PERSON_NAME = "name";
    public static final String KEY_PERSON_ATTEND_COUNT = "attend_count";
    public static final String KEY_PERSON_PAY_COUNT = "pay_count";
    public static final String KEY_PERSON_BALANCE = "blance";
    public static final String KEY_PERSON_EMAIL = "email";
    public static final String KEY_PERSON_PHONE ="phone";

    //KEYs for Pay Location
    public static final String KEY_LOCATION_NAME = "name";
    public static final String KEY_LOCATION_ATTEND_COUNT = "attend_account";
    public static final String KEY_LOCATION_LAST_ATTEND_TIME = "last_attend_time";

    //KEYs for selected DB parameters
    public static final String KEY_PARAM_ID = "id";
    public static final String KEY_PARAM_LAST_MODIFIED_TIME_FOR_PAY_HISTORY = "last_modify_time_for_pay_history";
    public static final String KEY_PARAM_LAST_MODIFIED_TIME_FOR_PAY_PERSONS = "last_modify_time_for_pay_persons";
    public static final String KEY_PARAM_LAST_MODIFIED_TIME_FOR_PAY_LOCATIONS = "last_modify_time_for_pay_locations";
    public static final String KEY_PARAM_STATUS = "status";
    public static final String KEY_PARAM_LAST_MODIFIED_TIME = "last_modify_time";

    //KEYs for global all DBs
    public static final String KEY_GLOBAL_ALL_DB_NAMES = "all_db_names";

    //KEYs for location groups
    public static final String KEY_LOCATION_GROUP_NAME = "group_name";
    public static final String KEY_LOCATION_GROUP_CHILDREN = "children";
}
