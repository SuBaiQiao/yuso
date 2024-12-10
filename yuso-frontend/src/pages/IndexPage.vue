<template>
  <div class="index-page">
    <a-input-search
      v-model:value="searchText"
      placeholder="input search text"
      enter-button="Search"
      size="large"
      @search="onSearch"
    />
    <my-divider />
    <a-tabs v-model:activeKey="activeKey" @change="onTabChange">
      <a-tab-pane key="post" tab="文章">
        <post-list :postList="postList" />
      </a-tab-pane>
      <a-tab-pane key="user" tab="用户">
        <user-list :userList="userList" />
      </a-tab-pane>
      <a-tab-pane key="picture" tab="图片">
        <picture-list :pictureList="pictureList" />
      </a-tab-pane>
    </a-tabs>
  </div>
</template>

<script lang="ts" setup>
import PostList from "@/components/PostList.vue";
import { ref, watchEffect } from "vue";
import UserList from "@/components/UserList.vue";
import PictureList from "@/components/PictureList.vue";
import MyDivider from "@/components/MyDivider.vue";
import { useRoute, useRouter } from "vue-router";
import MyAxios from "@/plugins/MyAxios";

const router = useRouter();
const route = useRoute();

const activeKey = route.params.category;

const initSearchParams = {
  text: "",
  pageSize: 10,
  pageNum: 1,
  type: route.params.category,
};
const searchText = ref(route.query.text || "");
const searchParams = ref(initSearchParams);
const postList = ref([]);
const userList = ref([]);
const pictureList = ref([]);

const loadData = (query: any) => {
  const postQuery = {
    ...query,
    searchText: query.text,
  };
  MyAxios.post("/search/all", postQuery).then((response) => {
    if (postQuery.type == "post") {
      postList.value = response.dataList || [];
    } else if (postQuery.type == "user") {
      userList.value = response.dataList || [];
    } else if (postQuery.type == "picture") {
      pictureList.value = response.dataList || [];
    }
  });
};

watchEffect(() => {
  searchParams.value = {
    ...initSearchParams,
    text: route.query.text as string,
    type: route.params.category,
  };
  loadData(searchParams.value);
});

const onSearch = (value: string) => {
  router.push({
    query: {
      ...searchParams.value,
      text: searchText.value,
    },
  });
};

const onTabChange = (key: string) => {
  router.push({
    path: `/${key}`,
    query: searchParams.value,
  });
};
</script>
