package com.yunsoo.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.yunsu.greendao.entity.Order;

/**
 * Created by yunsu on 2016/8/16.
 */
public class OrderEntity extends Order implements Parcelable {
    protected OrderEntity(Parcel in) {
    }

    public static final Creator<OrderEntity> CREATOR = new Creator<OrderEntity>() {
        @Override
        public OrderEntity createFromParcel(Parcel in) {
            return new OrderEntity(in);
        }

        @Override
        public OrderEntity[] newArray(int size) {
            return new OrderEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
