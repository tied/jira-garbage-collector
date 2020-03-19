Jira Garbage Collector Resource Plugin. Allows to remove all unused schemes and workflows

#### **How to use:**

Set authorization header with ***admin*** permissions

Send delete request and enjoy :)

#### **Support deleting:**
* Filters by id
* Screens by id / all unused schemes
* Workflow by id / all unused Workflow
* Screen Schemes by id / all unused Screen Schemes
* Workflow Schemes by id / all unused Workflow Schemes
* Issue Type Schemes by id / all unused Issue Type Schemes
* Issue Type Screen Schemes by id / all unused Issue Type Screen Schemes


#### Possible Routes:
* /rest/gc/1.0/screen
* /rest/gc/1.0/screen/:id
* /rest/gc/1.0/filter/:id
* /rest/gc/1.0/workflow
* /rest/gc/1.0/workflow/:id
* /rest/gc/1.0/screenscheme
* /rest/gc/1.0/screenscheme/:id
* /rest/gc/1.0/issuetypescheme
* /rest/gc/1.0/issuetypescheme/:id
* /rest/gc/1.0/issuetypescreenscheme
* /rest/gc/1.0/issuetypescreenscheme/:id