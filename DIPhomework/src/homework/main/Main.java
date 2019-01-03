package homework.main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.math3.linear.RealVector;
import org.neuroph.imgrec.filter.impl.GaussianNoise;

import homework.bean.Point;
import homework.util.Util;

public class Main {
	
	public static void main(String[] args) throws IOException {
		
		if (args[0].equals("1")) {
			resize();
		} else if (args[0].equals("2")) {
			sptialTransformations();
		} else if (args[0].equals("3")) {
			imageEnhancement();
		} else if (args[0].equals("4")) {
			imageNoiseFilter();
		} else if (args[0].equals("5")) {
			regionFilling();
		}
	}
	
	
	/**
	 * homework-1
	 * resize
	 * @throws IOException
	 */
	public static void resize() throws IOException{
		
		InputStream input = new Main().getClass().getResourceAsStream("/com/source/line.jpg");
        BufferedImage image;
        image = ImageIO.read(input);
        int width = image.getWidth();
        int height = image.getHeight();
        //原圖的pixels
        int[] pixels = Util.getImgPixels(image);
        int count = 0;
        
        for(int i=0; i<height; i++) {
        
           for(int j=0; j<width; j++) {
              pixels[count++] = image.getRGB(j, i);
           }
        }
        //nearestNeighbor
        int[] newPixelsA = Util.nearestNeighbor(pixels, width, height, width*10, height*10);
        Util.pixelsToImg(newPixelsA, width*10, height*10, "A");
        //bilinear
        int[] newPixelsB = Util.bilinear(pixels, width, height, width*10, height*10);
        Util.pixelsToImg(newPixelsB, width*10, height*10, "B");
	}
	
	/**
	 * homework-2
	 * sptialTransformations
	 * @throws IOException
	 */
	public static void sptialTransformations() throws IOException{
		
		//梯形位子四個點到輸出四個點，求解a, b ,c ,d ,e, f, g, h, 
		double[][] xVariable = { { 0, 0, 0, 1 }, { 1200, 0, 0, 1 }, { 0, 1500, 0, 1 }, { 1200, 1500, 1200 * 1500, 1 } };
		double[] xValues = new double[] { 330, 860, 55, 1050 };
		double[][] yVariable = { { 0, 0, 0, 1 }, { 1200, 0, 0, 1 }, { 0, 1500, 0, 1 }, { 1200, 1500, 1200 * 1500, 1 } };
		double[] yValues = new double[] { 400, 350, 1180, 1200 };
		
		//[a, b, c, d]
		RealVector resultX = Util.getFuntonRsult(xVariable, xValues);
		System.out.println(resultX);
		//[e, f, g, h]
		RealVector resultY = Util.getFuntonRsult(yVariable, yValues);
		System.out.println(resultY);

		BufferedImage image;
		
		InputStream input = new Main().getClass().getResourceAsStream("/com/source/S__18522114.jpg");
		image = ImageIO.read(input);
		
		//原圖片大小
		int width;
		int height;
		
		//輸出大小
		 int w2 = 1200, h2= 1500;
		 
		width = image.getWidth();
		height = image.getHeight();

		int[] pixels = Util.getImgPixels(image);

		int[] temp = new int[w2 * h2];

		float x_ratio = ((float)(width-1))/w2 ;
		float y_ratio = ((float)(height-1))/h2 ;
		float x_diff;
		float y_diff;
		float blue;
		float red;
		float green;
		int a, b, c, d, index,gray ;
		
		float x,y;

		int offset =0;
		for (int i = 0; i < h2; i++) {
			for (int j = 0; j < w2; j++) {
				
				float rx =   (float) (resultX.getEntry(0)*j + resultX.getEntry(1)*i+ resultX.getEntry(2)*j*i+ resultX.getEntry(3));
				float ry =   (float) (resultY.getEntry(0)*j + resultY.getEntry(1)*i+ resultY.getEntry(2)*j*i+ resultY.getEntry(3));
				 
				x = (int)(x_ratio * rx) ;
		        y = (int)(y_ratio * ry) ;

				x_diff = (x_ratio * rx) - x ;
				y_diff = (y_ratio * ry) - y;
				
				index = (int) (y * width + x);
				
				a = pixels[index];
				b = pixels[index + 1];
				c = pixels[index + width];
				d = pixels[index + width + 1];

	            // blue element
	            // Yb = Ab(1-w)(1-h) + Bb(w)(1-h) + Cb(h)(1-w) + Db(wh)
	            blue = (a&0xff)*(1-x_diff)*(1-y_diff) + (b&0xff)*(x_diff)*(1-y_diff) +
	                   (c&0xff)*(y_diff)*(1-x_diff)   + (d&0xff)*(x_diff*y_diff);

	            // green element
	            // Yg = Ag(1-w)(1-h) + Bg(w)(1-h) + Cg(h)(1-w) + Dg(wh)
	            green = ((a>>8)&0xff)*(1-x_diff)*(1-y_diff) + ((b>>8)&0xff)*(x_diff)*(1-y_diff) +
	                    ((c>>8)&0xff)*(y_diff)*(1-x_diff)   + ((d>>8)&0xff)*(x_diff*y_diff);

	            // red element
	            // Yr = Ar(1-w)(1-h) + Br(w)(1-h) + Cr(h)(1-w) + Dr(wh)
	            red = ((a>>16)&0xff)*(1-x_diff)*(1-y_diff) + ((b>>16)&0xff)*(x_diff)*(1-y_diff) +
	                  ((c>>16)&0xff)*(y_diff)*(1-x_diff)   + ((d>>16)&0xff)*(x_diff*y_diff);

	            temp[offset++] = 
	                    0xff000000 | // hardcode alpha
	                    ((((int)red)<<16)&0xff0000) |
	                    ((((int)green)<<8)&0xff00) |
	                    ((int)blue) ;
	            
			}
		}

		 Util.pixelsToImg(temp, w2, h2, "C");
	}
	
	/**
	 * homework-3
	 * @throws IOException 
	 */
	public static void imageEnhancement() throws IOException{
		//原圖
		InputStream input = new Main().getClass().getResourceAsStream("/com/source/0.png");
        BufferedImage image, image1;
        image = ImageIO.read(input);
        
        int [][]pixels = null;
        int width = image.getWidth();;
        int height = image.getHeight();
        
        //1.procduce img with laplacian
        pixels = Util.laplacian(image, 1);
        Util.pixelsArrayToImg(pixels, width, height, "1");
        
        //2.procduce img with sobel
        pixels = Util.sobel(image);
        Util.pixelsArrayToSobelImg(pixels, width, height, "2");
        
       
        //3.將2用low pass filter模糊結果去雜訊 
        image = ImageIO.read(new File(System.getProperty("user.dir")+"\\2.png"));
        pixels =Util.lowPassFilter(image);
        Util.pixelsArrayToImg(pixels, width, height, "3");
        
        //4.將3正規化到 0.0~1.0
        image = ImageIO.read(new File(System.getProperty("user.dir")+"\\3.png"));
        pixels = Util.normalization(image);
        Util.pixelsArrayToImg(pixels, width, height, "4");
        
        //5.將4 乘上 1
        image = ImageIO.read(new File(System.getProperty("user.dir")+"\\4.png"));
        image1 = ImageIO.read(new File(System.getProperty("user.dir")+"\\1.png"));
        pixels = Util.multiplyImages(image, image1);
        Util.pixelsArrayToImg(pixels, width, height, "5");
        
        //將5加上 原圖
        image = ImageIO.read(new File(System.getProperty("user.dir")+"\\5.png"));
        input = new Main().getClass().getResourceAsStream("/com/source/0.png");
        image1 = ImageIO.read(new Main().getClass().getResourceAsStream("/com/source/0.png"));
        pixels = Util.plusImages(image, image1);
        Util.pixelsArrayToImg(pixels, width, height, "6");
        
        //最後用gammaCorrection
        image = ImageIO.read(new File(System.getProperty("user.dir")+"\\6.png"));
        pixels = Util.gammaCorrection(image, 255, 0.2f);
        Util.pixelsArrayToImg(pixels, width, height, "7");
        
        
	}
	
	/**
	 * homework4
	 * @throws IOException
	 */
	public static void imageNoiseFilter() throws IOException{
		//https://homepages.inf.ed.ac.uk/rbf/HIPR2/median.htm
		//mage Processing - 中值濾波(Median Filter)
		//http://honglung.pixnet.net/blog/post/85115497-image-processing---%E4%B8%AD%E5%80%BC%E6%BF%BE%E6%B3%A2(median-filter)
		InputStream input = new Main().getClass().getResourceAsStream("/com/source/0.png");
		BufferedImage image = ImageIO.read(input); 
	    int [][]pixels = null;
        int width = image.getWidth();;
        int height = image.getHeight();
        
    	//Gaussian Noise
		GaussianNoise gaussian = new GaussianNoise();
		gaussian.setMean(0);
		gaussian.setSigma(20);
		BufferedImage img = gaussian.processImage(image);
		File f = new File(System.getProperty("user.dir")+"\\1.png");
		ImageIO.write(img, "png", f);
		System.out.println("output path : "+f.getAbsolutePath());
		
		//saltAndPepper  黑色10%  白色10% 
		image = Util.saltAndPepper(image, 0.1, 0.1);
		f = new File(System.getProperty("user.dir")+"\\2.png");
		ImageIO.write(image, "png", f);
		System.out.println("output path : "+f.getAbsolutePath());
		
		//mean filter
		pixels = Util.lowPassFilter(image);
		Util.pixelsArrayToImg(pixels, width, height, "3");
		
		//median filter
		pixels = Util.medianFilter(image);
		Util.pixelsArrayToImg(pixels, width, height, "4");
		
		//alpha trim filter 
		pixels = Util.alphaFilter(image);
		Util.pixelsArrayToImg(pixels, width, height, "5");
		input.close();
	}
	
	/**
	 * homework5
	 * @throws IOException
	 */
	public static void regionFilling() throws IOException{
		InputStream input = new Main().getClass().getResourceAsStream("/com/source/I.png");
		BufferedImage image = ImageIO.read(input); 
	    int [][]pixels = null;
	    int [][]backGroudPixels = null;
        int width = image.getWidth();;
        int height = image.getHeight();
        List<Point> list = new ArrayList<>();
        int oldSize = 0;
        
        //取原圖的補集
        backGroudPixels = Util.getBackGroud(image);
    	Util.pixelsArrayToImg(backGroudPixels, width, height, "backgroud");
    	
    	//先選定起啟位置
    	list.add(new Point(15, 11));
    	
    	//filling
    	for(int i=1; i<width*height-1; i++){
    		//dilation
        	Util.dilation(list);
        	//dilation points 與原圖補集做交集
        	Util.intersection(list, backGroudPixels);
        	
        	//每十次就輸出圖片
        	if(i%10 == 0){
        		//dilationPointsToImg
        		image = Util.dilationPointsToImg(list, width, height);
        		File f = new File(System.getProperty("user.dir")+"\\"+i+".png");
        		ImageIO.write(image, "png", f);
        		System.out.println("output path : "+f.getAbsolutePath());
        	}
        	
        	if(oldSize == list.size()) {
        		break;
        	}
        	oldSize = list.size();
    	}
    	
    	//dilationPointsToImg
    	image = Util.dilationPointsToImg(list, width, height);
		File f = new File(System.getProperty("user.dir")+"\\O.png");
		ImageIO.write(image, "png", f);
		System.out.println("output path : "+f.getAbsolutePath());
    	
		//做相乘
		image = ImageIO.read(new File(System.getProperty("user.dir") + "\\O.png"));
		BufferedImage image1 = ImageIO.read(new Main().getClass().getResourceAsStream("/com/source/I.png"));
		pixels = Util.multiplyImages(image1, image);
		
		Util.pixelsArrayToImg(pixels, width, height, "finish");
        input.close();
	}
}
