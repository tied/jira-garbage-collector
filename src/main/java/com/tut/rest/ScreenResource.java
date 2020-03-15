package com.tut.rest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.screen.*;
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenScheme;
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenSchemeManager;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.atomic.AtomicBoolean;

@Path("/screen")
public class ScreenResource {

    @DELETE
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/")
    public Response gcNotDefault() {
        FieldScreenManager fieldScreenManager = ComponentAccessor.getFieldScreenManager();
        //FieldScreenFactory fieldScreenFactory = ComponentAccessor.getComponent(FieldScreenFactory.class);

        FieldScreenSchemeManager fieldScreenSchemeManager = ComponentAccessor.getComponent(FieldScreenSchemeManager.class);

        //WorkflowManager workflowManager = ComponentAccessor.getWorkflowManager();

        //ViewFieldScreens viewFieldScreens = new ViewFieldScreens(fieldScreenManager, fieldScreenFactory, fieldScreenSchemeManager, workflowManager)

        fieldScreenManager.getFieldScreens().forEach(fieldScreen -> {
            AtomicBoolean allEmptyOrNull = new AtomicBoolean(true);
            fieldScreenSchemeManager.getFieldScreenSchemes(fieldScreen).forEach(fieldScreenScheme -> {
                if(fieldScreenScheme != null) {
                    allEmptyOrNull.set(false);
                }
            });

            if(!allEmptyOrNull.get()) {
                return;
            }

            // TODO: check workflows
//            viewFieldScreens.getWorkflows(fieldScreen).each { workflow ->
//                if(workflow != null) {
//                    allEmptyOrNull = false;
//                    return;
//                }
//            }

            if(allEmptyOrNull.get()) {
                //fieldScreenManager.removeFieldScreen(fieldScreen.getId());
            }
            System.out.println(String.format("Gonna delete '%s' with id '%d'", fieldScreen.getName(), fieldScreen.getId()));
        });


        return Response.ok(new IssueTypeScreenSchemeResourceModel("id", "testMsg")).build();
    }

    @DELETE
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{id}")
    public Response gcForKey(@PathParam("id") long id) {
        IssueTypeScreenSchemeManager schemeManager = ComponentAccessor.getIssueTypeScreenSchemeManager();
        IssueTypeScreenScheme screen = schemeManager.getIssueTypeScreenScheme(id);
        //screen.remove();

        System.out.println(String.format("Gonna delete '%s' with id '%d'", screen.getName(), screen.getId()));

        return Response.ok(new IssueTypeScreenSchemeResourceModel("id", "testMsg")).build();
    }
}
