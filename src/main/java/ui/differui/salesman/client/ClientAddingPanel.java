package ui.differui.salesman.client;

import java.awt.Color;


import javax.swing.JLabel;


import ui.commonui.myui.MyComboBox;

import ui.commonui.myui.MyLabel;
import ui.commonui.myui.MyPanel;
import ui.commonui.myui.MyTextField;

public class ClientAddingPanel extends MyPanel {

	private static final long serialVersionUID = 1L;

	MyTextField textField_name, textField_phone, textField_address, textField_post, textField_email, textField_limit;
	MyComboBox comboBox_category, comboBox_level;
	
	public ClientAddingPanel(){
			
		int x1 = 120, y1 = 10, x2 = 120, y2 = 75;
		
		Color foreColor = new Color(158, 213, 220);
		Color backColor = new Color(53, 84, 94);
		
		//information bar
		JLabel infoBar = new JLabel("新增一位客户",JLabel.CENTER);
		infoBar.setBounds(0, 0, 600, 20);
		infoBar.setOpaque(true);
		infoBar.setForeground(foreColor);
		infoBar.setBackground(backColor);
		this.add(infoBar);
		
		//ui for tips
		JLabel word_tip1 = new JLabel("* 必填:",JLabel.CENTER);
		word_tip1.setForeground(Color.RED);
		word_tip1.setBackground(new Color(0, 0, 0, 0));
		word_tip1.setBounds(40, 45, 65, 25);
		this.add(word_tip1);
		
		JLabel word_tip2 = new JLabel("选填:",JLabel.CENTER);
		word_tip2.setForeground(Color.RED);
		word_tip2.setBackground(new Color(0, 0, 0, 0));
		word_tip2.setBounds(46, 233, 65, 25);
		this.add(word_tip2);
		
		//ui for name
		JLabel word_name = new JLabel("* 客户名称:",JLabel.CENTER);
		word_name.setForeground(Color.WHITE);
		word_name.setBackground(new Color(0, 0, 0, 0));
		word_name.setBounds(30 + x1, 50 + y1, 65, 25);
		this.add(word_name);
		
		textField_name = new MyTextField(110 + x1, 50 + y1, 200, 25);
		this.add(textField_name);
		
		//ui for category
		JLabel word_category = new JLabel("* 客户分类:",JLabel.CENTER);
		word_category.setForeground(Color.WHITE);
		word_category.setBackground(new Color(0, 0, 0, 0));
		word_category.setBounds(30 + x1, 90 + y1, 65, 25);
		this.add(word_category);
		
		String[] comboBoxStr = {"----------请选择一种分类----------", "进货商", "销售商", "进货商/销售商(两者都是)"};
		comboBox_category = new MyComboBox(110 + x1, 90 + y1, 200, 25,comboBoxStr);
		this.add(comboBox_category);
		
		//ui for level
		JLabel word_level = new JLabel("* 客户星级:",JLabel.CENTER);
		word_level.setForeground(Color.WHITE);
		word_level.setBackground(new Color(0, 0, 0, 0));
		word_level.setBounds(30 + x1, 130 + y1, 65, 25);
		this.add(word_level);
		
		String[] comboBoxStr2 = {"----------请选择起始星级----------", "一星", "二星", "三星", "四星", "五星(VIP)"};
		comboBox_category = new MyComboBox(110 + x1, 130 + y1, 200, 25,comboBoxStr2);
		this.add(comboBox_category);
		
		//ui for receivableLimit
		JLabel word_limit = new JLabel("* 应收额度:",JLabel.CENTER);
		word_limit.setForeground(Color.WHITE);
		word_limit.setBackground(new Color(0, 0, 0, 0));
		word_limit.setBounds(30 + x1, 170 + y1, 65, 25);
		this.add(word_limit);
		
		textField_limit = new MyTextField(110 + x1, 170 + y1, 180, 25);
		this.add(textField_limit);
		
		JLabel word_limit_yuan = new JLabel("元",JLabel.CENTER);
		word_limit_yuan.setForeground(Color.WHITE);
		word_limit_yuan.setBackground(new Color(0, 0, 0, 0));
		word_limit_yuan.setBounds(290 + x1, 170 + y1, 20, 25);
		this.add(word_limit_yuan);
		
		//ui for phone
		JLabel word_phone = new JLabel("客户电话:",JLabel.CENTER);
		word_phone.setForeground(Color.WHITE);
		word_phone.setBackground(new Color(0, 0, 0, 0));
		word_phone.setBounds(36 + x2, 170 + y2, 60, 25);
		this.add(word_phone);
		
		textField_phone = new MyTextField(110 + x2, 170 + y2, 200, 25);
		this.add(textField_phone);
		
		//ui for address
		JLabel word_address = new JLabel("客户地址:",JLabel.CENTER);
		word_address.setForeground(Color.WHITE);
		word_address.setBackground(new Color(0, 0, 0, 0));
		word_address.setBounds(36 + x2, 210 + y2, 60, 25);
		this.add(word_address);
		
		textField_address = new MyTextField(110 + x2, 210 + y2, 200, 25);
		this.add(textField_address);
		
		//ui for post
		JLabel word_post = new JLabel("客户邮编:",JLabel.CENTER);
		word_post.setForeground(Color.WHITE);
		word_post.setBackground(new Color(0, 0, 0, 0));
		word_post.setBounds(36 + x2, 250 + y2, 60, 25);
		this.add(word_post);
		
		textField_post = new MyTextField(110 + x2, 250 + y2, 200, 25);
		this.add(textField_post);
		
		//ui for email
		JLabel word_email = new JLabel("电子邮件:",JLabel.CENTER);
		word_email.setForeground(Color.WHITE);
		word_email.setBackground(new Color(0, 0, 0, 0));
		word_email.setBounds(36 + x2, 290 + y2, 60, 25);
		this.add(word_email);
		
		textField_email = new MyTextField(110 + x2, 290 + y2, 200, 25);
		this.add(textField_email);
			
		//the label for button_return
		MyLabel label_return = new MyLabel(490 , 420 , 100, 20);
		label_return.setForeground(foreColor);
		label_return.setBackground(backColor);
		label_return.setText("返回");
		this.add(label_return);
		
		//ui for button add
		MyLabel label_add = new MyLabel(490 , 390, 100, 20);
		label_add.setForeground(foreColor);
		label_add.setBackground(backColor);
		label_add.setText("确认添加");
		this.add(label_add);
			
	}
		
	public String getinfo(){
		return textField_name.getText();
	}
}