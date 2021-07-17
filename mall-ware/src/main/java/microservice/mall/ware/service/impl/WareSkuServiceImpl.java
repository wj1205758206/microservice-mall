package microservice.mall.ware.service.impl;

import microservice.mall.common.to.SkuHasStockTo;
import microservice.mall.common.utils.R;
import microservice.mall.ware.feign.ProductFeignService;
import microservice.mall.ware.vo.SkuHasStockVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import microservice.mall.common.utils.PageUtils;
import microservice.mall.common.utils.Query;

import microservice.mall.ware.dao.WareSkuDao;
import microservice.mall.ware.entity.WareSkuEntity;
import microservice.mall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Resource
    private WareSkuDao wareSkuDao;

    @Resource
    private ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> wareSkuEntityQueryWrapper = new QueryWrapper<>();

        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)) {
            wareSkuEntityQueryWrapper.eq("ware_id", wareId);
        }
        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId)) {
            wareSkuEntityQueryWrapper.eq("sku_id", skuId);
        }


        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wareSkuEntityQueryWrapper
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        List<WareSkuEntity> wareSkuEntities = this.baseMapper.selectList(new QueryWrapper<WareSkuEntity>()
                .eq("sku_id", skuId)
                .eq("ware_id", wareId));
        if (wareSkuEntities.size() == 0 || wareSkuEntities == null) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);

            try {
                R info = productFeignService.info(skuId);
                Map<String, Object> skuInfo = (Map<String, Object>) info.get("skuInfo");
                if (info.get("code").equals(0)) {
                    wareSkuEntity.setSkuName((String) skuInfo.get("skuName"));
                }

            } catch (Exception e) {

            }
            wareSkuDao.insert(wareSkuEntity);
        } else {
            wareSkuDao.addStock(skuId, wareId, skuNum);
        }

    }

    @Override
    public List<SkuHasStockTo> getSkusHasStock(List<Long> skuIds) {

        List<SkuHasStockVo> vos = skuIds.stream().map((skuId) -> {
            SkuHasStockVo skuHasStockVo = new SkuHasStockVo();
            Long count = this.baseMapper.getSkuStock(skuId);
            skuHasStockVo.setSkuId(skuId);
            skuHasStockVo.setHasStock(count == null ? false : count > 0);

            return skuHasStockVo;
        }).collect(Collectors.toList());

        return vos;
    }

}