<template>
  <div>
    <UButton label="Add S3 Storage" icon="i-heroicons-plus-20-solid" @click="isOpen = true" />
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
          <UInput v-model="bucket" label="Bucket" placeholder="Enter bucket" required />
          <UInput v-model="region" label="Region" placeholder="Enter region" />
        </form>
        <template #footer>
            <div class="flex justify-between">
              <UButton type="cancel" size="xl" label="Cancel" @click="(isOpen = false)">
                <template #trailing>
                  <UIcon name="i-heroicons-no-symbol-20-solid" />
                </template>
              </UButton>
              <UButton type="submit" size="xl" label="Save" @click="handleSubmit">
                <template #trailing>
                  <UIcon name="i-heroicons-check-20-solid" />
                </template>
              </UButton>
            </div>
          </template>
      </UCard>
    </UModal>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const isOpen = ref(false)
const url = ref('')
const accessKey = ref('')
const secretKey = ref('')
const bucket = ref('')
const region = ref('')
const isLoading = ref(false)
const toast = useToast()
const { getHeaders } = useAuth()


async function handleSubmit() {
  isLoading.value = true
  const { data, error } = await useFetch(useRuntimeConfig().public.apiUrl + '/api/s3', {
    method: 'POST',
    body: JSON.stringify({ endpointUrl: url.value, accessKey: accessKey.value, secretKey: secretKey.value, bucketName: bucket.value, region: region.value }),
    headers: {
      ...getHeaders(),
      ...useRequestHeaders(['cookie'])
    },
    credentials: 'include',
  })
  if (error.value) {
    toast.add({ title: 'S3 storage create error', description: error.value.data.error + '', icon: 'i-heroicons-no-symbol-20-solid', color: 'red' })
  }
  if (data.value) {
    isLoading.value = false
    isOpen.value = false
    url.value = ''
    accessKey.value = ''
    secretKey.value = ''
    bucket.value = ''
    region.value = ''
    toast.add({ title: 'S3 storage successfully created', description: state.value.name + ' created', icon: 'i-heroicons-check-circle-20-solid' })
  }
}

// const handleSubmit = async () => {
//   isLoading.value = true
  
//   //TODO Implement S3 storage addition logic here
//   console.log('Adding S3 storage:', {
//     url: url.value,
//     accessKey: accessKey.value,
//     secretKey: secretKey.value,
//     bucket: bucket.value,
//     region: region.value
//   })

//   toast.add({ title: 'S3 storage successfully added', icon: 'i-heroicons-check-circle-20-solid' })
  
//   isLoading.value = false
//   isOpen.value = false
//   url.value = ''
//   accessKey.value = ''
//   secretKey.value = ''
//   bucket.value = ''
//   region.value = ''
// }
</script>

