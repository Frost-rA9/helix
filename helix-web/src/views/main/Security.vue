<script setup>
import {reactive, ref} from "vue";
import {Lock, Switch} from "@element-plus/icons-vue";
import {logout, post} from "@/net";
import {ElMessage} from "element-plus";
import router from "@/router";

const formRef = ref()
const valid = ref(false)
const onValidate = (prop, isValid) => valid.value = isValid

const form = reactive({
  oldPassword: '',
  newPassword: '',
  newPasswordRepeat: '',
})


const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.newPassword) {
    callback(new Error("两次输入的密码不一致"))
  } else {
    callback()
  }
}

const rules = {
  oldPassword: [
    {required: true, message: '请输入原来的密码', trigger: 'blur'},
  ],
  newPassword: [
    {required: true, message: '请输入新的密码', trigger: 'blur'},
    {min: 6, max: 16, message: '密码的长度必须在6-16个字符之间', trigger: ['blur']}
  ],
  newPasswordRepeat: [
    {required: true, message: '请重复输入新的密码', trigger: 'blur'},
    {validator: validatePassword, trigger: ['blur', 'change']},
  ]
}

function resetPassword() {
  formRef.value.validate(isValid => {
    if (isValid) {
      post('/api/user/change-password', form, () => {
        ElMessage.success('密码修改成功，请重新登录!')
        logout(() => router.push('/'))
      })
    }
  })
}
</script>

<template>
  <div style="display: flex;gap: 10px">
    <div style="flex: 50%">
      <div class="info-card">
        <el-form @validate="onValidate" :model="form" :rules="rules"
                 ref="formRef" style="margin: 20px" label-width="100">
          <el-form-item label="当前密码" prop="oldPassword">
            <el-input type="password" v-model="form.oldPassword"
                      :prefix-icon="Lock" placeholder="当前密码" maxlength="16"/>
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input type="password" v-model="form.newPassword"
                      :prefix-icon="Lock" placeholder="新密码" maxlength="16"/>
          </el-form-item>
          <el-form-item label="重复新密码" prop="newPasswordRepeat">
            <el-input type="password" v-model="form.newPasswordRepeat"
                      :prefix-icon="Lock" placeholder="重复新密码" maxlength="16"/>
          </el-form-item>
          <div style="text-align: center">
            <el-button :icon="Switch" @click="resetPassword"
                       type="success" :disabled="!valid">立即重置密码
            </el-button>
          </div>
        </el-form>
      </div>
      <div class="info-card" style="margin-top: 10px">

      </div>
    </div>
    <div class="info-card" style="flex: 50%">

    </div>
  </div>
</template>

<style scoped>
.info-card {
  border-radius: 7px;
  padding: 15px 20px;
  background-color: var(--el-bg-color);
}
</style>