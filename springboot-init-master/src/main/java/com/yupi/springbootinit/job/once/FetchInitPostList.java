package com.yupi.springbootinit.job.once;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yupi.springbootinit.esdao.PostEsDao;
import com.yupi.springbootinit.model.dto.post.PostEsDTO;
import com.yupi.springbootinit.model.entity.Post;
import com.yupi.springbootinit.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全量同步帖子到 es
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Component
@Slf4j
public class FetchInitPostList implements CommandLineRunner {

    @Resource
    private PostService postService;

    @Resource
    private PostEsDao postEsDao;

    @Override
    public void run(String... args) {
        String json = "{\"current\":1,\"pageSize\":10,\"sortField\":\"publishTime\",\"sortOrder\":\"descend\",\"searchText\":\"\"}";
        String url = "https://api.codefather.cn/api/news/list/page/vo";
        String result = HttpRequest.post(url)
                .body(json)
                .execute().body();
        Map<String, Object> map = JSONUtil.toBean(result, Map.class);
        JSONObject data = (JSONObject) map.get("data");
        JSONArray records = data.getJSONArray("records");
        List<Post> postList = new ArrayList<>();
        for (Object record : records) {
            JSONObject tempRecord = (JSONObject) record;
            Post post = new Post();
            post.setTitle(tempRecord.getStr("title"));
            post.setContent(tempRecord.getStr("content"));
            post.setUserId(1L);
            JSONArray tags = tempRecord.getJSONArray("tags");
            post.setTags(JSONUtil.toJsonStr(tags.toList(String.class)));
            postList.add(post);
        }
        boolean b = postService.saveBatch(postList);
        if (b) {
            log.info("首次初始化文章列表成功，总共{}条", postList.size());
        } else {
            log.error("首次初始化文章列表失败");
        }
    }
}
