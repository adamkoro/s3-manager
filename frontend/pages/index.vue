<template>
    <UContainer>
      <div>
        <div class="flex items-center justify-between p-2">
          <div class="">
            <UTooltip text="Update s3 storage list">
              <UButton icon="i-heroicons-arrow-path-20-solid" label="Refresh" @click="fetchS3Storages" />
            </UTooltip>
          </div>
          <div class="w-1/3 flex justify-end gap-2">
            <UTooltip text="Create S3 storage">
              <UButton label="Add S3 Storage" icon="i-heroicons-plus-20-solid" @click="isAddOpen = true" />
              <UModal v-model="isAddOpen" prevent-close>
                <UCard>
                  <template #header>
                    <div class="flex items-center justify-between">
                      <h3 class="text-base font-semibold leading-6 text-gray-900 dark:text-white">
                        Add S3 Storage
                      </h3>
                      <UButton color="gray" variant="ghost" icon="i-heroicons-x-mark-20-solid" class="-my-1" @click="isAddOpen = false; cleanFormValues()" />
                    </div>
                  </template>
                  <form @submit.prevent="addS3Storage" class="space-y-4 p-4 sm:p-6">
                    <UInput v-model="url" label="URL" placeholder="Enter S3 endpoint URL" required />
                    <UInput v-model="accessKey" label="Access Key" placeholder="Enter access key" type="password" required />
                    <UInput v-model="secretKey" label="Secret Key" placeholder="Enter secret key" type="password" required />
                    <UInput v-model="bucket" label="Bucket" placeholder="Enter bucket" required />
                    <UInput v-model="region" label="Region" placeholder="Enter region" />
                  </form>
                  <template #footer>
                      <div class="flex justify-between">
                        <UButton type="cancel" size="xl" label="Cancel" @click="isAddOpen = false; cleanFormValues()">
                          <template #trailing>
                            <UIcon name="i-heroicons-no-symbol-20-solid" />
                          </template>
                        </UButton>
                        <UButton type="submit" size="xl" label="Save" @click="addS3Storage">
                          <template #trailing>
                            <UIcon name="i-heroicons-check-20-solid" />
                          </template>
                        </UButton>
                      </div>
                    </template>
                </UCard>
              </UModal>
            </UTooltip>
          </div>
        </div>
        <div v-if="pending" class="flex flex-col items-center mt-6">
          <UIcon name="i-heroicons-arrow-path-20-solid" class="text-gray-500 text-2xl animate-spin" />
          <p class="mt-3 text-sm">Loading...</p>
        </div>
        <div v-else-if="s3Storages && s3Storages.length > 0" class="grid grid-cols-1 gap-6">
          <div v-for="storage in s3Storages" :key="storage.id">
            <div class="bg-gray-300 text-black rounded border-2 border-orange-500 p-1 shadow-lg">
              <div class="flex items-center gap-2">
                <UTooltip text="Browse storage">
                  <UButton type="submit" size="xl" :label="storage.endpointUrl" @click="router.push(`/s3/${storage.id}`)" />
                </UTooltip>
                <UTooltip text="Edit storage">
                  <UButton icon="i-heroicons-pencil-20-solid" @click="openEditModal(storage.id)"/>
                  <UModal v-model="isEditOpen" prevent-close>
                    <UCard>
                      <template #header>
                        <div class="flex items-center justify-between">
                          <h3 class="text-base font-semibold leading-6 text-gray-900 dark:text-white">
                            Edit S3 Storage
                          </h3>
                          <UButton color="gray" variant="ghost" icon="i-heroicons-x-mark-20-solid" class="-my-1" @click="isEditOpen = false; cleanFormValues()" />
                        </div>
                      </template>
                      <form @submit.prevent="updateS3Storage(storage.id)" class="space-y-4 p-4 sm:p-6">
                        <UInput v-model="url" label="URL" placeholder="Enter S3 endpoint URL" required />
                        <UInput v-model="accessKey" label="Access Key" placeholder="Enter access key" type="password" required />
                        <UInput v-model="secretKey" label="Secret Key" placeholder="Enter secret key" type="password" required />
                        <UInput v-model="bucket" label="Bucket" placeholder="Enter bucket" required />
                        <UInput v-model="region" label="Region" placeholder="Enter region" />
                      </form>
                      <template #footer>
                        <div class="flex justify-between">
                          <UButton type="cancel" size="xl" label="Cancel" @click="isEditOpen = false; cleanFormValues()">
                            <template #trailing>
                              <UIcon name="i-heroicons-no-symbol-20-solid" />
                            </template>
                          </UButton>
                          <UButton type="submit" size="xl" label="Update" @click="updateS3Storage(storage.id)">
                            <template #trailing>
                              <UIcon name="i-heroicons-check-20-solid" />
                            </template>
                          </UButton>
                        </div>
                      </template>
                    </UCard>
                  </UModal>
                </UTooltip>
                <UTooltip text="Delete storage">
                  <UButton type="submit" icon="i-heroicons-trash-20-solid" color="red" @click="(isDeleteOpen = true)" />
                  <UModal v-model="isDeleteOpen">
                    <UCard :ui="{ divide: 'divide-y divide-gray-100' }">
                      <template #header>
                        <div class="flex items-center justify-between ">
                          <h3 class="text-base font-semibold leading-6 text-gray-900 ">
                            Delete S3 Storage
                          </h3>
                          <UButton color="gray" variant="ghost" icon="i-heroicons-x-mark-20-solid" class="-my-1"
                            @click="(isDeleteOpen = false) && (storage.endpointUrl = '')" />
                        </div>
                      </template>
                      <div v-if="storage.endpointUrl" class="flex flex-wrap gap-1">
                        <div class="">Are you sure you want to delete</div>
                        <div class="text-red-600">{{ storage.endpointUrl }}</div>
                        <div>?</div>
                      </div>
                      <template #footer>
                        <div class="flex justify-between">
                          <UButton type="cancel" size="xl" label="Cancel"
                            @click="(isDeleteOpen = false) && (storage.endpointUrl = '')">
                            <template #trailing>
                              <UIcon name="i-heroicons-no-symbol-20-solid" />
                            </template>
                          </UButton>
                          <UButton type="submit" size="xl" label="Delete" color="red" @click="deleteS3Storage(storage.id)">
                            <template #trailing>
                              <UIcon name="i-heroicons-trash-20-solid" />
                            </template>
                          </UButton>
                        </div>
                      </template>
                    </UCard>
                  </UModal>
                </UTooltip>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="flex flex-col items-center mt-6">
          <UIcon name="i-heroicons-circle-stack-20-solid" class="text-gray-500 text-2xl" />
          <p class="mt-3 text-sm">No S3 storages found.</p>
        </div>
      </div>
    </UContainer>
</template>

<script setup lang="ts">
const toast = useToast()
const router = useRouter()
const config = useRuntimeConfig()
const { getHeaders, waitForAuthentication } = useAuth()
const s3Storages = ref([])
const pending = ref(true)
const error = ref(null)
const isAddOpen = ref(false)
const isDeleteOpen = ref(false)
const isEditOpen = ref(false)
const url = ref('')
const accessKey = ref('')
const secretKey = ref('')
const bucket = ref('')
const region = ref('')

export interface S3Storage {
  id: string;
  endpointUrl: string;
  accessKey: string;
  secretKey: string;
  bucketName: string;
  region: string;
}

function cleanFormValues() {
  url.value = ''
  accessKey.value = ''
  secretKey.value = ''
  bucket.value = ''
  region.value = ''
}

async function openEditModal(id: string) {
  cleanFormValues()
  const storage = s3Storages.value.find(s => s.id === id)
  if (storage) {
    url.value = storage.endpointUrl
    accessKey.value = storage.accessKey
    secretKey.value = storage.secretKey
    bucket.value = storage.bucketName
    region.value = storage.region
    isEditOpen.value = true
  }
}

async function addS3Storage() {
  pending.value = true
  const { data, error } = await useFetch(useRuntimeConfig().public.apiUrl + '/api/s3', {
    method: 'POST',
    body: JSON.stringify({ 
      endpointUrl: url.value, 
      accessKey: accessKey.value, 
      secretKey: secretKey.value, 
      bucketName: bucket.value, 
      region: region.value 
    }),
    headers: getHeaders(),
    credentials: 'include',
  })
  if (error.value) {
    toast.add({ title: 'S3 storage create error', description: error.value.data.error + '', icon: 'i-heroicons-no-symbol-20-solid', color: 'red' })
  }
  if (data.value) {
    pending.value = false
    isAddOpen.value = false
    isDeleteOpen.value = false
    cleanFormValues()
    fetchS3Storages()
    toast.add({ title: 'S3 storage successfully created', description: data.value.endpointUrl, icon: 'i-heroicons-check-circle-20-solid' })
  }
}

async function updateS3Storage(storageId: string) {
  pending.value = true
  const { data, error } = await useFetch(config.public.apiUrl + '/api/s3/' + storageId, {
    method: 'PUT',
    body: JSON.stringify({
      endpointUrl: url.value,
      accessKey: accessKey.value,
      secretKey: secretKey.value,
      bucketName: bucket.value,
      region: region.value
    }),
    headers: getHeaders(),
    credentials: 'include'
  })

  if (error.value) {
    toast.add({ title: 'S3 storage update error', description: error.value.data.error || 'An unknown error occurred', icon: 'i-heroicons-no-symbol-20-solid', color:'red' })
    return
  }

  toast.add({ title: 'S3 storage successfully updated', description: data.value.endpointUrl, icon: 'i-heroicons-check-circle-20-solid' })

  await fetchS3Storages()
  isEditOpen.value = false
  cleanFormValues()
  pending.value = false
}

async function deleteS3Storage(storageId: string) {
  const { data, error } = await useFetch(useRuntimeConfig().public.apiUrl + '/api/s3/' + storageId, {
    method: 'DELETE',
    headers: getHeaders(),
    credentials: 'include'
  })
  
  if (error.value) {
    toast.add({ 
      title: 'Delete S3 Storage Error', description: error.value.data.error || 'An unknown error occurred', icon: 'i-heroicons-no-symbol-20-solid', color: 'red' })
    return
  }
  toast.add({title: data.value.info, description: data.value.message ,icon: 'i-heroicons-check-circle-20-solid'})
  fetchS3Storages()
}

const fetchS3Storages = async () => {
  pending.value = true
  isDeleteOpen.value = false
  await waitForAuthentication()
  
  const response = await $fetch(config.public.apiUrl + '/api/s3', {
    method: 'GET',
    headers: getHeaders(),
    credentials: 'include'
  })
    
  s3Storages.value = response
    if (error.value) {
    toast.add({ title: 'S3 storage fetch error', description: error.value || 'Could not fetch requested data', icon: 'i-heroicons-no-symbol-20-solid', color: 'red' })
  }
  pending.value = false
}

onMounted(async () => {
  await fetchS3Storages()
})
</script>
