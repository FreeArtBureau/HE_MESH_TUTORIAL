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
package wblut.math;

import wblut.WB_Epsilon;
import wblut.geom.WB_Normal3d;
import wblut.geom.WB_Point3d;
import wblut.geom.WB_Vector3d;

// TODO: Auto-generated Javadoc
/**
 * 3x3 Matrix.
 *
 * @author Frederik Vanhoutte (W:Blut) 2010
 */
public class WB_M33 {

	/** First row. */
	public double	m11, m12, m13;

	/** Second row. */
	public double	m21, m22, m23;

	/** Third row. */
	public double	m31, m32, m33;

	/**
	 * Instantiates a new WB_M33.
	 */
	public WB_M33() {
	}

	/**
	 * Instantiates a new WB_M33.
	 *
	 * @param matrix33 double[3][3] array of values
	 */
	public WB_M33(final double[][] matrix33) {
		m11 = matrix33[0][0];
		m12 = matrix33[0][1];
		m13 = matrix33[0][2];
		m21 = matrix33[1][0];
		m22 = matrix33[1][1];
		m23 = matrix33[1][2];
		m31 = matrix33[2][0];
		m32 = matrix33[2][1];
		m33 = matrix33[2][2];
	}

	/**
	 * Instantiates a new WB_M33.
	 *
	 * @param m11 m11
	 * @param m12 m12
	 * @param m13 m13
	 * @param m21 m21
	 * @param m22 m22
	 * @param m23 m23
	 * @param m31 m31
	 * @param m32 m32
	 * @param m33 m33
	 */
	public WB_M33(final double m11, final double m12, final double m13,
			final double m21, final double m22, final double m23,
			final double m31, final double m32, final double m33) {
		this.m11 = m11;
		this.m12 = m12;
		this.m13 = m13;
		this.m21 = m21;
		this.m22 = m22;
		this.m23 = m23;
		this.m31 = m31;
		this.m32 = m32;
		this.m33 = m33;
	}

	/**
	 * Set values.
	 *
	 * @param matrix33 double[3][3] array of values
	 */
	public void set(final double[][] matrix33) {
		m11 = matrix33[0][0];
		m12 = matrix33[0][1];
		m13 = matrix33[0][2];
		m21 = matrix33[1][0];
		m22 = matrix33[1][1];
		m23 = matrix33[1][2];
		m31 = matrix33[2][0];
		m32 = matrix33[2][1];
		m33 = matrix33[2][2];
	}

	/**
	 * Set values.
	 *
	 * @param matrix33 float[3][3] array of values
	 */
	public void set(final float[][] matrix33) {
		m11 = matrix33[0][0];
		m12 = matrix33[0][1];
		m13 = matrix33[0][2];
		m21 = matrix33[1][0];
		m22 = matrix33[1][1];
		m23 = matrix33[1][2];
		m31 = matrix33[2][0];
		m32 = matrix33[2][1];
		m33 = matrix33[2][2];
	}

	/**
	 * Set values.
	 *
	 * @param matrix33 int[3][3] array of values
	 */
	public void set(final int[][] matrix33) {
		m11 = matrix33[0][0];
		m12 = matrix33[0][1];
		m13 = matrix33[0][2];
		m21 = matrix33[1][0];
		m22 = matrix33[1][1];
		m23 = matrix33[1][2];
		m31 = matrix33[2][0];
		m32 = matrix33[2][1];
		m33 = matrix33[2][2];
	}

	/**
	 * Set values.
	 *
	 * @param m11 m11
	 * @param m12 m12
	 * @param m13 m13
	 * @param m21 m21
	 * @param m22 m22
	 * @param m23 m23
	 * @param m31 m31
	 * @param m32 m32
	 * @param m33 m33
	 */
	public void set(final double m11, final double m12, final double m13,
			final double m21, final double m22, final double m23,
			final double m31, final double m32, final double m33) {
		this.m11 = m11;
		this.m12 = m12;
		this.m13 = m13;
		this.m21 = m21;
		this.m22 = m22;
		this.m23 = m23;
		this.m31 = m31;
		this.m32 = m32;
		this.m33 = m33;
	}

	/**
	 * Set values.
	 *
	 * @param m matrix WB_M33
	 */
	public void set(final WB_M33 m) {
		m11 = m.m11;
		m12 = m.m12;
		m13 = m.m13;
		m21 = m.m21;
		m22 = m.m22;
		m23 = m.m23;
		m31 = m.m31;
		m32 = m.m32;
		m33 = m.m33;
	}

	/**
	 * Get copy.
	 *
	 * @return copy
	 */
	public WB_M33 get() {
		return new WB_M33(m11, m12, m13, m21, m22, m23, m31, m32, m33);
	}

	/**
	 * Get row as WB_Vector.
	 *
	 * @param i 0,1,2
	 * @return row
	 */
	public WB_Vector3d row(final int i) {
		if (i == 0) {
			return new WB_Vector3d(m11, m12, m13);
		}
		if (i == 1) {
			return new WB_Vector3d(m21, m22, m23);
		}
		if (i == 2) {
			return new WB_Vector3d(m31, m32, m33);
		}
		return null;
	}

	/**
	 * Return row into provided WB_Vector.
	 *
	 * @param i 0,1,2
	 * @param result WB_Point to store the row in
	 */
	public void rowInto(final int i, final WB_Vector3d result) {
		if (i == 0) {
			result.set(m11, m12, m13);
		}
		if (i == 1) {
			result.set(m21, m22, m23);
		}
		if (i == 2) {
			result.set(m31, m32, m33);
		}
		result.set(0, 0, 0);
	}

	/**
	 * Get column as WB_Vector.
	 *
	 * @param i 0,1,2
	 * @return col
	 */
	public WB_Vector3d col(final int i) {
		if (i == 0) {
			return new WB_Vector3d(m11, m21, m31);
		}
		if (i == 1) {
			return new WB_Vector3d(m12, m22, m32);
		}
		if (i == 2) {
			return new WB_Vector3d(m13, m23, m33);
		}
		return null;
	}

	/**
	 * Return col into provided WB_Vector.
	 *
	 * @param i 0,1,2
	 * @param result WB_Point to store the col in
	 */
	public void colInto(final int i, final WB_Vector3d result) {
		if (i == 0) {
			result.set(m11, m21, m31);
		}
		if (i == 1) {
			result.set(m12, m22, m32);
		}
		if (i == 2) {
			result.set(m13, m23, m33);
		}
		result.set(0, 0, 0);
	}

	/**
	 * Add matrix.
	 *
	 * @param m matrix
	 */
	public void add(final WB_M33 m) {
		m11 += m.m11;
		m12 += m.m12;
		m13 += m.m13;
		m21 += m.m21;
		m22 += m.m22;
		m23 += m.m23;
		m31 += m.m31;
		m32 += m.m32;
		m33 += m.m33;
	}

	/**
	 * Subtract matrix.
	 *
	 * @param m matrix
	 */
	public void sub(final WB_M33 m) {
		m11 -= m.m11;
		m12 -= m.m12;
		m13 -= m.m13;
		m21 -= m.m21;
		m22 -= m.m22;
		m23 -= m.m23;
		m31 -= m.m31;
		m32 -= m.m32;
		m33 -= m.m33;
	}

	/**
	 * Multiply with scalar.
	 *
	 * @param f factor
	 */
	public void mult(final double f) {
		m11 *= f;
		m12 *= f;
		m13 *= f;
		m21 *= f;
		m22 *= f;
		m23 *= f;
		m31 *= f;
		m32 *= f;
		m33 *= f;
	}

	/**
	 * Divide by scalar.
	 *
	 * @param f factor
	 */
	public void div(final double f) {
		final double invf = (WB_Epsilon.isZero(f)) ? 0 : 1.0 / f;
		m11 *= invf;
		m12 *= invf;
		m13 *= invf;
		m21 *= invf;
		m22 *= invf;
		m23 *= invf;
		m31 *= invf;
		m32 *= invf;
		m33 *= invf;
	}

	/**
	 * Add matrix into provided matrix.
	 *
	 * @param m matrix
	 * @param result result
	 */
	public void addInto(final WB_M33 m, final WB_M33 result) {
		result.m11 = m11 + m.m11;
		result.m12 = m12 + m.m12;
		result.m13 = m13 + m.m13;
		result.m21 = m21 + m.m21;
		result.m22 = m22 + m.m22;
		result.m23 = m23 + m.m23;
		result.m31 = m31 + m.m31;
		result.m32 = m32 + m.m32;
		result.m33 = m33 + m.m33;
	}

	/**
	 * Subtract matrix into provided matrix.
	 *
	 * @param m matrix
	 * @param result result
	 */
	public void subInto(final WB_M33 m, final WB_M33 result) {
		result.m11 = m11 - m.m11;
		result.m12 = m12 - m.m12;
		result.m13 = m13 - m.m13;
		result.m21 = m21 - m.m21;
		result.m22 = m22 - m.m22;
		result.m23 = m23 - m.m23;
		result.m31 = m31 - m.m31;
		result.m32 = m32 - m.m32;
		result.m33 = m33 - m.m33;
	}

	/**
	 * Multiply with scalar into provided matrix.
	 *
	 * @param f factor
	 * @param result result
	 */
	public void multInto(final double f, final WB_M33 result) {
		result.m11 = f * m11;
		result.m12 = f * m12;
		result.m13 = f * m13;
		result.m21 = f * m21;
		result.m22 = f * m22;
		result.m23 = f * m23;
		result.m31 = f * m31;
		result.m32 = f * m32;
		result.m33 = f * m33;
	}

	/**
	 * Divide with scalar into provided matrix.
	 *
	 * @param f factor
	 * @param result result
	 */
	public void divInto(final double f, final WB_M33 result) {
		final double invf = (WB_Epsilon.isZero(f)) ? 0 : 1.0 / f;
		result.m11 = invf * m11;
		result.m12 = invf * m12;
		result.m13 = invf * m13;
		result.m21 = invf * m21;
		result.m22 = invf * m22;
		result.m23 = invf * m23;
		result.m31 = invf * m31;
		result.m32 = invf * m32;
		result.m33 = invf * m33;
	}

	/**
	 * Multiply matrices into new matrix.
	 *
	 * @param m matrix
	 * @param n matrix
	 * @return result
	 */
	public static WB_M33 mult(final WB_M33 m, final WB_M33 n) {
		return new WB_M33(m.m11 * n.m11 + m.m12 * n.m21 + m.m13 * n.m31, m.m11
				* n.m12 + m.m12 * n.m22 + m.m13 * n.m32, m.m11 * n.m13 + m.m12
				* n.m23 + m.m13 * n.m33, m.m21 * n.m11 + m.m22 * n.m21 + m.m23
				* n.m31, m.m21 * n.m12 + m.m22 * n.m22 + m.m23 * n.m32, m.m21
				* n.m13 + m.m22 * n.m23 + m.m23 * n.m33, m.m31 * n.m11 + m.m32
				* n.m21 + m.m33 * n.m31, m.m31 * n.m12 + m.m32 * n.m22 + m.m33
				* n.m32, m.m31 * n.m13 + m.m32 * n.m23 + m.m33 * n.m33);
	}

	/**
	 * Multiply matrices into provided matrix.
	 *
	 * @param m matrix
	 * @param n matrix
	 * @param result result
	 */
	public static void multInto(final WB_M33 m, final WB_M33 n,
			final WB_M33 result) {
		result.set(m.m11 * n.m11 + m.m12 * n.m21 + m.m13 * n.m31, m.m11 * n.m12
				+ m.m12 * n.m22 + m.m13 * n.m32, m.m11 * n.m13 + m.m12 * n.m23
				+ m.m13 * n.m33, m.m21 * n.m11 + m.m22 * n.m21 + m.m23 * n.m31,
				m.m21 * n.m12 + m.m22 * n.m22 + m.m23 * n.m32, m.m21 * n.m13
						+ m.m22 * n.m23 + m.m23 * n.m33, m.m31 * n.m11 + m.m32
						* n.m21 + m.m33 * n.m31, m.m31 * n.m12 + m.m32 * n.m22
						+ m.m33 * n.m32, m.m31 * n.m13 + m.m32 * n.m23 + m.m33
						* n.m33);
	}

	/**
	 * Multiply with matrix into new matrix.
	 *
	 * @param n matrix
	 * @return result
	 */
	public WB_M33 mult(final WB_M33 n) {
		return new WB_M33(m11 * n.m11 + m12 * n.m21 + m13 * n.m31, m11 * n.m12
				+ m12 * n.m22 + m13 * n.m32, m11 * n.m13 + m12 * n.m23 + m13
				* n.m33, m21 * n.m11 + m22 * n.m21 + m23 * n.m31, m21 * n.m12
				+ m22 * n.m22 + m23 * n.m32, m21 * n.m13 + m22 * n.m23 + m23
				* n.m33, m31 * n.m11 + m32 * n.m21 + m33 * n.m31, m31 * n.m12
				+ m32 * n.m22 + m33 * n.m32, m31 * n.m13 + m32 * n.m23 + m33
				* n.m33);
	}

	/**
	 * Multiply matrix into provided matrix.
	 *
	 * @param n matrix
	 * @param result result
	 */
	public void multInto(final WB_M33 n, final WB_M33 result) {
		result.set(m11 * n.m11 + m12 * n.m21 + m13 * n.m31, m11 * n.m12 + m12
				* n.m22 + m13 * n.m32, m11 * n.m13 + m12 * n.m23 + m13 * n.m33,
				m21 * n.m11 + m22 * n.m21 + m23 * n.m31, m21 * n.m12 + m22
						* n.m22 + m23 * n.m32, m21 * n.m13 + m22 * n.m23 + m23
						* n.m33, m31 * n.m11 + m32 * n.m21 + m33 * n.m31, m31
						* n.m12 + m32 * n.m22 + m33 * n.m32, m31 * n.m13 + m32
						* n.m23 + m33 * n.m33);
	}

	/**
	 * Multiply vector and matrix into new vector.
	 *
	 * @param v vector
	 * @param m matrix
	 * @return result
	 */
	public static WB_Vector3d mult(final WB_Vector3d v, final WB_M33 m) {
		return new WB_Vector3d(v.x * m.m11 + v.y * m.m21 + v.z * m.m31, v.x
				* m.m12 + v.y * m.m22 + v.z * m.m32, v.x * m.m13 + v.y * m.m23
				+ v.z * m.m33);
	}

	/**
	 * Multiply vector and matrix into provided vector.
	 *
	 * @param v vector
	 * @param m matrix
	 * @param result result
	 */
	public static void multInto(final WB_Vector3d v, final WB_M33 m,
			final WB_Vector3d result) {
		result.set(v.x * m.m11 + v.y * m.m21 + v.z * m.m31, v.x * m.m12 + v.y
				* m.m22 + v.z * m.m32, v.x * m.m13 + v.y * m.m23 + v.z * m.m33);
	}

	/**
	 * Multiply matrix and vector into new vector.
	 *
	 * @param m matrix
	 * @param v vector
	 * @return result
	 */
	public static WB_Vector3d mult(final WB_M33 m, final WB_Vector3d v) {
		return new WB_Vector3d(v.x * m.m11 + v.y * m.m12 + v.z * m.m13, v.x
				* m.m21 + v.y * m.m22 + v.z * m.m23, v.x * m.m31 + v.y * m.m32
				+ v.z * m.m33);
	}

	/**
	 * Multiply matrix and vector into provided vector.
	 *
	 * @param m matrix
	 * @param v vector
	 * @param result result
	 */
	public static void multInto(final WB_M33 m, final WB_Vector3d v,
			final WB_Vector3d result) {
		result.set(v.x * m.m11 + v.y * m.m12 + v.z * m.m13, v.x * m.m21 + v.y
				* m.m22 + v.z * m.m23, v.x * m.m31 + v.y * m.m32 + v.z * m.m33);
	}

	/**
	 * Multiply point and matrix into new point.
	 *
	 * @param v point
	 * @param m matrix
	 * @return result
	 */
	public static WB_Point3d mult(final WB_Point3d v, final WB_M33 m) {
		return new WB_Point3d(v.x * m.m11 + v.y * m.m21 + v.z * m.m31, v.x
				* m.m12 + v.y * m.m22 + v.z * m.m32, v.x * m.m13 + v.y * m.m23
				+ v.z * m.m33);
	}

	/**
	 * Multiply vector and matrix into provided point.
	 *
	 * @param v point
	 * @param m matrix
	 * @param result result
	 */
	public static void multInto(final WB_Point3d v, final WB_M33 m,
			final WB_Point3d result) {
		result.set(v.x * m.m11 + v.y * m.m21 + v.z * m.m31, v.x * m.m12 + v.y
				* m.m22 + v.z * m.m32, v.x * m.m13 + v.y * m.m23 + v.z * m.m33);
	}

	/**
	 * Multiply matrix and point into new point.
	 *
	 * @param m matrix
	 * @param v point
	 * @return result
	 */
	public static WB_Point3d mult(final WB_M33 m, final WB_Point3d v) {
		return new WB_Point3d(v.x * m.m11 + v.y * m.m12 + v.z * m.m13, v.x
				* m.m21 + v.y * m.m22 + v.z * m.m23, v.x * m.m31 + v.y * m.m32
				+ v.z * m.m33);
	}

	/**
	 * Multiply matrix and point into provided point.
	 *
	 * @param m matrix
	 * @param v point
	 * @param result result
	 */
	public static void multInto(final WB_M33 m, final WB_Point3d v,
			final WB_Point3d result) {
		result.set(v.x * m.m11 + v.y * m.m12 + v.z * m.m13, v.x * m.m21 + v.y
				* m.m22 + v.z * m.m23, v.x * m.m31 + v.y * m.m32 + v.z * m.m33);
	}

	/**
	 * Multiply normal and matrix into new normal.
	 *
	 * @param v normal
	 * @param m matrix
	 * @return result
	 */
	public static WB_Normal3d mult(final WB_Normal3d v, final WB_M33 m) {
		return new WB_Normal3d(v.x * m.m11 + v.y * m.m21 + v.z * m.m31, v.x
				* m.m12 + v.y * m.m22 + v.z * m.m32, v.x * m.m13 + v.y * m.m23
				+ v.z * m.m33);
	}

	/**
	 * Multiply normal and matrix into provided normal.
	 *
	 * @param v normal
	 * @param m matrix
	 * @param result result
	 */
	public static void multInto(final WB_Normal3d v, final WB_M33 m,
			final WB_Normal3d result) {
		result.set(v.x * m.m11 + v.y * m.m21 + v.z * m.m31, v.x * m.m12 + v.y
				* m.m22 + v.z * m.m32, v.x * m.m13 + v.y * m.m23 + v.z * m.m33);
	}

	/**
	 * Multiply matrix and normal into new normal.
	 *
	 * @param m matrix
	 * @param v normal
	 * @return result
	 */
	public static WB_Normal3d mult(final WB_M33 m, final WB_Normal3d v) {
		return new WB_Normal3d(v.x * m.m11 + v.y * m.m12 + v.z * m.m13, v.x
				* m.m21 + v.y * m.m22 + v.z * m.m23, v.x * m.m31 + v.y * m.m32
				+ v.z * m.m33);
	}

	/**
	 * Multiply matrix and normal into provided normal.
	 *
	 * @param m matrix
	 * @param v normal
	 * @param result result
	 */
	public static void multInto(final WB_M33 m, final WB_Normal3d v,
			final WB_Normal3d result) {
		result.set(v.x * m.m11 + v.y * m.m12 + v.z * m.m13, v.x * m.m21 + v.y
				* m.m22 + v.z * m.m23, v.x * m.m31 + v.y * m.m32 + v.z * m.m33);
	}

	/**
	 * Get determinant of matrix.
	 *
	 * @return determinant
	 */
	public double det() {
		return m11 * (m22 * m33 - m23 * m32) + m12 * (m23 * m31 - m21 * m33)
				+ m13 * (m21 * m32 - m22 * m31);
	}

	/**
	 * Transpose matrix.
	 */
	public void transpose() {
		double tmp = m12;
		m12 = m21;
		m21 = tmp;
		tmp = m13;
		m13 = m31;
		m31 = tmp;
		tmp = m23;
		m23 = m32;
		m32 = tmp;

	}

	/**
	 * Get the transpose.
	 *
	 * @return transposed matrix
	 */
	public WB_M33 getTranspose() {
		return new WB_M33(m11, m21, m31, m12, m22, m32, m13, m23, m33);

	}

	/**
	 * Put transposed matrix into provide matrix.
	 *
	 * @param result  result
	 */
	public void transposeInto(final WB_M33 result) {
		result.set(m11, m21, m31, m12, m22, m32, m13, m23, m33);

	}

	/**
	 * Inverse matrix.
	 *
	 * @return inverse
	 */
	public WB_M33 inverse() {
		final double d = det();
		if (WB_Epsilon.isZero(d)) {
			return null;
		}
		final WB_M33 I = new WB_M33(m22 * m33 - m23 * m32, m13 * m32 - m12
				* m33, m12 * m23 - m13 * m22, m23 * m31 - m21 * m33, m11 * m33
				- m13 * m31, m13 * m21 - m11 * m23, m21 * m32 - m22 * m31, m12
				* m31 - m11 * m32, m11 * m22 - m12 * m21);
		I.div(d);
		return I;

	}

	/**
	 * Cramer rule for solving 3 linear equations and 3 unknowns.
	 *
	 * @param a1 the a1
	 * @param b1 the b1
	 * @param c1 the c1
	 * @param d1 the d1
	 * @param a2 the a2
	 * @param b2 the b2
	 * @param c2 the c2
	 * @param d2 the d2
	 * @param a3 the a3
	 * @param b3 the b3
	 * @param c3 the c3
	 * @param d3 the d3
	 * @return the w b_ vector
	 */
	public static WB_Vector3d Cramer3(final double a1, final double b1,
			final double c1, final double d1, final double a2, final double b2,
			final double c2, final double d2, final double a3, final double b3,
			final double c3, final double d3) {
		final WB_M33 m = new WB_M33(a1, b1, c1, a2, b2, c2, a3, b3, c3);
		final double d = m.det();
		if (WB_Epsilon.isZero(d)) {
			return null;
		}
		m.set(d1, b1, c1, d2, b2, c2, d3, b3, c3);
		final double x = m.det();
		m.set(a1, d1, c1, a2, d2, c2, a3, d3, c3);
		final double y = m.det();
		m.set(a1, b1, d1, a2, b2, d2, a3, b3, d3);
		return new WB_Vector3d(x, y, m.det());
	}

	/**
	 * Symmetric schur2 subfunction of Jacobi().
	 *
	 * @param p the p
	 * @param q the q
	 * @param m the m
	 * @return the double[]
	 */
	private double[] symSchur2(final int p, final int q, final double[][] m) {
		final double[] result = new double[2];
		if (!WB_Epsilon.isZero(WB_Fast.abs(m[p][q]))) {
			final double r = (m[q][q] - m[p][p]) / (2 * m[p][q]);
			double t;
			if (r >= 0) {
				t = 1 / (r + Math.sqrt(1 + r * r));
			} else {
				t = -1 / (-r + Math.sqrt(1 + r * r));
			}
			result[0] = 1 / Math.sqrt(1 + t * t);
			result[1] = t * result[0];
		} else {
			result[0] = 1;
			result[1] = 0;
		}
		return result;

	}

	/**
	 * Jacobi.
	 *
	 * @return the w b_ m33
	 */
	public WB_M33 Jacobi() {
		int i, j, n, p, q;
		double prevoff = 0;
		double[] cs = new double[2];
		final double[][] Jm = new double[][] { { 0, 0, 0 }, { 0, 0, 0 },
				{ 0, 0, 0 } };

		final WB_M33 a = get();
		double[][] am = a.toArray();
		final WB_M33 J = new WB_M33(1, 0, 0, 0, 1, 0, 0, 0, 1);
		WB_M33 JT = new WB_M33();
		final WB_M33 v = new WB_M33(1, 0, 0, 0, 1, 0, 0, 0, 1);
		final int MAX_ITERATIONS = 50;
		for (n = 0; n < MAX_ITERATIONS; n++) {
			p = 0;
			q = 1;
			for (i = 0; i < 3; i++) {
				for (j = 0; j < 3; j++) {
					if (i == j) {
						continue;
					}
					if (WB_Fast.abs(am[i][j]) > WB_Fast.abs(am[p][q])) {
						p = i;
						q = j;
					}
				}
			}
			cs = symSchur2(p, q, am);
			for (i = 0; i < 3; i++) {
				Jm[i][0] = Jm[i][i] = Jm[i][2];
				Jm[i][i] = 1;
			}
			Jm[p][p] = cs[0];
			Jm[p][q] = cs[1];
			Jm[q][p] = -cs[1];
			Jm[q][q] = cs[0];
			J.set(Jm);
			v.mult(J);
			JT = J.getTranspose();
			JT.mult(a);
			JT.mult(J);
			a.set(JT);
			double off = 0;
			am = a.toArray();
			for (i = 0; i < 3; i++) {
				for (j = 0; j < 3; j++) {
					if (i == j) {
						continue;
					}
					off += am[i][j] * am[i][j];
				}
			}
			if (n > 2 && off >= prevoff) {
				return a;
			}

			prevoff = off;
		}
		return a;

	}

	/**
	 * Return matrix as array.
	 *
	 * @return double[3][3]
	 */
	public double[][] toArray() {
		return new double[][] { { m11, m12, m13 }, { m21, m22, m23 },
				{ m31, m32, m33 } };
	}

	/**
	 * Get covariance matrix of an array of WB_Point.
	 *
	 * @param points 
	 * @param numPoints 
	 * @return covariance matrix
	 */
	public static WB_M33 covarianceMatrix(final WB_Point3d[] points,
			final int numPoints) {
		final double oon = 1 / (double) numPoints;
		final WB_Point3d c = new WB_Point3d();
		final WB_Point3d p = new WB_Point3d();
		double e00, e11, e22, e01, e02, e12;
		for (int i = 0; i < numPoints; i++) {
			c.add(points[i]);

		}
		c.mult(oon);
		e00 = e11 = e22 = e01 = e02 = e12 = 0;
		for (int i = 0; i < numPoints; i++) {
			points[i].subInto(c, p);
			e00 += p.x * p.x;
			e11 += p.y * p.y;
			e22 += p.z * p.z;
			e01 += p.x * p.y;
			e02 += p.x * p.z;
			e12 += p.y * p.z;
		}
		final WB_M33 cov = new WB_M33(e00, e01, e02, e01, e11, e12, e02, e12,
				e22);
		cov.mult(oon);
		return cov;
	}

}
