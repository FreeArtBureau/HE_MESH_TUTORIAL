package controlP5;

/**
 * controlP5 is a processing gui library.
 *
 *  2006-2012 by Andreas Schlegel
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA
 *
 * @author 		Andreas Schlegel (http://www.sojamo.de)
 * @modified	05/30/2012
 * @version		0.7.5
 *
 */

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;

/**
 * A Multilist is a multi-menu-tree controller. see the example for more
 * information and how to 	use.
 * 
 * @example controllers/ControlP5multiList
 * 
 * @nosuperclasses Controller Controller
 */
public class MultiList extends Controller<MultiList> implements MultiListInterface, ControlListener {

	/*
	 * TODO reflection does not work properly. TODO add an option to remove
	 * MultiListButtons
	 */
	
	private List<MultiListButton> _myChildren;
	
	protected Tab _myTab;

	protected boolean isVisible = true;

	private int cnt;

	protected boolean isOccupied;
	
	protected boolean isUpdateLocation = false;

	protected MultiListInterface mostRecent;

	protected CRect _myRect;

	protected int _myDirection = ControlP5Constants.RIGHT;

	public int closeDelay = 30;

	protected int _myDefaultButtonHeight = 10;

	protected boolean isUpperCase = true;
	
	/**
	 * Convenience constructor to extend MultiList.
	 * 
	 * @example use/ControlP5extendController
	 * @param theControlP5
	 * @param theName
	 */
	public MultiList(ControlP5 theControlP5, String theName) {
		this(theControlP5, theControlP5.getDefaultTab(), theName, 0, 0, 99, 19);
		theControlP5.register(theControlP5.papplet, theName, this);
	}
	
	public MultiList(ControlP5 theControlP5, Tab theParent, String theName, int theX, int theY, int theWidth, int theHeight) {
		super(theControlP5, theParent, theName, theX, theY, theWidth, 0);
		_myDefaultButtonHeight = theHeight;
		setup();
	}
	
	public MultiList toUpperCase(boolean theValue) {
		isUpperCase = theValue;
		for(MultiListButton c:_myChildren) {
			c.toUpperCase(isUpperCase);
		}
		return this;
	}
	
	@ControlP5.Invisible
	public void setup() {
		_myChildren = new ArrayList<MultiListButton>();
		mostRecent = this;
		isVisible = true;
		updateRect(position.x, position.y, width, _myDefaultButtonHeight);
	}

	public List<MultiListButton> getChildren() {
		return _myChildren;
	}
	
	
	protected void updateRect(float theX, float theY, float theW, float theH) {
		_myRect = new CRect(theX, theY, theW, theH);
	}
	
	
	public int getDirection() {
		return _myDirection;
	}

	/**
	 * TODO does not work.
	 * 
	 * @param theDirection
	 */
	void setDirection(int theDirection) {
		_myDirection = (theDirection == LEFT) ? LEFT : RIGHT;
		for (int i = 0; i < _myChildren.size(); i++) {
			((MultiListButton) _myChildren.get(i)).setDirection(_myDirection);
		}
	}

	/**
	 * @param theX float
	 * @param theY float
	 */
	@ControlP5.Invisible
	public void updateLocation(float theX, float theY) {
		position.x += theX;
		position.y += theY;
		updateRect(position.x, position.y, width, _myDefaultButtonHeight);
		for (int i = 0; i < _myChildren.size(); i++) {
			((MultiListInterface) _myChildren.get(i)).updateLocation(theX, theY);
		}

	}

	/**
	 * removes the multilist.
	 */
	public void remove() {

		if (_myParent != null) {
			_myParent.remove(this);
		}
		if (cp5 != null) {
			cp5.remove(this);
		}
		for (int i = 0; i < _myChildren.size(); i++) {
			((MultiListButton) _myChildren.get(i)).removeListener(this);
			((MultiListButton) _myChildren.get(i)).remove();
		}
	}

	/**
	 * adds multilist buttons to the multilist.
	 * 
	 * @param theName String
	 * @param theValue int
	 * @return MultiListButton
	 */
	public MultiListButton add(String theName, int theValue) {
		int x = (int) position.x;
		int yy = 0;
		for(MultiListButton c:_myChildren) {
			yy+=c.getHeight()+1;
		}
		int y = (int) position.y + yy;//(_myDefaultButtonHeight + 1) * _myChildren.size();
		MultiListButton b = new MultiListButton(cp5, theName, theValue, x, y, width, _myDefaultButtonHeight, this, this);
		b.toUpperCase(isUpperCase);
		b.isMoveable = false;
		cp5.register(null, "", b);
		b.addListener(this);
		_myChildren.add(b);
		b.show();
		updateRect(position.x, position.y, width, (_myDefaultButtonHeight + 1) * _myChildren.size());
		return b;
	}

	/**
	 * @param theEvent
	 */
	@Override
	@ControlP5.Invisible
	public void controlEvent(ControlEvent theEvent) {
		if (theEvent.getController() instanceof MultiListButton) {
			_myValue = theEvent.getController().getValue();
			ControlEvent myEvent = new ControlEvent(this);
			cp5.getControlBroadcaster().broadcast(myEvent, ControlP5Constants.FLOAT);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@ControlP5.Invisible
	public void draw(PApplet theApplet) {
		update(theApplet);
	}

	/**
	 * 
	 * @param theApplet
	 * @return boolean
	 */
	@ControlP5.Invisible
	public boolean update(PApplet theApplet) {
		if (!isOccupied) {
			cnt++;
			if (cnt == closeDelay) {
				close();
			}
		}

		if (isUpdateLocation) {
			updateLocation((_myControlWindow.mouseX - _myControlWindow.pmouseX), (_myControlWindow.mouseY - _myControlWindow.pmouseY));
			isUpdateLocation = theApplet.mousePressed;
		}

		if (isOccupied) {
			if (theApplet.keyPressed && theApplet.mousePressed) {
				if (theApplet.keyCode == PApplet.ALT) {
					isUpdateLocation = true;
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @param theFlag boolean
	 */
	@ControlP5.Invisible
	public void occupied(boolean theFlag) {
		isOccupied = theFlag;
		cnt = 0;
	}

	/**
	 * @return boolean
	 */
	@ControlP5.Invisible
	public boolean observe() {
		return CRect.inside(_myRect, _myControlWindow.mouseX, _myControlWindow.mouseY);
	}

	/**
	 * @param theInterface MultiListInterface
	 */
	public void close(MultiListInterface theInterface) {
		for (int i = 0; i < _myChildren.size(); i++) {
			if (theInterface != (MultiListInterface) _myChildren.get(i)) {
				((MultiListInterface) _myChildren.get(i)).close();
			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
		for (int i = 0; i < _myChildren.size(); i++) {
			((MultiListInterface) _myChildren.get(i)).close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void open() {
		for (int i = 0; i < _myChildren.size(); i++) {
			((MultiListInterface) _myChildren.get(i)).open();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MultiList setValue(float theValue) {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MultiList update() {
		return setValue(_myValue);
	}

	@Deprecated
	public List<MultiListButton> subelements() {
		return _myChildren;
	}

}
