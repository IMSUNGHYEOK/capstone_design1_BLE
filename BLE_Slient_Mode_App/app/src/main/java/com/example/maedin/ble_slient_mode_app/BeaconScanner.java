package com.example.maedin.ble_slient_mode_app;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;

import java.util.List;
import java.util.UUID;

/*
 * eaconManager Estimote Beacon과의 상호 작용을 위한 게이트웨이 의 인스턴스
 */
public class BeaconScanner extends Application {

    private BeaconManager beaconManager;

    @Override
    public void onCreate() {
        super.onCreate();

        beaconManager = new BeaconManager(getApplicationContext());

        // 모니터링 시작
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                //자신의 beacon UUID, major, minor 설정
                beaconManager.startMonitoring(new BeaconRegion(
                        "monitored region",
                        UUID.fromString("74278BDA-B644-4520-8F0C-720EAF059935"),
                        4660, 64002));
            }
        });
    }


}
