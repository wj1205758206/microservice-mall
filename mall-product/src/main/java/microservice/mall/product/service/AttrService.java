package microservice.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import microservice.mall.common.utils.PageUtils;
import microservice.mall.product.entity.AttrEntity;
import microservice.mall.product.entity.ProductAttrValueEntity;
import microservice.mall.product.vo.AttrGroupRelationVo;
import microservice.mall.product.vo.AttrRespVo;
import microservice.mall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author wjian
 * @email wjian@gmail.com
 * @date 2021-07-04 09:48:57
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attrVo);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type);

    AttrRespVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);

    List<AttrEntity> getRelationAttr(Long attrgroupId);

    void deleteRelation(AttrGroupRelationVo[] vos);

    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);


    List<Long> selectSearchAttrs(List<Long> attrIds);
}

