/*
 * Author: Cameron Dykstra
 * Email: kramin42@gmail.com
 * 
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY 
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE.
 */

import java.applet.Applet;
import java.awt.AlphaComposite;
import java.awt.Event;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

public class g extends Applet implements Runnable, KeyListener, MouseListener, MouseMotionListener {

	public void start() {
		new Thread(this).start();
	}
	
	Random rand = new Random();

	int mx = 0, my = 0;
	boolean mouseDown = false;
	
	boolean antialiasing=true;
	//boolean trailTransperancy = true;
	
	FontMetrics fm;
	Rectangle2D rect;
	
	DecimalFormat df = new DecimalFormat();
	
	// fonts
	Font normalFont = new Font("Ariel", Font.PLAIN, 12);
	Font largeFont = new Font("Ariel", Font.PLAIN, 60);
	Font mediumFont = new Font("Ariel", Font.PLAIN, 30);
	Font hugeFont = new Font("Ariel", Font.PLAIN, 120);
	
	//Colours
	Color clrBG = Color.black;
	Color clrText = Color.white;
	Color clrEarth = Color.blue;
	Color clrMoon = Color.yellow;
	Color clrRocket = Color.red;
	Color clrGreen = new Color(0, 128, 0, 255);
	Color clrOrange = new Color(192, 96, 0, 255);
	Color clrRed = new Color(128, 0, 0, 255);
	
	Color clrLines = Color.darkGray;
	Color clrTextRed = Color.red;
	Color clrTextBlue = Color.blue;
	Color clrBtnBG = new Color(0x8040FF00,true);
	
	double radEarth = 12;
	double radMoon = 3;
	double posEarthX = 400, posEarthY = 300;
	double posMoonX = 400, posMoonY = 50;
	
	double velMoonX = 0.6, velMoonY = 0;
	
	double massEarth = 100;
	double massMoon = 1;
	double gravityConst = 0.9;
	
	double radRocket = 1.5;
	double posRocketX = 400, posRocketY = 300 - 16;
	double velRocketX = Math.sqrt(5.625), velRocketY = 0;
	
	double thrust = 0.01;
	double time = 10;
	
	boolean reset = false;
	boolean gameover = false;
	boolean crashed = false;
	boolean reachedMoon = false;
	boolean reachedEarth = false;
	
	int nearMoonTime = 0;
	int nearEarthTime = 0;
	double radMoonOrbit = radMoon+2;
	double radEarthOrbit = radEarth+8;
	
	//fire particles
	ArrayList<Float> fpx = new ArrayList<Float>();
	ArrayList<Float> fpy = new ArrayList<Float>();
	ArrayList<Float> fvx = new ArrayList<Float>();
	ArrayList<Float> fvy = new ArrayList<Float>();
	ArrayList<Float> falpha = new ArrayList<Float>();
	float falphaMult = 0.96f;
	float frandvMult = 0.2f;
	int numParticles = 4;
	float fv = 1.0f;
	

	public void run() {
		int w = 800, h = 600;
		setSize(w, h); // For AppletViewer, remove later.

		// Set up the graphics stuff, double-buffering.
		BufferedImage screen = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = (Graphics2D) screen.getGraphics();
		Graphics2D appletGraphics = (Graphics2D) getGraphics();
		
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		
		// Some variables to use for the fps.
		int tick = 0, fps = 0, acc = 0;
		long lastTime = System.nanoTime();
		
		df.setMinimumFractionDigits(2);
		df.setMaximumFractionDigits(2);

		// Game loop
		while (true) {
			
			
			long now = System.nanoTime();
			acc += now - lastTime;
			tick++;
			if (acc >= 1000000000L) {
				acc -= 1000000000L;
				fps = tick;
				tick = 0;
			}
			
			//metagame logic
			if (reset){
				double moonAngle = 2*Math.PI*rand.nextDouble();
				double rocketAngle = 2*Math.PI*rand.nextDouble();
				posMoonX = 400 + 250*Math.sin(moonAngle);
				posMoonY = 300 + 250*Math.cos(moonAngle);
				velMoonX = -0.6*Math.cos(moonAngle);
				velMoonY = 0.6*Math.sin(moonAngle);
				posRocketX = 400 + 16*Math.sin(rocketAngle);
				posRocketY = 300 + 16*Math.cos(rocketAngle);
				velRocketX = -Math.sqrt(5.625)*Math.cos(rocketAngle);
				velRocketY = Math.sqrt(5.625)*Math.sin(rocketAngle);
				time = 10;
				reset = false;
				gameover = false;
				crashed = false;
				reachedMoon = false;
				reachedEarth = false;
				nearMoonTime = 0;
				nearEarthTime = 0;
				fpx.clear();
				fpy.clear();
				fvx.clear();
				fvy.clear();
				falpha.clear();
			}

			//game update
			
			if (!crashed){
				//update the fire particles
				for (int i=0; i<fpx.size(); i++){
					fpx.set(i, fpx.get(i)+fvx.get(i));
					fpy.set(i, fpy.get(i)+fvy.get(i));
					falpha.set(i, falpha.get(i)*falphaMult);
					if (falpha.get(i)<0.02){
						fpx.remove(i);
						fpy.remove(i);
						fvx.remove(i);
						fvy.remove(i);
						falpha.remove(i);
						i--;
					}
				}
				
				//update the moon
				double EMdist = Math.sqrt((posEarthX-posMoonX)*(posEarthX-posMoonX) + (posEarthY-posMoonY)*(posEarthY-posMoonY));
				double accMoonMag = -gravityConst*massEarth/(EMdist*EMdist);
				double accMoonX = accMoonMag*(posMoonX-posEarthX)/EMdist;
				double accMoonY = accMoonMag*(posMoonY-posEarthY)/EMdist;
				velMoonX += accMoonX;
				velMoonY += accMoonY;
				posMoonX += velMoonX;
				posMoonY += velMoonY;
				
				//update the rocket
				double ERdist = Math.sqrt((posEarthX-posRocketX)*(posEarthX-posRocketX) + (posEarthY-posRocketY)*(posEarthY-posRocketY));
				double MRdist = Math.sqrt((posMoonX-posRocketX)*(posMoonX-posRocketX) + (posMoonY-posRocketY)*(posMoonY-posRocketY));
				//System.out.println(ERdist);
				double accRocketEarthMag = -gravityConst*massEarth/(ERdist*ERdist);
				double accRocketMoonMag = -gravityConst*massMoon/(MRdist*MRdist);
				
				double accThrusters = !gameover && mouseDown && time > 0 ? thrust : 0;
				double mouseDist = Math.sqrt((mx-posRocketX)*(mx-posRocketX) + (my-posRocketY)*(my-posRocketY));
				double accRocketX = accRocketEarthMag*(posRocketX-posEarthX)/ERdist + accRocketMoonMag*(posRocketX-posMoonX)/MRdist + accThrusters*(mx-posRocketX)/mouseDist;
				double accRocketY = accRocketEarthMag*(posRocketY-posEarthY)/ERdist + accRocketMoonMag*(posRocketY-posMoonY)/MRdist + accThrusters*(my-posRocketY)/mouseDist;
				//System.out.println("MRdist: "+MRdist+", accRocketX: "+accRocketX + ", accRocketY: " + accRocketY);
				velRocketX += accRocketX;
				velRocketY += accRocketY;
				posRocketX += velRocketX;
				posRocketY += velRocketY;
				
				if (!gameover && mouseDown && time > 0) {//create particles
					for (int i=0; i<numParticles; i++){
						fpx.add((float)posRocketX);
						fpy.add((float)posRocketY);
						fvx.add((float) (velRocketX+fv*(posRocketX-mx)/mouseDist+frandvMult*(rand.nextFloat()-0.5f)));
						fvy.add((float) (velRocketY+fv*(posRocketY-my)/mouseDist+frandvMult*(rand.nextFloat()-0.5f)));
						falpha.add(1.0f);
					}
				}
				
				time -= !gameover && mouseDown && time > 0 ? 1/60.0 : 0;
				
				if (!reachedMoon){
					if (MRdist<=radMoonOrbit){
						nearMoonTime++;
					} else {
						nearMoonTime = 0;
					}
					if (nearMoonTime >= 600){
						reachedMoon = true;
						//System.out.println("reached Moon");
					}
				} else if (!reachedEarth){
					if (ERdist<=radEarthOrbit){
						nearEarthTime++;
					} else {
						nearEarthTime = 0;
					}
					if (nearEarthTime >= 600){
						reachedEarth = true;
						gameover = true;
						//System.out.println("reached Earth");
					}
				}
				
				if (ERdist < radEarth || MRdist < radMoon){
					gameover = true;
					crashed = true;
				}
			}
			
			//prevent -0
			if (time <= 0){
				time = 0;
			}

			lastTime = now;

			// Render
			
			if (antialiasing)
			{
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			}
			else
			{
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			}

			// draw the background
			// g2d.drawImage(background, 0, 0, w-1, h-1, this);
			g2d.setColor(clrBG);
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			g2d.fillRect(0, 0, w, h);
			
			//g2d.setColor(clrLines);
			//g2d.drawOval((int) posEarthX - 250, (int) posEarthY - 250, 500, 500);
			
			//draw fire particles
			g2d.setColor(Color.red);
			for (int i=0; i<fpx.size(); i++){
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, falpha.get(i)));
				g2d.drawLine(fpx.get(i).intValue(), fpy.get(i).intValue(), fpx.get(i).intValue(), fpy.get(i).intValue());
			}
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			
			g2d.setColor(clrEarth);
			g2d.fill(new Ellipse2D.Float((float) (posEarthX-radEarth), (float) (posEarthY-radEarth), (float) (2*radEarth), (float) (2*radEarth)));
			
			g2d.setColor(clrMoon);
			g2d.fill(new Ellipse2D.Float((float) (posMoonX-radMoon), (float) (posMoonY-radMoon), (float) (2*radMoon), (float) (2*radMoon)));
			
			g2d.setColor(clrRocket);
			g2d.fill(new Ellipse2D.Float((float) (posRocketX-radRocket), (float) (posRocketY-radRocket), (float) (2*radRocket), (float) (2*radRocket)));
			
			//draw time and time bar
			fm   = g2d.getFontMetrics(mediumFont);
			rect = fm.getStringBounds(df.format(time), g2d);
			g2d.setFont(mediumFont);
			if (time >= 5){
				g2d.setColor(clrGreen);
			} else if (time >= 2.5){
				g2d.setColor(clrOrange);
			} else {
				g2d.setColor(clrRed);
			}
			g2d.fillRect(0, 0, (int) (w*time/10.0), (int) (rect.getHeight()));
			g2d.setColor(clrText);
			g2d.drawString(df.format(time), (int)(w/2 - rect.getWidth()/2), (int)(fm.getAscent()));
			
			//draw orbit progress
			if (nearMoonTime>0){
				g2d.setColor(clrMoon);
				g2d.fillRect(0, h-10, (int) (nearMoonTime*w/600.0), 10);
			}
			if (nearEarthTime>0){
				g2d.setColor(clrEarth);
				g2d.fillRect(0, h-20, (int) (nearEarthTime*w/600.0), 10);
			}
			
			//gameover message
			if (gameover){
				g2d.setColor(clrText);
				if (crashed){
					fm   = g2d.getFontMetrics(largeFont);
					rect = fm.getStringBounds("You Crashed!", g2d);
					g2d.setFont(largeFont);
					g2d.drawString("You Crashed!", (int)(w/2 - rect.getWidth()/2), (int)(150));
				} else {
					fm   = g2d.getFontMetrics(largeFont);
					rect = fm.getStringBounds("Success!", g2d);
					g2d.setFont(largeFont);
					g2d.drawString("Success!", (int)(w/2 - rect.getWidth()/2), (int)(150));
				}
				fm   = g2d.getFontMetrics(mediumFont);
				rect = fm.getStringBounds("Press 'r' to play again", g2d);
				g2d.setFont(mediumFont);
				g2d.drawString("Press 'r' to play again", (int)(w/2 - rect.getWidth()/2), (int)(400));
			}
			
			//g2d.setFont(normalFont);
			//g2d.setColor(clrText);
			//g2d.drawString("FPS: "+fps, 0, 10);
			// Draw the entire results on the screen.
			appletGraphics.drawImage(screen, 0, 0, null);
			

			do {
				Thread.yield();
			} while (System.nanoTime() - lastTime < 16000000L);

			if (!isActive()) {
				return;
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mx = e.getX();
		my = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mx = e.getX();
		my = e.getY();
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1){
			mouseDown = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1){
			mouseDown = false;
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()){
		case KeyEvent.VK_R:
			reset = true;
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}
}