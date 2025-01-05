import Keycloak, { type KeycloakConfig } from 'keycloak-js'
import { defineNuxtPlugin } from '#app'

export default defineNuxtPlugin(async () => {
  var config = useRuntimeConfig()
  const keycloakConfig: KeycloakConfig = {
    url: config.public.keycloakUrl,
    realm: config.public.keycloakRealm,
    clientId: config.public.keycloakCliendId,
  }

  const keycloak = new Keycloak(keycloakConfig)

  await keycloak.init({
    onLoad: 'login-required',
    silentCheckSsoRedirectUri: window.location.origin + '/',
    checkLoginIframe: false,
  })

  const MIN_VALIDITY_SECONDS = 250
  setInterval(async () => {
    try {
      const refreshed = await keycloak.updateToken(MIN_VALIDITY_SECONDS)
      if (refreshed) {
        console.log('Token was successfully refreshed')
      }
    } catch (err) {
      console.error('Failed to refresh token, or the session has expired', err)
      keycloak.login()
    }
  }, 10000)

  return {
    provide: {
      keycloak,
    },
  }
})
