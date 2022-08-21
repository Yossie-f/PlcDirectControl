import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

//import com.apple.laf.AquaButtonLabeledUI.LabeledButtonBorder;

public class PlcDirectControl_Array_7seg extends JFrame implements ActionListener {

	private int pl_data[] = {0, 0, 0, 0};		//パイロットランプの点灯状態保存用配列（初期値は0：消灯）

	//文字と画像を表示するJLabel
	JLabel label_plc, label_con_image, label_con_title, label_7seg_img;
	JLabel label_pl_title, label_d_pl1, label_d_pl2, label_d_pl3, label_d_pl4;
	JLabel[] label_pl = new JLabel[4];

	ImageIcon icon_con, icon_7seg;										//画像用イメージアイコン
	

	JList<String> list_plc;									//PLCのIPアドレス選択用Jリスト
	JScrollPane sp_plc;										//JListにスクロールをつけるためのJScrollPane
	JButton btn_plc;										//PLC IP選択用のJButton

	JButton btn_left, btn_stop, btn_right;					//ベルトコンベヤ制御用のJButton
	JButton[] btn_pl = new JButton[4];						//パイロットランプ制御用のJButtonを配列で用意
	JButton[] btn_7seg = new JButton[10];					//7seg用ボタン
	String[][] segArray = {{"0", "0000"}, {"1", "1000"}, {"2", "0100"}, {"3", "1100"}, 
			{"4", "0010"}, {"5", "1010"}, {"6", "0110"}, {"7", "1110"}, 
			{"8", "0001"}, {"9", "1001"}};
			
	JPanel panel_plc, panel_btn, panel_con, panel_pl, panel_7seg;		//ボタンなどを配置するためのJPanel

	Container content;										//部品を乗せるためのコンテナー


	//コンストラクタ
	public PlcDirectControl_Array_7seg(String title, int w, int h) {
		super(title);
		setSize(w, h);

		//文字表示と画像表示のラベルを作成
		label_plc = new JLabel("PLC IP選択");									//"IPアドレス選択"文字を表示するラベル
		icon_con = new ImageIcon("./src/ry_stop.png");							//ベルトコンベヤの停止状態の画像を用意
		label_con_image = new JLabel(icon_con);									//ラベルを用意して画像を貼る

		label_con_title = new JLabel("ベルトコンベヤの制御", JLabel.CENTER);	//ベルトコンベヤの制御
		label_pl_title = new JLabel("パイロットランプの制御", JLabel.CENTER);	//パイロットランプの制御

		//パイロットランプ用JLabel
		label_d_pl1 = new JLabel("PL1", JLabel.CENTER);	//PL1～PL4の文字を表示するラベルを作成
		label_d_pl2 = new JLabel("PL2", JLabel.CENTER);
		label_d_pl3 = new JLabel("PL3", JLabel.CENTER);
		label_d_pl4 = new JLabel("PL4", JLabel.CENTER);
		
		//IPアドレスリストの作成
		String[] plc = new String[6];						//JListおよびJScrollPaneの作成
		for(int i=0; i < 6; i++) {
			plc[i] = "172.16.110." + Integer.toString(220 + i);	//plc配列の各要素に、それぞれのIPアドレスをfor文で格納
		}
		list_plc = new JList<String>(plc);		//IPアドレス（配列plc）でアドレスのJリスト(list_plc)を作る
		list_plc.setVisibleRowCount(3);			//アドレスのJリスト(list_plc)の表示行数を３に設定
		sp_plc = new JScrollPane(list_plc);		//list_plcを乗せたスクロール領域（sp_plc）を作る
		sp_plc.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);	//sp_plcのスクロールバーを常に表示に設定

		//ベルトコンベヤの制御ボタン作成
		btn_left = new JButton("左");	//左ボタンを作る
		btn_stop = new JButton("停止");	//停止ボタンを作る
		btn_right = new JButton("右");	//右ボタンを作る
		
		//パイロットランプの制御ボタン作成
		for(int i = 0; i < 4; i++) {
			btn_pl[i] = new JButton(new ImageIcon("./src/led_off.png"));	//パイロットランプ用ボタン生成。各ボタンに画像を表示させる
		}
		
		//7seg画像のラベルを作成
		ImageIcon icon = new ImageIcon("./src/_7seg/_7seg_a.png");	
		Image iconImg = icon.getImage();
		Image newing = iconImg.getScaledInstance(130, 130,  java.awt.Image.SCALE_SMOOTH);	
		ImageIcon iconS = new ImageIcon(newing);
		label_7seg_img = new JLabel(iconS);	
		
		//7segボタンを作成
		for(int i = 0; i < 10; i++) {	//0〜9のボタン生成
				btn_7seg[i] = new JButton(segArray[i][0]);					
		}
		

		//パネル"plc"を生成。部品を配置, PLC IPアドレス部分
		panel_plc = new JPanel();	//パネル作る（IPアドレス用）
		panel_plc.add(label_plc);	//ラベル（文字）を乗せる
		panel_plc.add(sp_plc);		//IPアドレスのスクロール領域(sp_plc)を乗せる

		//パネル"btn"を生成。ベルトコンベヤ制御用ボタンを追加
		panel_btn = new JPanel();	//パネルを作る（コンベヤ操作ボタン用）
		panel_btn.add(btn_left);	//コンベヤーの各ボタンをパネルに乗せる
		panel_btn.add(btn_stop);	//
		panel_btn.add(btn_right);	//

		//パネル"pl"を生成。PL1～PL4と対応する画像
		panel_pl = new JPanel();	//パネルを作る（パイロットランプ用）
		panel_pl.setLayout(new GridLayout(2, 4));	//パネルレイアウトを2×４のグリッドに決める
		panel_pl.add(label_d_pl1);	//文字（PL1〜PL4）を表示するラベルをパネルに乗せる
		panel_pl.add(label_d_pl2);
		panel_pl.add(label_d_pl3);
		panel_pl.add(label_d_pl4);
		for(int i = 0; i < 4; i++) {	//ループ処理で
			panel_pl.add(btn_pl[i]);	//パイロットランプ用ボタンを乗せる
		}
		
		//7segボタン用パネルを生成	ボタンを配置
		panel_7seg = new JPanel();
		for(int i = 0; i < 10; i++) {	//パネルに7segボタンを追加
			panel_7seg.add(btn_7seg[i]);
		}
		panel_7seg.setLayout(new GridLayout(4, 3, 5, 5));		//パネルレイアウトを縦４×横３に設定
				
		//Containerへ部品を配置
		content = getContentPane();		//JFrameにでデフォルトで入っているContainerオブジェクトをメソッドで取得
		content.setLayout(new GridLayout(8, 1));	//コンテナ(content)のレイアウトを決める（６行１列）
		content.add(panel_plc);			//IPアドレスのパネルを載せる
		content.add(label_con_title);	//ベルトコンベヤー の 文字パネル        を載せる
		content.add(label_con_image);	//ベルトコンベヤー の 画像パネル        を載せる
		content.add(panel_btn);			//ベルトコンベヤー の ボタンパネル      を載せる
		content.add(label_pl_title);	//パイロットランプ の 文字パネル        を載せる
		content.add(panel_pl);			//パイロットランプ の 文字とボタンパネル を載せる
		content.add(label_7seg_img);	//7seg画像ラベルを載せる
		content.add(panel_7seg);		//7segボタンパネルを載せる

		
		//ボタンへイベントを追加
		btn_left.addActionListener(this);		//コンベヤの各ボタンにアクション設定を追加
		btn_stop.addActionListener(this);		//
		btn_right.addActionListener(this);		//
		for(int i = 0; i < 4; i++) {
			btn_pl[i].addActionListener(this);	//i番目のパロットランプボタンにアクション設定を追加
		}
		for(int i = 0; i < 10; i++) {
			btn_7seg[i].addActionListener(this);	//7segボタンにアクション設定を追加
		}
		
		//閉じるボタン設定
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	//閉じるボタンの設定
	}
	//PlcDirectControlクラスのコンストラクタの末尾

	
	//PLC押しボタンのイベント処理(ここからメソッド)
	@Override
	public void actionPerformed(ActionEvent e) {	//"actionPerformed"メソッドのなかに全てのイベント情報を記載する。
		//イベントが起きたボタンの処理
		Object obj = e.getSource();		//イベントが最初に発生したオブジェクトを"obj"に格納
		
		String con_belt = null;		//コンベヤーの伝聞命令の下２桁 を 代入するための変数を用意
		int kiki = 0;		//パイロットランプかコンベヤー を trueかfaule で区別するための変数を用意
		String seg_lmp = null;		//7seg用

		if(obj == btn_left) {		//押されたのがコンベヤーの左ボタンなら
			con_belt = "10";
			kiki = 1;
		}

		if(obj == btn_stop) {		//コンベヤーのストップボタンが押されたら
			con_belt = "00";
			kiki = 1;
		}

		if(obj == btn_right) {		//コンベヤーの右ボタンが押されたら
			con_belt = "01";
			kiki = 1;
		}

		for(int i = 0; i < 4; i++ ) {
			if(obj == btn_pl[i]) {		//パイロットランプのi番目のボタンが押されたら
				pl_data[i] ^= 1;		//パイロットランプの状態を示す配列のi番目だけ、排他的論理和で1を足す⇨要素が0なら1に、1なら0になる。
				kiki = 2;			//kiki をtrueに
			}
		}
		
		//7segスイッチ文
		for(int i = 0; i < 10; i++) {
			if(obj == btn_7seg[i]){
				seg_lmp = segArray[i][1]; 
				kiki = 3;		
			}
		}
		

		String plc_ip = (String)list_plc.getSelectedValue();	//IPアドレスリストから、選択された値を返取り出し、String型にキャストしてplc_ipに入れる

		if(plc_ip == null) {	//IPアドレスが選択されていなければ
			JOptionPane.showMessageDialog(this, "PLCのIPアドレスが選択されていません。");	//ユーザーへのメッセージウィンドウをポップアップする
			return;
		}

		//PLCへの伝聞命令を作る
		String send = null;		//伝聞命令となる、String型変数sendを用意。nullを入れる。
		
		if(kiki == 2) {				//kikiがtrueなら（パイロットランプボタンが押されたら）
			String str = "";	//空の変数strを用意
			for(int i = 0; i < 4; i++) {				//4回ループ
				str += Integer.toString(pl_data[i]);	//パイロットランプ状態配列のi番目の要素を数値から文字列に変換してstrに代入
			}
			send = "02ff00025920000000100400" + str;	//send に "PLCの伝聞命令" ＋ "パイロットランプ用の下４桁" を代入する
		} else if(kiki == 1){										//kikiがfalseなら（コンベヤーのボタンが押されたら）
			send = "02ff00025920000000140200" + con_belt;	//send　に　"PLCの伝聞命令" ＋ "コンベヤー用の下２桁" を代入する
		} else if(kiki == 3){
			send = "02ff00025920000000180400" + seg_lmp;
		}
		


		//ソケットアドレスを取得する
		InetSocketAddress address = null;	//ソケットアドレスクラスを用意
		try {
			address = new InetSocketAddress(InetAddress.getByName(plc_ip), 0XC000);	//選択されたIPアドレスを取得し、とポート番号（30000）を指定してソケットアドレスを作る
		} catch(Exception e1) {										//IPアドレスが見つからなかった場合
			JOptionPane.showMessageDialog(this,  e1.toString());	//オプションダイアログメッセージを表示する。内容はエクセプション。
			return;
		}
		
		System.out.println(send);	//test用　消す
		
/*		//ソケット接続させる対象がない場合はエラーになるのでコメントアウトしておく
 * 		SocketChannel channel = null;	//空のソケットチャネルを用意
		try {
			//PLCへの伝聞命令の設定
			channel = SocketChannel.open();						//ソケットチャネルのポートのオープン
			channel.connect(address);							//ソケットチャネルを 選択されたIPアドレスとポート番号に接続
//			channel.configureBlocking(false);					//ノンブロッキングに設定　※複数アクセプト実行中にブロッキングが行われないようする。
			Charset charset = Charset.forName("US-ASCII");		//文字セットオブジェクトを用意。ASCIIコードを取得して入れる。
			ByteBuffer s_data = charset.encode(send);			//伝聞命令sendをASCIIにしてバイトバッファに入れる
			channel.write(s_data);								//バイトバッファの内容（伝聞命令）をソケットに書き出す（送出）

			//PLCからの受信内容の設定
			ByteBuffer r_data = ByteBuffer.allocate(512);		//バイトバッファを用意。allocateで1024バイト分のデータ領域を確保する。
			channel.read(r_data);								//バイトバッファの内容を読みだす（受入）
			r_data.flip();										//次の読込操作のためにバッファ内の位置とリミットを現在位置に設定？
			byte[]data = new byte[r_data.limit()];				//limitメソッド…r_dataの文字数を表す。 読み込んだ文字数分の要素数の配列を用意
			r_data.get(data);									//バイトバッファのデータをdataに取得する？
			if(!"8200".equals(new String(data))) {				//PLCからの返信が8200でないなら
				JOptionPane.showMessageDialog(this,  "受信文字列が「8200」ではありません。");		//メッセージウィンドウを表示
				return;
			}
			channel.close();									//ソケットチャネルを閉じる
*/
			
			//コンベヤとパイロットランプの稼働状態を示す画像を切り替える処理
			if(kiki == 1) {		//kiki が true でないとき、つまり false の時。なのでコンベヤーのボタンが押された時
				if(con_belt.equals("10")) {							//左ボタンが押された時
					icon_con = new ImageIcon("./src/ry_left.gif");	//コンベヤの左回りの画像を用意
					label_con_image.setIcon(icon_con);				//左画像をコンベヤ画像用ラベルにセットする

				} else if(con_belt.equals("00")) {					//ストップボタンが押された時
					icon_con = new ImageIcon("./src/ry_stop.png");	//コンベヤ停止の画像を用意
					label_con_image.setIcon(icon_con);				//停止画像をコンベヤ画像用ラベルにセットする

				} else if(con_belt.equals("01")) {					//右ボタンが押された時
					icon_con = new ImageIcon("./src/ry_right.gif");
					label_con_image.setIcon(icon_con);
				}
			} else if(kiki == 2){		//kikiがtureの時、つまりパイロットランプのボタンが押された時
				for(int i = 0; i < 4; i++) {
					if(pl_data[i] == 0) {										//パイロットランプ状態配列のi番目が0の時
						btn_pl[i].setIcon(new ImageIcon("./src/led_off.png"));	//パイロットランプJボタンに、off画像にセットする
					} else {													//i番目が0(1)でないなら
						btn_pl[i].setIcon(new ImageIcon("./src/led_on.png"));	//on画像をセットする
					}
				}
			} else if(kiki == 3){
				for(int i = 0; i < 10; i++) {
					if(seg_lmp == segArray[i][1]) {
						label_7seg_img.setIcon(ImageScale.ImgScale("./src/_7seg/_7seg_" + i + ".png")); 									
					}
				}
				
			}
			
		} 
/*		catch(Exception e1) {	//例外がおこった場合
			JOptionPane.showMessageDialog(null, e1.toString());
			}
		}
*/		
		//メインメソッド
		public static void main(String[] args) {
		PlcDirectControl_Array_7seg app = new PlcDirectControl_Array_7seg("PLC制御アプリ", 400, 700);	//タイトルと枠サイズを指定してクラスを生成
		app.setVisible(true);													//appを表示して実行する
	}	
}
