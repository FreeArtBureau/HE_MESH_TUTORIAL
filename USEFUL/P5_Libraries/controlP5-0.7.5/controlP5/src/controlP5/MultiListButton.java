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

/**
 * Used by MultiList.
 * 
 * @example controllers/ControlP5multiList
 * @nosuperclasses Controller Controller
 */
public class MultiListButton extends Button implements MultiListInterface {
	
	private List<MultiListButton> _myChildren = new ArrayList<MultiListButton>();
	
	private MultiListInterface parent;

	private MultiList root;

	private CRect _myRect;

	protected int _myDirection = ControlP5Constants.RIGHT;
	
	private boolean isUpperCase =true;

	/**
	 * 
	 * @param theProperties ControllerProperties
	 * @param theParent MultiListInterface
	 * @param theRoot MultiList
	 */
	protected MultiListButton(ControlP5 theControlP5, String theName, float theValue, int theX, int theY, int theWidth, int theHeight, MultiListInterface theParent, MultiList theRoot) {
		super(theControlP5, (ControllerGroup<?>) theRoot.getParent(), theName, theValue, theX, theY, theWidth, theHeight);
		parent = theParent;
		root = theRoot;
		updateRect(position.x, position.y, width, height);
		_myCaptionLabel.align(LEFT,CENTER);
	}
	
	public MultiListButton toUpperCase(boolean theValue) {
		isUpperCase = theValue;
		for(MultiListButton c:_myChildren) {
			((MultiListButton)c).toUpperCase(isUpperCase);
		}
		_myCaptionLabel.toUpperCase(isUpperCase);
		return this;
	}

	public void remove() {
		int myYoffset = 0;
		for (int i = 0; i < parent.getChildren().size(); i++) {
			if (parent.getChildren().get(i) == this) {
				myYoffset = height + 1;
			}
			((MultiListButton) parent.getChildren().get(i)).updateLocation(0, -myYoffset);
		}

		if (_myParent != null) {
			removeListener(root);
			_myParent.remove(this);
		}
		if (cp5 != null) {
			removeListener(root);
			cp5.remove(this);
		}
		for (int i = 0; i < _myChildren.size(); i++) {
			((MultiListButton) _myChildren.get(i)).remove();
		}
	}

	public List<MultiListButton> getChildren() {
		return _myChildren;
	}

	public int getDirection() {
		return _myDirection;
	}

	protected void setDirection(int theDirection) {
		_myDirection = theDirection;
	}

	/**
	 * 
	 * @param theX float
	 * @param theY float
	 * @param theW float
	 * @param theH float
	 */
	public void updateRect(float theX, float theY, float theW, float theH) {
		_myRect = new CRect(theX, theY, theW, theH);
	}

	/**
	 * 
	 * @param theX float
	 * @param theY float
	 */
	public void updateLocation(float theX, float theY) {
		position.x += theX;
		position.y += theY;
		updateRect(position.x, position.y, width, height);
		for (int i = 0; i < _myChildren.size(); i++) {
			((MultiListInterface) _myChildren.get(i)).updateLocation(theX, theY);
		}
	}

	/**
	 * set the width of a multlist button.
	 * 
	 * @param theWidth int
	 */
	public MultiListButton setWidth(int theWidth) {
		// negative direction
		int dif = (_myDirection == LEFT) ? theWidth - width : 0;
		width = theWidth;
		updateLocation(-dif, 0);
		return this;
	}

	/**
	 * set the height of a multlist button.
	 * 
	 * @param theHeight int
	 */
	public MultiListButton setHeight(int theHeight) {
		int difHeight = height;
		height = theHeight;
		difHeight = height - difHeight;
		int myYoffset = 0;
		for (int i = 0; i < parent.getChildren().size(); i++) {
			(parent.getChildren().get(i)).updateLocation(0, myYoffset);
			if ((parent.getChildren().get(i)) == this) {
				myYoffset = difHeight;
			}
		}
		updateLocation(0, 0);
		return this;
	}

	/**
	 * add a new button to the sublist of this multilist button.
	 * 
	 * @param theName String
	 * @param theValue int
	 * @return MultiListButton
	 */
	public MultiListButton add(String theName, float theValue) {
		int myHeight = -(height + 1);
		for (int i = 0; i < getChildren().size(); i++) {
			myHeight += (getChildren().get(i)).height + 1;
		}
		// negative direction, this is static now, make it dynamic depending on
		// the
		// location of the list.
		int xx = ((int) position.x + (width + 1));
		MultiListButton b = new MultiListButton(cp5, theName, theValue, xx, (int) position.y + (height + 1) + myHeight, (int) width, (int) height, this, root);
		b.isMoveable = false;
		b.toUpperCase(isUpperCase);
		b.hide();
		cp5.register(null, "", b);
		b.addListener(root);
		_myChildren.add(b);
		updateRect(xx, position.y, width, (height + 1) + myHeight);
		return b;
	}

	/**
	 * 
	 */
	protected void onEnter() {
		if (!root.isUpdateLocation) {
			isActive = true;
			root.occupied(true);
			root.mostRecent = this;
			parent.close(this);
			open();
		}
	}

	/**
	 * 
	 */
	protected void onLeave() {
		if (!parent.observe() && !root.isUpdateLocation && root.mostRecent == this) {
			isActive = false;
			root.occupied(false);
		}
	}

	public void mouseReleasedOutside() {
		// !!! other than in the Button class, calling mouseReleased here
		// conflicts with mouseReleased();
	}

	/**
	 * 
	 * @param theMousePosition CVector3f
	 * @return boolean
	 */
	public boolean observe() {
		return CRect.inside(_myRect, _myControlWindow.mouseX, _myControlWindow.mouseY);
	}

	/**
	 * 
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
	 * 
	 */
	public void close() {
		for (int i = 0; i < _myChildren.size(); i++) {
			((MultiListButton) _myChildren.get(i)).close();
			((MultiListButton) _myChildren.get(i)).hide();
		}
	}

	/**
	 * 
	 */
	public void open() {
		for (int i = 0; i < _myChildren.size(); i++) {
			((MultiListButton) _myChildren.get(i)).show();
		}
	}


}
