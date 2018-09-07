package com.cailanzi.pojo.entities;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by v-hel27 on 2018/8/13.
 */
@Data
@Table(name="shop_jd")
public class ShopJd {

    private Long id;//主键ID(京东到家主键)
    private String venderId;//商家编码
    private String venderName;//商家名称
    private String stationName;//门店名称
    private String stationNo;//到家门店编码
    private String outSystemId;//商家门店编码
    private Double lat;//经度
    private Double lng;//纬度
    private String coordinateAddress;//地址
    private String coordinate;//坐标
    private Integer province;//省份
    private String provinceName;//省份名称
    private Integer city;//城市
    private String cityName;//城市名称
    private Integer county;//镇/区编码
    private String countyName;//镇名称
    private String phone;//门店电话
    private String mobile;//门店手机
    private String stationAddress;//详细地址
    private String createPin;//创建人
    private Date createTime;//创建时间
    private Byte yn;//门店状态,0启用,1禁用
    private Byte closeStatus;//营业状态,0正常营业,1休息中
    private Byte preWarehouse;//是否前置仓,1是,2不是
    private String updatePin;//更新操作人
    private Byte selfPickSupport;//是否支持自提,1支持,2不支持
    private Byte wareType;//是否是仓库类型门店,1是,2否
    private Byte stationDeliveryStatus;//是否已设置配送服务,1是,2否
    private Byte supportOfflinePurchase;//是否支持线下购,0不支持,1支持
    private String standByPhone;//备联电话，多个电话逗号隔开（只有备联电话才能联系客户，其他电话无法打通客户电话。如为座机，请连续输入区号和电话号，400电话不识别
    private Byte timeAmType;//是否支持上午配送,1支持,2不支持
    private Byte timePmType;//是否支持下午配送,1是,2否
    private Integer serviceTimeStart1;//营业开始时间1
    private Integer serviceTimeEnd1;//营业结束时间1
    private Integer serviceTimeStart2;//营业开始时间2
    private Integer serviceTimeEnd2;//营业结束时间2
    private Byte isMembership;//1 会员店，0 非会员店
    private Byte innerNoStatus;//中间号状态 1 开启，0关闭
    private Byte isAutoOrder;//是否自动接单，0:是1:否
    private Integer carrierNo;//9966 众包,2938 自送
    private Byte isNoPaper;//是否无纸化 1是，2否
    private Integer orderAging;//订单时效，单位：分钟
    private Byte orderNoticeType;//订单通知类型 1：下单即通知（订单通知商家的时间为：无论用户选择未来任何时段，订单都会立即下发给商家。）2：服务开始前n小时通知（订单通知商家的时间为：用户选择送达最晚时间-订单时效-n。）
    private Byte supportInvoice;//是否支持发票，0：不支持，1：支持
    private Byte regularFlag;//送达类型 1：立即送达；2：定时达 3：立即送达+定时达

    private Date updateTime;
    private Date syncTime;

}
