package com.assertteam.rest;

import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.Collection;

import com.assertteam.rest.utils.Auth;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.atomic.AtomicBoolean;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.screen.FieldScreenScheme;
import com.atlassian.jira.issue.fields.screen.FieldScreenSchemeManager;
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenSchemeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Path("/screenscheme")
public class ScreenSchemeResource {
    private Auth auth;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public ScreenSchemeResource(Auth auth) {
        this.auth = auth;
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/")
    public Response gcNotDefault() {
        if (!auth.canAccess()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        this.logger.info("GC - REST - Delete - ScreenScheme - / - Started");
        ArrayList<DeleteModel> deleted = new ArrayList<>();
        FieldScreenSchemeManager fssm = ComponentAccessor.getComponent(FieldScreenSchemeManager.class);
        IssueTypeScreenSchemeManager itssm = ComponentAccessor.getIssueTypeScreenSchemeManager();

        fssm.getFieldScreenSchemes().forEach(fss -> {
            Collection itssCollection = itssm.getIssueTypeScreenSchemes(fss);
            AtomicBoolean allDeleted = new AtomicBoolean(true);
            if(itssCollection != null) {
                itssCollection.forEach(itss -> {
                    if(itss != null) {
                        allDeleted.set(false);
                        return;
                    }
                });

                if(itssCollection.size() == 0 || allDeleted.get()) {
                    deleted.add(new DeleteModel("ScreenScheme", String.format("ID: '%d', Name: '%s' deleted", fss.getId(), fss.getName())));
                    // remove association to any screens
                    fssm.removeFieldSchemeItems(fss);
                    // remove field screen scheme
                    fssm.removeFieldScreenScheme(fss);
                }
            }
        });

        this.logger.info("GC - REST - Delete - ScreenScheme - / - Deleted");
        return Response.ok(deleted).build();
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{id}")
    public Response gcForKey(@PathParam("id") long id) {
        if (!auth.canAccess()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        this.logger.info(String.format("GC - REST - Delete - ScreenScheme - /%d - Started", id));
        FieldScreenSchemeManager fssm = ComponentAccessor.getComponent(FieldScreenSchemeManager.class);
        FieldScreenScheme screen = fssm.getFieldScreenScheme(id);

        if(screen == null) {
            return Response.status(404).build();
        }

        fssm.removeFieldSchemeItems(screen);
        fssm.removeFieldScreenScheme(screen);

        this.logger.info(String.format("GC - REST - Delete - ScreenScheme - /%d - Deleted", id));
        return Response.ok(new DeleteModel("ScreenScheme", String.format("ID: '%d', Name: '%s' deleted", screen.getId(), screen.getName()))).build();
    }
}
