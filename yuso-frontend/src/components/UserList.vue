<template>
  <a-list item-layout="horizontal" :data-source="props.userList">
    <template #renderItem="{ item }">
      <a-card hoverable style="width: 240px">
        <template #cover>
          <img alt="example" :src="gege" />
        </template>
        <a-card-meta :title="item.userName">
          <template #description>{{ item.userProfile }}</template>
        </a-card-meta>
      </a-card>
    </template>
  </a-list>
</template>

<script setup lang="ts">
import gege from "../assets/gege.png";
interface Props {
  userList: any[];
}
import { defineProps, withDefaults } from "vue";
const props = withDefaults(defineProps<Props>(), {
  userList: () => [],
});
</script>

<style scoped></style>
