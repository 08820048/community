package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: Tisox
 * @date: 2022/3/24 14:10
 * @description:
 * @blog:www.waer.ltd
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ElasticsearchTest {
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private DiscussPostRepository discussPostRepository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void testInsert(){
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(241));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(242));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(243));

    }

    @Test
    public void testInsertList(){
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(103,0,100,0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(138,0,100,0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(132,0,100,0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(133,0,100,0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(134,0,100,0));
    }

    @Test
    public void testUpdate(){
        DiscussPost post  = discussPostMapper.selectDiscussPostById(231);
        post.setContent("使劲灌水哈哈哈哈哈");
        discussPostRepository.save(post);
    }

    @Test
    public void testDelete(){
        discussPostRepository.deleteById(231);
    }

    @Test
    public void testDeleteAll(){
        discussPostRepository.deleteAll();
    }

    //multiMatchQuery多字段同时匹配
    @Test
    public void testSearchByResitory(){
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬","title","content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0,10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
        //看discussPostRepository.search源码
        //底层获取到了高亮显示的值，但是没有做处理
        Page<DiscussPost> page = discussPostRepository.search(searchQuery);
        //查询到的总数
        System.out.println(page.getTotalElements());
        //页数
        System.out.println(page.getTotalPages());
        //当前页码
        System.out.println(page.getNumber());
        //每页显示的数据
        System.out.println(page.getSize());
        for (DiscussPost post : page) {
            System.out.println(post);
        }
    }

    /**
     * 构建：
     * NativeSearchQueryBuilder是ES提供的一个searchQuery的工具类，可以构建一个NativeSearchQuery的实现类。
     * withQuery：构造搜索条件，这里使用QueryBuilders进行具体的条件构建，multiMatchQuery表示多字段进行搜索，表示从title和content字段进行搜索。
     * withSort：构造排序规则，相应的，使用SortBuilders进行具体排序条件的构建fieldSort("type")表示排序的字段，order(SortOrder.DESC)表示该字段进行一个
     * 倒序排序。
     * withPageable：由于匹配的数据量可能比较大，实际开发中一般不会全部查询，所以这里可以使用该属性进行分页查询
     * PageRequest.of(0,10)，PageRequest用来构建分页条件，这里表示从第几页开始，每页显示多少条数据。
     * withHighlightFields：指定哪些字段/词条作高亮显示。使用HighlightBuilder.Field指定需要高亮显示的字段，对与这些高亮的字段
     * 需要指定一个html标签，比如<em></em>标签，preTags表示前置标签，postTags表示后置标签
     * 在构建完所有的条件之后，使用build()方法直接完成构建即可。
     * ——————————————————————————————————————————————————————————————————————————————————————————————————————————
     * 查询;
     * 在构建完毕之后，直接调用elasticsearchTemplate的queryForPage方法进行查询，查询返回一个page分页对象，封装了查询的数据
     * queryForPage方法需要三个参数。分别是：
     * 1.searchQuery：构建的一个搜索条件对象。
     * 2.DiscussPost.class：查询实体的class。
     * 3.SearchResultMapper:这是一个接口，需要自己实现它，这里作了一个匿名实现，主要的操作也在这个接口方法的实现上体现。
     * SearchHits：得到查询来的多条数据
     *
     */

    @Test
    public void testSearchByTemplate(){
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬","title","content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0,10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
        Page<DiscussPost> page = elasticsearchTemplate.queryForPage(searchQuery, DiscussPost.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                //得到查询来的多条数据
                SearchHits hits = searchResponse.getHits();
                //判断是否有值，<=0表示没有查到数据，直接return;
                if(hits.getTotalHits()<=0){
                    return null;
                }
                //将查询到的数据封装到一个集合中进行处理返回
                List<DiscussPost> list  = new ArrayList<>();
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
                    if(titleField!=null){
                        post.setTitle(titleField.getFragments()[0].toString());
                    }
                    HighlightField contentField = hit.getHighlightFields().get("content");
                    if(contentField!=null){
                        post.setTitle(contentField.getFragments()[0].toString());
                    }
                    //将处理好的实体放入集合
                    list.add(post);
                }
                //AggregatedPageImpl：传入集合，传入结果，总数据，searchResponse.getScrollId(),对象，
                return new AggregatedPageImpl(list,pageable,hits.getTotalHits(),searchResponse.getScrollId(), hits.getMaxScore());
            }

        });
        //查询到的总数
        System.out.println(page.getTotalElements());
        //页数
        System.out.println(page.getTotalPages());
        //当前页码
        System.out.println(page.getNumber());
        //每页显示的数据
        System.out.println(page.getSize());
        for (DiscussPost post : page) {
            System.out.println(post);
        }
    }
}
