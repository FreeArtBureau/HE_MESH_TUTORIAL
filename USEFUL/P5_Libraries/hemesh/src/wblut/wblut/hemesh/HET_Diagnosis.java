/**
 * 
 */
package wblut.hemesh;

import java.util.Iterator;


/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class HET_Diagnosis {

	/**
	 * Check consistency of datastructure of closed mesh.
	 *
	 * @return true or false
	 */
	public static boolean isValidMesh(final HE_Mesh mesh) {
		return validate(mesh, false, false, false);
	}

	/**
	 * Check consistency of datastructure of surface.
	 *
	 * @return true or false
	 */
	public static boolean isValidSurface(final HE_Mesh mesh) {
		return validate(mesh, false, false, true);
	}

	/**
	 * Check consistency of datastructure.
	 *
	 * @return true or false
	 */
	public static boolean validate(final HE_Mesh mesh) {
		return validate(mesh, true, true,false);
	}

	/**
	 * Check consistency of datastructure.
	 *
	 * @param verbose true: print to console, HE.SILENT: no output
	 * @param force true: full scan, HE.BREAK: stop on first error
	 * @param allowSurface
	 * @return true or false
	 */
	public static boolean validate(final HE_Mesh mesh, final boolean verbose,
			final boolean force, final boolean allowSurface) {
		boolean result = true;
		if (verbose == true) {
			System.out.println("Checking face (" + mesh.numberOfFaces()
					+ ") properties");
		}

		HE_Face face;
		final Iterator<HE_Face> fItr = mesh.fItr();
		while (fItr.hasNext()) {
			face = fItr.next();
			if (face.getHalfedge() == null) {
				if (verbose == true) {
					System.out.println("Null reference in face " + face.key()
							+ ".");
				}
				if (force == true) {
					result = false;
				} else {
					return false;
				}

			} else {
				if (!mesh.contains(face.getHalfedge())) {
					if (verbose == true) {
						System.out.println("External reference in face "
								+ face.key() + ".");
					}
					if (force == true) {
						result = false;
					} else {
						return false;
					}
				} else {
					if (face.getHalfedge().getFace() != null) {
						if (face.getHalfedge().getFace() != face) {
							if (verbose == true) {
								System.out.println("Wrong reference in face "
										+ face.key() + ".");
							}
							if (force == true) {
								result = false;
							} else {
								return false;
							}
						}
					}
				}
			}
		}

		if (verbose == true) {
			System.out.println("Checking vertex (" + mesh.numberOfVertices()
					+ ") properties");
		}
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = mesh.vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			if (v.getHalfedge() == null) {
				if (verbose == true) {
					System.out.println("Null reference in vertex  " + v.key()
							+ ".");
				}
				if (force == true) {
					result = false;
				} else {
					return false;
				}
			} else {
				if (!mesh.contains(v.getHalfedge())) {
					if (verbose == true) {
						System.out.println("External reference in vertex  "
								+ v.key() + ".");
					}
					if (force == true) {
						result = false;
					} else {
						return false;
					}
				}
				if (v.getHalfedge().getVertex() != null) {
					if (v.getHalfedge().getVertex() != v) {
						if (verbose == true) {
							System.out.println("Wrong reference in vertex  "
									+ v.key() + ".");
						}
						if (force == true) {
							result = false;
						} else {
							return false;
						}
					}
				}
			}
		}

		if (verbose == true) {
			System.out.println("Checking edge (" + mesh.numberOfEdges()
					+ ") properties");
		}
		final Iterator<HE_Edge> eItr = mesh.eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			if (e.getHalfedge() == null) {
				if (verbose == true) {
					System.out.println("Null reference in edge  " + e.key()
							+ ".");
				}
				if (force == true) {
					result = false;
				} else {
					return false;
				}
			} else {
				if (!mesh.contains(e.getHalfedge())) {
					if (verbose == true) {
						System.out.println("External reference in edge  "
								+ e.key() + ".");
					}
					if (force == true) {
						result = false;
					} else {
						return false;
					}
				}
				if (e.getHalfedge().getEdge() != null) {
					if (e.getHalfedge().getEdge() != e) {
						if (verbose == true) {
							System.out.println("Wrong reference in edge  "
									+ e.key() + ".");
						}
						if (force == true) {
							result = false;
						} else {
							return false;
						}
					}
				}
			}
		}

		if (verbose == true) {
			System.out.println("Checking half edge ("
					+ mesh.numberOfHalfedges() + ") properties");
		}
		HE_Halfedge he;
		final Iterator<HE_Halfedge> heItr = mesh.heItr();
		while (heItr.hasNext()) {
			he = heItr.next();
			if (he.getNextInFace() == null) {
				if (verbose == true) {
					System.out.println("Null reference (next) in half edge  "
							+ he.key() + ".");
				}
				if (force == true) {
					result = false;
				} else {
					return false;
				}
			} else {
				if (!mesh.contains(he.getNextInFace())) {
					if (verbose == true) {
						System.out
								.println("External reference (next) in half edge  "
										+ he.key() + ".");
					}
					if (force == true) {
						result = false;
					} else {
						return false;
					}
				}
				if ((he.getFace() != null)
						&& (he.getNextInFace().getFace() != null)) {
					if (he.getFace() != he.getNextInFace().getFace()) {
						if (verbose == true) {
							System.out
									.println("Inconsistent reference (face) in half edge  "
											+ he.key() + ".");
						}
						if (force == true) {
							result = false;
						} else {
							return false;
						}
					}
				}
			}
			if (he.getPrevInFace() == null) {
				if (verbose == true) {
					System.out.println("Null reference (prev) in half edge  "
							+ he.key() + ".");
				}
				if (force == true) {
					result = false;
				} else {
					return false;
				}
			} else {
				if (!mesh.contains(he.getPrevInFace())) {
					if (verbose == true) {
						System.out
								.println("External reference (prev) in half edge  "
										+ he.key() + ".");
					}
					if (force == true) {
						result = false;
					} else {
						return false;
					}
				}
				if ((he.getFace() != null)
						&& (he.getPrevInFace().getFace() != null)) {
					if (he.getFace() != he.getPrevInFace().getFace()) {
						if (verbose == true) {
							System.out
									.println("Inconsistent reference (face) in half edge  "
											+ he.key() + ".");
						}
						if (force == true) {
							result = false;
						} else {
							return false;
						}
					}
				}
				if (he.getPrevInFace().getNextInFace() != he) {
					if (verbose == true) {
						System.out
								.println("Unmatched (next)/(prev) in half edge  "
										+ he.key() + ".");
					}
					if (force == true) {
						result = false;
					} else {
						return false;
					}
				}
			}

			if (he.getPair() == null) {
				if (verbose == true) {
					System.out.println("Null reference (pair) in half edge  "
							+ he.key() + ".");
				}
				if (force == true) {
					result = false;
				} else {
					return false;
				}
			} else {
				if (!mesh.contains(he.getPair())) {
					if (verbose == true) {
						System.out
								.println("External reference (pair) in half edge  "
										+ he.key() + ".");
					}
					if (force == true) {
						result = false;
					} else {
						return false;
					}
				}
				if (he.getPair().getPair() == null) {
					if (verbose == true) {
						System.out
								.println("No pair reference back to half edge  "
										+ he.key() + ".");
					}
				} else {
					if (he.getPair().getPair() != he) {
						if (verbose == true) {
							System.out
									.println("Wrong pair reference back to half edge  "
											+ he.key() + ".");
						}
						if (force == true) {
							result = false;
						} else {
							return false;
						}
					}
				}
				if ((he.getEdge() != null) && (he.getPair().getEdge() != null)) {
					if (he.getEdge() != he.getPair().getEdge()) {
						if (verbose == true) {
							System.out
									.println("Inconsistent reference (edge) in half edge  "
											+ he.key() + ".");
						}
						if (force == true) {
							result = false;
						} else {
							return false;
						}

					}
				}
			}

			if ((he.getNextInFace() != null) && (he.getPair() != null)) {
				if ((he.getNextInFace().getVertex() != null)
						&& (he.getPair().getVertex() != null)) {
					if (he.getNextInFace().getVertex() != he.getPair()
							.getVertex()) {
						if (verbose == true) {
							System.out
									.println("Inconsistent reference (pair)/(next) in half edge  "
											+ he.key() + ".");
						}
						if (force == true) {
							result = false;
						} else {
							return false;
						}
					}
				}
			}
			if (he.getFace() == null) {
				if (!allowSurface) {
					if (verbose == true) {
						System.out
								.println("Null reference (face) in half edge  "
										+ he.key() + ".");
					}
					if (force == true) {
						result = false;
					} else {
						return false;
					}
				}

			} else {
				if (!mesh.contains(he.getFace())) {
					if (verbose == true) {
						System.out
								.println("External reference (face) in half edge  "
										+ he.key() + ".");
					}
					if (force == true) {
						result = false;
					} else {
						return false;
					}
				}
			}
			if (he.getVertex() == null) {
				if (verbose == true) {
					System.out.println("Null reference (vert) in half edge  "
							+ he.key() + ".");
				}
				if (force == true) {
					result = false;
				} else {
					return false;
				}
			} else {
				if (!mesh.contains(he.getVertex())) {
					if (verbose == true) {
						System.out
								.println("External reference (vert) in half edge  "
										+ he.key() + ".");
					}
					if (force == true) {
						result = false;
					} else {
						return false;
					}
				}
			}
			if (he.getEdge() == null) {
				if (verbose == true) {
					System.out.println("Null reference (edge) in half edge  "
							+ he.key() + ".");
				}
				if (force == true) {
					result = false;
				} else {
					return false;
				}
			} else {
				if (!mesh.contains(he.getEdge())) {
					if (verbose == true) {
						System.out
								.println("External reference (edge) in half edge  "
										+ he.key() + ".");
					}
					if (force == true) {
						result = false;
					} else {
						return false;
					}
				}
			}
		}
		if (verbose == true) {
			System.out.println("Validation complete!");
		}
		return result;
	}

}
