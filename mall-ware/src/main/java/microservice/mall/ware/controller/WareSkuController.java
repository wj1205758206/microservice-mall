package microservice.mall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import microservice.mall.common.to.SkuHasStockTo;
import microservice.mall.ware.vo.SkuHasStockVo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import microservice.mall.ware.entity.WareSkuEntity;
import microservice.mall.ware.service.WareSkuService;
import microservice.mall.common.utils.PageUtils;
import microservice.mall.common.utils.R;


/**
 * 商品库存
 *
 * @author wjian
 * @email wjian@gmail.com
 * @date 2021-07-04 11:22:57
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 查询sku是否有库存
     */
    @PostMapping("/hasstock")
    public R getSkusHasStock(@RequestBody List<Long> skuIds) {
        List<SkuHasStockTo> skuHasStockTos = wareSkuService.getSkusHasStock(skuIds);
        return R.ok().put("data",skuHasStockTos);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("ware:waresku:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("ware:waresku:info")
    public R info(@PathVariable("id") Long id) {
        WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("ware:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("ware:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("ware:waresku:delete")
    public R delete(@RequestBody Long[] ids) {
        wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
