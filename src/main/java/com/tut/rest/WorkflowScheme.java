package com.tut.rest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.scheme.Scheme;
import com.atlassian.jira.workflow.WorkflowSchemeManager;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/workflowscheme")
public class WorkflowScheme {

    @DELETE
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/")
    public Response gcNotDefault() {
        WorkflowSchemeManager schemeManager = ComponentAccessor.getWorkflowSchemeManager();
        List<Scheme> temp = schemeManager.getAssociatedSchemes(false);
        temp.forEach(scheme -> {
            System.out.println(String.format("Gonna delete '%s' scheme", scheme.getName()));
        });


        List<Scheme> temp2 = schemeManager.getAssociatedSchemes(false);
        temp2.forEach(scheme -> {
            System.out.println(String.format("Gonna delete '%s' scheme", scheme.getName()));
        });




        return Response.ok(new IssueTypeScreenSchemeResourceModel("id", "testMsg")).build();
    }

    @DELETE
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{id}")
    public Response gcForKey(@PathParam("id") String id) {


        //TODO: add exception catch for not found
        //System.out.println(String.format("Gonna delete '%s' workflow with id '%d'", workflow.getName()));

        return Response.ok(new IssueTypeScreenSchemeResourceModel("id", "testMsg")).build();
    }
}
