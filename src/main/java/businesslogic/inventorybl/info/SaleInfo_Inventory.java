package businesslogic.inventorybl.info;

import java.rmi.RemoteException;


public interface SaleInfo_Inventory {
	
	/**
	 * 根据所有的ID（可能有也可能没有）查找单据，返回总钱数（是beforePrice！）
	 * @param IDs
	 * @return
	 * @author Zing
	 * @version Dec 2, 2014 4:47:46 PM
	 * @throws RemoteException 
	 */
	public double getMoney() throws RemoteException;
	
	/**
	 * 根据所有的ID（可能有也可能没有）查找单据，返回销售总数
	 * @param IDs
	 * @return
	 * @author Zing
	 * @version Dec 2, 2014 4:52:14 PM
	 * @throws RemoteException 
	 */
	public int getNumber() throws RemoteException;
}
