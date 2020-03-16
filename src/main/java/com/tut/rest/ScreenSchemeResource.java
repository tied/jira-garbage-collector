package com.tut.rest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.screen.FieldScreenScheme;
import com.atlassian.jira.issue.fields.screen.FieldScreenSchemeManager;
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenScheme;
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenSchemeManager;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

@Path("/screenscheme")
public class ScreenSchemeResource {

    @DELETE
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/")
    public Response gcNotDefault() {
        FieldScreenSchemeManager fssm = ComponentAccessor.getComponent(FieldScreenSchemeManager.class);
        IssueTypeScreenSchemeManager itssm = ComponentAccessor.getIssueTypeScreenSchemeManager();

        fssm.getFieldScreenSchemes().forEach(fss -> {
            try {
                Collection itssCollection = itssm.getIssueTypeScreenSchemes(fss);
                AtomicBoolean allDeleted = new AtomicBoolean(true);

                itssCollection.forEach(itss -> {
                    if(itss != null) {
                        allDeleted.set(false);
                        return;
                    }
                });

                if(itssCollection.size() == 0 || allDeleted.get()) {
                    // remove association to any screens
                    fssm.removeFieldSchemeItems(fss);
                    // remove field screen scheme
                    fssm.removeFieldScreenScheme(fss);
                }
            } catch(Exception e) {
                //noop
            }
        });

        return Response.ok(new DeleteModel("id", "testMsg")).build();
    }

    @DELETE
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{id}")
    public Response gcForKey(@PathParam("id") long id) {
        FieldScreenSchemeManager fssm = ComponentAccessor.getComponent(FieldScreenSchemeManager.class);
        FieldScreenScheme screen = fssm.getFieldScreenScheme(id);

        if(screen == null) {
            return Response.status(404).build();
        }

        fssm.removeFieldSchemeItems(screen);
        fssm.removeFieldScreenScheme(screen);
        return Response.ok(new DeleteModel("ScreenScheme", String.format("ID: '%d', Name: '%s' deleted", screen.getId(), screen.getName()))).build();
    }
}
