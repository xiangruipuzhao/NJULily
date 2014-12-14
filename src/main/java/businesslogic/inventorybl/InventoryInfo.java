package businesslogic.inventorybl;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import message.ResultMessage;
import po.CommodityItemPO;
import po.InventoryBillPO;
import vo.InventoryBillVO;
import vo.commodity.CommodityItemVO;
import businesslogic.approvalbl.info.InventoryInfo_Approval;
import businesslogic.approvalbl.info.ValueObject_Approval;
import businesslogic.commoditybl.CommodityInfo;
import businesslogic.common.Info;
import businesslogic.inventorybl.info.CommodityInfo_Inventory;
import businesslogic.promotionbl.info.InventoryInfo_Promotion;
import businesslogic.recordbl.info.InventoryInfo_Record;
import businesslogic.recordbl.info.ValueObjectInfo_Record;
import config.RMIConfig;
import dataenum.BillState;
import dataenum.BillType;
import dataenum.Storage;
import dataservice.TableInfoService;
import dataservice.inventorydataservice.InventoryDataService;
import dataservice.inventorydataservice.InventoryInfoService;

public class InventoryInfo extends Info<InventoryBillPO> implements InventoryInfo_Promotion, ValueObjectInfo_Record<InventoryBillVO>, InventoryInfo_Record, ValueObject_Approval<InventoryBillVO>, InventoryInfo_Approval{

	private Inventory inventory;
	
	public InventoryInfo() {
		inventory = new Inventory();
	}
	
	public TableInfoService<InventoryBillPO> getData() {
		try {
			return (InventoryInfoService)Naming.lookup(RMIConfig.PREFIX + InventoryInfoService.NAME);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private InventoryDataService getInventoryData() {
		return inventory.getInventoryData();
	}
	
	public ArrayList<InventoryBillVO> getGifts() throws RemoteException {
		InventoryShow inventoryShow = new InventoryShow();
		return inventoryShow.showGiftsPass();
	}

	/**
	 * 返回的是所有的符合条件的库存账单的ID
	 * @throws NotBoundException 
	 * @throws MalformedURLException 
	 * @throws RemoteException 
	 */
	public ArrayList<String> getID(String ID, String clientName, String salesman, Storage storage) throws RemoteException {
		ArrayList<String> IDs = new ArrayList<String>();
		IDs = getID(ID, clientName, salesman, storage, BillType.GIFT);
		IDs.addAll(getID(ID, clientName, salesman, storage, BillType.OVERFLOW));
		IDs.addAll(getID(ID, clientName, salesman, storage, BillType.LOSS));
		IDs.addAll(getID(ID, clientName, salesman, storage, BillType.ALARM));
		return IDs;
	}

	/**
	 * 根据单据ID，返回一个VO
	 * @throws RemoteException 
	 */
	public InventoryBillVO find(String ID) throws RemoteException {
		InventoryBillVO vo = inventory.poToVo(getInventoryData().find(ID));
		return vo;
	}

	public ResultMessage update(InventoryBillVO vo) throws RemoteException {
		String ID = vo.ID;
		BillType billType = vo.billType;
		String remark = vo.remark;
		ArrayList<CommodityItemPO> commodities = inventory.changeItems.itemsVOtoPO(vo.commodities);
		InventoryBillPO po = new InventoryBillPO(ID, billType, commodities, remark);
		return getInventoryData().update(po);
	}

	/**
	 * 通过审批后，更改相应的商品信息
	 * @throws RemoteException 
	 */
	public void pass(InventoryBillVO vo) throws RemoteException {
		InventoryBillPO po = getInventoryData().find(vo.ID);
		// 更新单据状态
		po.setState(BillState.SUCCESS);
		getInventoryData().update(po);
		// 更改商品数量
		ArrayList<CommodityItemPO> commodities = po.getCommodities();
		CommodityInfo_Inventory info = new CommodityInfo();
		for(CommodityItemPO commodity : commodities) {
			info.changeNumber(commodity.getID(), commodity.getNumber(), po.getBillType());
		}
	}

	/**
	 * 根据单据ID，返回单据类型
	 * @throws RemoteException 
	 */
	public BillType getType(String ID) throws RemoteException {
		return getInventoryData().find(ID).getBillType();
	}

	/**
	 * 根据单据ID，返回单据总额数
	 * @throws RemoteException 
	 */
	public double getTotalPrice(String ID) throws RemoteException {
		InventoryBillPO po = getInventoryData().find(ID);
		double totalPrice = 0;
		for (CommodityItemPO commodityPO : po.getCommodities()) {
			totalPrice += commodityPO.getTotal();
		}
		return totalPrice;
	}

	public InventoryBillVO addRed(InventoryBillVO vo, boolean isCopy) throws RemoteException {
		InventoryBillVO redVO = vo;
		// 取负
		ArrayList<CommodityItemVO> commodities = redVO.commodities;
		for (int i = 0; i < commodities.size(); i++) {
			int number = -commodities.get(i).number;
			commodities.get(i).number = number;
		}
		redVO.commodities = commodities;
		// 先建立对应的PO
		InventoryBillPO redPO = new InventoryBillPO(redVO.ID, redVO.billType, inventory.changeItems.itemsVOtoPO(redVO.commodities), redVO.remark);
		if (!isCopy) {
			getInventoryData().insert(redPO);
			pass(redVO);
		}
		else {
			// TODO 保存为草稿
		}
		return null;
	}
	
	/**
	 * 返回需要审批的VO
	 * @throws RemoteException 
	 */
	public ArrayList<InventoryBillVO> findApproval() throws RemoteException {
		InventoryShow show = new InventoryShow();
		ArrayList<InventoryBillVO> VOs = show.showGiftsApproving();
		VOs.addAll(show.showOverFlowApproving());
		VOs.addAll(show.showLossApproving());
		VOs.addAll(show.showAlarmApproving());
		return VOs;
	}

	@Override
	public ArrayList<InventoryBillVO> showPass() throws RemoteException {
		InventoryShow show = new InventoryShow();
		ArrayList<InventoryBillVO> VOs = show.showGiftsPass();
		VOs.addAll(show.showOverFlowPass());
		VOs.addAll(show.showLossPass());
		VOs.addAll(show.showAlarmPass());
		return VOs;
	}

	@Override
	public ArrayList<InventoryBillVO> showFailure() throws RemoteException {
		InventoryShow show = new InventoryShow();
		ArrayList<InventoryBillVO> VOs = show.showGiftsFailure();
		VOs.addAll(show.showOverFlowFailure());
		VOs.addAll(show.showLossFailure());
		VOs.addAll(show.showAlarmFailure());
		return VOs;
	}
}
