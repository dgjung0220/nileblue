package com.bearpot.dgjung.nileblue.Memo;

import android.content.Context;
import android.util.Log;

import com.bearpot.dgjung.nileblue.Database.LocationStateDBHelper;
import com.bearpot.dgjung.nileblue.Database.MemoDBHelper;

/**
 * Created by dg.jung on 2017-11-09.
 */

public class MemoManager {

    private MemoDBHelper memoDBHelper;
    Context mContext = null;

    public MemoManager(Context context) {
        mContext = context;
        memoDBHelper = new MemoDBHelper(context, "nileblue.db", null,1);
    }

    public void save(double lat, double lng, String description) {
        if (description == null) {
            return;
        }
        memoDBHelper.save(lat, lng, description);
    }

    public void save(int memo_id, double lat, double lng, String description) {
        if (description == null) {
            return;
        }
        memoDBHelper.save(memo_id, lat, lng, description);
    }

    public void delete(int memo_id) {
        memoDBHelper.delete(memo_id);
    }

}
