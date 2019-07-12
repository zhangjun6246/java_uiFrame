package com.globalegrow.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Product {
	public String id;
	public String URL;
	public String sku;
	public String shopPrice;
	public String promotePrice;
	public int type;
	public int stockNum;
	public int isPromote;
	public String promoteStartDate;
	public String promoteEndDate;
	public String goodsvweight;
	public String goodsweight;
	public int stepNum;
	public int goods_number;
	public int is_on_sale_sites ;
	public int is_on_sale_android;
	public int is_on_sale_ios;
	public List<String> stepName = new ArrayList<String>();
	public HashMap<String, List<String>> stepSectionMap = new HashMap<String, List<String>>();
	public HashMap<String, String> stepPriceMap = new HashMap<String, String>();
	public HashMap<String, String> widMap = new HashMap<String, String>();
	public HashMap<String, String> widMapValid = new HashMap<String, String>();
	public HashMap<String, List<String>> widCountryMap = new HashMap<String, List<String>>();

	public Product() {
		id = "";
		URL = "";
		sku = "";
		shopPrice = "";
		promotePrice = "";
		type = 0;
		stepNum = 0;
		stockNum = 0;
		isPromote = 0;
		promoteStartDate = "0";
		promoteEndDate = "0";
		goodsvweight = "0";
		goodsweight ="0";

		goods_number = 0;
		is_on_sale_sites =0;
		is_on_sale_android= 0;
		is_on_sale_ios = 0;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ",goodsweight=" + goodsweight + ",goodsvweight=" + goodsvweight + ", URL=" + URL + ", sku=" + sku + ", shopPrice=" + shopPrice + ", promotePrice="
				+ promotePrice + ", type=" + type + ", stockNum=" + stockNum + ", isPromote=" + isPromote
				+ ", promoteStartDate=" + promoteStartDate + ", promoteEndDate=" + promoteEndDate + ", stepNum="
				+ stepNum + ", goods_number=" + goods_number + ", is_on_sale_sites=" + is_on_sale_sites
				+ ", is_on_sale_android=" + is_on_sale_android + ", is_on_sale_ios=" + is_on_sale_ios + ", stepName="
				+ stepName + ", stepSectionMap=" + stepSectionMap + ", stepPriceMap=" + stepPriceMap + ", widMap="
				+ widMap + ", widMapValid=" + widMapValid + ", widCountryMap=" + widCountryMap + "]";
	}

	public String getStep1Price() {
		String price = "";
		if (stepNum > 0) {
			price = stepPriceMap.get(stepName.get(0));
		}
		return price;
	}

	public String getStep2Price() {
		String price = "";
		if (stepNum > 0) {
			price = stepPriceMap.get(stepName.get(1));
		}
		return price;
	}
}
