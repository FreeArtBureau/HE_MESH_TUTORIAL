package wblut.hemesh;

import wblut.geom.WB_Point3d;
import wblut.math.WB_Function2D;



/**
 * Creates a new mesh from a parametric uv equation.
 * 
 * @author David Bollinger
 * 
 */
public class HEC_UVParametric extends HEC_FromFacelist { // Creator {

	/**
	 * Number of subdivisions along u-axis
	 */
	protected int						usteps;
	/**
	 * Number of subdivisions along v-axis
	 */
	protected int						vsteps;

	protected boolean					uWrap;
	protected boolean					vWrap;

	/**
	 * Scale factor
	 */
	protected double					radius;

	/**
	 * The parametric evaluator
	 */
	protected WB_Function2D<WB_Point3d>	evaluator;

	/**
	 * Instantiates a new HEC_UVParametric
	 *
	 */
	public HEC_UVParametric() {
		super();
		override = true;
		usteps = 32;
		vsteps = 32;
		radius = 1.0;
		uWrap = false;
		vWrap = false;
	}

	/**
	 * Sets the evaluator.
	 *
	 * @param eval an implementation of HET_UVEvaluator
	 * @return self
	 */
	public HEC_UVParametric setEvaluator(final WB_Function2D<WB_Point3d> eval) {
		evaluator = eval;
		return this;
	}

	/**
	 * Sets the number of subdivisions along u/v axes.
	 *
	 * @param usteps the number of subdivisions along u-axis
	 * @param vsteps the number of subdivisions along v-axis
	 * @return self
	 */
	public HEC_UVParametric setUVSteps(final int usteps, final int vsteps) {
		this.usteps = (usteps > 1) ? usteps : 32;
		this.vsteps = (usteps > 1) ? vsteps : 32;
		return this;
	}

	/**
	 * Sets the scale factor.
	 *
	 * @param r
	 * @return self
	 */
	public HEC_UVParametric setRadius(final double r) {
		radius = r;
		return this;
	}

	/**
	 * is u a periodic parameter?
	 *
	 * @param b true/false
	 * @return self
	 */
	public HEC_UVParametric setUWrap(final boolean b) {
		uWrap = b;
		return this;
	}

	/**
	 * is v a periodic parameter?
	 *
	 * @param b true/false
	 * @return self
	 */
	public HEC_UVParametric setVWrap(final boolean b) {
		vWrap = b;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_Creator#create()
	 */
	@Override
	protected HE_Mesh createBase() {
		if (evaluator != null) {
			final int lusteps = (uWrap) ? usteps : usteps + 1;
			final int lvsteps = (vWrap) ? vsteps : vsteps + 1;
			int N = lusteps * lvsteps;
			final WB_Point3d[] vertices = new WB_Point3d[N];
			int index = 0;
			for (int iv = 0; iv < lvsteps; iv++) {
				final double v = (double) (iv) / (double) (vsteps);
				for (int iu = 0; iu < lusteps; iu++) {
					final double u = (double) (iu) / (double) (usteps);
					vertices[index] = evaluator.f(u, v);
					vertices[index].scale(radius);
					index++;
				} // for iu
			} // for iv

			N = usteps * vsteps;
			final int[][] faces = new int[N][4];
			index = 0;
			for (int iv = 0; iv < vsteps; iv++) {
				for (int iu = 0; iu < usteps; iu++) {
					faces[index][0] = iv * lusteps + iu;
					faces[index][1] = iv * lusteps + ((iu + 1) % lusteps);
					faces[index][2] = ((iv + 1) % lvsteps) * lusteps
							+ ((iu + 1) % lusteps);
					faces[index][3] = ((iv + 1) % lvsteps) * lusteps + iu;
					index++;
				} // for iu
			} // for iv
			this.setVertices(vertices).setFaces(faces).setDuplicate(true);
			return super.createBase();
		}
		return null;
	}
}
