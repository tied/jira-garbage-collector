package com.tut.rest.utils;

import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Auth {
    private JiraAuthenticationContext jiraAuthenticationContext;
    private GlobalPermissionManager globalPermissionManager;

    @Autowired
    public Auth(@ComponentImport JiraAuthenticationContext jiraAuthenticationContext, @ComponentImport GlobalPermissionManager globalPermissionManager) {
        this.globalPermissionManager = globalPermissionManager;
        this.jiraAuthenticationContext = jiraAuthenticationContext;
    }

    public boolean canAccess() {
        if (this.jiraAuthenticationContext.isLoggedInUser()) {
            return this.globalPermissionManager.hasPermission(GlobalPermissionKey.ADMINISTER, this.jiraAuthenticationContext.getLoggedInUser());
        }
        return false;
    }
}
