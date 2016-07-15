package com.hqu.indoor_pos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hqu.indoor_pos.bean.BleBase;
import com.hqu.indoor_pos.bean.Group;

/**
 *  <p>根据基站id进行分组，并分别求每个基站rssi的去除极端值的均值。</p>
 * @author megagao
 */

public class DoGroup {
	
	/*用来求组合数的数组*/
	private Integer[] a; 
	
	 /**
     * <p>
     * 根据传进来的基站列表，将基站进行分组，得到每个基站rssi的去除极端值的均值所组成的列表。
     * </p>
     * 
     * @param  bases	接收到的一组基站对象列表
     * 
     * @return ArrayList<BleBase>	返回每个基站rssi的去除极端值的均值所组成的列表。
     *    
     */
	public ArrayList<BleBase> doGroup(List<BleBase> bases) {
		
		Map<String, Group> groupedBases = group(bases);
		
		List<BleBase> uniqueBases = dealByGroup(groupedBases);
		
		return (ArrayList<BleBase>) uniqueBases;
	}
	
	 /**
     * <p>
     * 根据传进来的基站列表，将基站进行分组
     * </p>
     * 
     * @param bases	接收到的一组基站对象列表
     * 
     * @return Map<String, Group>	返回分好组的Map，其中，key为基站id，value为Group对象。
     * 								Group对象封装了所有接收到的该基站的所有rssi值列表。
     *    
     */
	public Map<String, Group> group(List<BleBase> bases) {
		
		Map<String, Group> groupedBases = new HashMap<String, Group>();
		
		/*由Set集合存放unique id值*/
		Set<String> ids = new HashSet<String>();
		
		for(BleBase base : bases){
			ids.add(base.getId());
		}
		
		for(String id : ids){
			groupedBases.put(id, new Group());
		}
		
		for (BleBase each : bases) {
			Group group = groupedBases.get(each.getId());
			group.getRssis().add(each.getRssi());
		}
		
		return  groupedBases;
	}
	
	 /**
     * <p>
     * 把根据id分组后的Map进行处理，得到每个id组中去掉首尾极端值的rssi均值，若某组的rssi值个数小于4，
     * 则得到其中值，最后返回List对象，其中List存放的元素
     * </p>
     * 
     * @param groups	根据id分组后的Map
     * 
     * @return ArrayList<BleBase>	返回List对象
     *    
     */
	public ArrayList<BleBase> dealByGroup(Map<String, Group> groups){
		
		Integer r;
		
		List<BleBase> bases = new ArrayList<BleBase>();
		
		/*一共收到了几个基站的值*/
		int baseNum = groups.size();
		
		/*new一个对应大小的数组，用来求组合数*/
		a = new Integer[baseNum];
		
		int k = 0;
		
		@SuppressWarnings("rawtypes")
		Iterator it = groups.keySet().iterator();
		
		while(it.hasNext()) { 
			
			String id = (String) it.next(); 
			
	        Group g = groups.get(id);
	        
	        ArrayList<Integer> rssis = (ArrayList<Integer>) g.getRssis();
	        
	        int len = rssis.size();
	        
	        int len2 = len/4;
	       
	        /*如果收到的数值个数大于4，则取中间的一部分然后求均值*/
	        if(len>=4){
	        	int count = 0;
	        	for(int i=len2;i<len-len2;i++){
					count+=rssis.get(i);
				}
	        	r = count/(len-2*len2);
	        }else if(len==1){
	        	/*如果收到的数值个数等于1，就用该数*/
		        r = rssis.get(0);
	        }else{
	        	/*如果收到的数值个数小于4，则求中位数*/
		        r = getMedian(rssis);
	        }
	        
	        BleBase base = new BleBase(id, r);
	        bases.add(base);
	        
	        /*a[k]代表bases的第k个元素*/
	        a[k] = k;
			k++;
		} 
		
		return (ArrayList<BleBase>) bases;	
	}
	
	 /**
     * <p>
     * 得到列表的中位数
     * </p>
     * 
     * @param ls	一个整型数列表
     * 
     * @return ArrayList<BleBase>	返回该List的中值
     *    
     */
	public Integer getMedian(List<Integer> ls){
		
		Integer m;
		
		/*对列表进行排序*/
        Collections.sort(ls);
        
		if(ls.size()%2==0){
        	m = (ls.get(ls.size()/2)+ls.get(ls.size()/2+1))/2;
        }else{
        	m=(ls.get(ls.size()/2));
        }
		
		return m;
	}

	public Integer[] getA() {
		return a;
	}

	public void setA(Integer[] a) {
		this.a = a;
	}
	
}
