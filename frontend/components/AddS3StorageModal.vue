<script setup lang="ts">
import { ref } from 'vue'

const isOpen = ref(false)
const url = ref('')
const accessKey = ref('')
const secretKey = ref('')
const bucket = ref('')
const isLoading = ref(false)

const handleSubmit = async () => {
  isLoading.value = true
  
  // TODO: Implement S3 storage addition logic here
  console.log('Adding S3 storage:', {
    url: url.value,
    accessKey: accessKey.value,
    secretKey: secretKey.value,
    bucket: bucket.value
  })
  
  isLoading.value = false
  isOpen.value = false
}
</script>

<template>
  <div>
    <UButton label="Add S3 Storage" @click="isOpen = true" />
    <UModal v-model="isOpen" prevent-close>
      <UCard>
        <template #header>
          <div class="flex items-center justify-between">
            <h3 class="text-base font-semibold leading-6 text-gray-900 dark:text-white">
              Add S3 Storage
            </h3>
            <UButton color="gray" variant="ghost" icon="i-heroicons-x-mark-20-solid" class="-my-1" @click="isOpen = false" />
          </div>
        </template>
        <form @submit.prevent="handleSubmit" class="space-y-4 p-4 sm:p-6">
          <UInput v-model="url" label="URL" placeholder="Enter S3 endpoint URL" required />
          <UInput v-model="accessKey" label="Access Key" placeholder="Enter access key" type="password" required />
          <UInput v-model="secretKey" label="Secret Key" placeholder="Enter secret key" type="password" required />
          <UInput v-model="bucket" label="Bucket Name" placeholder="Enter bucket name" required />
        </form>
        <template #footer>
          <div class="flex justify-end space-x-3 p-4 sm:px-6">
            <UButton label="Cancel" color="gray" variant="ghost" @click="isOpen = false" />
            <UButton type="submit" label="Add Storage" color="primary" :loading="isLoading" @click="handleSubmit" />
          </div>
        </template>
      </UCard>
    </UModal>
  </div>
</template>

