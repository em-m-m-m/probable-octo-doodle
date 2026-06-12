import { createRouter, createWebHistory } from 'vue-router'
import Login from '../components/Login.vue'
import Home from '../components/Home.vue'
import Statistics from '../components/Statistics.vue'
import DataManagement from '../components/DataManagement.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/home',
    name: 'Home',
    component: Home
  },
  {
    path: '/statistics',
    name: 'Statistics',
    component: Statistics
  },
  {
    path: '/data-management',
    name: 'DataManagement',
    component: DataManagement
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
