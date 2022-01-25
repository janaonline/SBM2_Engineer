package com.ichangemycity.model;

import android.os.Parcelable;

import java.io.Serializable;

public class ComplaintFilterModel implements Serializable {
    private String complaintType, displayTitle;
    private int complaintColor;
    private String complaintCount;
    private int resId;


    /**
     * @return the complaintColor
     */
    public int getComplaintColor() {
        return complaintColor;
    }

    /**
     * @param complaintColor
     *            the complaintColor to set
     */
    public void setComplaintColor(int complaintColor) {
        this.complaintColor = complaintColor;
    }

    /**
     * @return the displayTitle
     */
    public String getDisplayTitle() {
        return displayTitle;
    }

    /**
     * @param displayTitle
     *            the displayTitle to set
     */
    public void setDisplayTitle(String displayTitle) {
        this.displayTitle = displayTitle;
    }

    /**
     * @return the complaintType
     */
    public String getComplaintType() {
        return complaintType;
    }

    /**
     * @param complaintType
     *            the complaintType to set
     */
    public void setComplaintType(String complaintType) {
        this.complaintType = complaintType;
    }

    public String getComplaintCount() {
        return complaintCount;
    }

    public void setComplaintCount(String complaintCount) {
        this.complaintCount = complaintCount;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
