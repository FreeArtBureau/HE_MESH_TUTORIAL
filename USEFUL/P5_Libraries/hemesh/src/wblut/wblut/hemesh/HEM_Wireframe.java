/**
 * 
 */
package wblut.hemesh;

import wblut.geom.WB_Frame;
import wblut.math.WB_ConstantParameter;
import wblut.math.WB_Parameter;



/**
 * @author Frederik Vanhoutte, W:Blut
 * Tries to turn a mesh into a wireframe structure. Edges are replaced by cylindrical struts with a 
 * choosen number of facets. The joints are convex hulls formed by the endpoints of the incoming struts.
 *
 */
public class HEM_Wireframe extends HEM_Modifier {
	private WB_Parameter<Double>	strutR;
	private WB_Parameter<Double>	maxStrutOffset;
	private int						facetN;
	private WB_Parameter<Double>	angleFactor;
	private double					fillFactor;
	private WB_Frame				frame;
	private double					fidget;
	private boolean					cap;
	private boolean					taper;

	public HEM_Wireframe() {
		facetN = 4;
		angleFactor = new WB_ConstantParameter<Double>(0.5);
		fidget = 1.0001;
		fillFactor = 0.99;
		maxStrutOffset = new WB_ConstantParameter<Double>(Double.MAX_VALUE);
		cap = true;
		taper = false;
	}

	/**
	 * Set the radius of the connections
	 * @param r
	 * @return self
	 */
	public HEM_Wireframe setStrutRadius(final double r) {
		strutR = new WB_ConstantParameter<Double>(r);
		return this;
	}

	/**
	 * Set the radius of the connections
	 * @param r
	 * @return self
	 */
	public HEM_Wireframe setStrutRadius(final WB_Parameter<Double> r) {
		strutR = r;
		return this;
	}

	/**
	 * Set maximum strut offset
	 * @param r
	 * @return self
	 */
	public HEM_Wireframe setMaximumStrutOffset(final double r) {
		maxStrutOffset = new WB_ConstantParameter<Double>(r);
		return this;
	}

	/**
	 * Set maximum strut offset
	 * @param r
	 * @return self
	 */
	public HEM_Wireframe setMaximumStrutOffset(final WB_Parameter<Double> r) {
		maxStrutOffset = r;
		return this;
	}

	/**
	 * Set number of facets
	 * @param N
	 * @return self
	 */
	public HEM_Wireframe setStrutFacets(final int N) {
		facetN = N;
		return this;
	}

	/**
	 * Offset of facets in fraction of angle
	 * @param af offset as a fraction, ex. 0.5 will rotate the connections half a facet
	 * @return self
	 */
	public HEM_Wireframe setAngleOffset(final double af) {
		angleFactor = new WB_ConstantParameter<Double>(af);
		return this;
	}

	public HEM_Wireframe setAngleOffset(final WB_Parameter<Double> af) {
		angleFactor = af;
		return this;
	}

	public HEM_Wireframe setFidget(final double f) {
		fidget = f;
		return this;
	}

	public HEM_Wireframe setFillFactor(final double ff) {
		fillFactor = ff;
		return this;
	}

	public HEM_Wireframe setCap(final boolean b) {
		cap = b;
		return this;
	}

	public HEM_Wireframe setTaper(final boolean b) {
		taper = b;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.creators.HEC_Creator#createBase()
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		if ((strutR == null) || (facetN < 3)) {
			return mesh;
		}

		final HEC_FromFrame ff = new HEC_FromFrame();
		ff.setFrame(mesh);
		ff.setAngleOffset(angleFactor);
		ff.setCap(cap);
		ff.setStrutFacets(facetN);
		ff.setFidget(fidget);
		ff.setFillFactor(fillFactor);
		ff.setTaper(taper);
		ff.setStrutRadius(strutR);
		ff.setMaximumStrutOffset(maxStrutOffset);
		mesh.set(ff.create());
		return mesh;
	}

	/*
	 * (non-Javadoc)
	 * @seewblut.hemesh.modifiers.HEM_Modifier#applySelected(wblut.hemesh.core.
	 * HE_Selection)
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {

		return apply(selection.parent);
	}
}
