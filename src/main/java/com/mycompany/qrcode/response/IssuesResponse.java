package com.mycompany.qrcode.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mycompany.qrcode.beans.Issue;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IssuesResponse {
    @JsonProperty("issues")
    private List<Issue> issues;

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }
}

