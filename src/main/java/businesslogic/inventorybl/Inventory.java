package businesslogic.inventorybl;

import java.rmi.Naming;
import java.util.ArrayList;

import message.ResultMessage;
import po.InventoryBillPO;
import server.data.inventorydata.InventoryData;
import vo.CommodityItemVO;
import vo.InventoryBillVO;
import vo.InventoryCheckVO;
import vo.InventoryViewVO;
import businesslogic.common.ChangeCommodityItems;
import config.RMI;
import dataenum.BillState;
import dataenum.BillType;
import dataservice.DataFactoryService;
import dataservice.inventorydataservice.InventoryDataService;

public class Inventory extends ChangeCommodityItems {
	
	private BillList list;
		
	private BillType type;
	
	private String ID;
	
	public Inventory() {
		
	}
	/**
	 * 得到库存数据
	 * @return
	 * @author Zing
	 * @version Dec 2, 2014 6:11:29 PM
	 */
	public InventoryDataService getInventoryData(){
//		try {
//			DataFactoryService factory = (DataFactoryService)Naming.lookup(RMI.URL);
//			InventoryDataService inventoryData = (InventoryDataService)factory.getInventoryData();
//			return inventoryData;		
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
		// TODO 本地新建
		return new InventoryData();
	}
	
	/**
	 * 用时间区间来查看这段时间内的销售／进货数量
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @author Zing
	 * @version Dec 2, 2014 6:11:42 PM
	 */
	public InventoryViewVO viewInventory(String beginDate, String endDate) {
		ViewList viewList = new ViewList(beginDate, endDate);
		InventoryViewVO vo = new InventoryViewVO(viewList.getSaleNumber(), viewList.getPurNumber(), viewList.getSaleMoney(), viewList.getPurMoney());
		return vo;
	}
	
	/**
	 * 库存盘点，返回库存盘点的单子
	 * @return
	 * @author Zing
	 * @version Dec 2, 2014 6:00:48 PM
	 */
	public InventoryCheckVO checkRecord() {
		InventoryDataService inventoryData = getInventoryData();
		// 得到批号
		CheckList checkList = new CheckList(inventoryData.returnNumber());
		InventoryCheckVO vo = new InventoryCheckVO(checkList.getItemsVO(), checkList.getAvePrice(), checkList.getToday(), checkList.getLot());
		return vo;
	}
	
	/**
	 * 最开始要创建单据时，确定单据类型，返回单据ID
	 * @param type
	 * @return
	 * @author Zing
	 * @version Dec 2, 2014 7:17:13 PM
	 */
	public String getID(BillType type) {
		this.type = type;
		list = new BillList();
		this.ID = getInventoryData().getID(type);
		return ID;
	}
	
	/**
	 * 显示单子
	 * @param type
	 * @return
	 * @author Zing
	 * @version Dec 2, 2014 6:11:48 PM
	 */
	public ArrayList<InventoryBillVO> show(BillType type) {
		ArrayList<InventoryBillVO> VOs = new ArrayList<InventoryBillVO>();
		ArrayList<InventoryBillPO> POs = getInventoryData().show(type);
		for (int i = 0; i < POs.size(); i++) {
			InventoryBillPO po = POs.get(i);
			InventoryBillVO vo = poToVo(po);
			VOs.add(vo);
		}
		return VOs;
	}

	/**
	 * 往单子里添加商品
	 * @param ID
	 * @param number
	 * @author Zing
	 * @version Dec 2, 2014 6:11:55 PM
	 */
	public void addCommodity(String ID, int number) {
		BillListItem item = new BillListItem(ID, number);
		list.addItem(item);
	}
	
	/**
	 * 提交单子
	 * @param remark
	 * @return
	 * @author Zing
	 * @version Dec 2, 2014 6:11:58 PM
	 */
	public ResultMessage submit(String remark){
		list.setRemark(remark);
		return 	getInventoryData().insert(getInventoryBill());
	}
	
	/**
	 * 保存为草稿
	 * @param remark
	 * @return
	 * @author Zing
	 * @version Dec 2, 2014 6:12:02 PM
	 */
	public ResultMessage save(String remark) {
		list.setRemark(remark);
		// 保存为草稿
		return null;
	}
	
	/**
	 * 建立起一个库存单据（赠送单、报警单、报溢单、报损单）
	 * @return
	 * @author Zing
	 * @version Dec 2, 2014 5:34:33 PM
	 */
	private InventoryBillPO getInventoryBill(){
		InventoryBillPO po = new InventoryBillPO(ID, type, list.getCommodityPOs(), list.getRemark());
		return po;
	}
	
	/**
	 * 单子的po到vo的转换
	 * @param po
	 * @return
	 * @author Zing
	 * @version Dec 2, 2014 6:11:52 PM
	 */
	public InventoryBillVO poToVo(InventoryBillPO po) {
		String ID = po.getID();
		BillType billType = po.getBillType();
		ArrayList<CommodityItemVO> commodities = itemPOToVO(po.getCommodities());
		String remark = po.getRemark();
		BillState state = po.getState();
		InventoryBillVO vo = new InventoryBillVO(ID, billType, commodities, remark, state);
		return vo;
	}
	
}
