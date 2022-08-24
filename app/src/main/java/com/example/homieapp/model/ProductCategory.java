package com.example.homieapp.model;

import java.util.Map;

public class ProductCategory{
     String procateName;
     String procateId;
    String procateUrl;

    public ProductCategory() {
    }

    public ProductCategory(String procateId, String procateName, String procateUrl) {
        this.procateId = procateId;
        this.procateName = procateName;
        this.procateUrl = procateUrl;
    }

    public String getProcateId() {
        return procateId;
    }

    public void setProcateId(String procateId) {
        this.procateId = procateId;
    }

    public  String getProcateName() {
        return procateName;
    }

    public void setProcateName(String procateName) {
        this.procateName = procateName;
    }

    public  String getProcateUrl() {
        return procateUrl;
    }

    public void setProcateUrl(String procateUrl) {
        this.procateUrl = procateUrl;
    }
}
