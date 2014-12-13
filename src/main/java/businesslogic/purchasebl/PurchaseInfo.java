package businesslogic.purchasebl;

import java.util.ArrayList;

import message.ResultMessage;
import po.CommodityItemPO;
import po.PurchasePO;
import vo.PurchaseVO;
import vo.commodity.CommodityItemVO;
import businesslogic.approvalbl.info.PurchaseInfo_Approval;
import businesslogic.approvalbl.info.ValueObject_Approval;
import businesslogic.clientbl.ClientInfo;
import businesslogic.commoditybl.CommodityInfo;
import businesslogic.common.Info;
import businesslogic.inventorybl.info.PurchaseInfo_Inventory;
import businesslogic.recordbl.info.PurchaseInfo_Record;
import businesslogic.recordbl.info.ValueObjectInfo_Record;
import dataenum.BillState;
import dataenum.BillType;
import dataenum.Storage;
import dataservice.TableInfoService;
import dataservice.purchasedataservice.PurchaseDataService;

public class PurchaseInfo extends Info<PurchasePO> implements ValueObjectInfo_Record<PurchaseVO>, PurchaseInfo_Record, ValueObject_Approval<PurchaseVO>, PurchaseInfo_Inventory, PurchaseInfo_Approval{
	
	private Purchase purchase;
	
	ArrayList<String> purIDs;
	ArrayList<String> backIDs;
	
	public PurchaseInfo() {
		purchase = new Purchase();
	}
	
	public PurchaseInfo(ArrayList<String> IDs) {
		purchase = new Purchase();
		this.purIDs = new ArrayList<String>();
		this.backIDs = new ArrayList<String>();
		for (String id : IDs) {
			purIDs.addAll(getID(id, null, null, null, BillType.PURCHASE));
			backIDs.addAll(getID(id, null, null, null, BillType.PURCHASEBACK));
		}
	}
	
	public TableInfoService<PurchasePO> getData() {
		return purchase.getPurData().getInfo();
	}
	
	private PurchaseDataService getPurchaseData() {
		return purchase.getPurData();
	}
	
	
	public ArrayList<String> getID(String ID, String clientName, String salesman, Storage storage) {
		ArrayList<String> IDs = new ArrayList<String>();
		IDs = getID(ID, clientName, salesman, storage, BillType.PURCHASE);
		IDs.addAll(getID(ID, clientName, salesman, storage, BillType.PURCHASEBACK));
		return IDs;
	}

	public PurchaseVO find(String ID) {
		return purchase.poToVO(getPurchaseData().find(ID));
	}

	public ResultMessage update(PurchaseVO vo) {
		String ID = vo.ID;
		String clientID = vo.clientID;
		String client = vo.client;
		String user = vo.user;
		Storage storage = vo.storage;
		String remark = vo.remark;
		double beforePrice = vo.beforePrice;
		BillType type = vo.type;
		ArrayList<CommodityItemPO> commodities = purchase.changeItems.itemsVOtoPO(vo.commodities);
		PurchasePO po = new PurchasePO(ID, clientID, client, user, storage, commodities, beforePrice, remark, type);
		return getPurchaseData().update(po);
	}

	
	public double getMoney() {
		if (purIDs.isEmpty() && backIDs.isEmpty()) {
			return 0;
		}
		double purMoney = 0;
		for (String id : purIDs) {
			purMoney += getBeforePrice(id);
		}
		for (String id : backIDs) {
			purMoney -= getBeforePrice(id);
		}
		return purMoney;
	}

	public int getNumber() {
		if (purIDs.isEmpty() && backIDs.isEmpty()) {
			return 0;
		}
		int purNumber = 0;
		for (String id : purIDs) {
			purNumber += getAllCommoditiesNumber(id);
		}
		for (String id : backIDs) {
			purNumber -= getAllCommoditiesNumber(id);
		}
		return purNumber;
	}
	
	/**
	 * 得到一个进货单中的所有商品的数量
	 * @param ID
	 * @return
	 * @author Zing
	 * @version Dec 2, 2014 5:15:27 PM
	 */
	private int getAllCommoditiesNumber(String ID) {
		ArrayList<CommodityItemPO> POs = getPurchaseData().find(ID).getCommodities();
		int number = 0;
		for (CommodityItemPO po : POs) {
			number += po.getNumber();
		}
		return number;
	}
	/**
	 * 根据ID查找特定进货单/进货退货单的总价
	 * @param ID
	 * @return
	 * @author Zing
	 * @version Dec 2, 2014 5:17:15 PM
	 */
	private double getBeforePrice(String ID) {
		return getPurchaseData().find(ID).getBeforePrice();
	}

	public ResultMessage pass(PurchaseVO vo) {
		PurchasePO po = getPurchaseData().find(vo.ID);
		// 更改商品的数据
		CommodityInfo_Purchase commodityInfo = new CommodityInfo();
		ArrayList<CommodityItemPO> commodities = po.getCommodities();
		// 如果商品总数不够的话 
		if (po.getType() == BillType.PURCHASEBACK) {
			for (CommodityItemPO commodity : commodities) {
				if (!commodityInfo.checkNumber(commodity.getID(), commodity.getNumber())) {
					po.setState(BillState.FAILURE);
					getPurchaseData().update(po);
					return ResultMessage.COMMODITY_LACK;
				}
			}
		}
		for (CommodityItemPO commodity : commodities) {
			commodityInfo.changeCommodityInfo(commodity.getID(), commodity.getNumber(), commodity.getPrice(), po.getType());
		}
		// 更改客户的应付金额
		ClientInfo_Purchase clientInfo = new ClientInfo();
		if (po.getType() == BillType.PURCHASE) {
			clientInfo.changePayable(po.getClientID(), po.getBeforePrice());
		} else {
			clientInfo.changePayable(po.getClientID(), -po.getBeforePrice());
		}
		// 更新该进货／进货退货单状态
		po.setState(BillState.SUCCESS);
		getPurchaseData().update(po);
		return ResultMessage.SUCCESS;
	}

	public double getTotalPrice(String ID) {
		PurchasePO po = getPurchaseData().find(ID);
		switch (getPurchaseData().find(ID).getType()) {
		case PURCHASE:
			return po.getBeforePrice();
		case PURCHASEBACK:
			return -po.getBeforePrice();
		default:
			break;
		}
		return 0;
	}

	public PurchaseVO addRed(PurchaseVO vo, boolean isCopy) {
		PurchaseVO redVO = vo;
		// 取负
		ArrayList<CommodityItemVO> commodities = redVO.commodities;
		for (int i = 0; i < commodities.size(); i++) {
			int number = -commodities.get(i).number;
			commodities.get(i).number = number;
		}
		redVO.commodities = commodities;
		// 先建立对应的PO
		PurchasePO redPO = new PurchasePO(redVO.ID, redVO.clientID, redVO.client, redVO.user, 
				redVO.storage, purchase.changeItems.itemsVOtoPO(redVO.commodities), redVO.beforePrice, redVO.remark, redVO.type);
		if (!isCopy) {
			getPurchaseData().insert(redPO);
			pass(redVO);
		}
		else {
			// TODO 保存为草稿 
		}
		return null;
	}
	
	public ArrayList<PurchaseVO> findApproval() {
		PurchaseShow show = new PurchaseShow();
		ArrayList<PurchaseVO> VOs = show.showPurchaseApproving();
		VOs.addAll(show.showPurchaseBackApproving());
		return VOs;
	}

	@Override
	public ArrayList<PurchaseVO> showPass() {
		PurchaseShow show = new PurchaseShow();
		ArrayList<PurchaseVO> VOs = show.showPurchasePass();
		VOs.addAll(show.showPurchaseBackPass());
		return VOs;
	}

	@Override
	public ArrayList<PurchaseVO> showFailure() {
		PurchaseShow show = new PurchaseShow();
		ArrayList<PurchaseVO> VOs = show.showPurchaseFailure();
		VOs.addAll(show.showPurchaseBackFailure());
		return VOs;
	}
}
