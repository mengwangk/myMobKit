package com.mymobkit.data;

import android.content.ContentValues;
import android.content.Context;

import com.mymobkit.model.NotificationMsg;

import java.util.List;

import static com.mymobkit.common.LogUtils.makeLogTag;

public final class NotificationMsgHelper {

    private static final String TAG = makeLogTag(NotificationMsgHelper.class);

    private static NotificationMsgHelper notificationMsgHelper = null;

    /**
     * This constructor ensures that the database is setup correctly
     *
     * @param context
     */
    private NotificationMsgHelper(Context context) {
        new NotificationTable(context);
    }

    public static NotificationMsgHelper getNotificationMsgHelper(Context context) {
        if (notificationMsgHelper == null) {
            notificationMsgHelper = new NotificationMsgHelper(context);
        }
        return notificationMsgHelper;
    }

    public boolean addMsg(final NotificationMsg msg) {
        ContentValues values = new ContentValues();
        values.put(IDbContentProvider.Field.ID.getId(), msg.getId());
        values.put(IDbContentProvider.Field.ACTION.getId(), msg.getAction());
        values.put(IDbContentProvider.Field.VALUE.getId(), msg.getValue());
        values.put(IDbContentProvider.Field.TIMESTAMP.getId(), msg.getTimestamp());
        return NotificationTable.insert(values);
    }

    public int purge() {
        return NotificationTable.purge();
    }

    public List<NotificationMsg> getAllMsgs() {
        return NotificationTable.getAll();
    }

}
