import request from '@/utils/request'

/**
 * 上传图片（报修图片或头像）
 * 使用 FormData 格式，Content-Type 由浏览器自动设为 multipart/form-data
 * @param {File} file - 要上传的文件对象
 * @returns {Promise} 返回 { code, data: { url } }
 */
export function uploadImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/upload/image',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
