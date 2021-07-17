package microservice.mall.search;

import com.alibaba.fastjson.JSON;
import microservice.mall.search.config.ElasticSearchConfig;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class MallSearchApplicationTests {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    void contextLoads() {
        System.out.println(restHighLevelClient + "---------------------------------------------------------");
    }

    @Test
    void indexData() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");
        User user = new User();
        String toJSONString = JSON.toJSONString(user);
        indexRequest.source(toJSONString, XContentType.JSON);

        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, ElasticSearchConfig.COMMON_OPTIONS);

        System.out.println(indexResponse);

    }

    @Data
    class User {
        private String userName;
        private String gender;
        private Integer age;
    }

    @Test
    void searchData() throws IOException {
        SearchRequest searchRequest = new SearchRequest();

        searchRequest.indices("bank");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));

        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
        searchSourceBuilder.aggregation(ageAgg);

        AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
        searchSourceBuilder.aggregation(balanceAvg);

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);

        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            System.out.println(hit.toString());
        }

        Aggregations aggregations = searchResponse.getAggregations();
        Terms ageAgg1 = aggregations.get("ageAgg");
        for (Terms.Bucket bucket : ageAgg1.getBuckets()) {
            String keyAsString = bucket.getKeyAsString();
            System.out.println("年龄：" + keyAsString + "==>" + bucket.getDocCount());
        }

        Avg balanceAvg1 = aggregations.get("balanceAvg");
        System.out.println("平均薪资：" + balanceAvg1.getValue());

    }

}
