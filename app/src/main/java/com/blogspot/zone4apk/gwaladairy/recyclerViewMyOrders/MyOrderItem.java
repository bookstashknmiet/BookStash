package com.blogspot.zone4apk.gwaladairy.recyclerViewMyOrders;

import java.io.Serializable;

/**
 * Created by AMIT on 7/27/2018.
 */

public class MyOrderItem {

 String orderNo;
 String Address;

 public String getOrderstatus() {
  return orderstatus;
 }

 public void setOrderstatus(String orderstatus) {
  this.orderstatus = orderstatus;
 }

 String orderstatus;

 public String getAddress() {
  return Address;
 }

 public void setAddress(String address) {
  Address = address;
 }

 String time;

 public String getOrderNo() {
  return orderNo;
 }

 public void setOrderNo(String orderNo) {
  this.orderNo = orderNo;
 }

 public String getTime() {
  return time;
 }

 public void setTime(String time) {
  this.time = time;
 }
}
