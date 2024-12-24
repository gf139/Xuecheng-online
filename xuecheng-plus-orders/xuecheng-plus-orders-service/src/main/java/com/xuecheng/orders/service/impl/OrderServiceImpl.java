package com.xuecheng.orders.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.utils.IdWorkerUtils;
import com.xuecheng.orders.mapper.XcOrdersGoodsMapper;
import com.xuecheng.orders.mapper.XcOrdersMapper;
import com.xuecheng.orders.mapper.XcPayRecordMapper;
import com.xuecheng.orders.model.dto.AddOrderDto;
import com.xuecheng.orders.model.dto.PayRecordDto;
import com.xuecheng.orders.model.po.XcOrders;
import com.xuecheng.orders.model.po.XcOrdersGoods;
import com.xuecheng.orders.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    XcOrdersMapper ordersMapper;
    @Autowired
    XcOrdersGoodsMapper ordersGoodsMapper;
    
    @Autowired
    XcPayRecordMapper payRecordMapper;

    /**
     * 创建商品订单
     * @param userId
     * @param addOrderDto 订单信息
     * @return
     */
    @Transactional
    @Override
    public PayRecordDto createOrder(String userId, AddOrderDto addOrderDto) {

        //添加商品订单

        //添加支付记录
        
        //生成二维码
        
        return null;
    }

    public XcOrders saveXcOrders(String userId, AddOrderDto addOrderDto){
        //幂等性处理
        XcOrders order = getOrderByBusinessId(addOrderDto.getOutBusinessId());
        if(order!=null){
            return order;
        }
        order = new XcOrders();
        //生成订单号
        long orderId = IdWorkerUtils.getInstance().nextId();
        order.setId(orderId);
        order.setTotalPrice(addOrderDto.getTotalPrice());
        order.setCreateDate(LocalDateTime.now());
        order.setStatus("600001");//未支付
        order.setUserId(userId);
        order.setOrderType(addOrderDto.getOrderType());
        order.setOrderName(addOrderDto.getOrderName());
        order.setOrderDetail(addOrderDto.getOrderDetail());
        order.setOrderDescrip(addOrderDto.getOrderDescrip());
        order.setOutBusinessId(addOrderDto.getOutBusinessId());//选课记录id
        ordersMapper.insert(order);
        String orderDetailJson = addOrderDto.getOrderDetail();
        List<XcOrdersGoods> xcOrdersGoodsList = JSON.parseArray(orderDetailJson, XcOrdersGoods.class);
        xcOrdersGoodsList.forEach(goods->{
            XcOrdersGoods xcOrdersGoods = new XcOrdersGoods();
            BeanUtils.copyProperties(goods,xcOrdersGoods);
            xcOrdersGoods.setOrderId(orderId);//订单号
            ordersGoodsMapper.insert(xcOrdersGoods);
        });
        return order;
    }

    //根据业务id查询订单
    public XcOrders getOrderByBusinessId(String businessId) {
        XcOrders orders = ordersMapper.selectOne(new LambdaQueryWrapper<XcOrders>().eq(XcOrders::getOutBusinessId, businessId));
        return orders;
    }
}