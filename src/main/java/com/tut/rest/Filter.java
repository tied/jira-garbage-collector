package com.tut.rest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.issue.search.SearchRequestManager;
import com.atlassian.jira.workflow.WorkflowSchemeManager;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/filter")
public class Filter {

    @DELETE
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/")
    public Response gcNotDefault() {


        return Response.ok(new IssueTypeScreenSchemeResourceModel("id", "testMsg")).build();
    }

    @DELETE
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{id}")
    public Response gcForKey(@PathParam("id") long id) {
        SearchRequestManager searchRequestManager = ComponentAccessor.getComponent(SearchRequestManager.class);
        SearchRequest searchRequest = searchRequestManager.getSearchRequestById(id);
        //TODO: add exception catch for not found
        System.out.println(String.format("Gonna delete '%s' workflow with id '%d'", searchRequest.getName(), searchRequest.getId()));

        // searchRequestManager.delete(id);
        return Response.ok(new IssueTypeScreenSchemeResourceModel("id", "testMsg")).build();
    }
}
