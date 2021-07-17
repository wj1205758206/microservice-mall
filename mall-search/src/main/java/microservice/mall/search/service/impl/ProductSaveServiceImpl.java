package microservice.mall.search.service.impl;

import com.alibaba.fastjson.JSON;
import microservice.mall.search.config.ElasticSearchConfig;
import microservice.mall.search.constant.EsConstant;
import lombok.extern.slf4j.Slf4j;
import microservice.mall.common.es.SkuEsModel;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import microservice.mall.search.service.ProductSaveService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductSaveServiceImpl implements ProductSaveService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {
        //给es中建立product索引

        //给es中保存数据
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel skuEsModel : skuEsModels) {
            //构造保存请求
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(skuEsModel.getSkuId().toString());
            String toJSONString = JSON.toJSONString(skuEsModel);
            indexRequest.source(toJSONString, XContentType.JSON);

            bulkRequest.add(indexRequest);
        }

        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, ElasticSearchConfig.COMMON_OPTIONS);

        boolean b = bulkResponse.hasFailures();
        List<String> collect = Arrays.stream(bulkResponse.getItems()).map((item) -> {
            return item.getId();
        }).collect(Collectors.toList());

        log.info("商品上架成功：{}", collect);

        return b;
    }
}
