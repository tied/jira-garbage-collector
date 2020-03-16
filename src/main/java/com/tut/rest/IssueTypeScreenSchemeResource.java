package com.tut.rest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenScheme;
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenSchemeManager;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.tut.rest.utils.Auth;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/issuetypescreenscheme")
public class IssueTypeScreenSchemeResource {
    private Auth auth;

    @Autowired
    public IssueTypeScreenSchemeResource(Auth auth) {
        this.auth = auth;
    }

    @DELETE
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/")
    public Response gcNotDefault() {
        if (!auth.canAccess()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
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
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{id}")
    public Response gcForKey(@PathParam("id") long id) {
        if (!auth.canAccess()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
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
