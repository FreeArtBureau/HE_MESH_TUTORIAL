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
  
  //Laplacian modifier without adding vertices
  HEM_Smooth modifier=new HEM_Smooth();
  modifier.setIterations(4);
  modifier.setAutoRescale(true);// rescale mesh to original extents
  
  mesh.modify(modifier);
  
  render=new WB_Render(this);
}

void draw() {
  background(120);
  directionalLight(255, 255, 255, 1, 1, -1);
  directionalLight(127, 127, 127, -1, -1, 1);
  translate(400, 400, 0);
  rotateY(mouseX*1.0f/width*TWO_PI);
  rotateX(mouseY*1.0f/height*TWO_PI);
  fill(255);
  noStroke();
  render.drawFaces(mesh);
  stroke(0);
  render.drawEdges(mesh);
}


void createMesh(){
  HEC_SuperDuper creator=new HEC_SuperDuper();
  creator.setU(16).setV(8).setRadius(50);
  creator.setDonutParameters(0, 10, 10, 10, 3, 6, 12, 12, 3, 1);
  mesh=new HE_Mesh(creator);
  mesh.modify(new HEM_Extrude().setDistance(100).setChamfer(1));
mesh.subdivide(new HES_Planar().setRandom(true).setRange(0.8));
  
}

