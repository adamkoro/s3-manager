// plugins/keycloak.client.ts
import Keycloak, { type KeycloakConfig } from 'keycloak-js'
import { defineNuxtPlugin } from '#app'

export default defineNuxtPlugin(async () => {
  // The configuration below should match your Keycloak server setup
  var config = useRuntimeConfig()
  const keycloakConfig: KeycloakConfig = {
    url: config.public.keycloakUrl,
    realm: config.public.keycloakRealm,
    clientId: config.public.keycloakCliendId,
  }

  const keycloak = new Keycloak(keycloakConfig)

  /**
   * Initialize Keycloak on app load.
   * - Check if a valid token is available (from local storage or session).
   * - If there is no valid session, it will force a login redirect if required.
   */
  await keycloak.init({
    onLoad: 'login-required',    // or 'login-required' for immediate redirect
    silentCheckSsoRedirectUri: window.location.origin + '/',
    checkLoginIframe: false, // set to 'true' if you want to periodically check session
  })
  // Optionally refresh the token in some reliable way
  // e.g., if your tokens expire after e.g. 1 minute, set up an interval:
  const MIN_VALIDITY_SECONDS = 250
  setInterval(async () => {
    try {
      // if the token is not valid for at least MIN_VALIDITY_SECONDS, refresh it
      const refreshed = await keycloak.updateToken(MIN_VALIDITY_SECONDS)
      if (refreshed) {
        console.log('Token was successfully refreshed')
      }
    } catch (err) {
      console.error('Failed to refresh token, or the session has expired', err)
      keycloak.login()
    }
  }, 10000) // check token every 10 seconds, for example

  return {
    provide: {
      keycloak,
    },
  }
})
