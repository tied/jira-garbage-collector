package com.tut.rest;

import javax.ws.rs.*;
import java.util.Collection;
import com.tut.rest.utils.Auth;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.WorkflowManager;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.workflow.WorkflowSchemeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

@Path("/workflow")
public class WorkflowResource {
    private Auth auth;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public WorkflowResource(Auth auth) {
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
        this.logger.info("Cleaner - REST - Delete - Workflow - / - Started");
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
        if (!auth.canAccess()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        this.logger.info(String.format("Cleaner - REST - Delete - Workflow - /%s - Started", name));
        WorkflowManager workflowManager = ComponentAccessor.getWorkflowManager();
        JiraWorkflow workflow = workflowManager.getWorkflow(name);
        if(workflow == null) {
            return Response.status(404).build();
        }
        workflowManager.deleteWorkflow(workflow);

        return Response.ok(new DeleteModel("Workflow", String.format("Name: '%s' deleted", workflow.getName()))).build();
    }
}
