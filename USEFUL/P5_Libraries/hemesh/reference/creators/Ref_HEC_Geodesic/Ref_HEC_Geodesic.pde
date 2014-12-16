import wblut.math.*;
import wblut.processing.*;
import wblut.core.*;
import wblut.hemesh.*;
import wblut.geom.*;




HE_Mesh mesh;
WB_Render render;
//Primitive geodesic sphere, only supports 0,2,4,8,16,... subdivisions of icosahedron
void setup() {
  size(800, 800, P3D);
  HEC_Geodesic creator=new HEC_Geodesic();
  creator.setRadius(200); 
  creator.setLevel(2);// number of recursive subdivisions
  creator.setType(0);// 0=icosahedron, 1=tetrahedron, 2=octahedron
  mesh=new HE_Mesh(creator); 

  render=new WB_Render(this);
}

void draw() {
  background(255);
  directionalLight(255, 255, 255, 1, 1, -1);
  directionalLight(127, 127, 127, -1, -1, 1);
  translate(400, 400, 100);
  rotateY(mouseX*1.0f/width*TWO_PI);
  rotateX(mouseY*1.0f/height*TWO_PI);
  stroke(0);
  render.drawEdges(mesh);
  noStroke();
  render.drawFaces(mesh);

}

