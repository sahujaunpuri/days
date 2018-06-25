package com.clloret.days.model.entities;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Date;
import org.joda.time.Days;
import org.joda.time.LocalDate;

// If replace Parcelable, maintain field "event" implementation for Date type
public final class Event implements Parcelable {

  public static final Creator<Event> CREATOR = new Creator<Event>() {
    @Override
    public Event createFromParcel(Parcel in) {

      return new Event(in);
    }

    @Override
    public Event[] newArray(int size) {

      return new Event[size];
    }
  };
  private static final String[] EMPTY_ARRAY = new String[0];

  private String id;

  private String name;

  private String description;

  private Date date;

  private String[] tags = EMPTY_ARRAY;

  private boolean favorite;

  // empty constructor needed by the Parceler library
  public Event() {

  }

  protected Event(Parcel in) {

    id = in.readString();
    name = in.readString();
    description = in.readString();
    long tmpDate = in.readLong();
    this.date = tmpDate == -1 ? null : new Date(tmpDate);
    tags = in.createStringArray();
    favorite = in.readByte() != 0;
  }

  public Event(String id, String name, String description, Date date, String[] tags,
      boolean favorite) {

    this.id = id;
    this.name = name;
    this.description = description;
    this.date = date;
    this.tags = tags;
    this.favorite = favorite;
  }

  @Override
  public int describeContents() {

    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {

    dest.writeString(id);
    dest.writeString(name);
    dest.writeString(description);
    dest.writeLong(this.date != null ? this.date.getTime() : -1);
    dest.writeStringArray(tags);
    dest.writeByte((byte) (favorite ? 1 : 0));
  }

  public String getId() {

    return id;
  }

  public void setId(String id) {

    this.id = id;
  }

  public String getName() {

    return name;
  }

  public void setName(String name) {

    this.name = name;
  }

  public Date getDate() {

    return (Date) date.clone();
  }

  public void setDate(Date date) {

    this.date = (Date) date.clone();
  }

  public boolean isFavorite() {

    return favorite;
  }

  public void setFavorite(boolean favorite) {

    this.favorite = favorite;
  }

  public int getDaysSince() {

    LocalDate start = new LocalDate();
    LocalDate end = new LocalDate(date);

    return Days.daysBetween(start, end).getDays();
  }

  @Override
  public int hashCode() {

    return id != null ? id.hashCode() : 0;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Event event = (Event) o;

    return id != null ? id.equals(event.id) : event.id == null;
  }

  public String[] getTags() {

    return tags;
  }

  public void setTags(String[] tags) {

    this.tags = tags;
  }

  public String getDescription() {

    return description;
  }

  public void setDescription(String description) {

    this.description = description;
  }
}
