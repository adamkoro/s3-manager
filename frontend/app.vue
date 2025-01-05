<template>
  <NuxtLayout>
      <NuxtLoadingIndicator color="#FE7C3F" />
      <NuxtPage />
    <UNotifications :timeout="1000" />
  </NuxtLayout>
</template>