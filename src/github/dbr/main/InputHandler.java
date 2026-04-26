package github.dbr.main;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public abstract class InputHandler implements MouseWheelListener, MouseMotionListener {

	public Point mousePos = new Point(0, 0);
	
	@Override
	public abstract void mouseWheelMoved(MouseWheelEvent e);

	@Override
	public void mouseDragged(MouseEvent e) {
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mousePos.setLocation(e.getX(), e.getY());
	}

}
