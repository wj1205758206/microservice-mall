package microservice.mall.product.service.impl;

import microservice.mall.product.entity.AttrAttrgroupRelationEntity;
import microservice.mall.product.entity.AttrEntity;
import microservice.mall.product.service.AttrAttrgroupRelationService;
import microservice.mall.product.service.AttrService;
import microservice.mall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import microservice.mall.common.utils.PageUtils;
import microservice.mall.common.utils.Query;

import microservice.mall.product.dao.AttrGroupDao;
import microservice.mall.product.entity.AttrGroupEntity;
import microservice.mall.product.service.AttrGroupService;
import org.springframework.util.StringUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        //如果有key还需要模糊查询
        QueryWrapper<AttrGroupEntity> attrGroupEntityQueryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            attrGroupEntityQueryWrapper.and((obj) -> {
                obj.eq("attr_group_id", key).or().like("attr_group_name", key);
            });
        }
        //如果catelogId == 0 则查出所有的分类的分组属性
        if (catelogId == 0) {
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), attrGroupEntityQueryWrapper);
            return new PageUtils(page);
        } else {
            //查出指定categlogId的分组属性
            attrGroupEntityQueryWrapper.eq("catelog_id", catelogId);
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), attrGroupEntityQueryWrapper);
            return new PageUtils(page);
        }
    }

    /**
     * 根据分类id查出所有的分组以及这些分组里面的属性
     *
     * @param catelogId
     * @return
     */
    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrByCatelogId(Long catelogId) {


        //查询分类下的所有分组
        List<AttrGroupEntity> attrGroupEntityList = this.baseMapper.selectList(
                new QueryWrapper<AttrGroupEntity>()
                        .eq("catelog_Id", catelogId));

        //查询每个分组下的所有属性
        List<AttrGroupWithAttrsVo> vos = attrGroupEntityList.stream().map((item) -> {
            List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = attrAttrgroupRelationService.list(
                    new QueryWrapper<AttrAttrgroupRelationEntity>()
                            .eq("attr_group_id", item.getAttrGroupId())
            );

            List<Long> attrIds = attrAttrgroupRelationEntities.stream().map((attrAttrgroupRelationEntity -> {
                return attrAttrgroupRelationEntity.getAttrId();
            })).collect(Collectors.toList());

            Collection<AttrEntity> attrEntities = attrService.listByIds(attrIds);

            AttrGroupWithAttrsVo attrGroupWithAttrsVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(item, attrGroupWithAttrsVo);
            attrGroupWithAttrsVo.setAttrs((List<AttrEntity>) attrEntities);

            return attrGroupWithAttrsVo;
        }).collect(Collectors.toList());

        return vos;
    }


}