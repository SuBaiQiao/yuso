package com.yupi.springbootinit.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.model.dto.post.PostQueryRequest;
import com.yupi.springbootinit.model.dto.search.SearchRequest;
import com.yupi.springbootinit.model.dto.user.UserQueryRequest;
import com.yupi.springbootinit.model.entity.Picture;
import com.yupi.springbootinit.model.enums.SearchTypeEnum;
import com.yupi.springbootinit.model.vo.PostVO;
import com.yupi.springbootinit.model.vo.SearchVO;
import com.yupi.springbootinit.model.vo.UserVO;
import com.yupi.springbootinit.service.PictureService;
import com.yupi.springbootinit.service.PostService;
import com.yupi.springbootinit.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

@Component
public class SearchFacade {

    @Resource
    private PictureService pictureService;

    @Resource
    private UserService userService;

    @Resource
    private PostService postService;

    public SearchVO searchAll(SearchRequest searchRequest,
                                            HttpServletRequest request) {
        String type = searchRequest.getType();
        SearchTypeEnum searchTypeEnum = SearchTypeEnum.getEnumByValue(type);

        String searchText = searchRequest.getSearchText();
        int current = searchRequest.getCurrent();
        int pageSize = searchRequest.getPageSize();
        SearchVO searchVO = new SearchVO();

        if (null == searchTypeEnum) {
            UserQueryRequest userQueryRequest = new UserQueryRequest();
            userQueryRequest.setUserName(searchText);

            PostQueryRequest postQueryRequest = new PostQueryRequest();
            postQueryRequest.setSearchText(searchText);

            CompletableFuture<Page<Picture>> pictureCompletableFuture = CompletableFuture.supplyAsync(() -> pictureService.searchPicture(searchText, current, pageSize));
            CompletableFuture<Page<UserVO>> userCompletableFuture = CompletableFuture.supplyAsync(() -> userService.listUserVOByPage(userQueryRequest, current, pageSize));
            CompletableFuture<Page<PostVO>> postCompletableFuture = CompletableFuture.supplyAsync(() -> postService.listPostVOByPage(postQueryRequest, current, pageSize, request));

            CompletableFuture.allOf(pictureCompletableFuture, userCompletableFuture, postCompletableFuture).join();


            try {
                searchVO.setPictureList(pictureCompletableFuture.get().getRecords());
                searchVO.setPostList(postCompletableFuture.get().getRecords());
                searchVO.setUserList(userCompletableFuture.get().getRecords());
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据查询失败");
            }
        } else {
            switch (searchTypeEnum) {
                case POST:
                    PostQueryRequest postQueryRequest = new PostQueryRequest();
                    postQueryRequest.setSearchText(searchText);
                    searchVO.setPostList(postService.listPostVOByPage(postQueryRequest, current, pageSize, request).getRecords());
                    break;
                case USER:
                    UserQueryRequest userQueryRequest = new UserQueryRequest();
                    userQueryRequest.setUserName(searchText);
                    searchVO.setUserList(userService.listUserVOByPage(userQueryRequest, current, pageSize).getRecords());
                    break;
                case PICTURE:
                    searchVO.setPictureList(pictureService.searchPicture(searchText, current, pageSize).getRecords());
                    break;
                default:
                    throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        return searchVO;
    }
}
