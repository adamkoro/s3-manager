// composables/useAuth.ts
export const useAuth = () => {
  const nuxtApp = useNuxtApp()

  const getHeaders = () => {
    if (import.meta.server) {
      return {}
    }

    const keycloak = nuxtApp.$keycloak
    if (!keycloak?.authenticated) {
      return {}
    }

    return {
      'Authorization': `Bearer ${keycloak.token}`
    }
  }

  const isAuthenticated = () => {
    if (import.meta.server) {
      return false
    }
    return nuxtApp.$keycloak?.authenticated ?? false
  }

  const waitForAuthentication = () => {
    return new Promise((resolve) => {
      if (import.meta.server) {
        resolve(false)
        return
      }

      const checkAuth = () => {
        if (nuxtApp.$keycloak?.authenticated) {
          resolve(true)
        } else {
          setTimeout(checkAuth, 100)
        }
      }
      checkAuth()
    })
  }

  return {
    getHeaders,
    isAuthenticated,
    waitForAuthentication
  }
}
