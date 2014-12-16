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

  //HEC_DooSabin only support closed meshes.
  HES_DooSabin subdividor=new HES_DooSabin();
  subdividor.setFactors(1,1);//Relative importance of edges vs. face. Default (1.0,1.0);
  subdividor.setAbsolute(false);//Specify offset relative or absolute
  subdividor.setDistance(50);//Specify distance when absolute, will be multiplied with factors
  mesh.subdivide(subdividor,1);

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


void createMesh() {
  HEC_Cube creator=new HEC_Cube(500, 1,1, 1);
  mesh=new HE_Mesh(creator);
}

