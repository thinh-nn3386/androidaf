package com.example.androidaf.autofill;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.autofill.AutofillId;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Data implements Parcelable {
    public static final int TAG = 1;
    public int i = TAG;
    public AutofillId id;
    public int as;
    public String asd;

    public Data(int as, String asd){
        this.as = as;
        this.asd = asd;
    }


    protected Data(Parcel in) {
        i = in.readInt();
        id = in.readParcelable(AutofillId.class.getClassLoader());
        as = in.readInt();
        asd = in.readString();
    }

    public static final Creator<Data> CREATOR = new Creator<Data>() {
        @Override
        public Data createFromParcel(Parcel in) {
            return new Data(in);
        }

        @Override
        public Data[] newArray(int size) {
            return new Data[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(i);
        dest.writeParcelable(id, flags);
        dest.writeInt(as);
        dest.writeString(asd);
    }
}
