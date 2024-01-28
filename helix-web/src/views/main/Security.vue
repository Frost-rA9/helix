<script setup>
import {reactive, ref} from "vue";
import {Lock, Plus, Switch} from "@element-plus/icons-vue";
import {get, logout, post} from "@/net";
import {ElMessage} from "element-plus";
import router from "@/router";
import CreateSubAccount from "@/component/CreateSubAccount.vue";

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

const simpleList = ref([])
get('/api/monitor/simple-list', list => {
  simpleList.value = list
  initSubAccounts()
})

const accounts = ref([])
const initSubAccounts = () => get('/api/user/sub/list', list => accounts.value = list)

const createAccount = ref(false)

function deleteAccount(id) {
  get(`/api/user/sub/delete?uid=${id}`, () => {
    ElMessage.success('子账户删除成功')
    initSubAccounts()
  })
}
</script>

<template>
  <div style="display: flex;gap: 10px">
    <div style="flex: 50%">
      <div class="info-card">
        <div class="title"><i class="fa-solid fa-lock"></i> 修改密码</div>
        <el-divider style="margin: 10px 0"/>
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
      <div class="title"><i class="fa-solid fa-users"></i> 子用户管理</div>
      <el-divider style="margin: 10px 0"/>
      <div v-if="accounts.length" style="text-align: center">
        <div v-for="item in accounts" class="account-card">
          <el-avatar class="avatar" :size="30"
                     src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"/>
          <div style="margin-left: 15px;line-height: 18px;flex: 1">
            <div>
              <span>{{ item.username }}</span>
              <span style="font-size: 13px;color: grey;margin-left: 5px">
                管理 {{ item.clientList.length }} 个服务器
              </span>
            </div>
            <div style="font-size: 13px;color: grey">{{ item.email }}</div>
          </div>
          <el-button type="danger" :icon="Delete"
                     @click="deleteAccount(item.id)" text>删除子账户
          </el-button>
        </div>
        <el-button :icon="Plus" type="primary"
                   @click="createAccount = true" plain>添加更多子用户
        </el-button>
      </div>
      <el-empty :image-size="100" description="当前没有子账户" v-else>
        <el-button :icon="Plus" type="primary" plain
                   @click="createAccount = true">添加子用户
        </el-button>
      </el-empty>
    </div>
    <el-drawer v-model="createAccount" size="350" :with-header="false">
      <create-sub-account :clients="simpleList" @create="createAccount = false;initSubAccounts()"/>
    </el-drawer>
  </div>
</template>

<style scoped>
.info-card {
  border-radius: 7px;
  padding: 15px 20px;
  background-color: var(--el-bg-color);
  height: fit-content;
}

.title {
  font-size: 18px;
  font-weight: bold;
  color: dodgerblue;
}

:deep(.el-drawer) {
  margin: 10px;
  height: calc(100% - 20px);
  border-radius: 10px;
}

:deep(.el-drawer__body) {
  padding: 0;
}
</style>