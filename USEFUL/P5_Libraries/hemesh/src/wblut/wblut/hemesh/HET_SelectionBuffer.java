/**
 * 
 */
package wblut.hemesh;

import java.util.HashMap;

import processing.core.PGraphics3D;
import processing.core.PImage;

/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class HET_SelectionBuffer extends PGraphics3D {

	/** The current_color. */
	protected int						currentColor;
	protected HashMap<Integer, Integer>	colorToObject;

	/**
	 * Instantiates a new HET_SelectionBuffer.
	 */
	public HET_SelectionBuffer() {
		colorToObject = new HashMap<Integer, Integer>();
		currentColor = -16777216;
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics#displayable()
	 */
	@Override
	public boolean displayable() {
		return true;
	}

	/**
	 * Call check settings.
	 */
	public void callCheckSettings() {
		super.checkSettings();
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics#background(int)
	 */
	@Override
	public void background(final int arg) {
		super.background(0);
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics#background(float)
	 */
	@Override
	public void background(final float arg) {
		super.background(0);
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics#background(float, float)
	 */
	@Override
	public void background(final float arg, final float arg_1) {
		super.background(0);
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics#background(int, float)
	 */
	@Override
	public void background(final int arg, final float arg_1) {
		super.background(0);
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics#background(float, float, float)
	 */
	@Override
	public void background(final float arg, final float arg_1, final float arg_2) {
		super.background(0);
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics#background(float, float, float, float)
	 */
	@Override
	public void background(final float arg, final float arg_1,
			final float arg_2, final float arg_3) {
		super.background(0);
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics#background(processing.core.PImage)
	 */
	@Override
	public void background(final PImage arg) {
		super.background(0);
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics3D#lights()
	 */
	@Override
	public void lights() {
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics3D#smooth()
	 */
	@Override
	public void smooth() {
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics#fill(int)
	 */
	@Override
	public void fill(final int arg) {
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics#fill(float)
	 */
	@Override
	public void fill(final float arg) {
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics#fill(float, float)
	 */
	@Override
	public void fill(final float arg, final float arg_1) {
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics#fill(int, float)
	 */
	@Override
	public void fill(final int arg, final float arg_1) {
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics#fill(float, float, float)
	 */
	@Override
	public void fill(final float arg, final float arg_1, final float arg_2) {
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics#fill(float, float, float, float)
	 */
	@Override
	public void fill(final float arg, final float arg_1, final float arg_2,
			final float arg_3) {
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics#noFill()
	 */
	@Override
	public void noFill() {
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics#stroke(int)
	 */
	@Override
	public void stroke(final int arg) {
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics#stroke(float)
	 */
	@Override
	public void stroke(final float arg) {
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics#stroke(float, float)
	 */
	@Override
	public void stroke(final float arg, final float arg_1) {
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics#stroke(int, float)
	 */
	@Override
	public void stroke(final int arg, final float arg_1) {
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics#stroke(float, float, float)
	 */
	@Override
	public void stroke(final float arg, final float arg_1, final float arg_2) {
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics#stroke(float, float, float, float)
	 */
	@Override
	public void stroke(final float arg, final float arg_1, final float arg_2,
			final float arg_3) {
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics#noStroke()
	 */
	@Override
	public void noStroke() {
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics#textureMode(int)
	 */
	@Override
	public void textureMode(final int arg) {
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics3D#texture(processing.core.PImage)
	 */
	@Override
	public void texture(final PImage arg) {
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics#image(processing.core.PImage, float,
	 * float)
	 */
	@Override
	public void image(final PImage arg, final float arg_1, final float arg_2) {
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics#image(processing.core.PImage, float,
	 * float, float, float)
	 */
	@Override
	public void image(final PImage arg, final float arg_1, final float arg_2,
			final float arg_3, final float arg_4) {
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics#image(processing.core.PImage, float,
	 * float, float, float, int, int, int, int)
	 */
	@Override
	public void image(final PImage arg, final float arg_1, final float arg_2,
			final float arg_3, final float arg_4, final int arg_5,
			final int arg_6, final int arg_7, final int arg_8) {
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics#imageImpl(processing.core.PImage, float,
	 * float, float, float, int, int, int, int)
	 */
	@Override
	protected void imageImpl(final PImage image, final float x1,
			final float y1, final float x2, final float y2, final int u1,
			final int v1, final int u2, final int v2) {
	}

	/**
	 * Set the key.
	 *
	 * @param i new key
	 */
	public void setKey(final Integer i) {
		// ID 0 to 16777214 => COLOR -16777215 to -1 (white)
		// -16777216 is black
		currentColor++;
		colorToObject.put(currentColor, i);
		super.fill(currentColor);
		super.stroke(currentColor);
		super.strokeWeight(2);
	}

	/**
	 * Clear.
	 */
	public void clear() {
		background(0);
		colorToObject.clear();
	}

	/**
	 * Get the key at point in buffer.
	 *
	 * @param x x
	 * @param y y
	 * @return key
	 */
	public Integer getKey(final int x, final int y) {

		super.loadPixels();
		// COLOR -16777216 (black) to -1 => ID -1 (no object) to 16777214

		return colorToObject.get(pixels[y * width + x]);
	}

}
