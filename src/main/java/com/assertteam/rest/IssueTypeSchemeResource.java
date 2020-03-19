package com.assertteam.rest;

import javax.ws.rs.*;
import com.assertteam.rest.utils.Auth;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import com.atlassian.jira.component.ComponentAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.fields.config.manager.IssueTypeSchemeManager;

@Path("/issuetypescheme")
public class IssueTypeSchemeResource {
    private Auth auth;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public IssueTypeSchemeResource(Auth auth) {
        this.auth = auth;
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/")
    public Response gcNotDefault() {
        if (!auth.canAccess()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        this.logger.info("GC - REST - Delete - IssueTypeScheme - / - Started");
        ArrayList<DeleteModel> deleted = new ArrayList<>();
        IssueTypeSchemeManager schemeManager = ComponentAccessor.getIssueTypeSchemeManager();
        FieldConfigScheme defaultScheme = schemeManager.getDefaultIssueTypeScheme();

        schemeManager.getAllSchemes().forEach(screen -> {
            if(screen.getId() == defaultScheme.getId()) {
                // default, we will skip it
                return;
            }

            if(screen.getAssociatedProjectIds().size() == 0) {
                // no associated projects, lets remove it
                deleted.add(new DeleteModel("IssueTypeScheme", String.format("ID: '%d', Name: '%s' deleted", screen.getId(), screen.getName())));
                schemeManager.deleteScheme(screen);
            }
        });

        this.logger.info("GC - REST - Delete - IssueTypeScheme - / - Deleted");
        return Response.ok(deleted).build();
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{id}")
    public Response gcForKey(@PathParam("id") long id) {
        if (!auth.canAccess()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        this.logger.info(String.format("GC - REST - Delete - IssueTypeScheme - /%d - Started", id));
        IssueTypeSchemeManager schemeManager = ComponentAccessor.getIssueTypeSchemeManager();

        AtomicReference<FieldConfigScheme> removedScreen = new AtomicReference<>();
        schemeManager.getAllSchemes().forEach(screen -> {
            if(screen.getId() == id) {
                if(screen.getAssociatedProjectIds().size() == 0) {
                    removedScreen.set(screen);
                    schemeManager.deleteScheme(screen);
                }
            }
        });

        if(removedScreen.get() == null) {
            return Response.status(404).build();
        }
        this.logger.info(String.format("GC - REST - Delete - IssueTypeScheme - /%d - Deleted", id));
        return Response.ok(new DeleteModel("IssueTypeScheme", String.format("ID: '%d', Name: '%s' deleted", removedScreen.get().getId(), removedScreen.get().getName()))).build();
    }
}
