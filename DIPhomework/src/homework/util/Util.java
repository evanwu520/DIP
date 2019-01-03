package homework.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import homework.bean.Point;

public class Util {
	
	
	public static int maxGradient = 0;
	public static List<String> removePoints = new ArrayList<>();
	public static List<String> historyPoints = new ArrayList<>();
	

	/**
	 * nearestNeighbor
	 * @param pixels
	 * @param w1
	 * @param h1
	 * @param w2
	 * @param h2
	 * @return
	 */
	public static int[] nearestNeighbor(int[] pixels, int w1, int h1, int w2, int h2) {
		int[] temp = new int[w2 * h2];
		double xRatio = w1 / (double) w2;
		double yRatio = h1 / (double) h2;
		double x, y;
		// 由左至右，由上而下
		for (int i = 0; i < h2; i++) {
			for (int j = 0; j < w2; j++) {
				// 原圖與放大比例
				x = Math.floor(j * xRatio);
				y = Math.floor(i * yRatio);
				temp[(i * w2) + j] = pixels[(int) Math.round(((y * w1) + x))];
			}
		}
		return temp;
	}

	/**
	 * bilinear
	 * @param pixels
	 * @param w
	 * @param h
	 * @param w2
	 * @param h2
	 * @return
	 */
	public static int[] bilinear(int[] pixels, int w, int h, int w2, int h2) {
		int[] temp = new int[w2 * h2];
		int a, b, c, d, x, y, index;
		float x_ratio = ((float) (w - 1)) / w2;
		float y_ratio = ((float) (h - 1)) / h2;
		float x_diff, y_diff, blue, red, green;
		int offset = 0;
		// 由左至右，由上而下
		for (int i = 0; i < h2; i++) {
			for (int j = 0; j < w2; j++) {
				x = (int) (x_ratio * j);
				y = (int) (y_ratio * i);
				x_diff = (x_ratio * j) - x;
				y_diff = (y_ratio * i) - y;
				index = (y * w + x);
				// 取四個位置
				a = pixels[index];
				b = pixels[index + 1];
				c = pixels[index + w];
				d = pixels[index + w + 1];

				// blue element
				blue = calcuPixelgColor(a & 0xff, b & 0xff, c & 0xff, d & 0xff, x_diff, y_diff);
				// green element
				green = calcuPixelgColor((a >> 8) & 0xff, (b >> 8) & 0xff, (c >> 8) & 0xff, (d >> 8) & 0xff, x_diff,
						y_diff);
				// red element
				red = calcuPixelgColor((a >> 16) & 0xff, (b >> 16) & 0xff, (c >> 16) & 0xff, (d >> 16) & 0xff, x_diff,
						y_diff);

				temp[offset++] = 0xff000000 | // hardcode alpha
						((((int) red) << 16) & 0xff0000) | ((((int) green) << 8) & 0xff00) | ((int) blue);
			}
		}
		return temp;
	}
	
	/**
	 * calcuPixelgColor
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @param x_diff
	 * @param y_diff
	 * @return
	 */
	private static float calcuPixelgColor(int a, int b ,int c, int d, float x_diff, float y_diff){
	
		return a*(1-x_diff)*(1-y_diff)+b*(x_diff)*(1-y_diff)+c*(y_diff)*(1-x_diff)+d*(x_diff*y_diff);
	}
	
	/**
	 * pixelsToImg
	 * @param pixels
	 * @param w2
	 * @param h2
	 */
	public static void pixelsToImg(int[] pixels,int w2,int h2, String name){
	     //create buffered image object img
	     BufferedImage img = new BufferedImage(w2, h2, BufferedImage.TYPE_INT_ARGB);
	     //file object
	     File f = null;
	     
	     int count=0;
	     for(int y = 0; y < h2; y++){
	       for(int x = 0; x < w2; x++){
	         img.setRGB(x, y,  pixels[count++]);
	       }
	     }
	     //write image
	     try{
	       f = new File(System.getProperty("user.dir")+"\\"+name+".png");
	       ImageIO.write(img, "png", f);
	       System.out.println("output path : "+f.getAbsolutePath());
	     }catch(IOException e){
	       System.out.println("Error: " + e);
	     }
	}
	
	/**
	 * pixelsArrayToGrayImg
	 * @param pixels
	 * @param w2
	 * @param h2
	 */
	public static void pixelsArrayToImg(int[][] pixels,int w2,int h2, String name){
	     //create buffered image object img
	     BufferedImage img = new BufferedImage(w2, h2, BufferedImage.TYPE_BYTE_GRAY);
	     //file object
	     File f = null;
	     
	     for(int y = 0; y < h2; y++){
	       for(int x = 0; x < w2; x++){
	    	   
	         img.setRGB(x, y, pixels[y][x]);
	       }
	     }
	     //write image
	     try{
	       f = new File(System.getProperty("user.dir")+"\\"+name+".png");
	       ImageIO.write(img, "png", f);
	       System.out.println("output path : "+f.getAbsolutePath());
	     }catch(IOException e){
	       System.out.println("Error: " + e);
	     }
	}
	
	
	
	
	/**
	 * pixelsArrayToSobelImg
	 * @param pixels
	 * @param w2
	 * @param h2
	 * @param name
	 */
	public static void pixelsArrayToSobelImg(int[][] pixels,int w2,int h2,String name){
	     //create buffered image object img
	     BufferedImage img = new BufferedImage(w2, h2, BufferedImage.TYPE_BYTE_GRAY);
	     //file object
	     File f = null;
	     
	     double scale = 255.0 / maxGradient;
	     
	     
	     for(int y = 1; y < h2-1; y++){
	       for(int x = 1; x < w2-1; x++){
	    	   
	    	   int color = pixels[y][x];
	    	   color = (int)(color * scale);
	    	   color = 0xff000000 | (color << 16) | (color << 8) | color;
	    	   
               img.setRGB(x, y, color);
	       }
	     }
	     //write image
	     try{
	       f = new File(System.getProperty("user.dir")+"\\"+name+".png");
	       ImageIO.write(img, "png", f);
	       System.out.println("output path : "+f.getAbsolutePath());
	     }catch(IOException e){
	       System.out.println("Error: " + e);
	     }
	}
	
	/**
	 * getFuntonRsult
	 * @param variable
	 * @param values
	 * @return
	 */
	public static RealVector getFuntonRsult(double[][] variable, double[] values) {

		RealMatrix coefficients = new Array2DRowRealMatrix(variable, false);
		DecompositionSolver solver = new LUDecomposition(coefficients).getSolver();
		RealVector constants = new ArrayRealVector(values, false);
		RealVector solution = solver.solve(constants);
		return solution;

	}
	
	/**
	 * getImgPixels
	 * @param image
	 * @return
	 * @throws IOException
	 */
	public static int[] getImgPixels(BufferedImage image) throws IOException {

		int width = image.getWidth();
		int height = image.getHeight();

		int[] temp = new int[width * height];

		int count = 0;

		for (int i = 0; i < height; i++) {

			for (int j = 0; j < width; j++) {

				temp[count] = image.getRGB(j, i);

				count++;
			}
		}

		return temp;
	}
	
	
	/**
	 * getImgPixelsArray
	 * @param image
	 * @return
	 * @throws IOException
	 */
	public static int[][] getImgPixelsArray(BufferedImage image) throws IOException {

		int width = image.getWidth();
		int height = image.getHeight();

		int[][] temp = new int[width] [height];


		for (int i = 0; i < height; i++) {

			for (int j = 0; j < width; j++) {

				temp[i][j] = image.getRGB(j, i);

			}
		}

		return temp;
	}
	
	/*
	 * getLaplacianBylevel
	 */
	public static int[] getLaplacianBylevel(int level){
		
		//1-3微分
		int[][] laplacian = {{ 0, -1,  0, -1,  4, -1,  0, -1,  0},
                	 		{-1, -1, -1, -1,  8, -1, -1, -1, -1,},
                	 		{1, -2,  1, -2,  4, -2,  1, -2,  1 }};
		
		return laplacian[level];
	}
	
	/*
	 * getsobelByXY
	 */
	public static int[] getSobelByXY(String type){
		
		//x,y
		int[][] sobel = {{ -1, 0, 1, -2,  0, 2,  -1, 0, 1},
                	 	 { -1, -2, -1, 0,  0, 0, 1, 2, 1,}};
                	 		
		if("x".equals(type)){
			return sobel[0];
		} else if ("y".equals(type)){
			return sobel[1];
		}
		
		return null;
	}
	
	
	/**
	 * getColor
	 * @param color
	 * @param rgb
	 * @return
	 */
	public static int getColor(String color , int rgb) {
		
		int value = 0;
	
		
		if("r".equals(color)) {				// 取得紅色的資料 
			value =  (rgb >> 16) & 0xff;
		} else if("g".equals(color)) {		// 取得綠色資料 
			value =  (rgb >> 8) & 0xff;
		} else if("b".equals(color)) {		// 取得藍色資料 
			value =  rgb & 0xff;
		} else if ("gray".equals(color)) {
			
			int r = (rgb >> 16) & 0xff;
			int g = (rgb >> 8) & 0xff;
			int b = rgb & 0xff;
			//基于人眼感知
			//https://github.com/aooy/blog/issues/4
			value = (int)(0.2126 * r + 0.7152 * g + 0.0722 * b);
//			value =(int)(0.3*r + 0.59*g + 0.11*b);
//			value =(int)( (r + g + b)/3);
		}
		
		return value;
	}
	
	/**
	 * laplacian
	 * @param image
	 * @param laplacianBylevel
	 * @return
	 */
	public static int [][] laplacian(BufferedImage image, int laplacianBylevel){
		
		int width = image.getWidth();
        int height = image.getHeight();
        int [][]pixels = new int[height][width];
        // get Laplacian 2
        int[] laplacian = getLaplacianBylevel(laplacianBylevel);
        
		
		//邊框不做所以從1開始，length-1結束
        for(int y=1; y<height-1; y++){
        	
        	for(int x=1; x<width-1; x++){
        		
				// 處理 RGB Laplacian
				int r = getColor("r", image.getRGB(x - 1, y - 1)) * laplacian[0]
						+ getColor("r", image.getRGB(x - 1, y)) * laplacian[1]
						+ getColor("r", image.getRGB(x - 1, y + 1)) * laplacian[2]
						+ getColor("r", image.getRGB(x, y - 1)) * laplacian[3]
						+ getColor("r", image.getRGB(x, y)) * laplacian[4]
						+ getColor("r", image.getRGB(x, y + 1)) * laplacian[5]
						+ getColor("r", image.getRGB(x + 1, y - 1)) * laplacian[6]
						+ getColor("r", image.getRGB(x + 1, y)) * laplacian[7]
						+ getColor("r", image.getRGB(x + 1, y + 1)) * laplacian[8];

				int g = getColor("g", image.getRGB(x - 1, y - 1)) * laplacian[0]
						+ getColor("g", image.getRGB(x - 1, y)) * laplacian[1]
						+ getColor("g", image.getRGB(x - 1, y + 1)) * laplacian[2]
						+ getColor("g", image.getRGB(x, y - 1)) * laplacian[3]
						+ getColor("g", image.getRGB(x, y)) * laplacian[4]
						+ getColor("g", image.getRGB(x, y + 1)) * laplacian[5]
						+ getColor("g", image.getRGB(x + 1, y - 1)) * laplacian[6]
						+ getColor("g", image.getRGB(x + 1, y)) * laplacian[7]
						+ getColor("g", image.getRGB(x + 1, y + 1)) * laplacian[8];

				int b = getColor("b", image.getRGB(x - 1, y - 1)) * laplacian[0]
						+ getColor("b", image.getRGB(x - 1, y)) * laplacian[1]
						+ getColor("b", image.getRGB(x - 1, y + 1)) * laplacian[2]
						+ getColor("b", image.getRGB(x, y - 1)) * laplacian[3]
						+ getColor("b", image.getRGB(x, y)) * laplacian[4]
						+ getColor("b", image.getRGB(x, y + 1)) * laplacian[5]
						+ getColor("b", image.getRGB(x + 1, y - 1)) * laplacian[6]
						+ getColor("b", image.getRGB(x + 1, y)) * laplacian[7]
						+ getColor("b", image.getRGB(x + 1, y + 1)) * laplacian[8];
    		
        		//color 0-255
        		r = Math.min(255, Math.max(0, r));
                g = Math.min(255, Math.max(0, g));
                b = Math.min(255, Math.max(0, b));
						
        		Color color = new Color(r, g, b);
        		pixels[y][x] = color.getRGB();
        		
        	}
        }
        
        return pixels;
	}
	
	/**
	 * 
	 * @param image
	 * @return
	  * https://stackoverflow.com/questions/41468661/sobel-edge-detecting-program-in-java
	 */
	public static int [][] sobel(BufferedImage image){
		
		int width = image.getWidth();
        int height = image.getHeight();
        int [][]pixels = new int[height][width];
        // get sobelX
        int[] sobelX = getSobelByXY("x");
        // get sobelX
        int[] sobelY = getSobelByXY("y");
		
		//邊框不做所以從1開始，length-1結束
        for(int y=1; y<height-1; y++){
        	
        	for(int x=1; x<width-1; x++){
        		
        		//處理 RGB sobel X
				int valueX = getColor("gray", image.getRGB(x - 1, y - 1)) * sobelX[0]
						+ getColor("gray", image.getRGB(x - 1, y)) * sobelX[1]
						+ getColor("gray", image.getRGB(x - 1, y + 1)) * sobelX[2]
						+ getColor("gray", image.getRGB(x, y - 1)) * sobelX[3]
						+ getColor("gray", image.getRGB(x, y)) * sobelX[4]
						+ getColor("gray", image.getRGB(x, y + 1)) * sobelX[5]
						+ getColor("gray", image.getRGB(x + 1, y - 1)) * sobelX[6]
						+ getColor("gray", image.getRGB(x + 1, y)) * sobelX[7]
						+ getColor("gray", image.getRGB(x + 1, y + 1)) * sobelX[8];

				// 處理 RGB sobel Y
				int valueY = getColor("gray", image.getRGB(x - 1, y - 1)) * sobelY[0]
						+ getColor("gray", image.getRGB(x - 1, y)) * sobelY[1]
						+ getColor("gray", image.getRGB(x - 1, y + 1)) * sobelY[2]
						+ getColor("gray", image.getRGB(x, y - 1)) * sobelY[3]
						+ getColor("gray", image.getRGB(x, y)) * sobelY[4]
						+ getColor("gray", image.getRGB(x, y + 1)) * sobelY[5]
						+ getColor("gray", image.getRGB(x + 1, y - 1)) * sobelY[6]
						+ getColor("gray", image.getRGB(x + 1, y)) * sobelY[7]
						+ getColor("gray", image.getRGB(x + 1, y + 1)) * sobelY[8];

        		int value = (int)Math.sqrt((valueX * valueX) + (valueY*valueY));
        		
        		if(maxGradient < value) {
                    maxGradient = value;
                }

        		value = Math.min(255, Math.max(0, value));
        		pixels[y][x] = value;
        		
        	}
        }

        return pixels;
	}
	
	/**
	 * lowPassFilter
	 * @param image
	 * @return
	 */
	public static int [][] lowPassFilter(BufferedImage image){
		
		int width = image.getWidth();
        int height = image.getHeight();
        int [][]pixels = new int[height][width];
		
		//邊框不做所以從1開始，length-1結束
        for(int y=1; y<height-1; y++){
        	
        	for(int x=1; x<width-1; x++){
        		
        		int value = getColor("gray", image.getRGB(x-1, y-1))  + getColor("gray", image.getRGB(x-1, y))+ getColor("gray", image.getRGB(x-1, y+1)) +
        					getColor("gray", image.getRGB(x, y-1)) + getColor("gray", image.getRGB(x, y))+ getColor("gray", image.getRGB(x, y+1)+ 
        				    getColor("gray", image.getRGB(x+1, y-1)) + getColor("gray", image.getRGB(x+1, y)) + getColor("gray", image.getRGB(x+1, y+1)));
        				    
        		value = value/9;
        		int  color = 0xff000000 | (value << 16) | (value << 8) | value;
        		
        		pixels[y][x] = color;
        		
        	}
        }
        
        return pixels;
        
	}
	
	
	/**
	 * medianFilter
	 * @param image
	 * @return
	 */
	public static int [][] medianFilter(BufferedImage image){
		
		int width = image.getWidth();
        int height = image.getHeight();
        int [][]pixels = new int[height][width];
        List<Integer> list = new ArrayList<>();
		
		//邊框不做所以從1開始，length-1結束
        for(int y=1; y<height-1; y++){
        	
        	for(int x=1; x<width-1; x++){
        		
        		//sort
        		list.add(getColor("gray", image.getRGB(x-1, y-1)));
        		list.add(getColor("gray", image.getRGB(x-1, y))); 
        		list.add(getColor("gray", image.getRGB(x-1, y+1))); 
        		list.add(getColor("gray", image.getRGB(x, y-1))); 
        		list.add(getColor("gray", image.getRGB(x, y))); 
        		list.add(getColor("gray", image.getRGB(x, y+1))); 
        		list.add(getColor("gray", image.getRGB(x+1, y-1))); 
        		list.add(getColor("gray", image.getRGB(x+1, y))); 
        		list.add(getColor("gray", image.getRGB(x+1, y+1)));
        		Collections.sort(list);
        		
        		int value = list.get(4);
        		int  color = 0xff000000 | (value << 16) | (value << 8) | value;
        		pixels[y][x] = color;
        		
        		list.clear();
        		
        	}
        }
        
        return pixels;
        
	}
	
	/**
	 * alpha
	 * @param image
	 * @return
	 */
	public static int [][] alphaFilter(BufferedImage image){
		
		int width = image.getWidth();
        int height = image.getHeight();
        int [][]pixels = new int[height][width];
        List<Integer> list = new ArrayList<>();
		
		//邊框不做所以從1開始，length-1結束
        for(int y=1; y<height-1; y++){
        	
        	for(int x=1; x<width-1; x++){
        		
        		//sort
        		list.add(getColor("gray", image.getRGB(x-1, y-1)));
        		list.add(getColor("gray", image.getRGB(x-1, y))); 
        		list.add(getColor("gray", image.getRGB(x-1, y+1))); 
        		list.add(getColor("gray", image.getRGB(x, y-1))); 
        		list.add(getColor("gray", image.getRGB(x, y))); 
        		list.add(getColor("gray", image.getRGB(x, y+1))); 
        		list.add(getColor("gray", image.getRGB(x+1, y-1))); 
        		list.add(getColor("gray", image.getRGB(x+1, y))); 
        		list.add(getColor("gray", image.getRGB(x+1, y+1)));
        		Collections.sort(list);
        		
        		int value = 0;
        		//取中間5個平均
        		for(int i=3; i<=7; i++){
        			value += list.get(i)/5;
        		}
        		
        		int  color = 0xff000000 | (value << 16) | (value << 8) | value;
        		pixels[y][x] = color;
        		list.clear();
        		
        	}
        }
        
        return pixels;
        
	}
	
	
	//https://eclass.teicrete.gr/modules/document/file.php/TP283/Lab/03.%20Lab/lesson3Notes.pdf
	//https://github.com/a-badyda/image_processing/blob/master/p/src/image_enhancement/Normalization.java
	/**
	 * normalization
	 * @param image
	 * @return
	 */
	public static int [][] normalization(BufferedImage image){
		
		int width = image.getWidth();
        int height = image.getHeight();
        int [][]pixelsGray = new int[height][width];
        int minPixel= 0;
        int maxPixel = 0;
        int newValue = 0;
        
        //rgb to gray
        for(int y=0; y<height; y++){
        	
        	for(int x=0; x<width; x++){
        		pixelsGray[y][x] = getColor("gray", image.getRGB(x, y));
        	}
        }
        minPixel = findMinPixel(pixelsGray, width, height);
        maxPixel = findMaxPixel(pixelsGray, width, height);
        
        
        for(int y=0; y<height; y++){
        	
        	for(int x=0; x<width; x++){
        		
        		newValue = (int) ((pixelsGray[y][x] - minPixel)* (255/(maxPixel -minPixel)));
        		newValue =  Math.min(255, Math.max(0, newValue));
        		pixelsGray[y][x] = newValue;
        	}
        }
        
       return pixelsGray;
	}
	
	/**
	 * 
	 * @param image1
	 * @param image2
	 * @return
	 */
	public static int [][] multiplyImages(BufferedImage image1, BufferedImage image2) {
		
		int width = image1.getWidth();
        int height = image1.getHeight();
        int [][]pixels = new int[height][width];
        
		for (int y = 0; y < height; y++) {

			for (int x = 0; x < width; x++) {
				
				int r = getColor("r", image1.getRGB(x, y))  * getColor("r", image2.getRGB(x, y));
				int g = getColor("g", image1.getRGB(x, y))  * getColor("g", image2.getRGB(x, y));
				int b = getColor("b", image1.getRGB(x, y))  * getColor("b", image2.getRGB(x, y));
				
        		r = Math.min(255, Math.max(0, r));
                g = Math.min(255, Math.max(0, g));
                b = Math.min(255, Math.max(0, b));
        		Color color = new Color(r, g, b);
        		pixels[y][x] = color.getRGB();
        		
			}
		}
		
		return pixels;
	}
	
	/**
	 * plusImages
	 * @param image1
	 * @param image2
	 * @return
	 */
	public static int [][] plusImages(BufferedImage image1, BufferedImage image2) {
		
		int width = image1.getWidth();
        int height = image1.getHeight();
        int [][]pixels = new int[height][width];
        
		for (int y = 0; y < height; y++) {

			for (int x = 0; x < width; x++) {
			
				int r = getColor("r", image1.getRGB(x, y))  + getColor("r", image2.getRGB(x, y));
				int g = getColor("g", image1.getRGB(x, y))  + getColor("g", image2.getRGB(x, y));
				int b = getColor("b", image1.getRGB(x, y))  + getColor("b", image2.getRGB(x, y));
				
        		r = Math.min(255, Math.max(0, r));
                g = Math.min(255, Math.max(0, g));
                b = Math.min(255, Math.max(0, b));
        		Color color = new Color(r, g, b);
        		pixels[y][x] = color.getRGB();
				
			}	
		}
		return pixels;
	}
	
	/**
	 * bright
	 * @param image
	 */
	public static int [][] bright(BufferedImage image){
		
		int width = image.getWidth();
        int height = image.getHeight();
        int [][]pixels = new int[height][width];
        
    	for (int y = 0; y < height; y++) {

			for (int x = 0; x < width; x++) {
				
				int r = getColor("r", image.getRGB(x, y));
				int g = getColor("g", image.getRGB(x, y));  
				int b = getColor("b", image.getRGB(x, y));  
				
				r += 60;
				g += 60;
				b += 60;
        		r = Math.min(255, Math.max(0, r));
                g = Math.min(255, Math.max(0, g));
                b = Math.min(255, Math.max(0, b));
				
                Color color = new Color(r, g, b);
                
                pixels[y][x] = color.getRGB();
			}
    	}
    	
    	return pixels;
	}
	
	
	/**
	 * gammaCorrection
	 * @param image
	 * @param k
	 * @param n
	 * @return
	 */
	public static int [][] gammaCorrection(BufferedImage image, int k, float n){
		
		int width = image.getWidth();
        int height = image.getHeight();
        int [][]pixels = new int[height][width];
        
    	for (int y = 0; y < height; y++) {

			for (int x = 0; x < width; x++) {
				
				int r = getColor("r", image.getRGB(x, y));
				int g = getColor("g", image.getRGB(x, y));  
				int b = getColor("b", image.getRGB(x, y));  
				
                r = (int) (k * (Math.pow((double) r / (double) 255, n)));
                g = (int) (k * (Math.pow((double) g / (double) 255, n)));
                b = (int) (k * (Math.pow((double) b / (double) 255, n)));
				
                Color color = new Color(r, g, b);
                
                pixels[y][x] = color.getRGB();
			}
    	}
    	
    	return pixels;
	}
	
	/**
	 * saltAndPepper
	 * @param image
	 * @param salt
	 * @param pepper
	 * @return
	 * https://github.com/xuzebin/ImageProcessor/blob/master/src/ImageProcess.java
	 * https://www.cnblogs.com/oomusou/archive/2006/12/21/598795.html
	 */
	public static BufferedImage saltAndPepper(BufferedImage image, double salt, double pepper) {
		
		int h = image.getHeight();
		int w = image.getWidth();
		
		BufferedImage noise_img = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
		
		for(int x=0; x<w; x++){
			
			for(int y=0; y<h; y++){
				
				if(Math.random() < salt){
					
					noise_img.setRGB(x, y, Color.white.getRGB()); 
				}else if (Math.random() < pepper){
					
					noise_img.setRGB(x, y, Color.black.getRGB());
				}else {
					noise_img.setRGB(x, y, image.getRGB(x, y));
				}
			}
		}
		
		return noise_img;
	}
	
	/**
	 * 
	 * @param findMinPixel
	 * @param w
	 * @param h
	 * @return
	 */
	public static int findMinPixel(int[][] inputGray, int w, int h){
		int min = inputGray[0][0];
		for(int x=0; x<w; x++){
			for(int y=0; y<h; y++){
				int n = inputGray[y][x];
				if(n<min){
					min = n;
				}
			}
		}
		return min;
	}
	
	
	/**
	 * findMaxPixel
	 * @param inputGray
	 * @param w
	 * @param h
	 * @return
	 */
	public static int findMaxPixel(int[][] inputGray, int w, int h){
		int max = inputGray[0][0];
		for(int x=0; x<w; x++){
			for(int y=0; y<h; y++){
				int n = inputGray[y][x];
				if(n>max){
					max = n;
				}
			}
		}
		return max;
	}
	
	/**
	 * getBackGroud
	 * @param image
	 * @return
	 */
	public static int[][] getBackGroud(BufferedImage image) {
		
        int width = image.getWidth();;
        int height = image.getHeight();
        int [][]pixels = new int[height][width];
		
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				Color color = new Color(image.getRGB(x, y));

				if (color.getRGB() == Color.black.getRGB()) {
					 pixels[y][x] =  Color.white.getRGB();
				} else {
					 pixels[y][x] =  Color.black.getRGB();
				}

			}
		}
		return pixels;
	}
	
	/**
	 * dilation
	 * @param list
	 */
	public static void dilation(List<Point> list) {
		
		List<Point> dilationPoints = new ArrayList<>();
		
		for(Point point: list){
			
			int x = point.getX();
			int y = point.getY();
			//struct element 為3*3 的十字
			historyPoints.add(String.valueOf(x)+"_"+String.valueOf(y));
			
			if(!historyPoints.contains(String.valueOf(x)+"_"+String.valueOf(y-1))){
				dilationPoints.add(new Point(x, y-1));  //上
				historyPoints.add(String.valueOf(x)+"_"+String.valueOf(y-1));
			}
				
			if(!historyPoints.contains(String.valueOf(x)+"_"+String.valueOf(y+1))){
				dilationPoints.add(new Point(x, y+1));  //下
				historyPoints.add(String.valueOf(x)+"_"+String.valueOf(y+1));
			}
			
			if(!historyPoints.contains(String.valueOf(x-1)+"_"+String.valueOf(y))){
				dilationPoints.add(new Point(x-1, y));  //左
				historyPoints.add(String.valueOf(x-1)+"_"+String.valueOf(y));
			}
			if(!historyPoints.contains(String.valueOf(x+1)+"_"+String.valueOf(y))){
				dilationPoints.add(new Point(x+1, y));  //右
				historyPoints.add(String.valueOf(x+1)+"_"+String.valueOf(y));
			}
			
		}
		//將dilation point 加進來
		for(Point p: dilationPoints){
			
			String xy = String.valueOf(p.getX())+"_"+String.valueOf(p.getY());
			
			if(!removePoints.contains(xy)) {
				list.add(p);
			}
		}
	}
	
	/**
	 * intersection
	 * @param list
	 * @param backGroudPixels
	 */
	public static void intersection(List<Point> list, int[][] backGroudPixels) {
		
		Iterator<Point> iterator = list.iterator();
		
		while (iterator.hasNext()) {
			Point p = iterator.next();
			
			if(Color.black.getRGB() != backGroudPixels[p.getY()][p.getX()]) {
				iterator.remove();
				removePoints.add(String.valueOf(p.getX())+"_"+String.valueOf(p.getY()));
			}
		}
	}
	
	/**
	 * dilationPointsToImg
	 * @param list
	 * @param width
	 * @param height
	 * @return
	 */
	public static BufferedImage dilationPointsToImg(List<Point> list, int width, int height){
		
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				image.setRGB(x, y, Color.white.getRGB());
			}
		}
		for (Point point : list) {
			image.setRGB(point.getX(), point.getY(), Color.black.getRGB());
		}

		return image;
	}
}
