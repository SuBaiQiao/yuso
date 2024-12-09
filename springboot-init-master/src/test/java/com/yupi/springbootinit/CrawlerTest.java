package com.yupi.springbootinit;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yupi.springbootinit.model.entity.Picture;
import com.yupi.springbootinit.model.entity.Post;
import com.yupi.springbootinit.service.PostService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class CrawlerTest {

    @Resource
    private PostService postService;

    @Test
    void testFetchPicture() throws IOException {
        int current = 1;
        String url = "https://cn.bing.com/images/search?q=小黑子&form=HDRSC2&first=" + current;
        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.select(".iuscp.isv");
        List<Picture> pictureList = new ArrayList<>();
        for (Element element : elements) {
            Map<String, Object> map = JSONUtil.toBean(element.select(".iusc").attr("m"), Map.class);
            String murl = (String) map.get("murl");
            String t = (String) map.get("t");
            Picture picture = new Picture();
            picture.setTitle(t);
            picture.setUrl(murl);
            pictureList.add(picture);
        }
    }

    @Test
    void testFetchPassage() {
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
        postService.saveBatch(postList);
    }
}
