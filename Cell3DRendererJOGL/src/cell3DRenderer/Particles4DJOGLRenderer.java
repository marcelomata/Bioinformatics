
package cell3DRenderer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mcib3d.geom.Object3D;
import mcib3d.geom.Point3D;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.gl2.GLUgl2;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

public class Particles4DJOGLRenderer extends GLCanvas implements GLEventListener, KeyListener, MouseListener {
	
	private static final long serialVersionUID = 1L;

	private static final String TITLE = "JOGL 2.0 Particles 4D Viewer";
	
	private static final int CANVAS_WIDTH  = 640;
	private static final int CANVAS_HEIGHT = 480;
	private static final int FPS = 60;
	
	private static final float ZERO_F = 0.0f;
	private static final float ONE_F  = 1.0f;
	
	private GLUgl2 glu;
	private GLUT glut;
	
	private int mouseX = CANVAS_WIDTH/2;
	private int mouseY = CANVAS_HEIGHT/2;
	
	private List<List<Particle>> objects4DPerFrame;
	private Map<Integer, List<Particle>> objects4DPerTrack;
	
	private boolean drawTrack = false;
	
//	private MovementAnimatorThread movementAnimatorThread;
	private int maxTime;
	private int maxTrack;
	private int currentTime;
	private int currentTrack;
	private Point3D minPoint;
	private Point3D maxPoint;
	private Camera camera;

	public Particles4DJOGLRenderer(ParticlesObjects objects) {
		this.objects4DPerFrame = objects.getObjectsListFrame();
		this.objects4DPerTrack = objects.getObjectsListTrack();
		this.camera = new Camera();
		this.maxPoint = objects.getMaxPoint();
		this.minPoint = objects.getMinPoint();
		this.currentTime = 0;
		this.currentTrack = 0;
		setMaxTimePosition();
	}
	
	private void setMaxTimePosition() {
		Set<Integer> keys = objects4DPerTrack.keySet();
		
		maxTrack = keys.size();
		maxTime = objects4DPerFrame.size();
		
		camera.initPosition(maxPoint, minPoint);
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		glu = new GLUgl2();
		glut = new GLUT();
		gl.glClearColor(ZERO_F, ZERO_F, ZERO_F, ZERO_F);
		gl.glClearDepth(ONE_F); 
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		gl.glShadeModel(GL2.GL_SMOOTH);
	}
	
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();
	      
		if (height == 0) height = 1;
		float aspect = (float) (camera.getFieldOfViewH()/camera.getFieldOfViewV());
		
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(camera.getFieldOfViewV(), aspect, 0.1, 1500.0);
			 
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		drawScene(drawable.getGL().getGL2());
	}
	
	private void drawScene(GL2 gl) {
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		
		camera.draw(gl);
		
		drawWorldCenter(gl);
		
		drawBoundBoxFan(gl);
		drawBoundBoxLines(gl);
		
		drawTime(gl);
		
//		System.out.println(camera.toString());
//		System.out.println(maxPoint.toString()+" - "+minPoint.toString());
		
	}
	
	private void drawBoundBoxFan(GL2 gl) {
		gl.glPointSize(2f);
		gl.glBegin(GL2.GL_TRIANGLES);
			//back square
			gl.glColor3f(0.2f, 0.2f, 0.2f);
			gl.glVertex3f((float) maxPoint.getX(), (float) maxPoint.getY(), (float) minPoint.getZ());
	      	gl.glVertex3f((float) minPoint.getX(), (float) maxPoint.getY(), (float) minPoint.getZ());
	      	gl.glVertex3f((float) minPoint.getX(), (float) minPoint.getY(), (float) minPoint.getZ());
	      	
	      	gl.glVertex3f((float) minPoint.getX(), (float) minPoint.getY(), (float) minPoint.getZ());
	      	gl.glVertex3f((float) maxPoint.getX(), (float) minPoint.getY(), (float) minPoint.getZ());
	      	gl.glVertex3f((float) maxPoint.getX(), (float) maxPoint.getY(), (float) minPoint.getZ());
	      	
	      	//Up square
	      	gl.glColor3f(0.4f, 0.0f, 0.4f);
			gl.glVertex3f((float) minPoint.getX(), (float) maxPoint.getY(), (float) minPoint.getZ());
	      	gl.glVertex3f((float) maxPoint.getX(), (float) maxPoint.getY(), (float) minPoint.getZ());
	      	gl.glVertex3f((float) minPoint.getX(), (float) maxPoint.getY(), (float) maxPoint.getZ());
	      	
	      	gl.glVertex3f((float) minPoint.getX(), (float) maxPoint.getY(), (float) maxPoint.getZ());
	      	gl.glVertex3f((float) maxPoint.getX(), (float) maxPoint.getY(), (float) maxPoint.getZ());
	      	gl.glVertex3f((float) maxPoint.getX(), (float) maxPoint.getY(), (float) minPoint.getZ());
	      	
	      	//Down square
	      	gl.glColor3f(0.4f, 0.4f, 0.0f);
			gl.glVertex3f((float) minPoint.getX(), (float) minPoint.getY(), (float) minPoint.getZ());
	      	gl.glVertex3f((float) maxPoint.getX(), (float) minPoint.getY(), (float) minPoint.getZ());
	      	gl.glVertex3f((float) minPoint.getX(), (float) minPoint.getY(), (float) maxPoint.getZ());
	      	
	      	gl.glVertex3f((float) minPoint.getX(), (float) minPoint.getY(), (float) maxPoint.getZ());
	      	gl.glVertex3f((float) maxPoint.getX(), (float) minPoint.getY(), (float) maxPoint.getZ());
	      	gl.glVertex3f((float) maxPoint.getX(), (float) minPoint.getY(), (float) minPoint.getZ());
	      	
	      	//Left square
	      	gl.glColor3f(0.0f, 0.4f, 0.4f);
	      	gl.glVertex3f((float) maxPoint.getX(), (float) maxPoint.getY(), (float) maxPoint.getZ());
	      	gl.glVertex3f((float) maxPoint.getX(), (float) maxPoint.getY(), (float) minPoint.getZ());
	      	gl.glVertex3f((float) maxPoint.getX(), (float) minPoint.getY(), (float) maxPoint.getZ());
	      	
	      	gl.glVertex3f((float) maxPoint.getX(), (float) minPoint.getY(), (float) maxPoint.getZ());
	      	gl.glVertex3f((float) maxPoint.getX(), (float) minPoint.getY(), (float) minPoint.getZ());
	      	gl.glVertex3f((float) maxPoint.getX(), (float) maxPoint.getY(), (float) minPoint.getZ());
	      	
	      	//Right square
	      	gl.glColor3f(0.f, 0.0f, 0.4f);
	      	gl.glVertex3f((float) minPoint.getX(), (float) maxPoint.getY(), (float) maxPoint.getZ());
	      	gl.glVertex3f((float) minPoint.getX(), (float) maxPoint.getY(), (float) minPoint.getZ());
	      	gl.glVertex3f((float) minPoint.getX(), (float) minPoint.getY(), (float) maxPoint.getZ());
	      	
	      	gl.glVertex3f((float) minPoint.getX(), (float) minPoint.getY(), (float) maxPoint.getZ());
	      	gl.glVertex3f((float) minPoint.getX(), (float) minPoint.getY(), (float) minPoint.getZ());
	      	gl.glVertex3f((float) minPoint.getX(), (float) maxPoint.getY(), (float) minPoint.getZ());
      	gl.glEnd();
	}
	
	private void drawBoundBoxLines(GL2 gl) {
		gl.glPointSize(2f);
		gl.glBegin(GL2.GL_LINES);
			gl.glColor3f(0.4f, 0.4f, 0.4f);
			
			//back square
	      	gl.glVertex3f((float) maxPoint.getX(), (float) maxPoint.getY(), (float) maxPoint.getZ());
	      	gl.glVertex3f((float) minPoint.getX(), (float) maxPoint.getY(), (float) maxPoint.getZ());
	      	
	      	gl.glVertex3f((float) minPoint.getX(), (float) maxPoint.getY(), (float) maxPoint.getZ());
	      	gl.glVertex3f((float) minPoint.getX(), (float) minPoint.getY(), (float) maxPoint.getZ());
	      	
	      	gl.glVertex3f((float) minPoint.getX(), (float) minPoint.getY(), (float) maxPoint.getZ());
	      	gl.glVertex3f((float) maxPoint.getX(), (float) minPoint.getY(), (float) maxPoint.getZ());
	      	
	      	gl.glVertex3f((float) maxPoint.getX(), (float) minPoint.getY(), (float) maxPoint.getZ());
	      	gl.glVertex3f((float) maxPoint.getX(), (float) maxPoint.getY(), (float) maxPoint.getZ());
			
			// front square
	      	gl.glVertex3f((float) maxPoint.getX(), (float) maxPoint.getY(), (float) minPoint.getZ());
	      	gl.glVertex3f((float) minPoint.getX(), (float) maxPoint.getY(), (float) minPoint.getZ());
	      	
	      	gl.glVertex3f((float) minPoint.getX(), (float) maxPoint.getY(), (float) minPoint.getZ());
	      	gl.glVertex3f((float) minPoint.getX(), (float) minPoint.getY(), (float) minPoint.getZ());
	      	
	      	gl.glVertex3f((float) minPoint.getX(), (float) minPoint.getY(), (float) minPoint.getZ());
	      	gl.glVertex3f((float) maxPoint.getX(), (float) minPoint.getY(), (float) minPoint.getZ());
	      	
	      	gl.glVertex3f((float) maxPoint.getX(), (float) minPoint.getY(), (float) minPoint.getZ());
	      	gl.glVertex3f((float) maxPoint.getX(), (float) maxPoint.getY(), (float) minPoint.getZ());
	      	
	      	// link squares
	      	gl.glVertex3f((float) maxPoint.getX(), (float) maxPoint.getY(), (float) minPoint.getZ());
	      	gl.glVertex3f((float) maxPoint.getX(), (float) maxPoint.getY(), (float) maxPoint.getZ());
	      	
	      	gl.glVertex3f((float) minPoint.getX(), (float) maxPoint.getY(), (float) maxPoint.getZ());
	      	gl.glVertex3f((float) minPoint.getX(), (float) maxPoint.getY(), (float) minPoint.getZ());
	      	
	      	gl.glVertex3f((float) minPoint.getX(), (float) minPoint.getY(), (float) maxPoint.getZ());
	      	gl.glVertex3f((float) minPoint.getX(), (float) minPoint.getY(), (float) minPoint.getZ());
	      	
	      	gl.glVertex3f((float) maxPoint.getX(), (float) minPoint.getY(), (float) maxPoint.getZ());
	      	gl.glVertex3f((float) maxPoint.getX(), (float) minPoint.getY(), (float) minPoint.getZ());
	    gl.glEnd();
	}

	private void drawWorldCenter(GL2 gl) {
		gl.glPointSize(2f);
		gl.glBegin(GL2.GL_LINES);
	      	// X axis
			gl.glColor3f(ONE_F, ZERO_F, ZERO_F);
	      	gl.glVertex3f(ZERO_F, ZERO_F, ZERO_F);
			gl.glVertex3f(ONE_F*300, ZERO_F, ZERO_F);
			
			// Y axis
			gl.glColor3f(ZERO_F, ONE_F, ZERO_F);
			gl.glVertex3f(ZERO_F, ZERO_F, ZERO_F);
			gl.glVertex3f(ZERO_F, ONE_F*300, ZERO_F);
				 
			// Z axis
			gl.glColor3f(ZERO_F, ZERO_F, ONE_F);
			gl.glVertex3f(ZERO_F, ZERO_F, ZERO_F);
			gl.glVertex3f(ZERO_F, ZERO_F, ONE_F*300);
	    gl.glEnd();
	}
	
	int count = 0; //TODO
	int division = 0; //TODO
	List<Object3D> parents = new ArrayList<Object3D>();
	boolean canprint = true;
	private void drawTime(GL2 gl) {
		count = 0;
		division = 0;
		parents.clear();
		
		List<Particle> particles = objects4DPerFrame.get(currentTime);
		Object3D parentObj;
		Particle parent;
		for (Particle particle : particles) {
			if(!particle.isHidden()) {
				particle.draw(gl, glut);
				
				if(drawTrack) {
					drawTrack(gl, particle.getTrack());
				} else {
					drawTrack(gl, currentTrack);
				}
				
				parent = particle.getParent();
				if (parent != null) {
					parentObj = parent.getObject();
					if(parents.contains(parentObj)) {
						division++;
					} else {
						parents.add(parentObj);
					}
				}
				count++;
			}
		}
		
		if(canprint) {
//			System.out.println();
			System.out.println("Frame "+currentTime+" - Particles "+count+" - Divisions "+division+" - Parents "+parents.size());
			canprint = false;
		}
	}
	
	private void drawTrack(GL2 gl, int track) {
		Particle particle2;
		List<Particle> particles = objects4DPerTrack.get(track);
		if(particles != null) {
			for (int i = 1; i < particles.size(); i++) {
				particle2 = particles.get(i);
				if(!particle2.isHidden() && !particle2.getParent().isHidden()) {
					drawLine(gl, particle2.getPosition(), particle2.getParent().getPosition(), particle2.getColor());
				}
			}
		}
	}
	
	private void drawLine(GL2 gl, Point3D p1, Point3D p2, Color c) {
		float x1 = (float) p1.getX();
		float y1 = (float) p1.getY();
		float z1 = (float) p1.getZ();
		float x2 = (float) p2.getX();
		float y2 = (float) p2.getY();
		float z2 = (float) p2.getZ();
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
				canprint = true;
				break;
			case KeyEvent.VK_F:
				currentTime = currentTime >= maxTime-1 ? maxTime-1 : currentTime + 1;
				canprint = true;
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
				camera.reset(maxPoint, minPoint);
				break;
			case KeyEvent.VK_U:
				currentTrack = (currentTrack + 1) % maxTrack;
				break;
			case KeyEvent.VK_D:
				currentTrack = currentTrack > 0 ? currentTrack - 1 : 0;
				break;
			case KeyEvent.VK_T:
				drawTrack = !drawTrack;
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