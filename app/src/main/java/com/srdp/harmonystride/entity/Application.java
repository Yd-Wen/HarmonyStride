package com.srdp.harmonystride.entity;

import java.io.Serializable;

public class Application implements Serializable {
    //applicant：uid 主键
    private int applicant;
    //pid：主键
    private int pid;
    //datetime：帖子发布时间
    private String datetime;
    //reason
    private String reason;
    //status：1(修改post：receiver), 0（默认）
    private String status;

    public Application() {
    }

    public Application(int applicant, int pid, String datetime, String reason, String status) {
        this.applicant = applicant;
        this.pid = pid;
        this.datetime = datetime;
        this.reason = reason;
        this.status = status;
    }

    public int getApplicant() {
        return applicant;
    }

    public void setApplicant(int applicant) {
        this.applicant = applicant;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Application{" +
                "applicant='" + applicant + '\'' +
                ", pid='" + pid + '\'' +
                ", datetime='" + datetime + '\'' +
                ", reason='" + reason + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
