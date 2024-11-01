package com.mycompany.qrcode.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mycompany.qrcode.beans.Issue;
import java.util.List;

public class IssuesResponse {
    @JsonProperty("issues")
    private List<Issue> issues;

    // Getters y setters
    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }
}

