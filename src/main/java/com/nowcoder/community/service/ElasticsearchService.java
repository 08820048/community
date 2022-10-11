package com.nowcoder.community.service;

import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: Tisox
 * @date: 2022/3/26 18:58
 * @description:
 * @blog:www.waer.ltd
 */
@Service
public class ElasticsearchService {
    @Autowired
    private DiscussPostRepository discussPostRepository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 添加帖子
     * @param post 帖子实体
     */
    public void saveDiscussPost(DiscussPost post) {
        discussPostRepository.save(post);
    }

    /**
     * 删除帖子
     * @param id 帖子ID
     */
    public void deleteDiscussPost(int id) {
        discussPostRepository.deleteById(id);
    }

    /**
     * 实现帖子搜索
     *
     * @param keyword 搜索关键词
     * @param current  当前时第几页
     * @param limit    每页多少条数据
     * @return result
     */
    public Page<DiscussPost> searchDiscussPost(String keyword, int current, int limit) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword, "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(current, limit))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
        return elasticsearchTemplate.queryForPage(searchQuery, DiscussPost.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                //得到查询来的多条数据
                SearchHits hits = searchResponse.getHits();
                //判断是否有值，<=0表示没有查到数据，直接return;
                if (hits.getTotalHits() <= 0) {
                    return null;
                }
                //将查询到的数据封装到一个集合中进行处理返回
                List<DiscussPost> list = new ArrayList<>();
                //遍历查询命中的数据
                for (SearchHit hit : hits) {
                    DiscussPost post = new DiscussPost();
                    //每得到一个命中数据(它将json->map)，将其包装到实体类中返回
                    String id = hit.getSourceAsMap().get("id").toString();
                    post.setId(Integer.parseInt(id));

                    String userId = hit.getSourceAsMap().get("userId").toString();
                    post.setUserId(Integer.parseInt(userId));

                    String title = hit.getSourceAsMap().get("title").toString();
                    post.setTitle(title);

                    String content = hit.getSourceAsMap().get("content").toString();
                    post.setContent(content);

                    String status = hit.getSourceAsMap().get("status").toString();
                    post.setStatus(Integer.parseInt(status));

                    //ES再处理日期的时候是转为一个long类型的数据进行存储的
                    String createTime = hit.getSourceAsMap().get("createTime").toString();
                    post.setCreateTime(new Date(Long.parseLong(createTime)));

                    String commentCount = hit.getSourceAsMap().get("commentCount").toString();
                    post.setCommentCount(Integer.parseInt(commentCount));
                    /*处理高亮显示的结果*/
                    //如果结果不为空，就将其转为字符串存入实体，注意这个结果集Fragments是一个数组
                    //就是说，我们上面先将查询到的所有数据放到实体中，先面再单独处理其中高亮显示的数据，如果有这些数据，那么就将这些数据对之前的数据
                    //进行一个覆盖即可。
                    HighlightField titleField = hit.getHighlightFields().get("title");
                    if (titleField != null) {
                        post.setTitle(titleField.getFragments()[0].toString());
                    }
                    HighlightField contentField = hit.getHighlightFields().get("content");
                    if (contentField != null) {
                        post.setTitle(contentField.getFragments()[0].toString());
                    }
                    //将处理好的实体放入集合
                    list.add(post);
                }
                //AggregatedPageImpl：传入集合，传入结果，总数据，searchResponse.getScrollId(),对象，
                return new AggregatedPageImpl(list, pageable, hits.getTotalHits(), searchResponse.getScrollId(), hits.getMaxScore());
            }
        });
    }
}
