package com.cailanzi.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cailanzi.mapper.ProductJdMapper;
import com.cailanzi.mapper.ProductMapper;
import com.cailanzi.mapper.ProductStatusMapper;
import com.cailanzi.mapper.ShopMapper;
import com.cailanzi.pojo.CategoriesVo;
import com.cailanzi.pojo.ProductListInput;
import com.cailanzi.pojo.ProductStatusInput;
import com.cailanzi.pojo.ProductVo;
import com.cailanzi.pojo.entities.Product;
import com.cailanzi.pojo.entities.ProductJd;
import com.cailanzi.pojo.entities.ProductStatus;
import com.cailanzi.pojo.entities.ShopJd;
import com.cailanzi.utils.JdHttpCilentUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
    private ProductMapper productMapper;
    @Autowired
    private ProductJdMapper productJdMapper;
    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private ProductStatusMapper productStatusMapper;

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
        ProductJd selectProduct = new ProductJd();
        selectProduct.setFixedStatus((byte)1);//只处理了上架商品
        List<ProductJd> productJdList = productJdMapper.select(selectProduct);

        productStatusMapper.truncateProductStatus();

        List<ShopJd> shopList = shopMapper.selectAll();
        for (ShopJd shopJd : shopList) {
            String stationNo = shopJd.getStationNo();
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
            productStatusMapper.insertList(productStatusList);
        }
    }

    //利用多线程 处理了所有商品
   /* public void asynProductStatusAsync() throws Exception {
        List<ProductJd> productJdList = productJdMapper.selectAll();

        productStatusMapper.truncateProductStatus();

        List<ShopJd> shopList = shopMapper.selectAll();
        for (ShopJd shopJd : shopList) {
            String stationNo = shopJd.getStationNo();

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
            productStatusMapper.insertList(productStatusList);
        }
    }*/

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

    public List<CategoriesVo> getCategories() throws Exception {
        JSONArray result = getCategoriesBasic();

        List<CategoriesVo> list = new ArrayList<>();
        list.add(new CategoriesVo("0","0","所有分类","0",new ArrayList<>()));
        for (Object o : result) {
            JSONObject parent = JSONObject.parseObject(o.toString());
            String parentPid = parent.getString("pid");
            if("0".equals(parentPid)){
                String parentId = parent.getString("id");
                String parentName = parent.getString("shopCategoryName");
                String parentSort = parent.getString("sort");
                List<CategoriesVo> childVoList = new ArrayList<>();
                for (Object b : result) {
                    JSONObject child = JSONObject.parseObject(b.toString());
                    String childPid = child.getString("pid");
                    String childId = child.getString("id");
                    String childName = child.getString("shopCategoryName");
                    String childSort = parent.getString("sort");
                    if(childPid.equals(parentId)){
                        CategoriesVo childVo = new CategoriesVo(childPid,childId,childName,childSort,null);
                        childVoList.add(childVo);
                    }
                }
                /*if(childVoList.isEmpty()){
                    childVoList.add(new CategoriesVo(parentPid,parentId,parentName,parentSort,null));
                }*/
                CategoriesVo vo = new CategoriesVo(parentPid,parentId,parentName,parentSort,childVoList);
                list.add(vo);
            }
        }
        return list;
    }

    public JSONArray getCategoriesBasic() throws Exception {
        String url = "https://openo2o.jd.com/djapi/pms/queryCategoriesByOrgCode";
        String jdParamJson = "{\"fields\":[\"ID\",\"PID\",\"SHOP_CATEGORY_NAME\",\"SHOP_CATEGORY_LEVEL\",\"SORT\"]}";
        log.info("ProductService getCategoriesBasic jdParamJson={}", jdParamJson);
        JSONObject data = JdHttpCilentUtil.doGetAndGetData(url,jdParamJson);
        return data.getJSONArray("result");
    }

    /**
     * 查询上架的所有商品
     * @param productListInput
     * @return
     */
    public List<ProductVo> getProductsByCategoryId(ProductListInput productListInput) {
        int pageSize = 10;
        int startIndex = productListInput.getPageNo()*pageSize;
        String categoryId = productListInput.getCategoryId();
        String phone = productListInput.getPhone();
        String stationNo = productListInput.getStationNo();

        List<ProductVo> list = null;
        if("0".equals(stationNo)){
            ShopJd shopJd = new ShopJd();
            shopJd.setPhone(phone);
            String shopStationNo = shopMapper.selectOne(shopJd).getStationNo();

            list = productJdMapper.getProductsByCategoryId(shopStationNo,categoryId,startIndex,pageSize);
        }else{
            list = productJdMapper.getProductsByPhone(phone,stationNo,categoryId,startIndex,pageSize);
        }
        return list;
    }

    /**
     * 获取商品图片信息
     * @return
     * @throws Exception
     */
    /*public List<JSONArray> getImg() throws Exception {
        List<ProductJd> list = productJdMapper.selectAll();
        return getImagesBySkuIds(list);
    }

    public List<JSONArray> getImagesBySkuIds(List<ProductJd> list) throws Exception {
        String url = "https://openo2o.jd.com/djapi/order/queryListBySkuIds";
        List<JSONArray> temp = new ArrayList<>();
        for (ProductJd productJd : list) {
            String jdParamJson = "{\"skuIds\":"+productJd.getSkuId()+"}";
            JSONObject data = JdHttpCilentUtil.doGetAndGetData(url,jdParamJson);
            temp.add(data.getJSONArray("result"));
        }
        return temp;
    }*/


}
