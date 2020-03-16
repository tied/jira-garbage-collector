package com.tut.rest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.scheme.Scheme;
import com.atlassian.jira.workflow.AssignableWorkflowScheme;
import com.atlassian.jira.workflow.WorkflowSchemeManager;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.tut.rest.utils.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Component
@Path("/workflowscheme")
public class WorkflowSchemeResource {
    private Auth auth;

    @Autowired
    public WorkflowSchemeResource(Auth auth) {
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
        WorkflowSchemeManager schemeManager = ComponentAccessor.getWorkflowSchemeManager();

        List<Scheme> schemeObjects = schemeManager.getSchemeObjects();

        schemeObjects.forEach(scheme -> {
            try {
                AssignableWorkflowScheme wf = schemeManager.getWorkflowSchemeObj(scheme.getId());
                if(schemeManager.getProjectsUsing(wf).size() == 0 && !wf.isDefault()) {
                    schemeManager.deleteWorkflowScheme(wf);
                }
            } catch (Exception c) {
                // noop
            }
        });

        return Response.ok(new DeleteModel("id", "testMsg")).build();
    }

    @DELETE
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{id}")
    public Response gcForKey(@PathParam("id") int id) {
        if (!auth.canAccess()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        WorkflowSchemeManager schemeManager = ComponentAccessor.getWorkflowSchemeManager();
        AssignableWorkflowScheme workflowScheme = schemeManager.getWorkflowSchemeObj(id);

        if(workflowScheme == null) {
            return Response.status(404).build();
        }

        schemeManager.deleteWorkflowScheme(workflowScheme);
        return Response.ok(new DeleteModel("WorkflowScheme", String.format("ID: '%d', Name: '%s' deleted", workflowScheme.getId(), workflowScheme.getName()))).build();
    }
}
