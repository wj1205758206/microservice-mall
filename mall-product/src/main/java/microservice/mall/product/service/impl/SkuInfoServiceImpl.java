package microservice.mall.product.service.impl;

import microservice.mall.common.to.SkuReductionTo;
import microservice.mall.product.entity.SkuImagesEntity;
import microservice.mall.product.entity.SkuSaleAttrValueEntity;
import microservice.mall.product.entity.SpuInfoEntity;
import microservice.mall.product.feign.CouponFeignService;
import microservice.mall.product.service.SkuImagesService;
import microservice.mall.product.service.SkuSaleAttrValueService;
import microservice.mall.product.vo.Attr;
import microservice.mall.product.vo.Images;
import microservice.mall.product.vo.Skus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import microservice.mall.common.utils.PageUtils;
import microservice.mall.common.utils.Query;

import microservice.mall.product.dao.SkuInfoDao;
import microservice.mall.product.entity.SkuInfoEntity;
import microservice.mall.product.service.SkuInfoService;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Resource
    private CouponFeignService couponFeignService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SpuInfoEntity spuInfoEntity, List<Skus> skus) {
        if (skus != null && skus.size() > 0) {
            skus.forEach((item) -> {
                //5.1 保存sku基本信息
                String defaultImg = "";
                for (Images img : item.getImages()) {
                    if (img.getDefaultImg() == 1) {
                        defaultImg = img.getImgUrl();
                    }
                }
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item, skuInfoEntity);
                skuInfoEntity.setBrandId(skuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(skuInfoEntity.getCatalogId());
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSkuDefaultImg(defaultImg);

                this.baseMapper.insert(skuInfoEntity);

                //5.2 保存sku图片信息 pms_sku_images
                Long skuId = skuInfoEntity.getSkuId();
                List<SkuImagesEntity> skuImagesEntities = item.getImages().stream().map((img) -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    return skuImagesEntity;

                }).filter(entity -> {
                    return !StringUtils.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());

                skuImagesService.saveBatch(skuImagesEntities);

                //5.3 保存sku的销售属性信息 pms_sku_sale_attr_value
                List<Attr> attrs = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attrs.stream().map((attr) -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    skuSaleAttrValueEntity.setAttrId(attr.getAttrId());
                    skuSaleAttrValueEntity.setAttrName(attr.getAttrName());
                    skuSaleAttrValueEntity.setAttrValue(attr.getAttrValue());
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());

                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

                //5.4 保存sku的优惠、满减等信息 需要调用远程服务：mall_sms --> sms_sku_ladder\sms_sku_full_reduction等
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item, skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal(0)) == 1) {
                    couponFeignService.saveSkuReduction(skuReductionTo);
                }


            });
        }
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> skuInfoEntityQueryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            skuInfoEntityQueryWrapper.and((wrapper) -> {
                wrapper.eq("sku_id", key).or().like("sku_name", key);
            });
        }
        String catelogId = (String) params.get("catelogId");
        if ((!StringUtils.isEmpty(catelogId)) && (!"0".equalsIgnoreCase(catelogId))) {
            skuInfoEntityQueryWrapper.eq("catalog_id", catelogId);
        }
        String brandId = (String) params.get("brandId");
        if ((!StringUtils.isEmpty(brandId)) && (!"0".equalsIgnoreCase(brandId))) {
            skuInfoEntityQueryWrapper.eq("brand_id", brandId);
        }
        String min = (String) params.get("min");
        if (!StringUtils.isEmpty(min)) {
            skuInfoEntityQueryWrapper.ge("price", min);
        }
        String max = (String) params.get("max");
        if (!StringUtils.isEmpty(max) && new BigDecimal(max).compareTo(BigDecimal.ZERO) > 0) {
            skuInfoEntityQueryWrapper.le("price", max);
        }

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                skuInfoEntityQueryWrapper
        );

        return new PageUtils(page);

    }

    @Override
    public List<SkuInfoEntity> getSkuBySpuId(Long spuId) {

        List<SkuInfoEntity> list = this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        return list;
    }

}