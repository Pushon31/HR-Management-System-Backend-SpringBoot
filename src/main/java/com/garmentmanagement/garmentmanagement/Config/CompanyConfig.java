package com.garmentmanagement.garmentmanagement.Config;


import org.springframework.stereotype.Component;

@Component
public class CompanyConfig {


    private final String companyName = "SNS Garment Ltd";
    private final String address = "Dhaka,Bangladesh";
    private final String phone = "737821893";
    private final String email = "info@SNS.com";

    // Jasper Report Parameters
    public java.util.Map<String, Object> getReportParams() {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("COMPANY_NAME", companyName);
        params.put("COMPANY_ADDRESS", address);
        params.put("COMPANY_PHONE", phone);
        params.put("COMPANY_EMAIL", email);
        return params;
    }
}