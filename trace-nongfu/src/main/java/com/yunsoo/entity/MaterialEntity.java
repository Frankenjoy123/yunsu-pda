package com.yunsu.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yunsu on 2016/8/15.
 */
public class MaterialEntity implements Parcelable{
    private String materialNumber;
    private String productName;
    private int amount;

    public MaterialEntity(){

    }

    public MaterialEntity(Parcel in) {
        materialNumber = in.readString();
        productName = in.readString();
        amount = in.readInt();
    }


    public static final Creator<MaterialEntity> CREATOR = new Creator<MaterialEntity>() {
        @Override
        public MaterialEntity createFromParcel(Parcel in) {
            return new MaterialEntity(in);
        }

        @Override
        public MaterialEntity[] newArray(int size) {
            return new MaterialEntity[size];
        }
    };

    public String getMaterialNumber() {
        return materialNumber;
    }

    public void setMaterialNumber(String materialNumber) {
        this.materialNumber = materialNumber;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(materialNumber);
        parcel.writeString(productName);
        parcel.writeInt(amount);
    }
}
