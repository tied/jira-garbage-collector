package com.assertteam.rest.utils;

import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import org.springframework.beans.factory.annotation.Autowired;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.upm.api.license.PluginLicenseManager;

@Component
public class Auth {
    private JiraAuthenticationContext jiraAuthenticationContext;
    private GlobalPermissionManager globalPermissionManager;
    private Logger logger = LoggerFactory.getLogger(Auth.class);
    private PluginLicenseManager pluginLicenseManager;

    @Autowired
    public Auth(@ComponentImport JiraAuthenticationContext jiraAuthenticationContext, @ComponentImport GlobalPermissionManager globalPermissionManager, @ComponentImport PluginLicenseManager pluginLicenseManager) {
        this.globalPermissionManager = globalPermissionManager;
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.pluginLicenseManager = pluginLicenseManager;
    }

    public boolean canAccess() {
        Option<PluginLicense> license = this.pluginLicenseManager.getLicense();
        if (this.jiraAuthenticationContext.isLoggedInUser() && license.isDefined() && !license.get().getError().isDefined()) {
            return this.globalPermissionManager.hasPermission(GlobalPermissionKey.ADMINISTER, this.jiraAuthenticationContext.getLoggedInUser());
        }
        return false;
    }
}
