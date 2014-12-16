/*
 * Copyright (c) 2010, Frederik Vanhoutte This library is free software; you can
 * redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * http://creativecommons.org/licenses/LGPL/2.1/ This library is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package wblut.hemesh;

import processing.core.PApplet;

/**
 * HET_Selector is used for interactive selection of mesh elements.
 * 
 * Tweaked version of original code by
 * @author nicolas.clavaud <antiplastik@gmail.com>
 *
 */
public class HET_Selector {

	/** calling applet. */
	protected PApplet					home;

	/** Selection buffer. */
	private final HET_SelectionBuffer	buffer;

	/** Last key returned. */
	private Integer						_lastKey;

	/**
	 * Instantiates a new HET_Selector.
	 *
	 * @param home calling applet, typically "this"
	 */
	public HET_Selector(final PApplet home) {
		this.home = home;
		buffer = (HET_SelectionBuffer) home.createGraphics(home.width,
				home.height, "wblut.hemesh.tools.HET_SelectionBuffer");
		buffer.callCheckSettings();
		if (home.recorder == null) {
			home.recorder = buffer;
		}
		buffer.background(0);
		home.registerPre(this);
		home.registerDraw(this);
		_lastKey = -1;
	}

	/**
	 * Pre.
	 */
	public void pre() {
		buffer.beginDraw();
		if (home.recorder == null) {
			home.recorder = buffer;
		}
	}

	/**
	 * Draw.
	 */
	public void draw() {
		buffer.endDraw();
	}

	/**
	 * Clear recording buffer.
	 */
	public void clear() {
		buffer.clear();
	}

	/**
	 * Start recording.
	 *
	 * @param key the key
	 */
	public void start(final Integer key) {
		if (key < 0 || key > 16777214) {
			PApplet.println("[HE_Selector error] start(): ID out of range");
			return;
		}
		if (home.recorder == null) {
			home.recorder = buffer;
		}
		buffer.setKey(key);
	}

	/**
	 * Stop recording.
	 */
	public void stop() {
		home.recorder = null;
	}

	/**
	 * Resume recording.
	 */
	public void resume() {
		if (home.recorder == null) {
			home.recorder = buffer;
		}
	}

	/**
	 * Reads the ID of the object at point (x, y)
	 * -1 means there is no object at this point.
	 *
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @return Object ID
	 */
	public Integer get(final int x, final int y) {
		_lastKey = buffer.getKey(x, y);
		return _lastKey;
	}

	/**
	 * Last key.
	 *
	 * @return key of the last object selected
	 */
	public Integer lastKey() {
		return _lastKey;
	}

	public int bufferSize() {
		return buffer.colorToObject.size();
	}

}
