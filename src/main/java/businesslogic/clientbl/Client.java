package businesslogic.clientbl;

import java.util.ArrayList;

import message.ResultMessage;
import po.ClientPO;
import server.data.clientdata.ClientData;
import vo.ClientVO;
import businesslogic.userbl.UserInfo;
import dataenum.ClientCategory;
import dataenum.ClientLevel;
import dataenum.FindTypeClient;
import dataservice.clientdataservice.ClientDataService;

public class Client {

	private ClientDataService clientData;

	/**
	 * @author cylong
	 * @version 2014年11月29日 下午3:26:27
	 */
	public Client() {
		//		try {
		//			DataFactoryService factory = (DataFactoryService)Naming.lookup(RMI.URL);
		//			this.clientData = factory.getClientData();
		//		} catch (Exception e) {
		//			e.printStackTrace();
		//		}
		// TODO 本地新建
		this.clientData = new ClientData();
	}

	/**
	 * 返回全部的客户
	 * @author cylong
	 * @version 2014年11月29日 下午4:10:31
	 */
	public ArrayList<ClientVO> show() {
		ArrayList<ClientPO> clientsPO = clientData.show();
		ArrayList<ClientVO> clientsVO = posToVOs(clientsPO);
		return clientsVO;
	}

	/**
	 * 将全部的ClientPO转化成ClientVO
	 * @param clientsPO ArrayList<ClientPO>
	 * @return ArrayList<ClientVO>
	 * @author cylong
	 * @version 2014年11月29日 下午4:31:51
	 */
	public ArrayList<ClientVO> posToVOs(ArrayList<ClientPO> clientsPO) {
		ArrayList<ClientVO> clientsVO = new ArrayList<ClientVO>();
		for(int i = 0; i < clientsPO.size(); i++) {
			ClientPO po = clientsPO.get(i);
			clientsVO.add(poToVO(po));
		}
		return clientsVO;
	}

	/**
	 * 将一个ClientPO转化成VO
	 * @param po ClientPO
	 * @return ClientVO
	 * @author cylong
	 * @version 2014年11月29日 下午4:37:21
	 */
	private ClientVO poToVO(ClientPO po) {
		String ID = po.getID();
		ClientCategory category = po.getCategory();
		ClientLevel level = po.getLevel();
		String name = po.getName();
		String phone = po.getPhone();
		String address = po.getAddress();
		String post = po.getPost();
		String email = po.getEmail();
		double receivable = po.getReceivable();
		double payable = po.getPayable();
		double receivableLimit = po.getReceivableLimit();
		String salesman = po.getSalesman();
		ClientVO vo = new ClientVO(ID, category, level, name, phone, address, post, email, receivable, payable, receivableLimit, salesman);
		return vo;
	}

	private ClientPO voToPO(ClientVO vo) {
		String ID = vo.ID;
		ClientCategory category = vo.category;
		ClientLevel level = vo.level;
		String name = vo.name;
		String phone = vo.phone;
		String address = vo.address;
		String post = vo.post;
		String email = vo.email;
		double receivableLimit = vo.receivableLimit;
		String salesman = vo.salesman;
		ClientPO po = new ClientPO(ID, category, level, name, phone, address, post, email, receivableLimit, salesman);
		return po;
	}

	/**
	 * 模糊查找客户
	 * @param keywords 关键字
	 * @param type 查找类型
	 * @return 满足条件的客户
	 * @author cylong
	 * @version 2014年11月29日 下午4:29:04
	 */
	public ArrayList<ClientVO> findClient(String keywords, FindTypeClient type) {
		ArrayList<ClientPO> clientsPO = clientData.find(keywords, type);
		ArrayList<ClientVO> clientsVO = posToVOs(clientsPO);
		return clientsVO;
	}

	/**
	 * 以ID精确查找客户
	 * @param ID 客户ID
	 * @return ClientVO
	 * @author cylong
	 * @version 2014年12月1日 下午2:55:47
	 */
	public ClientVO findClient(String ID) {
		ClientPO po = clientData.find(ID);
		return poToVO(po);
	}

	/**
	 * 添加一位客户
	 * @param vo ClientVO
	 * @return
	 * @author cylong
	 * @version 2014年11月29日 下午4:35:22
	 */
	public ResultMessage addClient(ClientVO vo) {
		ClientPO po = voToPO(vo);
		return clientData.insert(po);
	}

	/**
	 * @return 创建客户时候的新ID
	 * @author cylong
	 * @version 2014年11月29日 下午4:47:04
	 */
	public String getID() {
		return clientData.getID();
	}

	/**
	 * 更新客户信息
	 * @param vo ClientVO
	 * @return 处理结果
	 * @author cylong
	 * @version 2014年11月29日 下午4:49:10
	 */
	public ResultMessage updClient(ClientVO vo) {
		UserInfo_Client userInfo = new UserInfo();
		ClientPO po = clientData.find(vo.ID);	// 从数据层获得相应的client
		po.setCategory(vo.category);
		po.setLevel(vo.level);
		po.setName(vo.name);
		po.setPhone(vo.phone);
		po.setAddress(vo.address);
		po.setPost(vo.post);
		po.setEmail(vo.email);
		if (vo.receivableLimit != po.getReceivableLimit()) {	// 仅最高权限可以修改
			boolean b = po.setReceivableLimit(vo.receivableLimit, userInfo.getUserIden());
			if (!b) {
				return ResultMessage.FAILURE;	// 权限不够，更新失败
			}
		}
		po.setSalesman(vo.salesman);
		return clientData.update(po);
	}

	/**
	 * 以客户ID删除客户
	 * @param ID
	 * @return
	 * @author cylong
	 * @version 2014年11月29日 下午5:17:16
	 */
	public ResultMessage deletClient(String ID) {
		return clientData.delete(ID);
	}

	public ClientDataService getClientData() {
		return clientData;
	}
	
	

}
