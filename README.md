# Auth0 action to add default role
### Environment Variables
These can be found in the Auth0 Management API applications settings.
You can also read the documentation [here](https://auth0.com/docs/manage-api-clients/manage-applications/create-management-api-application).
```dotenv
domain=your-auth0-domain
clientId=your-management-api-client-id
clientSecret=your-management-api-client-secret
```
### Code
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
  const data = { "roles" : ["rol_IatI22wjEZr0fwsp"]};

  try {
    const res = await management.users.assignRoles(params, data)
  } catch (e) {
    console.log(e)
    // Handle error
  }
};
```