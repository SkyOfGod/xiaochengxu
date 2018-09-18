package com.cailanzi.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cailanzi.mapper.*;
import com.cailanzi.pojo.ProductListInput;
import com.cailanzi.pojo.ProductStatusInput;
import com.cailanzi.pojo.entities.*;
import com.cailanzi.utils.ConstantsUtil;
import com.cailanzi.utils.JdHttpCilentUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by v-hel27 on 2018/8/8.
 */
@Service
@Slf4j
public class ProductService {

    @Autowired
    private ProductJdMapper productJdMapper;
    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private ProductStatusMapper productStatusMapper;
    @Autowired
    private ProductCategoryMapper productCategoryMapper;

    public void asynProduct() throws Exception {
        ProductListInput productListInput = new ProductListInput();
        productListInput.setPageSize(50);//每页最大只能为50

        int pageNo = 1;
        JSONArray products = new JSONArray();
        while (true){
            JSONObject data = JSONObject.parseObject(getJdProductList(productListInput));
            JSONArray temp = data.getJSONArray("result");
            if(temp==null||temp.size()==0){
                break;
            }
            products.addAll(temp);
            pageNo++;
            productListInput.setPageNo(pageNo);
        }
        if(!products.isEmpty()){
            handleProductJd(products);
        }
    }

    @Transactional
    private void handleProductJd(JSONArray products) {
        Date date = new Date();

        List<ProductJd> list = new ArrayList<>();
        for (Object product : products) {
            JSONObject data = JSONObject.parseObject(product.toString());
            ProductJd jd = JSONObject.toJavaObject(data,ProductJd.class);
            if(jd.getShopCategories()!=null){
                String temp = jd.getShopCategories().substring(1,jd.getShopCategories().length()-1);
                jd.setShopCategories(temp);
            }
            if(jd.getSellCities()!=null){
                String temp = jd.getSellCities().substring(1,jd.getSellCities().length()-1);
                jd.setSellCities(temp);
            }
            jd.setSyncTime(date);
            list.add(jd);
        }
        productJdMapper.truncateProductJd();
        productJdMapper.insertList(list);
    }

    private String getJdProductList(ProductListInput productListInput) throws Exception {
        log.info("ProductService getJdProductList ProductListInput productListInput={}", productListInput);
        String url = "https://openo2o.jd.com/djapi/pms/querySkuInfos";
        String jdParamJson = JdHttpCilentUtil.getJdParamJson(productListInput);

        JSONObject data = JdHttpCilentUtil.doGetAndGetData(url,jdParamJson);
        return data.getString("result");
    }

    public void asynProductStatus() throws Exception {
        Date date = new Date();

        List<ShopJd> shopList = shopMapper.selectAll();
        for (ShopJd shopJd : shopList) {
            String stationNo = shopJd.getStationNo();
            //只处理当前门店下面没有拉回的商品状态数据
            List<ProductJd> productJdList = productJdMapper.selectByStationNoLeftJoinProductStatus(stationNo);
            if(productJdList.isEmpty()){
                continue;
            }
            Map<Long,ProductJd> productJdListMap = new HashMap<>();
            for (ProductJd productJd : productJdList) {
                productJdListMap.put(productJd.getSkuId(),productJd);
            }

            List<ProductStatus> productStatusList = new ArrayList<>();
            List<ProductStatusInput> productStatusInputList = new ArrayList<>();
            for (int i = 0; i < productJdList.size(); i++) {
                Long skuId = productJdList.get(i).getSkuId();
                ProductStatusInput productStatusInput = new ProductStatusInput(stationNo,skuId);
                productStatusInputList.add(productStatusInput);
                if(productStatusInputList.size()==50||i==productJdList.size()-1){
                    getJdProductStatusList(productStatusInputList,productStatusList);
                    productStatusInputList.clear();
                }
            }
            for (ProductStatus productStatus : productStatusList) {
                productStatus.setCreateTime(date);
                productStatus.setCurrentQty(productStatus.getLockQty()+productStatus.getUsableQty()+productStatus.getOrderQty());

                ProductJd productJd = productJdListMap.get(productStatus.getSkuId());
                productStatus.setName(productJd.getSkuName());
                productStatus.setPrice(productJd.getSkuPrice());
            }
            if(!productStatusList.isEmpty()){
                productStatusMapper.insertList(productStatusList);
            }
        }
    }

    private void getJdProductStatusList(List<ProductStatusInput> list,List<ProductStatus> productStatusList) throws Exception {
        log.info("ProductService getJdProductStatusList List<ProductStatusInput> list={},productStatusList={}", list,productStatusList);
        Map<String,Object> map = new HashMap<>();
        map.put("listBaseStockCenterRequest",list);
        String jdParamJson = JSON.toJSONString(map);

        String url = "https://openo2o.jd.com/djapi/stock/queryOpenUseable";
        JSONObject data = JdHttpCilentUtil.doGetAndGetData(url,jdParamJson,"0","retCode","retMsg");

        String text = data.getString("data");
        log.info("ProductService getJdProductStatusList text={}", text);
        if(text != null){
            productStatusList.addAll(JSONArray.parseArray(text,ProductStatus.class));
        }
    }

    /* 	根据商家商品编码和门店编码批量查询商品库存及可售状态信息接口
    public void asynProductStatus() throws Exception {
        ProductJd selectProduct = new ProductJd();
        selectProduct.setFixedStatus((byte)1);//只处理了上架商品
        List<ProductJd> productJdList = productJdMapper.select(selectProduct);

        productStatusMapper.truncateProductStatus();

        List<ShopJd> shopList = shopMapper.selectAll();
        for (ShopJd shopJd : shopList) {
            List<ProductStatus> productStatusList = new ArrayList<>();

            List<SkuIdEntity> skuIds = new ArrayList<>();
            ProductStatusJdImport productStatusJdImport = new ProductStatusJdImport();
            productStatusJdImport.setOutStationNo(shopJd.getStationNo());
            for (int i = 0; i < productJdList.size(); i++) {
                String  skuId = productJdList.get(i).getSkuId()+"";
                skuIds.add(new SkuIdEntity(skuId));
                if(skuIds.size()==50||i==productJdList.size()-1){
                    productStatusJdImport.setSkuIds(skuIds);
                    getJdProductStatusList(productStatusJdImport,productStatusList);
                    skuIds.clear();
                }
            }
            productStatusMapper.insertList(productStatusList);
        }
    }

    public void getJdProductStatusList(ProductStatusJdImport productStatusJdImport, List<ProductStatus> productStatusList) throws Exception {
        log.info("ProductService getJdProductStatusList ProductStatusJdImport productStatusJdImport={},productStatusList={}", productStatusJdImport,productStatusList);
        String jdParamJson = JSON.toJSONString(productStatusJdImport);

        String url = "https://openo2o.jd.com/djapi/stock/queryStockCenter";
        JSONObject data = JdHttpCilentUtil.doGetAndGetData(url,jdParamJson,"0","retCode","retMsg");

        String text = data.getString("data");
        log.info("ProductService getJdProductStatusList text={}", text);
        if(text != null){
            productStatusList.addAll(JSONArray.parseArray(text,ProductStatus.class));
        }
    }*/

    public void asynCategories() throws Exception {
        JSONArray result = getCategoriesBasic();

        Date date = new Date();
        List<ProductCategory> list = new ArrayList<>();
//        list.add(new ProductCategory(ConstantsUtil.ProductCategory.ALL_ID,"0","所有分类","0","0",date));
        for (Object o : result) {
            JSONObject jsonObject = JSONObject.parseObject(o.toString());
            ProductCategory productCategory = new ProductCategory();
            productCategory.setId(jsonObject.getString("id"));
            productCategory.setPid(jsonObject.getString("pid"));
            productCategory.setShopCategoryName(jsonObject.getString("shopCategoryName"));
            productCategory.setShopCategoryLevel(jsonObject.getString("shopCategoryLevel"));
            productCategory.setSort(jsonObject.getString("sort"));
            productCategory.setCreateTime(date);
            list.add(productCategory);
        }
        if(!list.isEmpty()){
            log.info("ProductService asynCategories List<ProductCategory> list={}", list);
            productCategoryMapper.delete(new ProductCategory());
            productCategoryMapper.batchInsertList(list);
        }
    }

    public JSONArray getCategoriesBasic() throws Exception {
        String url = "https://openo2o.jd.com/djapi/pms/queryCategoriesByOrgCode";
        String jdParamJson = "{\"fields\":[\"ID\",\"PID\",\"SHOP_CATEGORY_NAME\",\"SHOP_CATEGORY_LEVEL\",\"SORT\"]}";
        log.info("ProductService getCategoriesBasic jdParamJson={}", jdParamJson);
        JSONObject data = JdHttpCilentUtil.doGetAndGetData(url,jdParamJson);
        return data.getJSONArray("result");
    }

}
