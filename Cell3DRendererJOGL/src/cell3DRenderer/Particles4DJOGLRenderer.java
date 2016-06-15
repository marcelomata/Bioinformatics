
package cell3DRenderer;

import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_TEST;
import static javax.media.opengl.GL.GL_LEQUAL;
import static javax.media.opengl.GL.GL_NICEST;
import static javax.media.opengl.GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SMOOTH;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

import mcib3d.geom.Point3D;

public class Particles4DJOGLRenderer extends GLCanvas implements GLEventListener, KeyListener, MouseListener {
	
	private static final long serialVersionUID = 1L;

	private static final String TITLE = "JOGL 2.0 Particles 4D Viewer";
	
	private static final int CANVAS_WIDTH  = 640;
	private static final int CANVAS_HEIGHT = 480;
	private static final int FPS = 60;
	
	private static final float ZERO_F = 0.0f;
	private static final float ONE_F  = 1.0f;
	
	private GLU glu;
	private GLUT glut;
	
	private int mouseX = CANVAS_WIDTH/2;
	private int mouseY = CANVAS_HEIGHT/2;
	
	private Map<Integer, List<Particle>> objects4D;
	private Map<Integer, Color> particleColor;
	private List<Integer>  colorUsed;
	
//	private MovementAnimatorThread movementAnimatorThread;
	private int maxTime;
	private int currentTime;
	private Point3D minPosition;
	private Camera camera;

	public Particles4DJOGLRenderer(ParticlesObjects objects) {
		this.minPosition = new Point3D(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
		this.objects4D = objects.getObjectsListId();
		this.camera = new Camera();
		this.particleColor = new HashMap<Integer, Color>();
		this.colorUsed = new ArrayList<Integer>();
		this.currentTime = 0;
		setMaxTimePosition();
	}
	
	private void setMaxTimePosition() {
		Set<Integer> keys = objects4D.keySet();
		int max = 0;
		Point3D min = new Point3D(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
		Random random = new Random();
		Color color;
		float r;
		float g;
		float b;
		for (Integer integer : keys) {
			max = objects4D.get(integer).size();
			min = objects4D.get(integer).get(0).getPosition();
			if(max > maxTime) {
				maxTime = max;
			}
			if(min != null) {
				if(min.getX() < minPosition.getX()) {
					minPosition.setX(min.getX());
				}
				if(min.getY() < minPosition.getY()) {
					minPosition.setY(min.getY());
				}
				if(min.getZ() < minPosition.getZ()) {
					minPosition.setZ(min.getZ());
				}
			}
			
			r = (float) (1-(random.nextFloat()*0.7));
			g = (float) (1-(random.nextFloat()*0.7));
			b = (float) (1-(random.nextFloat()*0.7));
			color = new Color(r, g, b);
//			while(colorUsed.contains(color.getRGB())) {
//				r = 255-random.nextInt(55);
//				g = 255-random.nextInt(55);
//				b = 255-random.nextInt(55);
//				color = new Color(r/255, g/255, b/255);
//			}
			colorUsed.add(color.getRGB());
			particleColor.put(integer, color);
		}
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		glu = new GLU();
		glut = new GLUT();
		gl.glClearColor(ZERO_F, ZERO_F, ZERO_F, ZERO_F);
		gl.glClearDepth(ONE_F); 
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
		gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		gl.glShadeModel(GL_SMOOTH);
	}
	
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();
	      
		if (height == 0) height = 1;
		float aspect = (float) width/height;
		
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(45.0, aspect, 0.1, 1000.0);
			 
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		drawScene(drawable.getGL().getGL2());
	}
	
	private void drawScene(GL2 gl) {
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		
		// camera transformations
		gl.glTranslatef((float)camera.getPosition().getX(), (float)camera.getPosition().getY(), (float)camera.getPosition().getZ());
		gl.glRotatef(camera.getAngleX(), ONE_F, ZERO_F, ZERO_F);
		gl.glRotatef(camera.getAngleY(), ZERO_F, ONE_F, ZERO_F);
		gl.glRotatef(camera.getAngleZ(), ZERO_F, ZERO_F, ONE_F);
		
		drawWorldCenter(gl);
		
		drawTime(gl);
		
	}
	
	private void drawWorldCenter(GL2 gl) {
		gl.glPointSize(2f);
		gl.glBegin(GL2.GL_LINES);
	      	// X axis
			gl.glColor3f(ONE_F, ZERO_F, ZERO_F);
	      	gl.glVertex3f(ZERO_F, ZERO_F, ZERO_F);
			gl.glVertex3f(ONE_F*100, ZERO_F, ZERO_F);
			
			// Y axis
			gl.glColor3f(ZERO_F, ONE_F, ZERO_F);
			gl.glVertex3f(ZERO_F, ZERO_F, ZERO_F);
			gl.glVertex3f(ZERO_F, ONE_F*100, ZERO_F);
				 
			// Z axis
			gl.glColor3f(ZERO_F, ZERO_F, ONE_F);
			gl.glVertex3f(ZERO_F, ZERO_F, ZERO_F);
			gl.glVertex3f(ZERO_F, ZERO_F, ONE_F*100);
	    gl.glEnd();
	}
	
//	int count = 0; //TODO
//	double max = 0;
	private void drawTime(GL2 gl) {
		Set<Integer> keys = objects4D.keySet();
		Particle particle;
		Particle particle2;
		Particle lastParticle;
		Particle currentParticle;
		List<Particle> particles;
//		count = 0;
		for (Integer motionTime : keys) {
			particles = objects4D.get(motionTime);
			if(particles.size() > currentTime) {
				particle = particles.get(currentTime);
				if(!particle.isHidden()) {
					particle.draw(gl, glut, minPosition, particleColor.get(motionTime)); //TODO
//					particle.draw(gl, glut, minPosition, Color.BLUE);
//					count++;
				}
//				int time;
//				for (int i = 1; i < 2; i++) { //TODO
//					time = currentTime+i;
//					if(time >= 1 && time < particles.size()) {
//						lastParticle = particles.get(time-1);
//						currentParticle = particles.get(time);
//						if(!lastParticle.isHidden() && !currentParticle.isHidden()) {
//							drawLine(gl, lastParticle.getPosition(), currentParticle.getPosition(), particleColor.get(motionTime));
//						}
//					}
//				}
			}
			
			int newtime = currentTime+1;
			if(particles.size() > newtime) {
				particle2 = particles.get(currentTime+1);
				if(!particle2.isHidden()) {
					particle2.draw(gl, glut, minPosition, particleColor.get(motionTime)); //TODO
//					particle2.draw(gl, glut, minPosition, Color.WHITE);
					lastParticle = particles.get(newtime-1);
					currentParticle = particles.get(newtime);
					if(!lastParticle.isHidden() && !currentParticle.isHidden()) {
						drawLine(gl, lastParticle.getPosition(), currentParticle.getPosition(), particleColor.get(motionTime));
					}
				}
			}
		}
//		System.out.println("\n MAX = "+max+"\n\n###"); //TODO
	}
	
	private void drawLine(GL2 gl, Point3D p1, Point3D p2, Color c) {
		float x1 = (float) (p1.getX() - minPosition.getX());
		float y1 = (float) (p1.getY() - minPosition.getY());
		float z1 = (float) (p1.getZ() - minPosition.getZ());
		float x2 = (float) (p2.getX() - minPosition.getX());
		float y2 = (float) (p2.getY() - minPosition.getY());
		float z2 = (float) (p2.getZ() - minPosition.getZ());
//		double distance = p1.distance(p2);  //TODO
//		if(distance > max) {
//			max = distance;
//		}
//		System.out.println(distance);
		gl.glPointSize(2f);
		gl.glBegin(GL2.GL_LINES);
			gl.glColor3f((float)c.getRed()/255, (float)c.getGreen()/255, (float)c.getBlue()/255);
	      	gl.glVertex3f(x1, y1, z1);
	      	gl.glVertex3f(x2, y2, z2);
	    gl.glEnd();
	}
	
//	private void toggleVideoCells() {
//		if (movementAnimatorThread == null || !movementAnimatorThread.isAlive()) {
//			movementAnimatorThread = new MovementAnimatorThread();
//			movementAnimatorThread.start();
//		} else {
//			movementAnimatorThread.terminate();
//		}
//	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		int orientation = -1;
		if (e.isShiftDown()) {
			orientation = 1;
		}
		switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
				camera.rotateUp();
				break;
			case KeyEvent.VK_DOWN:
				camera.rotateDown();
				break;
			case KeyEvent.VK_LEFT:
				camera.rotateLeft();
				break;
			case KeyEvent.VK_RIGHT:
				camera.rotateRight();
				break;
			case KeyEvent.VK_B:
				currentTime = (currentTime-1) < 0 ? 0 : currentTime - 1;
//				System.out.println(count); //TODO
				break;
			case KeyEvent.VK_F:
				currentTime = (currentTime+1) >= maxTime-1 ? maxTime-1 : currentTime + 1;
//				System.out.println(count); //TODO
				break;
			case KeyEvent.VK_X:
				camera.changeX(orientation);
				break;
			case KeyEvent.VK_Y:
				camera.changeY(orientation);
				break;
			case KeyEvent.VK_Z:
				camera.changeZ(orientation);
				break;
			case KeyEvent.VK_R:
				camera.reset();
				break;
		}
	}
	
	@Override
	public void mouseWheelMoved(MouseEvent e) {
		camera.zoom(e.getRotation()[1]);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		final int buffer = 2;
		
		if (e.getX() < mouseX-buffer) {
			camera.rotateY(-1);
		} else if (e.getX() > mouseX+buffer) {
			camera.rotateY(1);
		}
		
		if (e.getY() < mouseY-buffer) {
			camera.rotateX(-1);
		} else if (e.getY() > mouseY+buffer) {
			camera.rotateX(1);
		}
		
		mouseX = e.getX();
		mouseY = e.getY();
	}
	
	@Override public void dispose(GLAutoDrawable drawable) { }
	@Override public void keyReleased(KeyEvent e) { }
	@Override public void mouseClicked(MouseEvent e) { }
	@Override public void mouseEntered(MouseEvent e) { }
	@Override public void mouseExited(MouseEvent e) { }
	@Override public void mousePressed(MouseEvent e) { }
	@Override public void mouseReleased(MouseEvent e) { }
	@Override public void mouseMoved(MouseEvent e) { }
	
	public void run() {
		GLProfile glp = GLProfile.getDefault();
		GLCapabilities caps = new GLCapabilities(glp);
		GLWindow window = GLWindow.create(caps);
		 
		final FPSAnimator animator = new FPSAnimator(window, FPS, true);
		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowDestroyNotify(WindowEvent e) {
				new Thread() {
					@Override
					public void run() {
						animator.stop();
						System.exit(0);
					}
				}.start();
			};
		});
		 
		window.addGLEventListener(this);
		window.addKeyListener(this);
		window.addMouseListener(this);
		window.setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		window.setTitle(TITLE);
		window.setVisible(true);
		animator.start();	
	}
	
//	private class MovementAnimatorThread extends Thread {
//		private boolean isTerminated = false;
//		
//		int time = 0;
//		
//		protected boolean isComplete() { 
//			return (time == maxTime-1); 
//		}
//		
//		public void terminate() { 
//			isTerminated = true; 
//		}
//		
//		@Override
//		public void run() {
//			while (!isTerminated && !isComplete()) {
////				while (isRotating()) {
//					try { Thread.sleep(10); }
//					catch (InterruptedException e) { }
////				}
////				drawTime(time);
//				time++;
//			}
//		}
//	}
	
}