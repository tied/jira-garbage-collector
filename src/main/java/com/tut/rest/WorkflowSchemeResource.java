package com.tut.rest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.scheme.Scheme;
import com.atlassian.jira.workflow.AssignableWorkflowScheme;
import com.atlassian.jira.workflow.WorkflowSchemeManager;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/workflowscheme")
public class WorkflowSchemeResource {

    @DELETE
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/")
    public Response gcNotDefault() {
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
        WorkflowSchemeManager schemeManager = ComponentAccessor.getWorkflowSchemeManager();
        AssignableWorkflowScheme workflowScheme = schemeManager.getWorkflowSchemeObj(id);

        if(workflowScheme == null) {
            return Response.status(404).build();
        }

        schemeManager.deleteWorkflowScheme(workflowScheme);
        return Response.ok(new DeleteModel("WorkflowScheme", String.format("ID: '%d', Name: '%s' deleted", workflowScheme.getId(), workflowScheme.getName()))).build();
    }
}
