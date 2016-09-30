package com.mymobkit.service.api.contact;

import com.google.gson.annotations.Expose;

/**
 * Contact information.
 */
public final class Contact {

    @Expose
    private String displayName;

    @Expose
    private String mobileNumber;

    @Expose
    private String homeNumber;

    @Expose
    private String workNumber;

    @Expose
    private String email;

    @Expose
    private String company;

    @Expose
    private String jobTitle;

    /**
     * Constructor
     *
     * @param displayName Display name.
     */
    public Contact(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getHomeNumber() {
        return homeNumber;
    }

    public void setHomeNumber(String homeNumber) {
        this.homeNumber = homeNumber;
    }

    public String getWorkNumber() {
        return workNumber;
    }

    public void setWorkNumber(String workNumber) {
        this.workNumber = workNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
}
