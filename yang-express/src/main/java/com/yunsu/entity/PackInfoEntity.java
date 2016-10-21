package com.yunsu.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * created by xiaowu on 2016/9/9
 * the pack information of standard , staff and product
 */
public class PackInfoEntity implements Parcelable{
    private Long staffId;
    private String staffNumber;
    private String staffName;

    private Long productBaseId;
    private String productBaseNumber;
    private String productBaseName;

    private int standard;

    public PackInfoEntity(){};

    protected PackInfoEntity(Parcel in) {
        staffId=in.readLong();
        staffNumber = in.readString();
        staffName = in.readString();

        productBaseId=in.readLong();
        productBaseNumber = in.readString();
        productBaseName = in.readString();

        standard = in.readInt();
    }


    public static final Creator<PackInfoEntity> CREATOR = new Creator<PackInfoEntity>() {
        @Override
        public PackInfoEntity createFromParcel(Parcel in) {
            return new PackInfoEntity(in);
        }

        @Override
        public PackInfoEntity[] newArray(int size) {
            return new PackInfoEntity[size];
        }
    };

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public String getStaffNumber() {
        return staffNumber;
    }

    public void setStaffNumber(String staffNumber) {
        this.staffNumber = staffNumber;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public Long getProductBaseId() {
        return productBaseId;
    }

    public void setProductBaseId(Long productBaseId) {
        this.productBaseId = productBaseId;
    }

    public String getProductBaseNumber() {
        return productBaseNumber;
    }

    public void setProductBaseNumber(String productBaseNumber) {
        this.productBaseNumber = productBaseNumber;
    }

    public String getProductBaseName() {
        return productBaseName;
    }

    public void setProductBaseName(String productBaseName) {
        this.productBaseName = productBaseName;
    }

    public int getStandard() {
        return standard;
    }

    public void setStandard(int standard) {
        this.standard = standard;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(staffId);
        parcel.writeString(staffNumber);
        parcel.writeString(staffName);
        parcel.writeLong(productBaseId);
        parcel.writeString(productBaseNumber);
        parcel.writeString(productBaseName);
        parcel.writeInt(standard);
    }
}
