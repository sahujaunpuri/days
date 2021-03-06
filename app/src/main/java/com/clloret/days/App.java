package com.clloret.days;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import com.clloret.days.dagger.AppComponent;
import com.clloret.days.dagger.DaggerAppComponent;
import com.clloret.days.device.reminders.EventRemindersManager;
import com.clloret.days.domain.AppDataStore;
import com.clloret.days.utils.StethoUtils;
import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import timber.log.Timber;
import timber.log.Timber.DebugTree;

public class App extends DaggerApplication implements HasSupportFragmentInjector {

  private static final String ROBOLECTRIC_FINGERPRINT = "robolectric";

  @Inject
  DispatchingAndroidInjector<Fragment> androidInjector;

  @Inject
  AppDataStore appDataStore;

  @Inject
  EventRemindersManager eventRemindersManager;

  @Override
  public void onCreate() {

    super.onCreate();

    Timber.plant(new DebugTree());
    if (!isRoboUnitTest()) {
      StethoUtils.install(this);
    }
  }

  @Override
  protected AndroidInjector<? extends DaggerApplication> applicationInjector() {

    AppComponent appComponent = DaggerAppComponent.builder().application(this).build();
    appComponent.inject(this);

    return appComponent;
  }

  public static App get(Context context) {

    return (App) context.getApplicationContext();
  }

  public void restart() {

    Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());

    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
        PendingIntent.getActivity(getBaseContext(), 0, new Intent(intent),
            intent.getFlags()));

    System.exit(0);
  }

  public void invalidateDataAndRestart() {

    appDataStore.invalidateAll()
        .doOnComplete(this::restart)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe();
  }

  private static boolean isRoboUnitTest() {

    return ROBOLECTRIC_FINGERPRINT.equals(Build.FINGERPRINT);
  }

  @Override
  public AndroidInjector<Fragment> supportFragmentInjector() {

    return androidInjector;
  }
}
