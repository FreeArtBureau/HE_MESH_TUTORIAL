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

import processing.core.PApplet;

/**
 * use the Canvas class instead to draw your custom graphics into the
 * default sketch or a ControlWindow.
 * 
 */
@Deprecated
public abstract class ControlCanvas {

	public final static int PRE = 0;
	
	public final static int POST = 1;
	
	protected int _myMode = PRE;

	public ControlCanvas() {
	}

	/**
	 * the setup method can be, but doesnt have to be overwritten by your custom
	 * canvas class.
	 * 
	 * @param theApplet
	 */
	public void setup(PApplet theApplet) {
	}

	/**
	 * controlCanvas is an abstract class and therefore needs to be extended by
	 * your class. draw(PApplet theApplet) is the only method that needs to be
	 * overwritten.
	 */
	public abstract void draw(PApplet theApplet);

	/**
	 * get the drawing mode of a ControlWindowCanvas. this can be PRE or POST.
	 * 
	 * @return
	 */
	public final int mode() {
		return _myMode;
	}

	/**
	 * set the drawing mode to PRE. PRE is the default.
	 */
	public final void pre() {
		setMode(PRE);
	}

	/**
	 * set the drawing mode to POST.
	 */
	public final void post() {
		setMode(POST);
	}

	/**
	 * 
	 * @param theMode
	 */
	public final void setMode(int theMode) {
		if (theMode == PRE) {
			_myMode = PRE;
		} else {
			_myMode = POST;
		}
	}

}
