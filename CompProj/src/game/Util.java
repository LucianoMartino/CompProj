package game;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.*;

import java.io.*;

public class Util {
	public static double getAngle(double vecX, double vecY) {

		if (vecX == 0 && vecY == 0) {
			return 0;
		} else if (vecX >= 0 && vecY >= 0) {
			return Math.atan(vecY / vecX);
		} else if (vecX <= 0 && vecY >= 0) {
			return Math.atan(Math.abs(vecX) / vecY) + Math.PI / 2.;
		} else if (vecX <= 0 && vecY <= 0) {
			return Math.atan(Math.abs(vecY) / Math.abs(vecX)) + Math.PI;
		} else if (vecX >= 0 && vecY <= 0) {
			return Math.atan(vecX / Math.abs(vecY)) + (3 * Math.PI) / 2;
		}

		return 0;
	}

	public static BufferedImage loading(String path) {
		if (!path.startsWith("/"))
			path = "/" + path;

		try {
			InputStream str = Util.class.getResourceAsStream(path);
			BufferedImage img = ImageIO.read(str);
			return img;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error loading img path: " + path, "Fatal Imaging Error",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		return null;
	}
	public static void debugPrint(String x){
		System.out.println(x);	
	}
}
