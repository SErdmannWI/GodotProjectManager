import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import HomeView from '../views/HomeView.vue';
import ProjectListView from '../components/ProjectListView.vue';
import ProjectDetailView from '../components/ProjectDetailView.vue';
import DailyJournal from '../components/DailyJournal.vue';
const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'Home',
    component: ProjectListView,
  },
  {
    path: '/projects',
    name: 'Projects',
    component: ProjectListView,
  },
  {
    path: '/project/:projectId',
    name: 'ProjectDetail',
    component: ProjectDetailView,
    props: true, // Allows route params to be passed as props
  },
  {
    path: '/journal',
    name: 'DailyJournal',
    component: DailyJournal,
  }
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
