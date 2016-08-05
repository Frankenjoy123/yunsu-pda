/**
 * 
 */
package com.yunsoo.unity;

import java.io.Serializable;
import java.util.List;

/**
 * @author thinkpad
 *
 */
public class PackageDetail implements Serializable{
	private String packageId;
	private List<String> productIdList;
	public String getPackageId() {
		return packageId;
	}
	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}
	public List<String> getProductIdList() {
		return productIdList;
	}
	public void setProductIdList(List<String> productIdList) {
		this.productIdList = productIdList;
	}
	
	

}
