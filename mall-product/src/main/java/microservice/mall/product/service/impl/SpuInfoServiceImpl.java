package microservice.mall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import microservice.mall.common.constant.ProductConstant;
import microservice.mall.common.es.SkuEsModel;
import microservice.mall.common.to.SkuHasStockTo;
import microservice.mall.common.to.SpuBoundTo;
import microservice.mall.common.utils.R;
import microservice.mall.product.dao.SpuInfoDescDao;
import microservice.mall.product.entity.*;
import microservice.mall.product.feign.CouponFeignService;
import microservice.mall.product.feign.SearchFeignService;
import microservice.mall.product.feign.WareFeignService;
import microservice.mall.product.service.*;
import microservice.mall.product.vo.BaseAttrs;
import microservice.mall.product.vo.Bounds;
import microservice.mall.product.vo.Skus;
import microservice.mall.product.vo.SpuSaveVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import microservice.mall.common.utils.PageUtils;
import microservice.mall.common.utils.Query;

import microservice.mall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Resource
    private CouponFeignService couponFeignService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrService attrService;

    @Resource
    private WareFeignService wareFeignService;

    @Resource
    private SearchFeignService searchFeignService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {
        //1、保存spu的基本信息  pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);

        //2、保存spu的描述图片 pms_spu_info_desc
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",", decript));
        spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);

        //3、保存spu的图片集   pms_spu_images
        List<String> images = vo.getImages();
        spuImagesService.saveSpuImages(spuInfoEntity.getId(), images);

        //4、保存spu的规格参数  pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        productAttrValueService.saveBaseAttrs(spuInfoEntity.getId(), baseAttrs);

        //6、保存spu的积分信息 调用远程服务 mall_sms --> sms_spu_bounds
        Bounds bounds = vo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();   //将Bounds对象封装成SpuBoundTo传输对象
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(spuInfoEntity.getId());
        couponFeignService.saveSpuBounds(spuBoundTo);

        //5、保存当前spu对于的所有sku信息
        List<Skus> skus = vo.getSkus();
        skuInfoService.saveSkuInfo(spuInfoEntity, skus);


    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        QueryWrapper<SpuInfoEntity> spuInfoEntityQueryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            spuInfoEntityQueryWrapper.and((wrapper) -> {
                wrapper.eq("id", key).or().like("spu_name", key);
            });
        }
        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            spuInfoEntityQueryWrapper.eq("publish_status", status);
        }
        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId)) {
            spuInfoEntityQueryWrapper.eq("catalog_id", catelogId);
        }
        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId)) {
            spuInfoEntityQueryWrapper.eq("brand_id", brandId);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                spuInfoEntityQueryWrapper
        );

        return new PageUtils(page);

    }

    @Override
    public void up(Long spuId) {
        //spuId对应多个sku商品
        List<SkuEsModel> upProducts = new ArrayList<>();

        //查询当前sku的所有可以被用来检索的规格属性
        List<ProductAttrValueEntity> productAttrValueEntities = productAttrValueService.baseAttrListForSpu(spuId);
        List<Long> attrIds = productAttrValueEntities.stream().map((attr) -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());

        List<Long> searchAttrIds = attrService.selectSearchAttrs(attrIds);


        Set<Long> idSet = new HashSet<>(searchAttrIds);
        List<SkuEsModel.Attrs> attrs = productAttrValueEntities.stream().filter((item) -> {
            return idSet.contains(item.getAttrId());
        }).map(attrValue -> {
            SkuEsModel.Attrs attrs1 = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(attrValue, attrs1);
            return attrs1;
        }).collect(Collectors.toList());

        //查出当前spuid对应的所有sku信息，品牌的名字
        List<SkuInfoEntity> skus = skuInfoService.getSkuBySpuId(spuId);
        List<Long> skuIds = skus.stream().map(sku -> {
            return sku.getSkuId();
        }).collect(Collectors.toList());

        //TODO 发生远程调用，查询库存
        Map<Long, Boolean> stockMap = new HashMap<>();
        try {
            R skusHasStock = wareFeignService.getSkusHasStock(skuIds);
            Object data = skusHasStock.get("data");
            String toJSONString = JSONObject.toJSONString(data);
            List<SkuHasStockTo> skuHasStockTos = JSONObject.parseObject(toJSONString, new TypeReference<List<SkuHasStockTo>>() {
            });
            stockMap = skuHasStockTos.stream().collect(Collectors.toMap(SkuHasStockTo::getSkuId, item -> item.isHasStock()));

        } catch (Exception e) {
            log.error("库存服务查询异常：{}", e);
        }

        //封装每个sku，组装成SkuEsModel
        Map<Long, Boolean> finalStockMap = stockMap;
        upProducts = skus.stream().map(sku -> {
            SkuEsModel esModel = new SkuEsModel();
            BeanUtils.copyProperties(sku, esModel);

            esModel.setSkuPrice(sku.getPrice());
            esModel.setSkuImg(sku.getSkuDefaultImg());
            if (finalStockMap == null) {
                esModel.setHasStock(true);
            } else {
                esModel.setHasStock(finalStockMap.get(sku.getSkuId()));
            }

            //TODO 热度评分，0
            esModel.setHotScore(0L);

            BrandEntity brandEntity = brandService.getById(esModel.getBrandId());
            esModel.setBrandName(brandEntity.getName());
            esModel.setBrandImg(brandEntity.getLogo());

            CategoryEntity categoryEntity = categoryService.getById(esModel.getCatalogId());
            esModel.setCatalogName(categoryEntity.getName());

            esModel.setAttrs(attrs);

            return esModel;
        }).collect(Collectors.toList());


        R r = searchFeignService.productStatusUp(upProducts);

        if ((Integer) r.get("code") == 0){

            this.baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        } else{

        }

    }


}