# Auth0 integration steps
[Official documentation](https://auth0.com/docs/get-started/architecture-scenarios/spa-api/part-2#auth0-configuration)

## Setting up your account 

1. Create an Auth0 account at [Auth0](https://auth0.com/).
![Sign up page](guide_pictures/auth0_signup.png)
2. Set up the tenant domain and other settings as per your requirements.
![Tenant Set up](guide_pictures/tenant_setup.png)
    - In our case select React and Single Page Application as the application type (Should be automatic).
3. Click "Create Application"

## Create the application
1. Go to the settings page of your application and note down the following details:
    - Domain
    - Client ID
    - Client Secret
![Application Settings](guide_pictures/app_settings.png)

2. Add the callback URL for your application (e.g., `http://localhost:3000/callback` for local development).
   - This is the URL where Auth0 will redirect users after they have authenticated.
3. Add the allowed logout URL for your application (e.g., `http://localhost:3000` for local development).
4. Add the allowed web origins for your application (e.g., `http://localhost:3000` for local development).
5. Save the changes.

## Create an API for the Spring Boot backend
1. Go to the "APIs" section under "Applications" in the Auth0 dashboard.
2. Click "Create API".
![Create API](guide_pictures/create_api.png)
3. Fill in the details:
    - Name: Your API name (e.g., "My Spring Boot API")
    - Identifier: A unique identifier for your API (e.g., `https://myapi.example.com`)
      - This will be used as the audience in your Spring Boot application and frontend application.
    - Signing Algorithm: RS256
![API Information form](guide_pictures/API_information_form.png)
4. Click "Create".

## Set up the application to use Auth0

`Note`: all values with `your-...` should be replaced with your actual Auth0 values. Anything inputed by you in the previous steps 
should be used here, be attentive to the names, they should match what you have set in Auth0.

1. In the environment variables of your frontend application, set the following variables:
```env
REACT_APP_DOMAIN=your-auth0-domain
REACT_APP_CLIENT_ID=your-app-client-id
REACT_APP_CALLBACK_URL=http://localhost:3000/callback
REACT_APP_API_SERVER_URL=http://localhost:8080
REACT_APP_AUDIENCE=your-api-identifier
```
### Client Id and Domain can be found in the application settings page.
![Client Id and Domain](guide_pictures/client_id_domain.png)

### API Identifier can be found in the API settings page.
![API identifier](guide_pictures/api_identifier.png)

## Set up the Spring Boot application to use Auth0
1. In the `application.yml` file of your Spring Boot application , set the following properties:
```yaml
okta:
  oauth2:
    issuer: https://<your-auth0-domain>/
    audience: <your-api-identifier>
```

## Set up authorization and sending permissions in the access token
1. Go to the "APIs" section under "Applications" in the Auth0 dashboard
2. Select the API you created for your Spring Boot backend.
3. Go to the "Permissions" tab and add the required permissions (scopes) for your API.
![Add permissions](guide_pictures/adding_perms.png)
   - `Note:` if you use the code in this repository, I used `read:customer` and `read:admin-messages` permissions for the roles.
   - For example, you might add `read:messages` and `write:messages` permissions.
   - These permissions will be used in your Spring Boot application to secure endpoints. (e.g., `@PreAuthorize("hasAuthority('read:admin-messages')")`)

## Create a role and add permissions to it
1. Go to the "Roles" section under "User Management" in the Auth0 dashboard.
2. Click "Create Role".
![Adding role](guide_pictures/add_role.png)
3. Fill in the details:
    - Name: Your role name (e.g., "default-role")
    - Description: A brief description of the role.
4. Click "Create".
5. After you will be redirected to the role details page, go to the "Permissions" tab.
   - Otherwise, you can click on the role from the roles list to go to the role details page.
6. Click "Add Permissions" and select the permissions you created for your API.
![Add permissions page](guide_pictures/add_permission_page.png)
7. Select the API and the permissions to add to the role, then click "Add Permissions".
![Add permissions to role](guide_pictures/adding_permissions_to_role_for_api.png)

### RBAC warning

You should see a warning at the top of the page indicating that RBAC is not enabled for the API.
![RBAC warning](guide_pictures/rbac_warning.png)

Click on the link in the warning to go to the API settings page, and scroll down to the "RBAC Settings" section.
Enable the "Enable RBAC" and "Add Permissions in the Access Token" options, then click "Save".
![Enable RBAC](guide_pictures/RBAC_setting.png)

## Add role to a user
1. Go to the "Users" section under "User Management" in the Auth0 dashboard.
2. Click on a user to view their details.
3. Go to the "Roles" tab and click "Assign Roles".
4. Select the role you created earlier and click "Assign Roles".
5. The user should now have the role assigned.

## Conclusion 
You have successfully set up Auth0 authentication and authorization for your React frontend and Spring Boot backend applications. You can now use Auth0 to manage users, roles, and permissions in your applications.

# Bonus settings

## Spring boot

### Get user token
To get the user token in your Spring Boot application, you can use the following code snippet in your controller or service:
```java

@GetMapping("/protected/customer")
@PreAuthorize("hasAuthority('read:customer')")
public Message getProtectedCustomer(@AuthenticationPrincipal Jwt user) {
    return messageService.getCustomerMessage(user);
}
```
This will inject the JWT token of the authenticated user into the `user` parameter, which you can then use to access user information or claims.

You can call the user id using:
```java
String id = user.getSubject();
```

## Auth0

### Add phone number as required field
1. Go to the "Authentication" section and click on "Database".
2. Click on the database connection you are using (e.g., "Username-Password-Authentication").
3. Go to the "Attributes" tab.
4. Click "Activate" here.
![Custom field prompt](guide_pictures/activate_custom_fields.png)

5. Click on "Add Attribute" and select "phone_number" from the side menu.
![Side pane](guide_pictures/add_attribute_side_pane.png)

6. Select and put the settings as per your requirements and click "Save".

![Phone number options](guide_pictures/phone_options.png)

`Note:` You can also add Username as a required field by following the same steps.

`Note:` There's a warning at the top of the options because you need a SMS provider to send verification codes.

# Auth0 action to add default role

## Create Management API application
1. Go to the "Applications" section and click on "Create Application".
![Create Management API.png](guide_pictures/creating_management_api.png)
2. Fill in the details:
    - Name: Your application name (e.g., "Management API App")
    - Application Type: Machine to Machine Applications
3. Click "Create".
4. Select the "Auth0 Management API" from the list of APIs.
![Create.png](guide_pictures/adding_app_management.png)
5. In the "Permissions" section, select the following scopes (at least):
    - create:role_members
6. Click "Authorize".
7. Then on the settings page of the application, note down the Client ID and Client Secret.
8. Navigate to "APIs" section and authorize the application to access the Auth0 Management API if not done already.
9. You will also add the Spring Boot API you created earlier as an allowed audience for this application in the settings page.
![Machine to machine UI](guide_pictures/apis_management_api.png)
10. Click "Save".

## Create Post Login Action to assign default role

1. Go to the "Actions" section then "Triggers" in the Auth0 dashboard.
2. Click on the "Post Login" flow.
3. Click on "Add Action" and then "Create Custom".
![Post login page](guide_pictures/post_login_flow_page.png)
4. Fill in the details:
    - Name: Your action name (e.g., "Assign Default Role")
    - Trigger: Leave as "Post Login"
    - Runtime: Leave as default (Node.js 22 as of writing)
5. Click "Create".
6. Copy and paste the code below into the code editor, replacing any existing code.
    - Make sure to replace `"default-role-id"` with the actual role ID you created earlier. 
    - You can find the role ID in the "Roles" section under "User Management" in the Auth0 dashboard by clicking on the role and checking the role ID.
    - ![Getting role id](guide_pictures/role_id.png)
7. Add the dependency `auth0` by clicking on the "Add Dependency" button and writing `auth0@latest` in the input field.
   
![Flow Auth0 UI](guide_pictures/flow_dep_icon.png)

![Adding dependencies icon](guide_pictures/adding_dep.png)

9. Add the environment variables as per the instructions below.
   - domain - Your Auth0 domain (e.g., `your-domain.auth0.com`)
   - clientId - Your Management API client ID (from the application you created earlier)
   - clientSecret - Your Management API client secret
10. Click "Deploy" to save and deploy the action.
11. Back in the Post Login flow page, connect the action to the flow by dragging the action from the right side panel to the flow diagram.
![Flow chart](guide_pictures/flow_chart.png)
12. Click "Apply".
13. Test the action by creating a new user and logging in. The user should be assigned the default role automatically.
   - You can verify this by checking the user's roles in the Auth0 dashboard under "User Management" -> "Users".

### Environment Variables
These can be found in the Auth0 Management API applications settings.
You can also read the documentation [here](https://auth0.com/docs/manage-api-clients/manage-applications/create-management-api-application).
```dotenv
domain=your-auth0-domain
clientId=your-management-api-client-id
clientSecret=your-management-api-client-secret
```
### Code

#### Latest code for newer SDK versions
```js
exports.onExecutePostLogin = async (event, api) => {
  // Only on first login
  if (event.stats.logins_count !== 1) {
    return;
  }

  const { ManagementClient } = require("auth0");

  const management = new ManagementClient({
    domain: event.secrets.domain,
    clientId: event.secrets.clientId,
    clientSecret: event.secrets.clientSecret,
  });

  const userId = event.user.user_id;
  const roleId = "default-role-id"; // Replace with your role ID

  try {
    await management.users.roles.assign(userId, {
      roles: [roleId],
    });

    console.log(`Assigned role ${roleId} to user ${userId}`);
  } catch (e) {
    console.log("Error assigning role:", e);
  }
};
```

#### Legacy code for older SDK versions
For using older versions :
```js
exports.onExecutePostLogin = async (event, api) => {
  // If we want to do it only when the first login is done.
  if (event.stats.logins_count !== 1) {
    return;
  }

  // If we want to make sure the user has a role, and add it whenever none is found
  // if (event.authorization && event.authorization.roles && event.authorization.roles.length > 0) {
  //   return;
  // }

  const ManagementClient = require('auth0').ManagementClient;

  const management = new ManagementClient({
      domain: event.secrets.domain,
      clientId: event.secrets.clientId,
      clientSecret: event.secrets.clientSecret,
  });

  const params =  { id : event.user.user_id};
  // This is the role id you want to be the default role.
  const data = { "roles" : ["default-role-id"] };

  try {
      // If using an older version of the SDK, use `assignRolesToUser` instead.
    const res = await management.users.assignRoles(params, data)
  } catch (e) {
    console.log(e)
    // Handle error
  }
};
```

