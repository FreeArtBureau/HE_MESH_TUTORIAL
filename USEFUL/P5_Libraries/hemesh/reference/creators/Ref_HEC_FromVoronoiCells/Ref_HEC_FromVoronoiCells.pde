import wblut.math.*;
import wblut.processing.*;
import wblut.core.*;
import wblut.hemesh.*;
import wblut.geom.*;

import processing.opengl.*;

float[][] points;
int numpoints;
HE_Mesh container;
HE_Mesh[] cells;
int numcells;

HE_Mesh mesh;

WB_Render render;

void setup() {
  size(800,800,OPENGL);
  
  //HEC_FromVoronoiCells can be used to recombine meshes generated
  //bij HEMC_VoronoiCells into a single mesh.
  
  //create a container mesh
  container=new HE_Mesh(new HEC_Geodesic().setRadius(250).setLevel(1)); 
  //generate points
  numpoints=50;
  points=new float[numpoints][3];
  for(int i=0;i<numpoints;i++) {
    points[i][0]=random(-250,250);
    points[i][1]=random(-250,250);
    points[i][2]=random(-250,250);
  }
  
  // generate voronoi cells
  HEMC_VoronoiCells multiCreator=new HEMC_VoronoiCells().setPoints(points).setN(numpoints).setContainer(container).setOffset(0);
  cells=multiCreator.create();
  numcells=cells.length;
  
  boolean[] isCellOn=new boolean[numcells];
  
  for(int i=0;i<numcells;i++){
   isCellOn[i]=(random(100)<50); 
  }
  
  //build new mesh from active cells
 
 HEC_FromVoronoiCells creator=new HEC_FromVoronoiCells();
 creator.setCells(cells);// output of HEMC_VoronoiCells, 
 creator.setActive(isCellOn);// boolean array
 
 mesh=new HE_Mesh(creator); 
 render=new WB_Render(this);
}

void draw() {
  background(255);
  directionalLight(255, 255, 255, 1, 1, -1);
  directionalLight(127, 127, 127, -1, -1, 1);
  translate(400, 400, 0);
  rotateY(mouseX*1.0f/width*TWO_PI);
  rotateX(mouseY*1.0f/height*TWO_PI);
   noStroke();
  render.drawFaces(mesh);
 stroke(0);
  render.drawEdges(mesh);
  stroke(255,0,0);
  render.drawBoundaryEdges(mesh);
}

