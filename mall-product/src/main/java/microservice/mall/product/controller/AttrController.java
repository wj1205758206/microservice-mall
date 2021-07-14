package microservice.mall.product.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import microservice.mall.common.utils.PageUtils;
import microservice.mall.common.utils.R;
import microservice.mall.product.entity.AttrEntity;
import microservice.mall.product.entity.ProductAttrValueEntity;
import microservice.mall.product.service.AttrService;
import microservice.mall.product.service.ProductAttrValueService;
import microservice.mall.product.vo.AttrRespVo;
import microservice.mall.product.vo.AttrVo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 商品属性
 *
 * @author wjian
 * @email wjian@gmail.com
 * @date 2021-07-04 09:48:57
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    /**
     * 修改Spu规格列表信息
     */
    @PostMapping("/update/{spuId}")
    //@RequiresPermissions("product:attr:update")
    public R updatBaseAttrListForSpu(@PathVariable("spuId") Long spuId,
                                     @RequestBody List<ProductAttrValueEntity> entities) {
        productAttrValueService.updatBaseAttrListForSpu(spuId, entities);

        return R.ok();
    }

    /**
     * 获取Spu规格列表信息
     */
    @GetMapping("/base/listforspu/{spuId}")
    public R baseAttrListForSpu(@PathVariable("spuId") Long spuId) {

        List<ProductAttrValueEntity> entities = productAttrValueService.baseAttrListForSpu(spuId);
        return R.ok().put("data", entities);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 获取分类规格参数列表
     */
    @GetMapping("/{attrType}/list/{catelogId}")
    //@RequiresPermissions("product:attr:list")
    public R baseAttrList(@RequestParam Map<String, Object> params,
                          @PathVariable("catelogId") Long catelogId,
                          @PathVariable("attrType") String type) {

        PageUtils page = attrService.queryBaseAttrPage(params, catelogId, type);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId) {
        //AttrEntity attr = attrService.getById(attrId);
        AttrRespVo attrRespVo = attrService.getAttrInfo(attrId);
        return R.ok().put("attr", attrRespVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attr) {
        attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVo attr) {
        attrService.updateAttr(attr);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds) {
        attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

/*    @GetMapping("/sale/list/{catelog_id}")
    public R saleAttrList(@RequestParam Map<String, Object> params,
                          @PathVariable("catelogId") Long catelogId) {

        PageUtils page = attrService.querySaleAttrPage(params, catelogId);

        return R.ok().put("page", page);
    }*/


}
