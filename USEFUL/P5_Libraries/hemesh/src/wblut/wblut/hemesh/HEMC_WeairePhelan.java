/**
 * 
 */
package wblut.hemesh;

import java.util.ArrayList;

import wblut.geom.WB_Plane;
import wblut.geom.WB_Point3d;
import wblut.geom.WB_Vector3d;
import wblut.math.WB_Fast;



/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class HEMC_WeairePhelan extends HEMC_MultiCreator {
	private static final double[][]	dodecahedronPoints			= {
			{ 0.31498, 0, 0.62996 }, { -0.31498, 0, 0.62996 },
			{ 0.41997, 0.41997, 0.41997 }, { 0, 0.62996, 0.31498 },
			{ -0.41997, 0.41997, 0.41997 }, { -0.41997, -0.41997, 0.41997 },
			{ 0, -0.62996, .31498 }, { .41997, -.41997, .41997 },
			{ .62996, .31498, 0 }, { -.62996, .31498, 0 },
			{ -.62996, -.31498, 0 }, { .62996, -.31498, 0 },
			{ .41997, .41997, -.41997 }, { 0, .62996, -.31498 },
			{ -.41997, .41997, -.41997 }, { -.41997, -.41997, -.41997 },
			{ 0, -.62996, -.31498 }, { .41997, -.41997, -.41997 },
			{ .31498, 0, -.62996 }, { -.31498, 0, -.62996 }	};

	private static final double[][]	tetrakaidecahedronPoints	= {
			{ .314980, .370039, .5 }, { -.314980, .370039, .5 },
			{ -.5, 0, .5 }, { -.314980, -.370039, .5 },
			{ .314980, -.370039, .5 }, { .5, 0, .5 },
			{ .419974, .580026, 0.080026 }, { -.419974, .580026, 0.080026 },
			{ -.685020, 0, .129961 }, { -.419974, -.580026, 0.080026 },
			{ .419974, -.580026, 0.080026 }, { .685020, 0, .129961 },
			{ .580026, .419974, -0.080026 }, { 0, .685020, -0.129961 },
			{ -.580026, .419974, -0.080026 },
			{ -.580026, -.419974, -0.080026 }, { 0, -.685020, -.129961 },
			{ .580026, -.419974, -0.080026 }, { .370039, .314980, -.5 },
			{ 0, .5, -.5 }, { -.370039, .314980, -.5 },
			{ -.370039, -.314980, -.5 }, { 0, -.5, -.5 },
			{ .370039, -.314980, -.5 }							};

	private final HE_Mesh			dodecahedron;
	private final HE_Mesh			tetrakaidecahedron;
	private WB_Point3d				origin;
	private WB_Vector3d				extents;
	private int						U, V, W;
	private double					scU, scV, scW;
	private boolean					cropUp, cropVp, cropWp;
	private boolean					cropUm, cropVm, cropWm;;

	public HEMC_WeairePhelan() {
		super();
		dodecahedron = new HE_Mesh(
				new HEC_ConvexHull().setPoints(dodecahedronPoints));
		tetrakaidecahedron = new HE_Mesh(
				new HEC_ConvexHull().setPoints(tetrakaidecahedronPoints));
		dodecahedron.fuseCoplanarFaces();
		tetrakaidecahedron.fuseCoplanarFaces();
		cropUp = false;
		cropVp = false;
		cropWp = false;
		cropUm = false;
		cropVm = false;
		cropWm = false;

	}

	public HEMC_WeairePhelan setOrigin(final WB_Point3d p) {
		origin = p.get();
		return this;
	}

	public HEMC_WeairePhelan setExtents(final WB_Vector3d v) {
		extents = v.get();
		return this;
	}

	public HEMC_WeairePhelan setScale(final double scU, final double scV,
			final double scW) {
		this.scU = scU;
		this.scV = scV;
		this.scW = scW;

		return this;
	}

	public HEMC_WeairePhelan setNumberOfUnits(final int U, final int V,
			final int W) {
		this.U = WB_Fast.max(1, U);
		this.V = WB_Fast.max(1, V);
		this.W = WB_Fast.max(1, W);

		return this;
	}

	public HEMC_WeairePhelan setCrop(final boolean crop) {
		cropUm = crop;
		cropVm = crop;
		cropWm = crop;
		cropUp = crop;
		cropVp = crop;
		cropWp = crop;
		return this;
	}

	public HEMC_WeairePhelan setCrop(final boolean cropU, final boolean cropV,
			final boolean cropW) {
		cropUm = cropU;
		cropVm = cropV;
		cropWm = cropW;
		cropUp = cropU;
		cropVp = cropV;
		cropWp = cropW;
		return this;
	}

	public HEMC_WeairePhelan setCrop(final boolean cropUm,
			final boolean cropVm, final boolean cropWm, final boolean cropUp,
			final boolean cropVp, final boolean cropWp) {
		this.cropUm = cropUm;
		this.cropVm = cropVm;
		this.cropWm = cropWm;
		this.cropUp = cropUp;
		this.cropVp = cropVp;
		this.cropWp = cropWp;
		return this;
	}

	private HE_Mesh[] singleCell(final WB_Vector3d offset) {
		final HE_Mesh[] cells = new HE_Mesh[8];
		cells[0] = tetrakaidecahedron.get();
		cells[0].move(0, 0, -.5);
		cells[1] = tetrakaidecahedron.get();
		cells[1].rotateAboutAxis(0.5 * Math.PI, 0, 0, 0, 0, 0, 1);
		cells[1].move(0, 0, 0.5);
		cells[2] = tetrakaidecahedron.get();
		cells[2].rotateAboutAxis(-0.5 * Math.PI, 0, 0, 0, 0, 1, 0);
		cells[2].move(-.5, 1, 1);
		cells[3] = tetrakaidecahedron.get();
		cells[3].rotateAboutAxis(0.5 * Math.PI, 0, 0, 0, 0, 1, 0);
		cells[3].move(.5, 1, 1);
		cells[4] = tetrakaidecahedron.get();
		cells[4].rotateAboutAxis(0.5 * Math.PI, 0, 0, 0, 1, 0, 0);
		cells[4].move(1, .5, 0);
		cells[5] = tetrakaidecahedron.get();
		cells[5].rotateAboutAxis(-0.5 * Math.PI, 0, 0, 0, 1, 0, 0);
		cells[5].move(1, -.5, 0);
		cells[6] = dodecahedron.get();
		cells[6].move(1, 0, 1);
		cells[7] = dodecahedron.get();
		cells[7].rotateAboutAxis(-0.5 * Math.PI, 0, 0, 0, 0, 1, 0);
		cells[7].move(0, 1, 0);

		for (int i = 0; i < 8; i++) {
			cells[i].scale(0.5 * scU, 0.5 * scV, 0.5 * scW, new WB_Point3d(0, 0,
					0));
			cells[i].move(offset);
		}
		return cells;

	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_MultiCreator#create()
	 */
	@Override
	public HE_Mesh[] create() {
		if (scU == 0) {
			scU = extents.x / U;
		}
		if (scV == 0) {
			scV = extents.y / V;
		}
		if (scW == 0) {
			scW = extents.z / W;
		}
		final ArrayList<HE_Mesh> tmp = new ArrayList<HE_Mesh>();
		HE_Mesh[] tmpCells;
		final ArrayList<WB_Plane> planes = new ArrayList<WB_Plane>(6);

		if (cropUm) {
			planes.add(new WB_Plane(origin, new WB_Vector3d(1, 0, 0)));
		}
		if (cropVm) {
			planes.add(new WB_Plane(origin, new WB_Vector3d(0, 1, 0)));
		}
		if (cropWm) {
			planes.add(new WB_Plane(origin, new WB_Vector3d(0, 0, 1)));
		}
		final WB_Point3d end = origin.addAndCopy(extents);

		if (cropUp) {
			planes.add(new WB_Plane(end, new WB_Vector3d(-1, 0, 0)));
		}
		if (cropVp) {
			planes.add(new WB_Plane(end, new WB_Vector3d(0, -1, 0)));
		}
		if (cropWp) {
			planes.add(new WB_Plane(end, new WB_Vector3d(0, 0, -1)));
		}
		final HEM_MultiSlice ms = new HEM_MultiSlice().setPlanes(planes);
		for (int i = 0; i < U + 1; i++) {
			for (int j = 0; j < V + 1; j++) {
				for (int k = 0; k < W + 1; k++) {
					final WB_Vector3d offset = new WB_Vector3d(origin.x + (i - 0.5)
							* scU, origin.y + (j - 0.5) * scV, origin.z
							+ (k - 0.5) * scW);
					tmpCells = singleCell(offset);
					for (int c = 0; c < 8; c++) {
						if (planes.size() > 0) {
							tmpCells[c].modify(ms);
						}

						if (tmpCells[c].numberOfVertices() > 0) {
							tmp.add(tmpCells[c]);
						}
					}
				}
			}
		}
		final HE_Mesh[] result = new HE_Mesh[tmp.size()];
		HE_Mesh cell;
		for (int i = 0; i < tmp.size(); i++) {
			cell = tmp.get(i);

			result[i] = cell;

		}
		_numberOfMeshes = tmp.size();
		return result;
	}

}