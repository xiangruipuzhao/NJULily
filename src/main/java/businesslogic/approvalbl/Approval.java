package businesslogic.approvalbl;

import java.rmi.RemoteException;
import java.util.ArrayList;

import message.ResultMessage;
import vo.AccountBillVO;
import vo.CashBillVO;
import vo.InventoryBillVO;
import vo.PurchaseVO;
import vo.ValueObject;
import vo.sale.SalesVO;
import dataenum.BillType;

/**
 * 审批
 * @author Zing
 * @version Dec 2, 2014 11:48:42 PM
 */
public class Approval {

	/**
	 * 更新特定单据的数据 直接传递一个数据vo过来，还要有数据的类型
	 * 
	 * @param VOs
	 * @return
	 * @author Zing
	 * @version Dec 2, 2014 2:11:54 PM
	 * @throws RemoteException 
	 */
	
	public ResultMessage updateBill(ValueObject vo, BillType billType) throws RemoteException {
		UpdateApproval update = new UpdateApproval();
		switch (billType) {
		case SALE:
		case SALEBACK:
			return update.updateBill((SalesVO)vo);
		case PURCHASE:
		case PURCHASEBACK:
			return update.updateBill((PurchaseVO)vo);
		case GIFT:
		case OVERFLOW:
		case LOSS:
			return update.updateBill((InventoryBillVO)vo);
		case PAY:
		case EXPENSE:
			return update.updateBill((AccountBillVO)vo);
		case CASH:
			return update.updateBill((CashBillVO)vo);
		default:
			break;
		}
		return ResultMessage.FAILURE;
	}
	

	/**
	 * 单子通过审核（可以批量）
	 * @param VOs
	 * @return
	 * @author Zing
	 * @version Dec 2, 2014 9:22:47 PM
	 * @throws RemoteException 
	 */
	public ResultMessage passBill(ArrayList<ValueObject> VOs, ArrayList<BillType> billTypes) throws RemoteException {
		PassApproval pass = new PassApproval();
		for (int i=0; i < VOs.size(); i++) {
			BillType billType = billTypes.get(i);
			ValueObject vo = VOs.get(i);
			switch (billType) {
			case SALE:
			case SALEBACK:
				pass.passBill((SalesVO)vo);
				 break;
			case PURCHASE:
			case PURCHASEBACK:
				pass.passBill((PurchaseVO)vo);
				 break;
			case GIFT:
			case OVERFLOW:
			case LOSS:
				pass.passBill((InventoryBillVO)vo);
				 break;
			case PAY:
			case EXPENSE:
				pass.passBill((AccountBillVO)vo);
				 break;
			case CASH:
				pass.passBill((CashBillVO)vo);
				 break;
			default:
				break;
			}
		}
		return ResultMessage.SUCCESS;
	}

	public ResultMessage noPassBill(ArrayList<ValueObject> VOs,
			ArrayList<BillType> billTypes) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
