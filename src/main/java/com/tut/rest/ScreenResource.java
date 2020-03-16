package com.tut.rest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.screen.*;
import com.atlassian.jira.workflow.WorkflowActionsBean;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@Path("/screen")
public class ScreenResource {

    @DELETE
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/")
    public Response gcNotDefault() {
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
            FieldScreen sc = fieldScreenManager.getFieldScreen(screen);
            System.out.println(String.format("Gonna delete '%s' with id '%d'", sc.getName(), sc.getId()));
            // fieldScreenManager.removeFieldScreen(screen);
        });

        return Response.ok(new IssueTypeScreenSchemeResourceModel("id", "testMsg")).build();
    }

    @DELETE
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{id}")
    public Response gcForKey(@PathParam("id") long id) {
        FieldScreenManager fieldScreenManager = ComponentAccessor.getFieldScreenManager();
        FieldScreen screen = fieldScreenManager.getFieldScreen(id);

        System.out.println(String.format("Gonna delete '%s' with id '%d'", screen.getName(), screen.getId()));
        // fieldScreenManager.removeFieldScreen(screen.getId());

        return Response.ok(new IssueTypeScreenSchemeResourceModel("id", "testMsg")).build();
    }
}
