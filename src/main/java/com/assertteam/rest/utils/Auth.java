package com.assertteam.rest.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import org.springframework.beans.factory.annotation.Autowired;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

@Component
public class Auth {
    private JiraAuthenticationContext jiraAuthenticationContext;
    private GlobalPermissionManager globalPermissionManager;
    private Logger logger = LoggerFactory.getLogger(Auth.class);

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
