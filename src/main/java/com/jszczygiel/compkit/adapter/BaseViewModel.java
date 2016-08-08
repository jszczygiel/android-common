package com.jszczygiel.compkit.adapter;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.annotation.IntDef;

import com.jszczygiel.foundation.containers.Triple;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class BaseViewModel implements Parcelable {

    public static final String ID_NOT_SET = "";
    public static final int NOT_SET = -1;

    @Types.Type
    protected final int type;
    protected final String id;
    private Triple<Integer, Long, Long> oldChatState;

    public BaseViewModel(int type) {
        this(type, ID_NOT_SET);
    }

    public BaseViewModel(int type, String id) {
        this.type = type;
        this.id = id;
    }

    public BaseViewModel(Parcel in) {
        id = in.readString();
        //noinspection WrongConstant
        type = in.readInt();
    }


    public static class Types {

        public static final int NOT_SET = -1;


        @IntDef({NOT_SET})
        @Retention(RetentionPolicy.SOURCE)
        public @interface Type {
        }

        @Type
        int navigationType;

        public Types(@Type int navigationType) {
            this.navigationType = navigationType;
        }

        @Type
        public int getType() {
            return navigationType;
        }
    }

    @Types.Type
    public int getModelType() {
        return type;
    }

    public String getId() {
        return id;
    }

    @Override
    @CallSuper
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeInt(type);
    }

    /**
     * If object have the same type and id field they are considered equal
     *
     * @param o object to compare with
     * @return if object are equal
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof BaseViewModel && id.equalsIgnoreCase(((BaseViewModel) o).id)
                && type == ((BaseViewModel) o).type;
    }

    @Override
    public int hashCode() {
        int result = 42;
        result = 37 * result + id.hashCode();
        result = 37 * result + type;
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}