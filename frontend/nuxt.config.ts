// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2024-11-01',
  devtools: { enabled: true },
  runtimeConfig: {
    public: {
      apiUrl: '',
      keycloakUrl: '',
      keycloakRealm: '',
      keycloakCliendId: ''
    }
  },
  modules: [
    '@nuxtjs/color-mode',
    '@nuxt/ui',
    '@nuxtjs/tailwindcss'
  ]
})