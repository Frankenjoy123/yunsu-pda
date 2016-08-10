package com.yunsoo.util;

public final class Constants {

	
	public final static class App
	{
		public final static String APP_VERSION = "1.0.0";
		
	}

	public final static class Favor
	{
		public static final String KANGCAI="KangCai";
	}

	public final static class Logistic
	{
		public final static String INBOUND_CODE = "inbound";
		public final static String OUTBOUND_CODE = "outbound";
		public final static String INBOUND = "入库";
		public final static String OUTBOUND = "出库";
		public static final String AGENCY_ID="agency_id";
		public static final String AGENCY_NAME="agency_name";
		public static final String REVOKE_INBOUND="撤销入库";
		public static final String REVOKE_OUTBOUND="撤销出库";
		public static final int LIMIT_ITEM=1000;

		public static final String ACTION_ID="action_id";
		public static final String ACTION_NAME="action_name";

	}

	public final static class Key
	{
		public final static String KEY_STATUS="key_status";
		public final static String NOT_EXIST="not_exist";
	}

	public final static class DB
	{
		public final static String PATH_TABLE="path";
		public final static String PACK_TABLE="Pack";
		public final static String AGENCY_COLUMN="agency";
		public final static String ACTION_ID_COLUMN="action_id";
		public final static String PACK_KEY_COLUMN="pack_key";
		public final static String TIME_COLUMN="save_time";
		public final static String STATUS_COLUMN="status";
		public final static String SYNC="sync";
		public final static String NOT_SYNC="not_sync";
		public final static String DISABLE="disable";
		public static final String  DB_NAME ="trace" ;
	}
	
	public final static class Cache {
		public final static String CACHE_SDCARD_IMAGE_PATH = "/image";
		public final static String CACHE_SDCARD_DATA_PATH = "/data";
		public final static String CACHE_SDCARD_DOWNLOAD_PATH = "/download";
	}

	public final static class Preference {
		public final static String PREF_LOGIN = "login";

		public final static String SETTING="setting";
		public final static String SYNC_RATE="sync_rate";

        public final static String PREF_LOGISTIC="logistic";
        public final static String LOGISTIC_ACTION="logistic_action";
		public final static String LOGISTIC_ORG_AGENCY="organization_agency";

        public final static String PREF_FILE="file_in";
        public final static String PACK_FILE_LAST_INDEX="pack_file_index";
        public final static String PATH_FILE_LAST_INDEX="path_file_index";

        public final static String PREF_SQLITE="sqLite_in";
        public final static String SQ_PACK_LAST_ID ="sqLite_pack_last_id";

        public final static String SQ_PATH_LAST_ID ="sqLite_path_last_id";

		// json String
		public final static String KEY_USER_INFO = "user_info";
		
		public final static String PREF_ENTERPRISE_LOGIN = "enterprise_login";
		
		public final static String KEY_ENTERPRISE_ACCOUNT_INFO = "account_info";
		
		public static final String PREF_LAST_KNOWN_LOCATION = "location";
		public static final String KEY_LAST_LOCATION = "location_info";
		
		public final static String PREF_DEVICE_INFO = "device_in";
		public static final String KEY_DEVICE_INFO = "device_info";
		
		public final static String PREF_FOLLOWING = "manufacturers";
		public final static String KEY_FOLLOWING_DATA = "manufacturer_data";
		public final static String PREF_ORG = "org";
		public final static String KEY_LAST_MESSAGE_ID = "manufacturer_data";
	}

	public final static class Request {

		public static final String LastModified = "Last-Modified";		
	}
	// we need / slash
	public static final String SERVER_URL = "http://dev.yunsu.co:6080";
//	public static final String SERVER_URL = "http://api.test.yunsu.co:6080";
//    public static final String SERVER_URL = "http://enterprise.test.yunsu.co:9080";
	public static final String USER_AGREEMENT_URL = "http://t.m.yunsu.co/usercontract";

	public static final String EmptyString = "";
	public static final String QuestionMark = "?";
	public static final String NA = "N/A";

	public static final String ACCESS_TOKEN = "X-YS-AccessToken";
	public static final String APP_ID = "X-YS-AppId";
	public static final String APP_ID_VALUE="2mlvpegbfroxlyiej4y";
	public static final String DEVICE_ID = "X-YS-DeviceId";
	
	public static final String CODE_BASE_URL = "http://t.m.yunsu.co/";

    public static final String SQ_DATABASE="yunsu_pda";

    public static final String YUNSOO_FOLDERNAME = "/yunsu";

    public static final String PACK_SYNC_TASK_FOLDER = "/pack/sync_task";

    public static final String PACK_SYNC_SUCCESS_FOLDER="/pack/sync_success";

    public static final String PATH_SYNC_TASK_FOLDER = "/path/sync_task";

    public static final String PATH_SYNC_SUCCESS_FOLDER="/path/sync_success";

	public static final String PATH_LOG_NOT_SYNC_FOLDER ="/path/log/not_sync";

	public static final String PATH_LOG_SYNC_FOLDER ="/path/log/sync";

	public static final String PATH_CACHE_DB="/path/cache/db";

	public static final String BLANK="";

	public static final int SYNC_RATE=10;

	public static final String DEFAULT_STORAGE="default";

	public static final String TITLE="title";

	public static final boolean INIT_DATA=true;


}
