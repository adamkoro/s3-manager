<template>
  <UContainer>
    <div>
      <div class="flex items-center justify-between p-2">
        <div>
          <UTooltip text="Update S3 storage list">
            <UButton icon="i-heroicons-arrow-path-20-solid" label="Refresh" @click="fetchBucketContents" />
          </UTooltip>
        </div>
        <div>
          <UTooltip text="Upload file">
            <label
              class="
                focus:outline-none
                disabled:cursor-not-allowed
                disabled:opacity-75
                aria-disabled:cursor-not-allowed
                aria-disabled:opacity-75
                flex-shrink-0
                font-medium
                rounded-md
                inline-flex
                items-center
                text-sm
                px-3
                py-2
                gap-x-2
                shadow-sm
                text-white
                bg-primary-500
                hover:bg-primary-600
                focus-visible:outline
                focus-visible:outline-2
                focus-visible:outline-offset-2
                focus-visible:outline-primary-500
                cursor-pointer
              ">
              <UIcon name="i-heroicons-arrow-up-tray-20-solid" class="text-xl"/>
              <span>Upload</span>
              <input type="file" ref="fileInput" class="absolute opacity-0 w-full h-full cursor-pointer" @change="uploadFile"
              />
            </label>
          </UTooltip>
        </div>
      </div>
      <div v-if="error" class="flex flex-col items-center mt-6">
        <UIcon name="i-heroicons-exclamation-circle-20-solid" class="text-red-500 text-2xl" />
        <p class="mt-3 text-sm text-red-500">Error: {{ error }}</p>
      </div>
      <div v-else-if="loading" class="flex flex-col items-center mt-6">
        <UIcon name="i-heroicons-arrow-path-20-solid" class="text-gray-500 text-2xl animate-spin" />
        <p class="mt-3 text-sm">Loading...</p>
      </div>
      <div v-else class="grid grid-cols-1">
        <div v-for="item in bucketContents" :key="item">
          <div v-if="item.endsWith('/')" class="bg-gray-300 text-black rounded border-2 border-orange-500 p-1 mt-6 shadow-lg">
            {{ item }}
          </div>
          <div class="mt-1">
            <div v-if="!item.endsWith('/')" class="text-black bg-gray-200 rounded p-2 flex items-center justify-between">
              <span>{{ item }}</span>
              <div class="flex gap-2">
                <UTooltip text="Download file">
                  <UButton icon="i-heroicons-arrow-down-tray-20-solid" size="sm" color="gray" @click="downloadFile(item)" />
                </UTooltip>
                <UTooltip text="Delete file">
                  <UButton icon="i-heroicons-trash-20-solid" size="sm" color="red" @click="(isDeleteOpen = true) && (deleteSelectedFile = item)" />
                  <UModal v-model="isDeleteOpen">
                    <UCard :ui="{ divide: 'divide-y divide-gray-100' }">
                      <template #header>
                        <div class="flex items-center justify-between ">
                          <h3 class="text-base font-semibold leading-6 text-gray-900 ">
                            Delete file
                          </h3>
                          <UButton color="gray" variant="ghost" icon="i-heroicons-x-mark-20-solid" class="-my-1"
                            @click="(isDeleteOpen = false) && (deleteSelectedFile = '')" />
                        </div>
                      </template>
                      <div v-if="deleteSelectedFile" class="flex flex-wrap gap-1">
                        <div class="">Are you sure you want to delete</div>
                        <div class="text-red-600">{{ deleteSelectedFile }}</div>
                        <div>?</div>
                      </div>
                      <template #footer>
                        <div class="flex justify-between">
                          <UButton type="cancel" size="xl" label="Cancel"
                            @click="(isDeleteOpen = false) && (deleteSelectedFile = '')">
                            <template #trailing>
                              <UIcon name="i-heroicons-no-symbol-20-solid" />
                            </template>
                          </UButton>
                          <UButton type="submit" size="xl" label="Delete" color="red" @click="deleteFile(deleteSelectedFile)">
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
      </div>
    </div>
  </UContainer>
</template>

<script setup lang="ts">

const route = useRoute()
const storageId = route.params.id
const { getHeaders, waitForAuthentication } = useAuth()
const toast = useToast()
const config = useRuntimeConfig()
const bucketContents = ref<string[]>([])
const loading = ref(true)
const error = ref<string | null>(null)
const fileInput = ref<HTMLInputElement | null>(null)
const uploading = ref(false)
const isDeleteOpen = ref(false)
const deleteSelectedFile = ref('')

const fetchBucketContents = async () => {
  loading.value = true
  error.value = null
  try {
    await waitForAuthentication()
    const response = await $fetch(`${config.public.apiUrl}/api/s3/${storageId}`, {
      headers: getHeaders(),
      credentials: 'include'
    })
    bucketContents.value = response
  } catch (err) {
    error.value = err.message || 'Failed to fetch bucket contents'
    toast.add({ title: 'Error', description: error.value, icon: 'i-heroicons-no-symbol-20-solid', color: 'red' })
  } finally {
    loading.value = false
  }
}
onMounted(fetchBucketContents)

const downloadFile = async (filename: string) => {
  const loadingToast = toast.add({
    id: "downloadFileToast",
    title: 'Downloading...', 
    description: `Downloading ${filename}...`,
    color: 'blue',
    icon: 'i-heroicons-download-20-solid',
  })

  try {
    const { data, error } = await useFetch(config.public.apiUrl + `/api/s3/${storageId}/download/${filename}`, {
      headers: getHeaders(),
      credentials: 'include',
      responseType: 'blob'
    })

    if (error.value) {
      throw error.value
    }

    const blob = new Blob([data.value])
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = filename
    link.click()
    URL.revokeObjectURL(link.href)
    toast.update("downloadFileToast", {title: 'Download Successful',description: `${filename} successfully downloaded!`,icon: 'i-heroicons-check-circle-20-solid',color: 'green',})
  } catch (err) {
    toast.update("downloadFileToast", {title: 'File download error',description: err.message || 'An error occurred during the download.',icon: 'i-heroicons-no-symbol-20-solid',color: 'red',})
  }
}


const deleteFile = async (filename: string) => {
  const { data, error } = await useFetch(config.public.apiUrl + `/api/s3/${storageId}/${filename}`, {
    method: 'DELETE',
    headers: getHeaders(),
    credentials: 'include',
  })
  if (error.value) {
    toast.add({ title: 'File delete error', description: error.value + '', icon: 'i-heroicons-no-symbol-20-solid', color: 'red' })
  }
  await fetchBucketContents()
  toast.add({ title: 'File successfully deleted', description: data.value, icon: 'i-heroicons-check-circle-20-solid' })
}


const uploadFile = async () => {
  const file = fileInput.value?.files?.[0];
  if (!file) {
    console.log('No file selected or fileInput is empty');
    return;
  }
  //console.log('Selected file:', file);

  uploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', file)

    const { data, error } = await useFetch(config.public.apiUrl + `/api/s3/${storageId}/upload`, {
      method: 'POST',
      body: formData,
      headers: {
        ...getHeaders(),
      },
      credentials: 'include',
    })

    if (error.value) {
      throw error.value
    }

    toast.add({ title: 'File successfully uploaded', description: data.value, icon: 'i-heroicons-check-circle-20-solid' })
    await fetchBucketContents()
  } catch (err) {
    error.value = err.message || 'Failed to upload file'
    toast.add({ title: 'Upload error', description: error.value, icon: 'i-heroicons-no-symbol-20-solid', color: 'red' })
  } finally {
    uploading.value = false
    if (fileInput.value) {
      fileInput.value.value = ''
    }
  }
}

</script>
