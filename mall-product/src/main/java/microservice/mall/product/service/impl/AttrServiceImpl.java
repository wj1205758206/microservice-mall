package microservice.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import microservice.mall.common.constant.ProductConstant;
import microservice.mall.product.dao.AttrAttrgroupRelationDao;
import microservice.mall.product.dao.AttrGroupDao;
import microservice.mall.product.dao.CategoryDao;
import microservice.mall.product.entity.*;
import microservice.mall.product.service.CategoryService;
import microservice.mall.product.vo.AttrGroupRelationVo;
import microservice.mall.product.vo.AttrRespVo;
import microservice.mall.product.vo.AttrVo;
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

import microservice.mall.product.dao.AttrDao;
import microservice.mall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Resource
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Resource
    private CategoryDao categoryDao;

    @Resource
    private AttrGroupDao attrGroupDao;

    @Autowired
    private CategoryService categoryService;

    @Resource
    private AttrDao attrDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttr(AttrVo attrVo) {
        //保存基本数据
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attrEntity);
        this.save(attrEntity);

        //保存关联关系
        if (attrVo.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationEntity.setAttrGroupId(attrVo.getAttrGroupId());
            attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
        }

    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type) {
        QueryWrapper<AttrEntity> attrEntityQueryWrapper =
                new QueryWrapper<AttrEntity>().eq("attr_type",
                        "base".equalsIgnoreCase(type)
                                ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()
                                : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());

        if (catelogId != 0) {
            attrEntityQueryWrapper.and((wrapper) -> {
                wrapper.eq("catelog_id", catelogId);
            });
        }
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            attrEntityQueryWrapper.and((obj) -> {
                obj.eq("attr_id", key).or().like("attr_name", key);
            });
        }


        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), attrEntityQueryWrapper);

        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> attrEntityList = page.getRecords();
        List<AttrRespVo> respVos = attrEntityList.stream().map((attrEntity) -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);

            //额外设置catelogName分类名称和groupName分组名称
            AttrAttrgroupRelationEntity attrId = attrAttrgroupRelationDao.selectOne(
                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));

            if (attrId != null && "base".equalsIgnoreCase(type)) {
                if (attrId.getAttrGroupId() != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrId.getAttrGroupId());
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }

            }

            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                attrRespVo.setCatelogName(categoryEntity.getName());
            }

            return attrRespVo;
        }).collect(Collectors.toList());

        pageUtils.setList(respVos);
        return pageUtils;
    }

    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrEntity attrEntity = this.getById(attrId);

        AttrRespVo attrRespVo = new AttrRespVo();
        BeanUtils.copyProperties(attrEntity, attrRespVo);

        AttrAttrgroupRelationEntity attrAttrgroupRelationEntity =
                attrAttrgroupRelationDao.selectOne(
                        new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));

        //设置分组信息
        if (attrAttrgroupRelationEntity != null && (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode())) {
            attrRespVo.setAttrGroupId(attrAttrgroupRelationEntity.getAttrGroupId());
            AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
            if (attrGroupEntity != null) {
                attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
            }
        }

        //设置分类信息
        CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
        if (categoryEntity != null) {
            attrRespVo.setCatelogName(categoryEntity.getName());
        }
        Long[] catelogPath = categoryService.findCatelogPath(attrEntity.getCatelogId());
        attrRespVo.setCatelogPath(catelogPath);


        return attrRespVo;

    }

    @Transactional
    @Override
    public void updateAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.updateById(attrEntity);

        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrId(attr.getAttrId());
            attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());

            Integer count = attrAttrgroupRelationDao.selectCount(
                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));

            if (count > 0) {
                attrAttrgroupRelationDao.update(attrAttrgroupRelationEntity,
                        new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            } else {
                attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
            }
        }
    }

    /**
     * 根据分组id查找关联的所有基本属性
     *
     * @param attrgroupId
     * @return
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> attrgroupRelationEntities = attrAttrgroupRelationDao.selectList(
                new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));

        List<Long> attrIds = attrgroupRelationEntities.stream().map((attr) -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());

        if (attrIds == null || attrIds.size() == 0) {
            return null;
        }
        Collection<AttrEntity> attrEntities = this.listByIds(attrIds);

        return (List<AttrEntity>) attrEntities;
    }

    @Override
    public void deleteRelation(AttrGroupRelationVo[] vos) {
        List<AttrAttrgroupRelationEntity> attrgroupRelationEntityList =
                Arrays.asList(vos).stream().map(attrGroupRelationVo -> {
                    AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
                    BeanUtils.copyProperties(attrGroupRelationVo, attrAttrgroupRelationEntity);
                    return attrAttrgroupRelationEntity;
                }).collect(Collectors.toList());

        attrAttrgroupRelationDao.deleteBatchRelation(attrgroupRelationEntityList);
    }

    /**
     * 获取当前分组没有关联的属性
     *
     * @param params
     * @param attrgroupId
     * @return
     */
    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
        //当前分组只能关联自己所属的分类里面的属性
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        Long catelogId = attrGroupEntity.getCatelogId();

        //当前分组只能关联别的分组没有引用的属性
        List<AttrGroupEntity> attrGroupEntities = attrGroupDao.selectList(
                new QueryWrapper<AttrGroupEntity>()
                        .eq("catelog_id", catelogId));
        List<Long> collect = attrGroupEntities.stream().map(attrGroup -> {
            return attrGroup.getAttrGroupId();
        }).collect(Collectors.toList());

        List<AttrAttrgroupRelationEntity> groupId = attrAttrgroupRelationDao.selectList(
                new QueryWrapper<AttrAttrgroupRelationEntity>()
                        .in("attr_group_id", collect));

        List<Long> attrIds = groupId.stream().map(item -> {
            return item.getAttrId();
        }).collect(Collectors.toList());


        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>()
                .eq("catelog_id", catelogId)
                .eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if (attrIds != null && attrIds.size() > 0) {
            queryWrapper.notIn("attr_id", attrIds);
        }
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and((item) -> {
                queryWrapper.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);

        return new PageUtils(page);
    }

    @Override
    public List<Long> selectSearchAttrs(List<Long> attrIds) {

        return this.baseMapper.selectSearchAttrIds(attrIds);
    }



  /*  public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
        //当前分组只能关联自己所属的分类里面的属性
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        List<AttrEntity> attrEntityList = attrDao.selectList(
                new QueryWrapper<AttrEntity>()
                        .eq("catelog_id", attrGroupEntity.getCatelogId()));

        List<Long> attrIds = attrEntityList.stream().map((attrEntity -> {
            return attrEntity.getAttrId();
        })).collect(Collectors.toList());


        //当前分组只能关联别的分组没有引用的属性
        List<AttrAttrgroupRelationEntity> attrGroupIds = attrAttrgroupRelationDao.selectList(
                new QueryWrapper<AttrAttrgroupRelationEntity>()
                        .ne("attr_group_id", attrgroupId));
        List<Long> ids = attrGroupIds.stream().map(attrAttrgroupRelationEntity -> {
            return attrAttrgroupRelationEntity.getAttrId();
        }).collect(Collectors.toList());

        attrIds.removeAll(ids);
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
                        .in("attr_id", attrIds)
                        .eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()));


        return new PageUtils(page);
    }*/
}