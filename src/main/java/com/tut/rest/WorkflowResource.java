package com.tut.rest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.WorkflowManager;
import com.atlassian.jira.workflow.WorkflowSchemeManager;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

@Path("/workflow")
public class WorkflowResource {

    @DELETE
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/")
    public Response gcNotDefault() {
        WorkflowManager workflowManager = ComponentAccessor.getWorkflowManager();
        WorkflowSchemeManager schemeManager = ComponentAccessor.getWorkflowSchemeManager();

        workflowManager.getWorkflows().forEach(jiraWorkflow -> {
            if(!jiraWorkflow.isSystemWorkflow() && !jiraWorkflow.isDefault()) {
                Collection<org.ofbiz.core.entity.GenericValue> schemes = schemeManager.getSchemesForWorkflow(jiraWorkflow);
                if(schemes.size() == 0) {
                    workflowManager.deleteWorkflow(jiraWorkflow);
                }
            }
        });

        return Response.ok(new DeleteModel("id", "testMsg")).build();
    }

    @DELETE
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{name}")
    public Response gcForKey(@PathParam("name") String name) {
        WorkflowManager workflowManager = ComponentAccessor.getWorkflowManager();
        JiraWorkflow workflow = workflowManager.getWorkflow(name);
        if(workflow == null) {
            return Response.status(404).build();
        }
        workflowManager.deleteWorkflow(workflow);

        return Response.ok(new DeleteModel("Workflow", String.format("Name: '%s' deleted", workflow.getName()))).build();
    }
}
