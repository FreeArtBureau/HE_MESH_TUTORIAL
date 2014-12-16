import wblut.math.*;
import wblut.processing.*;
import wblut.core.*;
import wblut.hemesh.*;
import wblut.geom.*;

HE_Mesh mesh;
WB_Render render;

void setup() {
  size(800, 800, P3D);
  HEC_Cube creator=new HEC_Cube();
  creator.setEdge(200); 
  //alternatively 
  //creator.setRadius(200);
  //creator.setInnerRadius(200);// radius of sphere inscribed in cube
  //creator.setOuterRadius(200);// radius of sphere circumscribing cube
  //creator.setMidRadius(200);// radius of sphere tangential to edges
  creator.setWidthSegments(4).setHeightSegments(3).setDepthSegments(2);
  mesh=new HE_Mesh(creator); 
  HET_Diagnosis.validate(mesh);
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

