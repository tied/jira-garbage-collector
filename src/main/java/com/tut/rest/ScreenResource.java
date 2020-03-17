package com.tut.rest;

import javax.ws.rs.*;
import java.util.ArrayList;
import com.tut.rest.utils.Auth;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.atlassian.jira.issue.fields.screen.*;
import org.springframework.stereotype.Component;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.workflow.WorkflowActionsBean;
import org.springframework.beans.factory.annotation.Autowired;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

@Component
@Path("/screen")
public class ScreenResource {
    private Auth auth;

    @Autowired
    public ScreenResource(Auth auth) {
        this.auth = auth;
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/")
    public Response gcNotDefault() {
        if (!auth.canAccess()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        FieldScreenManager fieldScreenManager = ComponentAccessor.getFieldScreenManager();
        FieldScreenSchemeManager fieldScreenSchemeManager = ComponentAccessor.getComponent(FieldScreenSchemeManager.class);

        ArrayList<Long> allScreensIds = new ArrayList<>();
        fieldScreenManager.getFieldScreens().forEach(screen -> allScreensIds.add(screen.getId()));
        Iterable<FieldScreenScheme> screenSchemes = fieldScreenSchemeManager.getFieldScreenSchemes();

        ArrayList<Long> screenIdsWithScheme = new ArrayList<>();

        screenSchemes.forEach(fieldScreenScheme -> {
            ArrayList<Long> items = new ArrayList<>();

            fieldScreenSchemeManager.getFieldScreenSchemeItems(fieldScreenScheme).forEach(fieldScreenSchemeItem -> {
                items.add(fieldScreenSchemeItem.getFieldScreen().getId());
            });
            screenIdsWithScheme.addAll(items);
        });

        WorkflowActionsBean workflowBean = new WorkflowActionsBean();
        ArrayList<Long> screenIdsWithWorkflowAction = new ArrayList<>();

        ComponentAccessor.getWorkflowManager().getWorkflows().forEach(jiraWorkflow -> {
            ArrayList<Long> items = new ArrayList<>();
            jiraWorkflow.getAllActions().forEach(actionDescriptor -> {
                try {
                    items.add(workflowBean.getFieldScreenForView(actionDescriptor).getId());
                } catch (Exception e) {}
            });

            screenIdsWithWorkflowAction.addAll(items);
        });

        allScreensIds.removeAll(screenIdsWithScheme);
        allScreensIds.removeAll(screenIdsWithWorkflowAction);
        allScreensIds.forEach(screen -> {
            fieldScreenManager.removeFieldScreen(screen);
        });

        return Response.ok(new DeleteModel("id", "testMsg")).build();
    }

    @DELETE
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{id}")
    public Response gcForKey(@PathParam("id") long id) {
        if (!auth.canAccess()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        FieldScreenManager fieldScreenManager = ComponentAccessor.getFieldScreenManager();
        FieldScreen screen = fieldScreenManager.getFieldScreen(id);

        if(screen == null) {
            return Response.status(404).build();
        }

        fieldScreenManager.removeFieldScreen(screen.getId());
        return Response.ok(new DeleteModel("Screen", String.format("ID: '%d', Name: '%s' deleted", screen.getId(), screen.getName()))).build();
    }
}
