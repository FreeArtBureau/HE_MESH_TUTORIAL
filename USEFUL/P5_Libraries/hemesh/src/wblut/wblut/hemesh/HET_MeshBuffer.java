/**
 * 
 */
package wblut.hemesh;

import java.util.ArrayList;


import processing.core.PApplet;
import processing.core.PGraphics3D;
import wblut.geom.WB_ExplicitTriangle;
import wblut.geom.WB_Point3d;
import wblut.geom.WB_Quad;

/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class HET_MeshBuffer extends PGraphics3D {

	/** Calling applet. */
	protected PApplet				home;

	/** The meshes. */
	protected ArrayList<HE_Mesh>	meshes;

	/** The temp meshes. */
	private ArrayList<HE_Mesh>		tempMeshes;

	/** The triangles. */
	private ArrayList<WB_ExplicitTriangle>	triangles;

	/** The quads. */
	private ArrayList<WB_Quad>		quads;

	/** The p4. */
	private WB_Point3d				p1, p2, p3, p4;

	/** The shape. */
	private int						shape;

	/** The shapectr. */
	private int						shapectr;

	/**
	 * Instantiates a new hE t_ mesh buffer.
	 */
	public HET_MeshBuffer() {
		meshes = new ArrayList<HE_Mesh>();

	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics3D#allocate()
	 */
	@Override
	protected void allocate() {

	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics#dispose()
	 */
	@Override
	public void dispose() {
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics#displayable()
	 */
	@Override
	public boolean displayable() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics3D#beginDraw()
	 */
	@Override
	public void beginDraw() {

	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics3D#endDraw()
	 */
	@Override
	public void endDraw() {

	}

	/**
	 * Next mesh.
	 */
	public void nextMesh() {
		final HE_Mesh m = new HE_Mesh();

		if (triangles != null) {
			if (triangles.size() > 0) {
				final HEC_FromTriangles ft = new HEC_FromTriangles()
						.setTriangles(triangles);
				m.add(new HE_Mesh(ft));
			}
		}
		if (quads != null) {
			if (quads.size() > 0) {
				final HEC_FromQuads fq = new HEC_FromQuads().setQuads(quads);
				m.add(new HE_Mesh(fq));
			}
		}
		if (tempMeshes != null) {
			if (tempMeshes.size() > 0) {
				for (int i = 0; i < tempMeshes.size(); i++) {
					m.add(tempMeshes.get(i));
				}

			}
		}
		if (m.numberOfVertices() > 0) {
			meshes.add(m);
		}
		tempMeshes = new ArrayList<HE_Mesh>();
		triangles = new ArrayList<WB_ExplicitTriangle>();
		quads = new ArrayList<WB_Quad>();
		vertexCount = 0;

	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics3D#beginShape(int)
	 */
	@Override
	public void beginShape(final int kind) {
		shape = kind;
		shapectr = 0;
		if ((shape != TRIANGLES) && (shape != TRIANGLE_STRIP)
				&& (shape != TRIANGLE_FAN) && (shape != QUADS)
				&& (shape != QUAD_STRIP)) {
			final String err = "HET_Recorder can only be used with TRIANGLES, TRIANGLE_STRIP, TRIANGLE_FAN, QUADS OR QUAD_STRIP.";
			throw new RuntimeException(err);
		}
		vertexCount = 0;
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics3D#vertex(float, float)
	 */
	@Override
	public void vertex(final float x, final float y) {
		vertex(x, y, 0);
	}

	/*
	 * (non-Javadoc)
	 * @see processing.core.PGraphics#vertex(float, float, float)
	 */
	@Override
	public void vertex(final float x, final float y, final float z) {
		final float vertex[] = vertices[vertexCount];
		vertex[X] = x;
		vertex[Y] = y;
		vertex[Z] = z;
		final float tvertex[] = new float[3];
		tvertex[X] = modelX(vertex[X], vertex[Y], vertex[Z]);
		tvertex[Y] = modelY(vertex[X], vertex[Y], vertex[Z]);
		tvertex[Z] = modelZ(vertex[X], vertex[Y], vertex[Z]);
		vertex[X] = tvertex[X];
		vertex[Y] = tvertex[Y];
		vertex[Z] = tvertex[Z];
		vertexCount++;
		if ((shape == TRIANGLES) || (shape == TRIANGLE_STRIP)
				|| (shape == TRIANGLE_FAN)) {
			if (vertexCount == 3) {
				addTriangle();
			}
		} else if ((shape == QUADS) || (shape == QUAD_STRIP)) {
			if (vertexCount == 4) {
				addQuad();
			}

		}
	}

	/**
	 * Adds the triangle.
	 */
	protected void addTriangle() {
		if (shape == TRIANGLES) {
			p1 = new WB_Point3d(vertices[0][X], vertices[0][Y], vertices[0][Z]);
			p2 = new WB_Point3d(vertices[1][X], vertices[1][Y], vertices[1][Z]);
			p3 = new WB_Point3d(vertices[2][X], vertices[2][Y], vertices[2][Z]);
			if (triangles == null) {
				triangles = new ArrayList<WB_ExplicitTriangle>();
			}
			triangles.add(new WB_ExplicitTriangle(p1, p2, p3));
			vertexCount = 0;
		} else if (shape == TRIANGLE_STRIP) {
			p1 = new WB_Point3d(vertices[0][X], vertices[0][Y], vertices[0][Z]);
			p2 = new WB_Point3d(vertices[1][X], vertices[1][Y], vertices[1][Z]);
			p3 = new WB_Point3d(vertices[2][X], vertices[2][Y], vertices[2][Z]);
			if (triangles == null) {
				triangles = new ArrayList<WB_ExplicitTriangle>();
			}
			if (shapectr % 2 == 0) {
				triangles.add(new WB_ExplicitTriangle(p1, p2, p3));
			} else {
				triangles.add(new WB_ExplicitTriangle(p2, p1, p3));
			}
			vertices[0][X] = vertices[1][X];
			vertices[0][Y] = vertices[1][Y];
			vertices[0][Z] = vertices[1][Z];
			vertices[1][X] = vertices[2][X];
			vertices[1][Y] = vertices[2][Y];
			vertices[1][Z] = vertices[2][Z];
			vertexCount = 2;
			shapectr++;

		} else if (shape == TRIANGLE_FAN) {
			p1 = new WB_Point3d(vertices[0][X], vertices[0][Y], vertices[0][Z]);
			p2 = new WB_Point3d(vertices[1][X], vertices[1][Y], vertices[1][Z]);
			p3 = new WB_Point3d(vertices[2][X], vertices[2][Y], vertices[2][Z]);
			if (triangles == null) {
				triangles = new ArrayList<WB_ExplicitTriangle>();
			}

			triangles.add(new WB_ExplicitTriangle(p1, p2, p3));

			vertices[1][X] = vertices[2][X];
			vertices[1][Y] = vertices[2][Y];
			vertices[1][Z] = vertices[2][Z];
			vertexCount = 2;

		}
	}

	/**
	 * Adds the quad.
	 */
	protected void addQuad() {
		if (shape == QUADS) {
			p1 = new WB_Point3d(vertices[0][X], vertices[0][Y], vertices[0][Z]);
			p2 = new WB_Point3d(vertices[1][X], vertices[1][Y], vertices[1][Z]);
			p3 = new WB_Point3d(vertices[2][X], vertices[2][Y], vertices[2][Z]);
			p4 = new WB_Point3d(vertices[3][X], vertices[3][Y], vertices[3][Z]);
			if (quads == null) {
				quads = new ArrayList<WB_Quad>();
			}
			quads.add(new WB_Quad(p1, p2, p3, p4));

			vertexCount = 0;
		} else if (shape == QUAD_STRIP) {
			p1 = new WB_Point3d(vertices[0][X], vertices[0][Y], vertices[0][Z]);
			p2 = new WB_Point3d(vertices[1][X], vertices[1][Y], vertices[1][Z]);
			p3 = new WB_Point3d(vertices[2][X], vertices[2][Y], vertices[2][Z]);
			p4 = new WB_Point3d(vertices[3][X], vertices[3][Y], vertices[3][Z]);
			if (quads == null) {
				quads = new ArrayList<WB_Quad>();
			}
			quads.add(new WB_Quad(p1, p2, p4, p3));
			vertices[0][X] = vertices[2][X];
			vertices[0][Y] = vertices[2][Y];
			vertices[0][Z] = vertices[2][Z];
			vertices[1][X] = vertices[3][X];
			vertices[1][Y] = vertices[3][Y];
			vertices[1][Z] = vertices[3][Z];
			vertexCount = 2;

		}
	}

}
