package _7seg;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class PlcDirectControl_7segkai extends JFrame implements ActionListener{
	//パイロットランプの点灯状態保存用配列(初期は0:消灯)
	private int	pl_data[] = {0, 0, 0, 0};
	//文字と画像を表示するためのJLabel
	JLabel	label_plc, label_con_image, label_con_title;
	JLabel	label_pl_title, label_d_pl1, label_d_pl2, label_d_pl3,label_d_pl4;
	JLabel[] label_pl = new JLabel[4];
	JLabel label_7seg;//7seg
	//画像用ImageIcon
	ImageIcon	icon_con;
	ImageIcon icon_7seg,icon_7seg_SMALL;//7seg
	Image icon_7seg_small;//7seg
	//PLCのアドレス選択用JList
	JList<String> list_plc;
	//JListにスクルールをつけるためのJScrollPane
	JScrollPane	sp_plc;
	//ベルトコンベヤとパイロットランプ制御用のJButton
	JButton		btn_left, btn_stop, btn_right;
	JButton[]	btn_pl = new JButton[4];
	JButton[]   _7seg_btn=new JButton[10];//7seg
	//ボタンなどを配置するためのJPanel
	JPanel panel_plc, panel_btn, panel_con, panel_pl;
	JPanel panel_7seg;//7seg
	//上記部品を載せるためのコンテナ
	Container		content;
	//コンストラクタ
	public PlcDirectControl_7segkai( String title, int w, int h){
		super(title);
		setSize(w, h);
		//文字表示と画像表示のラベルを作成
		//PLC
		label_plc = new JLabel("PLC IP選択");
		//ベルトコンベヤの画像
		icon_con = new ImageIcon("./src/ry_stop.png");
		label_con_image = new JLabel(icon_con);
		//"ベルトコンベヤの制御"
		label_con_title = new JLabel("ベルトコンベヤの制御",
                                                          JLabel.CENTER);
		//"パイロットランプの制御"
		label_pl_title = new JLabel("パイロットランプの制御",JLabel.CENTER);
		//パイロットランプ用JLabel
		for(int i=0; i<4; i++){
			label_pl[i] = new JLabel("", JLabel.CENTER);
		}
		//"PL1"-"PL4"
		label_d_pl1 = new JLabel("PL1", JLabel.CENTER);
		label_d_pl2 = new JLabel("PL2", JLabel.CENTER);
		label_d_pl3 = new JLabel("PL3", JLabel.CENTER);
		label_d_pl4 = new JLabel("PL4", JLabel.CENTER);
	      //WebサーバのIPとPLCのIPを選択するための
		//JListおよびSCrollPaneの作成
		String[] plc = new String[6];
		for(int i=0; i<6; i++){
			plc[i] = "172.16.110." +Integer.toString(220+i);
		}
		list_plc = new JList<String>(plc);
		list_plc.setVisibleRowCount(4);
		sp_plc = new JScrollPane(list_plc);
		sp_plc.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		//ベルトコンベヤとパイロットランプの制御のボタンの作成
		btn_left = new JButton("左");
		btn_stop = new JButton("停止");
		btn_right = new JButton("右");
		for(int i=0; i<4; i++){
			btn_pl[i] =new JButton(new ImageIcon("./src/led_off.png"));
		}
		//7segの画像
		icon_7seg = new ImageIcon("./src/_7seg/_7seg_0.png");
		int iconHeight = (int) ((icon_7seg.getIconHeight())*0.4);
		int iconWidth = (int) ((icon_7seg.getIconWidth())*0.4);
		icon_7seg_small=icon_7seg.getImage().getScaledInstance(iconWidth,iconHeight, Image.SCALE_SMOOTH);
		icon_7seg_SMALL=new ImageIcon(icon_7seg_small);
		label_7seg = new JLabel(icon_7seg_SMALL);
		//7segのボタン
		for(int i=0; i<10; i++){
			String j = String.valueOf(i);
			_7seg_btn[i] =new JButton(j);
		}

		//JPanelに部品を配置
		//PLC IPアドレス選択部分
		panel_plc = new JPanel();
		panel_plc.add(label_plc);
		panel_plc.add(sp_plc);
		//ベルトコンベヤ制御用ボタン
		panel_btn = new JPanel();
		panel_btn.add(btn_left);
		panel_btn.add(btn_stop);
		panel_btn.add(btn_right);
		//"PL1"-"PL4"と対応する画像
		panel_pl = new JPanel();
		panel_pl.setLayout(new GridLayout(2,4));
		panel_pl.add(label_d_pl1);
		panel_pl.add(label_d_pl2);
		panel_pl.add(label_d_pl3);
		panel_pl.add(label_d_pl4);
		for(int i=0; i<4; i++){
			panel_pl.add(btn_pl[i]);
		}
		//7segPanel
		panel_7seg=new JPanel();
		panel_7seg.setLayout(new GridLayout(4,3));
		for(int i=0; i<10; i++){
			panel_7seg.add(_7seg_btn[i]);
		}

		//Containerへ部品を配置
		GridBagLayout layout=new GridBagLayout();
		getContentPane().setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);

		gbc.gridx = 0;gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(panel_plc, gbc);
		getContentPane().add(panel_plc);

		gbc.gridx = 0;gbc.gridy = 1;
		gbc.fill = GridBagConstraints.BOTH;
		//layout.add(label_con_title);
		layout.setConstraints(label_con_title, gbc);
		getContentPane().add(label_con_title);

		gbc.gridx = 0;gbc.gridy = 2;
		gbc.fill = GridBagConstraints.BOTH;
		//layout.add(label_con_image);
		layout.setConstraints(label_con_image, gbc);
		getContentPane().add(label_con_image);

		gbc.gridx = 0;gbc.gridy = 3;
		gbc.fill = GridBagConstraints.BOTH;
		//layout.add(panel_btn);
		layout.setConstraints(panel_btn, gbc);
		getContentPane().add(panel_btn);

		gbc.gridx = 0;gbc.gridy = 4;
		gbc.fill = GridBagConstraints.BOTH;
		//layout.add(label_pl_title);
		layout.setConstraints(label_pl_title, gbc);
		getContentPane().add(label_pl_title);

		gbc.gridx = 0;gbc.gridy = 5;
		gbc.fill = GridBagConstraints.BOTH;
		//layout.add(panel_pl);
		layout.setConstraints(panel_pl, gbc);
		getContentPane().add(panel_pl);

		gbc.gridx = 0;gbc.gridy = 6;
        gbc.weightx = 1.0d;gbc.weighty = 1.0d;
		gbc.fill = GridBagConstraints.VERTICAL;
		//layout.add(label_7seg);
		layout.setConstraints(label_7seg, gbc);
		getContentPane().add(label_7seg);

		gbc.gridx = 0;gbc.gridy = 7;
		//gbc.weightx = 1.0d;gbc.weighty = 1.0d;
		gbc.fill = GridBagConstraints.BOTH;
		//layout.add(panel_7seg);
		layout.setConstraints(panel_7seg, gbc);
		getContentPane().add(panel_7seg);

		//ボタンへイベントを追加
		btn_left.addActionListener(this);
		btn_stop.addActionListener(this);
		btn_right.addActionListener(this);
		for(int i=0; i<4; i++){
			btn_pl[i].addActionListener(this);
		}
		//7seg
		for(int i=0; i<10; i++){
			_7seg_btn[i].addActionListener(this);
		}
		//「閉じる」ボタンの処理を設定
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	//各イベントに対する処理
	//PLC押しボタンのイベント処理
	public void actionPerformed(ActionEvent e){
		//PLC用送信文字列の作成
		String	send;
		//イベントが起きたボタンオブジェクトの取得
		Object obj = e.getSource();
		//ベルトコンベヤ制御文字列
		String	con_belt = null;
		//ベルトコンベヤかパイロットランプかの判別
		//  (false:ベルトコンベヤ true:パイロットランプ)
		Boolean kiki = false;
		//イベントが起きたボタン毎の処理
		//「左」ボタン 左回転
		if(obj == btn_left){
			con_belt = "10";
			kiki = false;
		}
		//「停止」ボタン 停止
		if(obj == btn_stop){
			con_belt = "00";
			kiki = false;
		}
		//「右」ボタン 右回転
		if(obj == btn_right){
			con_belt = "01";
			kiki = false;
		}
		//PL1-PL4
		for(int i=0; i<4; i++){
			if(obj == btn_pl[i]){
				//点灯データを反転(0->1, 1->0)
				pl_data[i] ^= 1;
				kiki = true;
			}
		}

		//PLC IPアドレス
		String plc_ip =(String)list_plc.getSelectedValue();
	//PLCのIPアドレスが選択されていない場合は、ダイアログを表示
		if( plc_ip == null ){
			JOptionPane.showMessageDialog(null,"PLCのIPアドレスが選択されていません");
			return;
		}
		//PLC用送信文字列の作成
		if(kiki){
			//true:パイロットランプの場合
			String str = "";
			for(int i=0; i<4; i++){
				str += Integer.toString(pl_data[i]);
			}
			send = "02ff000a5920000000100400" + str;
		}
		else{
			//ベルトコンベヤの場合
			send = "02ff000a5920000000140200" + con_belt;
		}
		//_7seg_btnの動作
				for(int i=0; i<10; i++){
					if(obj==_7seg_btn[i]){
						switch(i){
						case 0:
							con_belt="0000";
							icon_7seg = new ImageIcon("./src/_7seg/_7seg_0.png");
							break;
						case 1:
							con_belt="1000";
							icon_7seg = new ImageIcon("./src/_7seg/_7seg_1.png");
							break;
						case 2:
							con_belt="0100";
							icon_7seg = new ImageIcon("./src/_7seg/_7seg_2.png");
							break;
						case 3:
							con_belt="1100";
							icon_7seg = new ImageIcon("./src/_7seg/_7seg_3.png");
							break;
						case 4:
							con_belt="0010";
							icon_7seg = new ImageIcon("./src/_7seg/_7seg_4.png");
							break;
						case 5:
							con_belt="1010";
							icon_7seg = new ImageIcon("./src/_7seg/_7seg_5.png");
							break;
						case 6:
							con_belt="0110";
							icon_7seg = new ImageIcon("./src/_7seg/_7seg_6.png");
							break;
						case 7:
							con_belt="1110";
							icon_7seg = new ImageIcon("./src/_7seg/_7seg_7.png");
							break;
						case 8:
							con_belt="0001";
							icon_7seg = new ImageIcon("./src/_7seg/_7seg_8.png");
							break;
						case 9:
							con_belt="1001";
							icon_7seg = new ImageIcon("./src/_7seg/_7seg_9.png");
							break;
						}
						int iconHeight = (int) ((icon_7seg.getIconHeight())*0.4);
						int iconWidth = (int) ((icon_7seg.getIconWidth())*0.4);
						icon_7seg_small=icon_7seg.getImage().getScaledInstance(iconWidth,iconHeight, Image.SCALE_SMOOTH);
						icon_7seg_SMALL=new ImageIcon(icon_7seg_small);
						label_7seg.setIcon(icon_7seg_SMALL);
						send = "02ff000a5920000000180400" + con_belt;
					}
				}

		//InetSocketAddressの作成
		InetSocketAddress	address = null;
		try{
			address = new InetSocketAddress(InetAddress.getByName(plc_ip), 0xC000);
		}
		catch(Exception e1){
		//例外の場合は、ダイアログを表示
		   JOptionPane.showMessageDialog(null,e1.toString());
			return;
		}
		//PLCへの接続
		SocketChannel channel = null;
		try{
		    channel = SocketChannel.open();//ポートのオープン
		    channel.connect(address);//PLCへ接続
		   //PLCへ送信
		    Charset charset = Charset.forName("US-ASCII");
		    ByteBuffer s_data = charset.encode(send);
		            channel.write(s_data);//PLCへコマンド送信
		   //PLCから受信
			ByteBuffer r_data = ByteBuffer.allocate(512);
			channel.read(r_data);//受信
			r_data.flip();//byte列の開始を0からに移動させる
			byte[] data = new byte[r_data.limit()];
			r_data.get(data);
			//受信文字列が不正な場合
			if(!"8200".equals(new String(data))){
			   //例外の場合は、ダイアログを表示
			    JOptionPane.showMessageDialog(null,"受信文字列が\"8200\"ではありません。");
			     return;
			}
			//接続のクローズ
			channel.close();
			//成功の場合、画像を変更
			if(!kiki){
			 //PLC画像の変更
			     if(con_belt.equals("10")){
			    	 icon_con = new ImageIcon("./src/ry_left.gif");
		        	  label_con_image.setIcon(icon_con);
				}
				else if(con_belt.equals("00")){
				   icon_con = new ImageIcon("./src/ry_stop.png");
			           label_con_image.setIcon(icon_con);
				}
				else if(con_belt.equals("01")){
					icon_con = new ImageIcon("./src/ry_right.gif");
			           label_con_image.setIcon(icon_con);
				}
			}
			else{//パイロットランプ画像の変更
				for(int i=0; i<4; i++){
				   if(pl_data[i] == 0){//0の場合、消灯画像表示
				     btn_pl[i].setIcon(new ImageIcon("./src/led_off.png"));
				    }
				    else{//1の場合、点灯画像表示
			              btn_pl[i].setIcon(new ImageIcon("./src/led_on.png"));
				    }
				}
			}
		}
		catch(Exception e1){//例外の場合は、ダイアログを表示
		   JOptionPane.showMessageDialog(null,e1.toString());
		}
	}
	//mainメソッド
	public static void main(String [] args){
		PlcDirectControl_7segkai  app = new PlcDirectControl_7segkai ("PLC直接制御アプリ", 400, 800);
		app.setVisible(true);
	}
}
