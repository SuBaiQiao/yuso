<template>
  <a-list
    :grid="{ gutter: 16, column: 4 }"
    item-layout="horizontal"
    :data-source="props.pictureList"
  >
    <template #renderItem="{ item }">
      <a-card hoverable>
        <template #cover>
          <img alt="example" :src="item.url" />
        </template>
        <a-card-meta title=" ">
          <template #description>{{ item.title }}</template>
        </a-card-meta>
      </a-card>
    </template>
  </a-list>
</template>

<script setup lang="ts">
interface Props {
  pictureList: any[];
}
import { defineProps, withDefaults } from "vue";
const props = withDefaults(defineProps<Props>(), {
  pictureList: () => [],
});
</script>

<style scoped></style>
