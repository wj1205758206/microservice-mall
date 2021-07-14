package microservice.mall.product.service.impl;

import microservice.mall.product.entity.AttrEntity;
import microservice.mall.product.service.AttrService;
import microservice.mall.product.vo.BaseAttrs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import microservice.mall.common.utils.PageUtils;
import microservice.mall.common.utils.Query;

import microservice.mall.product.dao.ProductAttrValueDao;
import microservice.mall.product.entity.ProductAttrValueEntity;
import microservice.mall.product.service.ProductAttrValueService;
import org.springframework.transaction.annotation.Transactional;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Autowired
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveBaseAttrs(Long spuId, List<BaseAttrs> baseAttrs) {
        if (baseAttrs == null || baseAttrs.size() == 0) {
            return;
        } else {
            List<ProductAttrValueEntity> productAttrValueEntities = baseAttrs.stream().map((attr) -> {
                ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
                productAttrValueEntity.setSpuId(spuId);
                AttrEntity byId = attrService.getById(attr.getAttrId());
                productAttrValueEntity.setAttrName(byId.getAttrName());
                productAttrValueEntity.setAttrId(attr.getAttrId());
                productAttrValueEntity.setAttrValue(attr.getAttrValues());
                productAttrValueEntity.setQuickShow(attr.getShowDesc());

                return productAttrValueEntity;
            }).collect(Collectors.toList());

            this.saveBatch(productAttrValueEntities);
        }
    }

    @Override
    public List<ProductAttrValueEntity> baseAttrListForSpu(Long spuId) {

        List<ProductAttrValueEntity> entities = this.baseMapper.selectList(
                new QueryWrapper<ProductAttrValueEntity>()
                        .eq("spu_id", spuId));
        return entities;
    }

    @Transactional
    @Override
    public void updatBaseAttrListForSpu(Long spuId, List<ProductAttrValueEntity> entities) {
        //删除这个spuId之前对应的所有属性
        this.baseMapper.delete(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));

        List<ProductAttrValueEntity> collect = entities.stream().map((item) -> {
            item.setSpuId(spuId);
            return item;
        }).collect(Collectors.toList());

        this.saveBatch(collect);
    }

}