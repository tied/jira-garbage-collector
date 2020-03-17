package com.tut.rest;

import javax.ws.rs.*;
import com.tut.rest.utils.Auth;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.search.SearchRequestManager;
import org.springframework.beans.factory.annotation.Autowired;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

@Path("/filter")
public class FilterResource {
    private Auth auth;

    @Autowired
    public FilterResource(Auth auth) {
        this.auth = auth;
    }

    @DELETE
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{id}")
    public Response gcForKey(@PathParam("id") long id) {
        if (!auth.canAccess()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        SearchRequestManager searchRequestManager = ComponentAccessor.getComponent(SearchRequestManager.class);
        SearchRequest searchRequest = searchRequestManager.getSearchRequestById(id);

        if(searchRequest == null) {
            return Response.status(404).build();
        }

        searchRequestManager.delete(id);
        return Response.ok(new DeleteModel("Filter", String.format("ID: '%d', Name: '%s' deleted", searchRequest.getId(), searchRequest.getName()))).build();
    }
}
