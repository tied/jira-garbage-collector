package com.tut.rest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.fields.config.manager.IssueTypeSchemeManager;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.atomic.AtomicReference;

@Path("/issuetypescheme")
public class IssueTypeSchemeResource {

    @DELETE
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/")
    public Response gcNotDefault() {
        IssueTypeSchemeManager schemeManager = ComponentAccessor.getIssueTypeSchemeManager();
        FieldConfigScheme defaultScheme = schemeManager.getDefaultIssueTypeScheme();

        schemeManager.getAllSchemes().forEach(screen -> {
            if(screen.getId() == defaultScheme.getId()) {
                // default, we will skip it
                return;
            }

            if(screen.getAssociatedProjectIds().size() == 0) {
                System.out.println(String.format("Gonna delete '%s' scheme with id '%d'", screen.getName(), screen.getId()));
                // no associated projects, lets remove it
                // schemeManager.deleteScheme(screen);
            }
        });

        return Response.ok(new DeleteModel("id", "testMsg")).build();
    }

    @DELETE
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{id}")
    public Response gcForKey(@PathParam("id") long id) {
        IssueTypeSchemeManager schemeManager = ComponentAccessor.getIssueTypeSchemeManager();

        AtomicReference<FieldConfigScheme> removedScreen = new AtomicReference<>();
        schemeManager.getAllSchemes().forEach(screen -> {
            if(screen.getId() == id) {
                if(screen.getAssociatedProjectIds().size() == 0) {
                    System.out.println(String.format("Gonna delete '%s' scheme with id '%d'", screen.getName(), screen.getId()));
                    removedScreen.set(screen);
                    schemeManager.deleteScheme(screen);
                }
            }
        });

        if(removedScreen.get() == null) {
            return Response.status(404).build();
        }

        return Response.ok(new DeleteModel("IssueTypeScheme", String.format("ID: '%d', Name: '%s' deleted", removedScreen.get().getId(), removedScreen.get().getName()))).build();
    }
}
