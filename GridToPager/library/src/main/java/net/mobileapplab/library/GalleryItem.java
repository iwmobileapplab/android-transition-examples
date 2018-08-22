package net.mobileapplab.library;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class GalleryItem implements Parcelable {

    private final Uri uri;
    private final String transitionName;

    public GalleryItem(@NonNull Uri uri, @NonNull String transitionName) {
        this.uri = uri;
        this.transitionName = transitionName;
    }

    @NonNull
    public Uri getUri() {
        return uri;
    }

    @NonNull
    public String getTransitionName() {
        return transitionName;
    }

    protected GalleryItem(Parcel in) {
        uri = in.readParcelable(Uri.class.getClassLoader());
        transitionName = in.readString();
    }

    public static final Creator<GalleryItem> CREATOR = new Creator<GalleryItem>() {
        @Override
        public GalleryItem createFromParcel(Parcel in) {
            return new GalleryItem(in);
        }

        @Override
        public GalleryItem[] newArray(int size) {
            return new GalleryItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(uri, flags);
        dest.writeString(transitionName);
    }
}
