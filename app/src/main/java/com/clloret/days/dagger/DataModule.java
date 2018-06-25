package com.clloret.days.dagger;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.clloret.days.R;
import com.clloret.days.model.AppDataStore;
import com.clloret.days.model.AppRepository;
import com.clloret.days.model.local.DaysDatabase;
import com.clloret.days.model.local.LocalDataStore;
import com.clloret.days.model.local.ReadOnlyDataStore;
import com.clloret.days.model.remote.AirtableDataStore;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

@Module
public abstract class DataModule {

  private static final String DATABASE = "days";

  @Provides
  @Singleton
  static AppDataStore providesAppDataStore(@Named("local") AppDataStore localDataStore,
      @Named("remote") AppDataStore remoteDataStore) {

    return new AppRepository(localDataStore, remoteDataStore);
  }

  @Provides
  @Singleton
  @Named("local")
  static AppDataStore providesLocalDataStore(Context context, SharedPreferences preferences) {

    DaysDatabase db = Room.databaseBuilder(context, DaysDatabase.class, DATABASE).build();

    RemoteDataStoreResult remoteDataStoreResult = checkIsRemoteDataStore(context, preferences);

    if (remoteDataStoreResult.isRemoteDataStore) {

      return new ReadOnlyDataStore(db);
    } else {

      return new LocalDataStore(db);
    }
  }

  @Provides
  @Singleton
  @Named("remote")
  static AppDataStore providesAirtableDataStore(Context context, SharedPreferences preferences) {

    RemoteDataStoreResult remoteDataStoreResult = checkIsRemoteDataStore(context, preferences);

    if (remoteDataStoreResult.isRemoteDataStore) {

      DaysDatabase db = Room.databaseBuilder(context, DaysDatabase.class, DATABASE).build();
      return new LocalDataStore(db);
    } else {

      return new AirtableDataStore(context, remoteDataStoreResult.airtableKey,
          remoteDataStoreResult.airtableBase);
    }
  }

  private static RemoteDataStoreResult checkIsRemoteDataStore(Context context,
      SharedPreferences preferences) {

    boolean remoteDataStore = preferences
        .getBoolean(context.getString(R.string.pref_remote_datastore), false);
    String airtableKey = preferences
        .getString(context.getString(R.string.pref_airtable_api_key), "");
    String airtableBase = preferences
        .getString(context.getString(R.string.pref_airtable_base_id), "");

    boolean isRemoteDataStore =
        !remoteDataStore || TextUtils.isEmpty(airtableKey) || TextUtils.isEmpty(airtableBase);

    return new RemoteDataStoreResult(isRemoteDataStore, airtableKey, airtableBase);
  }

  static class RemoteDataStoreResult {

    boolean isRemoteDataStore;
    String airtableKey;
    String airtableBase;

    RemoteDataStoreResult(boolean isRemoteDataStore, String airtableKey, String airtableBase) {

      this.isRemoteDataStore = isRemoteDataStore;
      this.airtableKey = airtableKey;
      this.airtableBase = airtableBase;
    }
  }
}