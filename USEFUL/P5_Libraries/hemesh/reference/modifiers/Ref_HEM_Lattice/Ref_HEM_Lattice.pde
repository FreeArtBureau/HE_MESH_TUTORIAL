import wblut.math.*;
import wblut.processing.*;
import wblut.core.*;
import wblut.hemesh.*;
import wblut.geom.*;


HE_Mesh mesh;
WB_Render render;

void setup() {
  size(800, 800, P3D);
  createMesh();
  // Tries to create a mesh lattice by insetting all faces,making an expanded copy and
  // then connect the inset faces...
  HEM_Lattice modifier=new HEM_Lattice();
  modifier.setWidth(10);// desired width of struts
  modifier.setDepth(10);// depth of struts
  modifier.setThresholdAngle(1.5*HALF_PI);// treat edges sharper than this angle as hard edges
  modifier.setFuse(true);// try to fuse planar adjacent planar faces created by the extrude
  modifier.setFuseAngle(0.05*HALF_PI);// threshold angle to be considered coplanar
  mesh.modify(modifier);

  render=new WB_Render(this);
}

void draw() {
  background(120);
  directionalLight(255, 255, 255, 1, 1, -1);
  directionalLight(127, 127, 127, -1, -1, 1);
  translate(400,400, 0);
  rotateY(mouseX*1.0f/width*TWO_PI);
  rotateX(mouseY*1.0f/height*TWO_PI);
  fill(255);
  noStroke();
  render.drawFaces(mesh);
  stroke(0);
  render.drawEdges(mesh);
}


void createMesh(){
  HEC_Geodesic creator=new HEC_Geodesic().setRadius(300).setLevel(2);
  mesh=new HE_Mesh(creator); 
}

