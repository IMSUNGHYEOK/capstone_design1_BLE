package com.example.maedin.ble_slient_mode_app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;

import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    LinearLayout main_layout;

    private Button btn_setting;     //설정 버튼
    private Button btn_set_mode;    //모드 설정 버튼
    private Button btn_beacon_info; //비콘 정보 확인 버튼
    private Button btn_exit;        //종료 버튼
    private TextView txt_data;      //데이터 출력 텍스트

    private Switch btn_switch_info; //모드 복구 설정 스위치 버튼
    boolean switch_info = false;            //모드 복구 설정 스위치 on/off

    private BeaconManager beaconManager;
    private BeaconRegion region;
    Beacon nearestBeacon;

    private int soundstate = 0;     //현재 음량 상태
    boolean isConnected = false;   //false 변경X true 변경
    private double distance = 0;

    AudioManager mAudiomanger;

    private int room = 10;   //방 크기


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //설정 버튼 연결
        btn_setting = (Button) findViewById(R.id.btn_setting);
        btn_setting.setOnClickListener(this);
        main_layout = (LinearLayout) findViewById(R.id.main_layout);

        beaconManager = new BeaconManager(this);

        chagneState();

        region = new BeaconRegion("ranged region",
                UUID.fromString("74278BDA-B644-4520-8F0C-720EAF059935"), null, null);

        mAudiomanger = (AudioManager)getSystemService(getApplicationContext().AUDIO_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //런타임 권한 (Android 6.0이상) - ACCESS_COARSE_LOCATION 권한 요청
        //Bluetooth on, Location on 등등을 고려되었는지 확인
        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);

        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_setting:
                setDialog();
                break;
        }

    }

    //설정 다이얼로그
    private void setDialog()
    {
        final LinearLayout setting_layout = (LinearLayout) View.inflate(this, R.layout.setting, null);

        btn_beacon_info = (Button) setting_layout.findViewById(R.id.btn_beacon);
        btn_set_mode = (Button) setting_layout.findViewById(R.id.btn_mode);
        btn_exit = (Button) setting_layout.findViewById(R.id.btn_exit);

        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setTitle("설정");
        dlg.setView(setting_layout);
        //dlg.setIcon(R.drawable);
        dlg.setCancelable(false);
        dlg.setPositiveButton("확인", null);
        dlg.show();


        btn_set_mode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setModeDialog();
            }
        });
        btn_beacon_info.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
               beaconInfoDialog();
            }
        });

        btn_exit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

     }

     //모드 설정 다이얼로그
     private void setModeDialog()
     {
         final LinearLayout set_mode_layout = (LinearLayout) View.inflate(this, R.layout.set_mode, null);
         AlertDialog.Builder dlg = new AlertDialog.Builder(this);
         btn_switch_info = (Switch) set_mode_layout.findViewById(R.id.btn_switch);
         btn_switch_info.setChecked(switch_info);

         dlg.setTitle("모드 설정");
         dlg.setView(set_mode_layout);
         dlg.setCancelable(false);
         dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {

                 switch_info = btn_switch_info.isChecked();
             }
         });
         dlg.show();
     }

     //비콘 정보 다이얼로그
     private void beaconInfoDialog()
     {
         final LinearLayout beacon_info_layout = (LinearLayout) View.inflate(this, R.layout.beacon_info, null);

         txt_data = (TextView) beacon_info_layout.findViewById(R.id.txt_data);

         AlertDialog.Builder dlg = new AlertDialog.Builder(this);
         dlg.setTitle("비콘 정보");
         dlg.setView(beacon_info_layout);
         //dlg.setIcon(R.drawable);
         dlg.setCancelable(false);
         dlg.setPositiveButton("확인", null);
         dlg.show();

         if (isConnected)
         {
             txt_data.setText("Rssi: "+nearestBeacon.getRssi() + "\n"
                      +"UUID: " + nearestBeacon.getProximityUUID() + "\n"
                      +"Major, Minor: " +nearestBeacon.getMajor() + ", " + nearestBeacon.getMinor()+ "\n"
             +"Distance: " + distance);
         }
         else
             txt_data.setText("연결이 되어있지 않습니다.");

     }

     private void chagneState()
     {
         final LinearLayout set_mode_layout = (LinearLayout) View.inflate(this, R.layout.setting, null);
         btn_switch_info = (Switch) set_mode_layout.findViewById(R.id.btn_switch);
         beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener()
         {
             @Override
             public void onBeaconsDiscovered(BeaconRegion beaconRegion, List<Beacon> beacons) {
                 if (!beacons.isEmpty()){
                     nearestBeacon = beacons.get(0);

                     distance = calculateDistance(nearestBeacon.getMeasuredPower(), nearestBeacon.getRssi());

                     if (distance < room && !isConnected)   //앱이 작동되지않은 상태로 비콘감지
                     {
                         main_layout.setBackgroundResource(R.drawable.on);
                         btn_setting.setBackgroundResource(R.drawable.on_setting);
                         isConnected = true;
                         soundstate = mAudiomanger.getRingerMode();
                         mAudiomanger.setRingerMode(AudioManager.RINGER_MODE_VIBRATE); // RINGER_MODE_VIBRATE = 1
                         showNotification("강의실 입장", "매너모드 어플리케이션이 작동되었습니다.");

                     }
                     else  //앱이 작동된 상태로 비콘감지
                     {
                         showNotification("강의실 퇴장", "매너모드 어플리케이션이 해제되었습니다.");
                         main_layout.setBackgroundResource(R.drawable.off);
                         btn_setting.setBackgroundResource(R.drawable.off_setting);
                         isConnected = false;
                         if(switch_info == true)
                         {
                             showNotification("복구", "매너모드 어플리케이션이 복구되었습니다.");
                             mAudiomanger.setRingerMode(soundstate);
                         }else if(soundstate==0){
                             mAudiomanger.setRingerMode(AudioManager.RINGER_MODE_VIBRATE); // RINGER_MODE_NORMAL= 2
                         }
                     }

                 }
             }
         });
     }

    private static double calculateDistance(int txPower, int rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        // Log.d(, "calculating accuracy based on rssi of "+rssi);

        double ratio = rssi*1.0/txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        }
        else {
            double cal_distance =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            //Log.d(TAG, " avg rssi: "+rssi+" accuracy: "+accuracy);
            return cal_distance;
        }
    }


    //알림에 사용되는 함수
    public void showNotification(String title, String message) {
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[] { notifyIntent }, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

}