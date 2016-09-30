
package com.mymobkit.data;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.LOGI;
import static com.mymobkit.common.LogUtils.LOGW;
import static com.mymobkit.common.LogUtils.makeLogTag;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper singleton class to manage SQLiteDatabase Create and Restore
 */
public final class DatabaseUtils extends SQLiteOpenHelper implements IDbContentProvider {

    private static final String TAG = makeLogTag(DatabaseUtils.class);

    // Database version
    private static final int DATABASE_VERSION = 8;

    // Database name
    public static final String DATABASE_NAME = "mymobkit.db";

    // information for the alias table
    public static final String ALIAS_TABLE_NAME = "alias";
    private static final String ALIAS_TABLE_DROP = "DROP TABLE " + ALIAS_TABLE_NAME;
    private static final String ALIAS_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + ALIAS_TABLE_NAME + " (" + "aliasName TEXT NOT NULL, " + "number TEXT NOT NULL, " + "contactName TEXT, " + "PRIMARY KEY(aliasName)" + ")";

    // information for the key value string table
    public static final String KV_TABLE_NAME = "key_value";
    private static final String KV_TABLE_DROP = "DROP TABLE " + KV_TABLE_NAME;
    private static final String KV_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + KV_TABLE_NAME + " (" + "key TEXT NOT NULL, " + "value TEXT NOT NULL, " + "PRIMARY KEY(key)" + ")";

    // information for SMS table
    public static final String SMS_TABLE_NAME = "sms";
    private static final String SMS_TABLE_DROP = "DROP TABLE " + SMS_TABLE_NAME;
    private static final String SMS_TABLE_CREATE = "Create TABLE IF NOT EXISTS " + SMS_TABLE_NAME + " (" + "smsID INTEGER NOT NULL, " + "phoneNumber TEXT NOT NULL, " + "name TEXT NOT NULL, " + "shortenedMessage TEXT NOT NULL, " + "answerTo TEXT NOT NULL, "
            + "dIntents TEXT NOT NULL, " + "sIntents TEXT NOT NULL, " + "numParts INTEGER NOT NULL, " + "resSIntent INTEGER NOT NULL, " + "resDIntent INTEGER NOT NULL, " + "date INTEGER NOT NULL, " + "PRIMARY KEY(smsID)" + ")";

    // information for configuration table
    public static final String APPCONFIG_TABLE_NAME = "AppConfig";
    private static final String APPCONFIG_TABLE_DROP = "DROP TABLE " + APPCONFIG_TABLE_NAME;
    private static final String APPCONFIG_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + APPCONFIG_TABLE_NAME + " (" + Field.ID.getId() + " VARCHAR PRIMARY KEY, " + Field.NAME.getId() + " VARCHAR NOT NULL, " + Field.VALUE.getId() + " VARCHAR, "
            + Field.MODULE.getId() + " VARCHAR, " + Field.DESCRIPTION.getId() + " VARCHAR, " + Field.CONFIGURABLE.getId() + " BIT DEFAULT('1'), " + Field.DATE_CREATED.getId() + " VARCHAR NOT NULL DEFAULT( CURRENT_TIMESTAMP ), "
            + Field.DATE_MODIFIED.getId() + " VARCHAR NOT NULL DEFAULT( CURRENT_TIMESTAMP ), UNIQUE(name, module) " + ")";

    public static final String[] APPCONFIG_TABLE_COLUMNS = new String[]{Field.ID.getId(), Field.NAME.getId(), Field.VALUE.getId(), Field.MODULE.getId(), Field.DESCRIPTION.getId(), Field.CONFIGURABLE.getId(), Field.DATE_CREATED.getId(), Field.DATE_MODIFIED.getId()};


    // information for GCM_MESSAGE table
    public static final String GCM_MESSAGE_TABLE_NAME = "gcm_message";
    private static final String GCM_MESSAGE_TABLE_DROP = "DROP TABLE " + GCM_MESSAGE_TABLE_NAME;
    private static final String GCM_MESSAGE_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + GCM_MESSAGE_TABLE_NAME +
            " (" + Field.ID.getId() + " VARCHAR PRIMARY KEY, " +
            Field.ACTION.getId() + " VARCHAR NOT NULL, " +
            Field.VALUE.getId() + " VARCHAR, " +
            Field.TIMESTAMP.getId() + " INTEGER NOT NULL" + ")";

    public static final String[] GCM_MESSAGE_TABLE_COLUMNS = new String[]{Field.ID.getId(), Field.ACTION.getId(), Field.VALUE.getId(), Field.TIMESTAMP.getId()};

    // information for MMS table
    public static final String MMS_TABLE_NAME = "mms";
    private static final String MMS_TABLE_DROP = "DROP TABLE " + MMS_TABLE_NAME;
    private static final String MMS_TABLE_CREATE = "Create TABLE IF NOT EXISTS " + MMS_TABLE_NAME + " (" + "mmsID INTEGER NOT NULL, " + "_to TEXT NOT NULL, "
            + "cc TEXT, " + "bcc TEXT, " + "subject TEXT, "
            + "body TEXT, " + "delivery INTEGER NOT NULL, " + "read INTEGER NOT NULL, "
            + "delivered INTEGER NOT NULL, "  + "isRead INTEGER NOT NULL, " + "date INTEGER NOT NULL, " + "PRIMARY KEY(mmsID)" + ")";

    /**
     * Constructor.
     *
     * @param context
     */
    public DatabaseUtils(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            LOGI(TAG, "onCreate :Creating the database for the 1st time");
            db.execSQL(APPCONFIG_TABLE_CREATE);
            db.execSQL(ALIAS_TABLE_CREATE);
            db.execSQL(KV_TABLE_CREATE);
            db.execSQL(SMS_TABLE_CREATE);
            db.execSQL(GCM_MESSAGE_TABLE_CREATE);
            db.execSQL(MMS_TABLE_CREATE);

            db.execSQL("delete from " + APPCONFIG_TABLE_NAME);
            db.execSQL("INSERT INTO [AppConfig] ([id], [name], [value], [module], [description], [configurable], [date_created], [date_modified]) VALUES (1, 'surveillance_mode', 0, 'surveillance', 'indicator for surveillance', 1, '2013-06-10 16:20:54', '2013-06-10 16:20:54')");
            db.execSQL("INSERT INTO [AppConfig] ([id], [name], [value], [module], [description], [configurable], [date_created], [date_modified]) VALUES (2, 'surveillance_shutdown', 0, 'surveillance', 'Indicate if the camera is shut down', 1, '2013-06-10 16:20:54', '2013-06-10 16:20:54')");
            db.execSQL("INSERT INTO [AppConfig] ([id], [name], [value], [module], [description], [configurable], [date_created], [date_modified]) VALUES (3, 'lock_pattern', ' ', 'security', 'lock pattern for app start up and surveillance', 1, '2013-06-10 16:20:54', '2013-06-10 16:20:54')");

        } catch (Exception ex){
            LOGE(TAG, "[onCreate] Failed to create database", ex);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LOGI(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        if (oldVersion <= 5) {
            LOGI(TAG, "Upgrading database from " + oldVersion + " to " + newVersion);
            try {
                // Drop tables if they exist
                db.execSQL(ALIAS_TABLE_DROP);
                db.execSQL(KV_TABLE_DROP);
                db.execSQL(SMS_TABLE_DROP);
            } catch (Exception e) {
                LOGE(TAG, "Failed to upgrade database", e);
            }
            try {
                // Recreate the tables
                db.execSQL(ALIAS_TABLE_CREATE);
                db.execSQL(KV_TABLE_CREATE);
                db.execSQL(SMS_TABLE_CREATE);
            } catch (Exception e) {
                LOGE(TAG, "Failed to upgrade database", e);
            }
        }

        if (oldVersion < 7) {
            LOGI(TAG, "Upgrading database from " + oldVersion + " to " + newVersion);
            try {
                // Drop tables if they exist
                db.execSQL(GCM_MESSAGE_TABLE_DROP);
            } catch (Exception e) {
                LOGE(TAG, "Failed to drop GCM_MESSAGE table", e);
            }
            try {
                // Recreate the tables
                db.execSQL(GCM_MESSAGE_TABLE_CREATE);
            } catch (Exception e) {
                LOGE(TAG, "Failed to create GCM_MESSAGE table", e);
            }
        }

        if (oldVersion < 8) {
            LOGI(TAG, "Upgrading database from " + oldVersion + " to " + newVersion);
            try {
                // Drop tables if they exist
                db.execSQL(MMS_TABLE_DROP);
            } catch (Exception e) {
                LOGE(TAG, "Failed to drop MMS table", e);
            }
            try {
                // Recreate the tables
                db.execSQL(MMS_TABLE_CREATE);
            } catch (Exception e) {
                LOGE(TAG, "Failed to create MMS table", e);
            }
        }
    }

    /*
     * Should only happen for DEV.
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LOGW(TAG, "DB Version downgraded from " + oldVersion + " to " + newVersion + ", dropping all tables, all data will be lost");
        try {
            // Drop tables if they exist
            db.execSQL(ALIAS_TABLE_DROP);
            db.execSQL(KV_TABLE_DROP);
            db.execSQL(SMS_TABLE_DROP);
            db.execSQL(APPCONFIG_TABLE_DROP);
            db.execSQL(GCM_MESSAGE_TABLE_DROP);
            db.execSQL(MMS_TABLE_DROP);

            onCreate(db);
        } catch (Exception e) {
            LOGE(TAG, "Failed to downgrade database", e);
        }
    }
}