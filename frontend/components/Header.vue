<template>
  <header
      class="header sticky top-0 bg-gray-200 shadow-md flex items-center justify-between px-4 mb-5 rounded z-10">
      <h1 class="w-1/3">
          <div class="flex items-center gap-1 cursor-pointer">
            <UIcon name="i-heroicons-circle-stack-20-solid"/>
            <NuxtLink to="/" class="text-l font-semibold ">S3 Manager</NuxtLink>
          </div>
      </h1>
      <nav class="nav font-semibold">
          <ul class="flex items-center">
              <li class="p-4 cursor-pointer"
                  :class="{ 'border-b-2 border-primary-500  border-opacity-100 text-primary-500': route.path === '/' }">
                  <NuxtLink to="/">Home</NuxtLink>
              </li>
          </ul>
      </nav>
      <div class="w-1/3 flex justify-end gap-2">
          <div class="flex font-semibold items-center">
              <div class="">Welcome,</div>
              <div class="ml-1 text-orange-600">{{ username }}</div>
          </div>

          <UButton @click="handleLogout" label="Logout" />
      </div>
  </header>
</template>

<script setup lang="ts">

const route = useRoute()
const nuxtApp = useNuxtApp()
const keycloak = nuxtApp.$keycloak

const { username } = useUser()

const handleLogout = async () => {
  try {
    const redirectUri = window.location.origin
    await keycloak.logout({
      redirectUri: redirectUri
    })
  } catch (error) {
    console.error('Logout failed:', error)
  }
}
</script>