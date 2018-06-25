package com.clloret.days.model;

import android.support.annotation.NonNull;
import com.clloret.days.model.entities.Event;
import com.clloret.days.model.entities.Tag;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import java.util.List;

public interface AppDataStore {

  void insertAllEvents(List<Event> events);

  void insertAllTags(List<Tag> tags);

  Completable deleteAllEvents();

  Completable deleteAllTags();

  Completable invalidateAll();

  Single<List<Event>> getEvents(boolean refresh);

  Single<List<Event>> getEventsByTagId(@NonNull String tagId);

  Single<List<Event>> getEventsByFavorite();

  Maybe<Event> createEvent(@NonNull Event event);

  Maybe<Event> editEvent(@NonNull Event event);

  Maybe<Boolean> deleteEvent(@NonNull Event event);

  Single<List<Tag>> getTags(boolean refresh);

  Maybe<Tag> createTag(@NonNull Tag tag);

  Maybe<Tag> editTag(@NonNull Tag tag);

  Maybe<Boolean> deleteTag(@NonNull Tag tag);
}
