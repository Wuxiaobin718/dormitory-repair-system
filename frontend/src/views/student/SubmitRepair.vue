<template>
  <div class="submit-repair">
    <!-- Page Header -->
    <div class="page-header">
      <div class="page-header-text">
        <h2 class="section-title">提交报修</h2>
        <p>请详细描述故障信息，我们将尽快为您处理</p>
      </div>
    </div>

    <!-- Steps -->
    <div class="steps-wrap">
      <el-steps :active="step" align-center finish-status="success" process-status="process">
        <el-step title="填写信息" icon="el-icon-edit"></el-step>
        <el-step title="上传图片" icon="el-icon-picture"></el-step>
        <el-step title="确认提交" icon="el-icon-circle-check"></el-step>
      </el-steps>
    </div>

    <!-- Step 1: Form -->
    <div class="step-content" v-show="step === 0">
      <div class="form-card">
        <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
          <el-form-item label="报修宿舍" prop="dormId">
            <el-select v-model="form.dormId" placeholder="请选择宿舍" size="large" style="width:100%">
              <el-option v-for="d in dormList" :key="d.id"
                :label="d.building + ' ' + d.floor + '楼 ' + d.room + '室'" :value="d.id">
                <span>{{ d.building }}</span>
                <span style="margin:0 8px;color:#ccc">/</span>
                <span>{{ d.floor }}楼</span>
                <span style="margin:0 8px;color:#ccc">/</span>
                <span>{{ d.room }}室</span>
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="故障类型" prop="type">
            <el-radio-group v-model="form.type" class="type-group">
              <el-radio-button label="水电故障">水电</el-radio-button>
              <el-radio-button label="门窗故障">门窗</el-radio-button>
              <el-radio-button label="家具损坏">家具</el-radio-button>
              <el-radio-button label="卫浴问题">卫浴</el-radio-button>
              <el-radio-button label="墙体地面">墙体</el-radio-button>
              <el-radio-button label="其他">其他</el-radio-button>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="故障描述" prop="content">
            <el-input v-model="form.content" type="textarea" :rows="4"
              placeholder="请详细描述故障情况，例如：漏水位置、损坏程度等" maxlength="500" show-word-limit />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" size="large" @click="step = 1" class="next-btn">
              下一步 <i class="el-icon-arrow-right"></i>
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- Step 2: Upload -->
    <div class="step-content" v-show="step === 1">
      <div class="form-card upload-card">
        <div class="upload-area">
          <p class="upload-hint"><i class="el-icon-camera"></i> 上传故障现场照片（最多3张，选填）</p>
          <el-upload action="#" :http-request="handleUpload" list-type="picture-card"
            :file-list="fileList" :on-preview="handlePreview" :on-remove="handleRemove" :limit="3"
            class="custom-upload">
            <div class="upload-placeholder">
              <i class="el-icon-plus"></i>
              <span>点击上传</span>
            </div>
          </el-upload>
          <el-dialog :visible.sync="dialogVisible" append-to-body>
            <img :src="dialogImageUrl" style="width:100%" />
          </el-dialog>
        </div>
        <div class="upload-actions">
          <el-button size="large" @click="step = 0"><i class="el-icon-arrow-left"></i> 上一步</el-button>
          <el-button type="primary" size="large" @click="step = 2" :disabled="!form.dormId">
            下一步 <i class="el-icon-arrow-right"></i>
          </el-button>
        </div>
      </div>
    </div>

    <!-- Step 3: Confirm -->
    <div class="step-content" v-show="step === 2">
      <div class="form-card confirm-card">
        <div class="confirm-icon">
          <i class="el-icon-document-checked"></i>
        </div>
        <h3>请确认报修信息</h3>
        <div class="confirm-details">
          <div class="confirm-item">
            <span class="confirm-label">宿舍</span>
            <span class="confirm-value">{{ selectedDormText }}</span>
          </div>
          <div class="confirm-item">
            <span class="confirm-label">故障类型</span>
            <el-tag>{{ form.type }}</el-tag>
          </div>
          <div class="confirm-item">
            <span class="confirm-label">故障描述</span>
            <span class="confirm-value">{{ form.content }}</span>
          </div>
        </div>
        <div class="confirm-actions">
          <el-button size="large" @click="step = 1"><i class="el-icon-arrow-left"></i> 修改</el-button>
          <el-button type="primary" size="large" :loading="loading" @click="handleSubmit">
            <i class="el-icon-circle-check"></i> 确认提交
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from '@/api'

export default {
  data() {
    return {
      step: 0,                           // 步骤：0=填写信息，1=上传图片，2=确认提交
      form: { dormId: null, type: '', content: '', img: '' },
      fileList: [],                       // 已上传的图片列表
      dormList: [],                       // 宿舍下拉列表
      loading: false,
      dialogVisible: false,              // 图片预览弹窗
      dialogImageUrl: '',
      rules: {
        dormId: [{ required: true, message: '请选择宿舍', trigger: 'change' }],
        type: [{ required: true, message: '请选择故障类型', trigger: 'change' }],
        content: [{ required: true, message: '请填写故障描述', trigger: 'blur' }]
      }
    }
  },
  computed: {
    // 在确认步骤中展示已选宿舍的文字描述
    selectedDormText() {
      const d = this.dormList.find(d => d.id === this.form.dormId)
      return d ? `${d.building} ${d.floor}楼 ${d.room}室` : ''
    }
  },
  mounted() {
    this.loadDorms()  // 初始化宿舍列表
  },
  methods: {
    // 加载宿舍列表供用户选择
    async loadDorms() {
      const res = await api.dorm.getList()
      if (res.code === 200) this.dormList = res.data
    },
    // 自定义上传：调用上传接口，拿到图片 URL
    async handleUpload(file) {
      const res = await api.upload.uploadImage(file.file)
      if (res.code === 200) {
        this.form.img = res.data.url
        file.url = 'http://localhost:8080' + res.data.url
        this.$message.success('上传成功')
      }
    },
    handlePreview(file) {
      this.dialogImageUrl = file.url
      this.dialogVisible = true
    },
    handleRemove() {
      this.form.img = ''  // 移除图片时清空
    },
    // 最终提交报修单
    handleSubmit() {
      this.$refs.formRef.validate(async valid => {
        if (!valid) return
        this.loading = true
        try {
          const res = await api.repair.submit(this.form)
          if (res.code === 200) {
            this.$message.success('报修提交成功！')
            this.$router.push('/repair/my')
          }
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
.submit-repair {
  max-width: 740px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 28px;
}
.page-header p {
  color: var(--c-text-secondary);
  font-size: 14px;
  margin: 4px 0 0;
}

/* Steps */
.steps-wrap {
  background: #fff;
  border-radius: var(--r-lg);
  padding: 28px 40px;
  margin-bottom: 24px;
  box-shadow: var(--shadow-card);
}
.steps-wrap .el-step__icon {
  border-color: var(--c-border);
}
.steps-wrap .el-step__title {
  font-size: 14px;
}

/* Form Card */
.form-card {
  background: #fff;
  border-radius: var(--r-lg);
  padding: 36px;
  box-shadow: var(--shadow-card);
}

/* Type radio buttons */
.type-group .el-radio-button__inner {
  border: 1.5px solid var(--c-border);
  border-radius: 10px !important;
  padding: 10px 18px;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.25s ease;
}
.type-group .el-radio-button__inner i {
  font-size: 16px;
}
.type-group .el-radio-button:first-child .el-radio-button__inner {
  border-radius: 10px !important;
  border-left: 1.5px solid var(--c-border);
}
.type-group .el-radio-button:last-child .el-radio-button__inner {
  border-radius: 10px !important;
}
.type-group .el-radio-button__orig-radio:checked + .el-radio-button__inner {
  background: var(--c-primary-lighter);
  border-color: var(--c-primary);
  color: var(--c-primary);
  box-shadow: none;
}
.type-group {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.next-btn, .upload-actions, .confirm-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* Upload */
.upload-card {
  text-align: center;
}
.upload-hint {
  font-size: 15px;
  color: var(--c-text-secondary);
  margin: 0 0 20px;
}
.upload-hint i {
  margin-right: 6px;
  color: var(--c-primary);
}
.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--c-text-muted);
}
.custom-upload .el-upload--picture-card {
  width: 120px;
  height: 120px;
  line-height: 120px;
}

/* Confirm */
.confirm-card {
  text-align: center;
}
.confirm-icon {
  font-size: 48px;
  color: var(--c-accent);
  margin-bottom: 12px;
}
.confirm-card h3 {
  font-size: 20px;
  font-weight: 700;
  color: var(--c-text);
  margin: 0 0 24px;
}
.confirm-details {
  text-align: left;
  background: var(--c-bg);
  border-radius: var(--r-md);
  padding: 20px 24px;
  margin-bottom: 24px;
}
.confirm-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid var(--c-border-light);
}
.confirm-item:last-child {
  border-bottom: none;
}
.confirm-label {
  font-weight: 600;
  color: var(--c-text-secondary);
  font-size: 14px;
}
.confirm-value {
  color: var(--c-text);
  font-size: 14px;
  max-width: 60%;
  text-align: right;
}
</style>
