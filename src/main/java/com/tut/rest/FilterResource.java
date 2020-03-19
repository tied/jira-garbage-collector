package com.tut.rest;

import javax.ws.rs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tut.rest.utils.Auth;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.search.SearchRequestManager;
import org.springframework.beans.factory.annotation.Autowired;

@Path("/filter")
public class FilterResource {
    private Auth auth;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public FilterResource(Auth auth) {
        this.auth = auth;
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{id}")
    public Response gcForKey(@PathParam("id") long id) {
        if (!auth.canAccess()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        this.logger.info(String.format("GC - REST - Delete - Filter - /%d - Started", id));
        SearchRequestManager searchRequestManager = ComponentAccessor.getComponent(SearchRequestManager.class);
        SearchRequest searchRequest = searchRequestManager.getSearchRequestById(id);

        if(searchRequest == null) {
            return Response.status(404).build();
        }

        searchRequestManager.delete(id);
        this.logger.info(String.format("GC - REST - Delete - Filter - /%d - Deleted", id));
        return Response.ok(new DeleteModel("Filter", String.format("ID: '%d', Name: '%s' deleted", searchRequest.getId(), searchRequest.getName()))).build();
    }
}
