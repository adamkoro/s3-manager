export const useUser = () => {
  const username = ref('')
  
  onMounted(() => {
    const nuxtApp = useNuxtApp()
    const keycloak = nuxtApp.$keycloak
    if (keycloak?.tokenParsed?.preferred_username) {
      username.value = keycloak.tokenParsed.preferred_username
    }
  })

  return {
    username
  }
}