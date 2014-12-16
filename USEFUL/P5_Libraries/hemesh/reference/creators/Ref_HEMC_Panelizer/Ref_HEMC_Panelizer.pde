import wblut.math.*;
import wblut.processing.*;
import wblut.core.*;
import wblut.hemesh.*;
import wblut.geom.*;


HE_Mesh hull;
HE_Mesh[] panels;
int numpanels;

WB_Render render;

void setup() {
  size(800,800,P3D);
  
  
  //create a hull mesh
  hull=new HE_Mesh(new HEC_Geodesic().setRadius(250).setLevel(0));
  hull.midSplitFacesHole();
  hull.subdivide(new HES_CatmullClark().setKeepBoundary(false),2);
  hull.midSplitFacesHole();
  //panelize the hull
  HEMC_Panelizer multiCreator=new HEMC_Panelizer();
  multiCreator.setMesh(hull);
  multiCreator.setThickness(10);
  
  panels=multiCreator.create();
  numpanels=panels.length;
  
  render=new WB_Render(this);
}

void draw() {
  background(255);
  directionalLight(255, 255, 255, 1, 1, -1);
  directionalLight(127, 127, 127, -1, -1, 1);
  translate(400, 400, 0);
  rotateY(mouseX*1.0f/width*TWO_PI);
  rotateX(mouseY*1.0f/height*TWO_PI);
  drawFaces();
  drawEdges();
}

void drawEdges(){
  smooth();
  stroke(0);
  for(int i=0;i<numpanels;i++) {
    render.drawEdges(panels[i]);
  } 
}

void drawFaces(){
  noSmooth();
  noStroke();
  fill(255);
  for(int i=0;i<numpanels;i++) {
    render.drawFaces(panels[i]);
  }   
}

