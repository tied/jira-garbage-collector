package com.tut.rest;

import javax.ws.rs.*;
import com.tut.rest.utils.Auth;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.atlassian.jira.component.ComponentAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenScheme;
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenSchemeManager;

@Path("/issuetypescreenscheme")
public class IssueTypeScreenSchemeResource {
    private Auth auth;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public IssueTypeScreenSchemeResource(Auth auth) {
        this.auth = auth;
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/")
    public Response gcNotDefault() {
        if (!auth.canAccess()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        this.logger.info("Cleaner - REST - Delete - IssueTypeScreenScheme - / - Started");
        IssueTypeScreenSchemeManager schemeManager = ComponentAccessor.getIssueTypeScreenSchemeManager();
        IssueTypeScreenScheme defaultScheme = schemeManager.getDefaultScheme();

        schemeManager.getIssueTypeScreenSchemes().forEach(screen -> {
            if(screen.getId() == defaultScheme.getId()) {
                // default, we will skip it
                return;
            }

            if(screen.getProjects().size() == 0) {
                // no associated projects, lets remove it
                schemeManager.removeIssueTypeSchemeEntities(screen);
                schemeManager.removeIssueTypeScreenScheme(screen);
                screen.remove();
            }
        });

        return Response.ok(new DeleteModel("id", "testMsg")).build();
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{id}")
    public Response gcForKey(@PathParam("id") long id) {
        if (!auth.canAccess()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        this.logger.info(String.format("Cleaner - REST - Delete - IssueTypeScreenScheme - /%d - Started", id));
        IssueTypeScreenSchemeManager schemeManager = ComponentAccessor.getIssueTypeScreenSchemeManager();
        IssueTypeScreenScheme screen = schemeManager.getIssueTypeScreenScheme(id);

        if(screen == null) {
            return Response.status(404).build();
        }
        schemeManager.removeIssueTypeSchemeEntities(screen);
        schemeManager.removeIssueTypeScreenScheme(screen);
        screen.remove();
        return Response.ok(new DeleteModel("IssueTypeScreenScheme", String.format("ID: '%d', Name: '%s' deleted", screen.getId(), screen.getName()))).build();
    }
}
