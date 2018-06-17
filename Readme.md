
Android application : BLE_Silent_Mode_App
=================================================================================================================================
Service introduce
---------------------------------------------------------------------------------------------------------------------------------
>Bluetooth 4.0 버전 Bluetooth Low Energy를 활용하여 아두이노를 비콘으로 사용.  
비콘이 설치된 공공 장소(영화관,도서관,강의실 )에 출입시 비콘으로부터 신호를 받아 사용자의 모바일 기기를 매너모드로 자동 전환시켜 소음 문제를 예방하기 위한 어플리케이션.


Supported Platforms
---------------------------------------------------------------------------------------------------------------------------------
>라이브러리 : Android Oreo 8.1 (SDK version 27).  
모바일 기기 : Bluetooth 4.0 (BLE)를 지원하는 기기, Android 4.3 이상.  
비콘 정보 : Arduino Uno + HM-10 모듈  


Beacon Setup
---------------------------------------------------------------------------------------------------------------------------------
>1. Arduino + HM-10 모듈 연결
<img width="280" alt="arduino" src="https://user-images.githubusercontent.com/37177670/41099310-19d17a98-6a99-11e8-8b62-c9d0dfd3208e.png">  

>2. Arduino 프로그래밍.  
~~~  
#include <SoftwareSerial.h>

SoftwareSerial BTSerial(4, 5); // 소프트웨어 시리얼 (TX,RX)

void setup(){
Serial.begin(9600);
Serial.println("Hello!");

BTSerial.begin(9600);
}

void loop(){
  while (BTSerial.available()){
    byte data = BTSerial.read();
    Serial.write(data);
  }  

  while (Serial.available()){
    byte data = Serial.read();
    BTSerial.write(data);
  }
}
  
~~~
reference : ibeaconCode/ibeaconCode.ino

Usage of iBeacon
---------------------------------------------------------------------------------------------------------------------------------
>1. AT+RENEW : 공장 초기화  
>2. AT+RESET : HM-10 리붓  
>3. AT ß 시험 작동  
>4. AT+MARJ0x1234 : iBeacon의 Major number설정 (0x1234는 임의값 설정 가능)  
>5. AT+MINO0xFA02 : iBeacon의 Minor number설정 (0xFA02는 임의값 설정 가능)  
>6. AT+ADVI5 : advertising(신호 송출) 주기를 5로 설정(약 0.5초)  
>7. AT+NAMEBBANGPAN : HM-10 이름 정의 (BBANGPAN은 임의값 정의 가능)  
>8. AT+ADTY3 : 전원 절약을 위해 맺지않음(non-connectable)모드로 설정  
>9. AT+IBEA1 : iBeacon을 활성화  
>10. AT+DELO2 : iBeacon의 broadcast-only 로 설정  
>11. AT+PWRM0 : 전원 절약을 위해 auto-sleep으로 설정(최소 절전 모드)  
>12. AT+RESET : 리부트하여 반영  

Application Project Setup
---------------------------------------------------------------------------------------------------------------------------------
>1. 프로젝트 Clone.  
>2. Android Studio에 기존 프로젝트로 불러오기.  
>3. AndroidManifest.xml에 다음 권한을 추가.  
~~~
<div>
...
</application>
    <uses-permission android:name="android.permission.ACCESS_NOTIFICATION_POLICY" />
</manifest>
</div>
~~~
>4. build.gradle (Module: app)에 다음 sdk version 설정
~~~  

android {
    compileSdkVersion 27
    defaultConfig {
        applicationId "com.example.maedin.ble_slient_mode_app"
        minSdkVersion 18
        targetSdkVersion 27
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
    ...  

~~~
>5. 모바일 기기에 컴파일 및 어플리케이션 설치/실행  

Usage of Application
---------------------------------------------------------------------------------------------------------------------------------
>1. 어플리케이션 실행.  
>2. 블루투스 권한 설정. 
>3. 비콘 신호 수신에 따라 매너 모드 전환.  
>4. 설정 다이얼로그  
>- 복구 기능 설정  
>ON : 비콘 영역에서 벗어날 경우 매너모드 전환 이전 모드로 다시 변경.  
>OFF : 비콘 영역에서 벗어나더라도 매너모드 유지  
>- 비콘 정보 확인  
>블루투스 신호를 보내준 비콘의 정보를 출력.  
>- 종료  
>어플리케이션 종료.   
