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

import java.util.Iterator;
import java.util.List;

import wblut.WB_Epsilon;
import wblut.geom.WB_Point3d;
import wblut.geom.WB_Vector3d;



/**
 * Cylinder.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */

public class HEC_DataCylinder extends HEC_Creator {

	/** Base radius. */
	private double		Ri;

	/** Top Radius. */
	private double		Ro;

	/** Height. */
	private double		H;

	/** Facets. */
	private int			_facets;

	/** Height steps. */
	private int			_steps;

	private boolean		topcap;
	private boolean		bottomcap;
	private double		taper;
	private double[][]	data;
	private boolean		spiky;
	private double		chamfer;
	private int			reduceSteps;
	private int			reduceFacets;

	/**
	 * Instantiates a new cylinder.
	 *
	 */
	public HEC_DataCylinder() {
		super();
		Ri = 0;
		Ro = 0;
		H = 0;
		_facets = 6;
		_steps = 1;
		Z = WB_Vector3d.Y();
		topcap = true;
		bottomcap = true;
		taper = 1.0;
		spiky = false;
		final double chamfer = 0;
		reduceSteps = 1;
		reduceFacets = 1;
	}

	/**
	 * Instantiates a new cylinder. 
	 * 
	 * @param Ri bottom radius
	 * @param Ro top radius
	 * @param H height
	 * @param facets number of facets
	 * @param steps number of height divisions
	 */
	public HEC_DataCylinder(final double Ri, final double Ro, final double H) {
		this();
		this.Ri = Ri;
		this.Ro = Ro;
		this.H = H;
		taper = 1.0;
	}

	/**
	 * Set fixed radius.
	 *
	 * @param R radius
	 * @return self
	 */
	public HEC_DataCylinder setRadius(final double R) {
		Ri = R;
		Ro = R;
		return this;
	}

	/**
	 * Set lower and upper radius.
	 *
	 * @param Ri lower radius
	 * @param Ro upper radius
	 * @return self
	 */
	public HEC_DataCylinder setRadius(final double Ri, final double Ro) {
		this.Ri = Ri;
		this.Ro = Ro;
		return this;
	}

	/**
	 * set height.
	 *
	 * @param H height
	 * @return self
	 */
	public HEC_DataCylinder setHeight(final double H) {
		this.H = H;
		return this;
	}

	/**
	 * Set vertical divisions.
	 *
	 * @param steps vertical divisions
	 * @return self
	 */
	public HEC_DataCylinder setDataFromDouble(final List<double[]> data) {
		this.data = new double[data.size()][];
		for (int i = 0; i < data.size(); i++) {
			this.data[i] = data.get(i);
		}

		_steps = data.get(0).length;
		_facets = data.size();
		return this;
	}

	public HEC_DataCylinder setDataFromDouble(final double[][] data) {
		this.data = new double[data.length][];
		for (int i = 0; i < data.length; i++) {
			this.data[i] = data[i];
		}

		_steps = data[0].length;
		_facets = data.length;
		return this;
	}

	public HEC_DataCylinder setDataFromFloat(final List<float[]> data) {
		this.data = new double[data.size()][];
		for (int i = 0; i < data.size(); i++) {
			final float[] rowdata = data.get(i);
			final int dl = rowdata.length;
			this.data[i] = new double[dl];
			for (int j = 0; j < dl; j++) {
				this.data[i][j] = rowdata[j];
			}
		}
		_steps = data.get(0).length;
		_facets = data.size();
		return this;
	}

	public HEC_DataCylinder setDataFromFloat(final float[][] data) {
		this.data = new double[data.length][];
		for (int i = 0; i < data.length; i++) {
			final float[] rowdata = data[i];
			final int dl = rowdata.length;
			this.data[i] = new double[dl];
			for (int j = 0; j < dl; j++) {
				this.data[i][j] = rowdata[j];
			}
		}
		_steps = data[0].length;
		_facets = data.length;
		return this;
	}

	/**
	 * Set capping options.
	 *
	 * @param topcap create top cap?
	 * @param bottomcap create bottom cap?
	 * @return self
	 */
	public HEC_DataCylinder setCap(final boolean topcap, final boolean bottomcap) {
		this.topcap = topcap;
		this.bottomcap = bottomcap;
		return this;
	}

	public HEC_DataCylinder setTaper(final double t) {
		taper = t;
		return this;

	}

	public HEC_DataCylinder setSpiky(final boolean b) {
		spiky = b;
		return this;

	}

	public HEC_DataCylinder setChamfer(final double d) {
		chamfer = d;
		return this;

	}

	public HEC_DataCylinder setProto(final int reduceSteps,
			final int reduceFacets) {
		this.reduceSteps = reduceSteps;
		this.reduceFacets = reduceFacets;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_Creator#create()
	 */
	@Override
	protected HE_Mesh createBase() {
		if (WB_Epsilon.isZero(Ro)) {
			Ro = 1.0;

		}
		if (WB_Epsilon.isZero(Ri)) {
			Ri = 1.0;

		}
		final int steps = _steps / reduceSteps;
		final int facets = _facets / reduceFacets;
		final double[][] vertices = new double[(steps + 1) * facets][3];
		final double invs = 1.0 / steps;
		for (int i = 0; i < steps + 1; i++) {
			final double R = Ri + Math.pow(i * invs, taper) * (Ro - Ri);
			final double Hj = i * H * invs;
			for (int j = 0; j < facets; j++) {
				vertices[j + i * facets][0] = R
						* Math.cos(2 * Math.PI / facets * j);
				vertices[j + i * facets][2] = R
						* Math.sin(2 * Math.PI / facets * j);
				vertices[j + i * facets][1] = Hj;
			}
		}
		int nfaces = steps * facets;
		int bc = 0;
		int tc = 0;
		if (bottomcap) {
			bc = nfaces;
			nfaces++;
		}
		if (topcap) {
			tc = nfaces;
			nfaces++;
		}
		final int[][] faces = new int[nfaces][];

		if (bottomcap) {
			faces[bc] = new int[facets];
		}
		if (topcap) {
			faces[tc] = new int[facets];
		}
		for (int j = 0; j < facets; j++) {
			if (bottomcap) {
				faces[bc][j] = j;
			}
			if (topcap) {
				faces[tc][facets - 1 - j] = steps * facets + j;
			}
			for (int i = 0; i < steps; i++) {
				faces[j + i * facets] = new int[4];
				faces[j + i * facets][0] = j + i * facets;
				faces[j + i * facets][1] = j + i * facets + facets;
				faces[j + i * facets][2] = ((j + 1) % facets) + facets + i
						* facets;
				faces[j + i * facets][3] = (j + 1) % facets + i * facets;
			}
		}

		final HEC_FromFacelist fl = new HEC_FromFacelist();
		fl.setVertices(vertices).setFaces(faces);
		final HE_Mesh mesh = fl.createBase();
		final Iterator<HE_Face> fItr = mesh.fItr();
		for (int i = 0; i < steps; i++) {
			for (int j = 0; j < facets; j++) {
				final HE_Face currentFace = fItr.next();
				final int currentstep = i * reduceSteps;
				final int currentfacet = j * reduceFacets;
				final double datapoint = data[currentfacet][currentstep];
				if (spiky) {

					final WB_Point3d p = currentFace.getFaceCenter();
					p.add(currentFace.getFaceNormal(), datapoint);
					mesh.triSplitFace(currentFace, p);
				} else {
					System.out
							.println("Currentface: step=" + i + " facet=" + j);
					final HE_Selection sel = new HE_Selection(mesh);
					sel.add(currentFace);
					final HEM_Extrude ef = new HEM_Extrude().setDistance(
							datapoint).setChamfer(chamfer);
					mesh.modifySelected(ef, sel);

				}
			}
		}
		return mesh;

	}

}
