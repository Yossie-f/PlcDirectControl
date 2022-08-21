import java.awt.Image;

import javax.swing.ImageIcon;

public class ImageScale {
	
	static ImageIcon ImgScale(String src){		//引数srcには画像のパスを指定
		ImageIcon icon = new ImageIcon(src);	
		Image iconImg = icon.getImage();
		Image newing = iconImg.getScaledInstance(45, 70, java.awt.Image.SCALE_SMOOTH);	
		ImageIcon iconS = new ImageIcon(newing);
		return iconS;
	}
	
}
