package com.bearpot.dgjung.nileblue;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bearpot.dgjung.nileblue.Memo.MemoManager;
import com.bearpot.dgjung.nileblue.VO.LocationVo;
import com.bearpot.dgjung.nileblue.VO.MemoVo;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by dg.jung on 2017-11-06.
 */

public class MemoActivity extends AppCompatActivity {
    private EditText memoEdit = null;
    private TextView locationView = null;

    private MemoManager mManager = null;
    private LocationVo currentPosition;
    private MemoVo memoInfo;

    private Intent intent;

    private boolean editable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memo_main);

        memoEdit = (EditText) findViewById(R.id.memo_edit);
        locationView = (TextView) findViewById(R.id.locationView);

        mManager = new MemoManager(this);

        intent = new Intent(this.getIntent());
        currentPosition = (LocationVo) intent.getSerializableExtra("currentPosition");
        memoInfo = (MemoVo) intent.getSerializableExtra("memoInfo");

        if (memoInfo != null) {
            memoEdit.setText(memoInfo.getDescription());
            locationView.setText(memoInfo.getLocation().getLat() + "," + memoInfo.getLocation().getLng());
            editable = true;
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_btn: {
                String description = memoEdit.getText().toString();

                if (!editable) {
                    mManager.save(currentPosition.getLat(),currentPosition.getLng(),description);
                    Toast.makeText(this,"마커가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, description, Toast.LENGTH_SHORT).show();
                    mManager.save(memoInfo.getMemoId(), memoInfo.getLocation().getLat(), memoInfo.getLocation().getLng(), description);
                    Toast.makeText(this,"마커가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                }
                memoEdit.setText("");
                returnToMain();

                break;
            }

            case R.id.delete_btn: {
                mManager.delete(memoInfo.getMemoId());
                returnToMain();

                Toast.makeText(this,"마커가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }

    public void returnToMain() {
        intent = new Intent(MemoActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}