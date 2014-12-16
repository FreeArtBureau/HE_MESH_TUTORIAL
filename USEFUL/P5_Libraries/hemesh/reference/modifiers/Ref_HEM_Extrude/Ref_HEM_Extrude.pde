import wblut.math.*;
import wblut.processing.*;
import wblut.core.*;
import wblut.hemesh.*;
import wblut.geom.*;



HE_Mesh mesh;
WB_Render render;
HEM_Extrude modifier;
void setup() {
  size(800, 800, P3D);
  createMesh();
  
  modifier=new HEM_Extrude();
  modifier.setDistance(0);// extrusion distance, set to 0 for inset faces
  modifier.setRelative(false);// treat chamfer as relative to face size or as absolute value
  modifier.setChamfer(10);// chamfer for non-hard edges
  modifier.setHardEdgeChamfer(200);// chamfer for hard edges
  modifier.setThresholdAngle(1.5*HALF_PI);// treat edges sharper than this angle as hard edges
  modifier.setFuse(true);// try to fuse planar adjacent planar faces created by the extrude
  modifier.setFuseAngle(0.05*HALF_PI);// threshold angle to be considered coplanar
  modifier.setPeak(true);//if absolute chamfer is too large for face, create a peak on the face
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
  render.drawFaces(1,mesh);//HEM_Extrude sets all extruded faces to label 1
  fill(255,0,0);
  render.drawFaces(2,mesh);//HEM_Extrude sets all "wall" faces to label 2
  fill(250,120,120);
  render.drawFaces(3,mesh);//HEM_Extrude sets all fused faces to label 3
   fill(120,120,120);
  render.drawFaces(4,mesh);//HEM_Extrude sets all peaked faces to label 4
  stroke(0);
  render.drawEdges(mesh);
  
  createMesh();
  float d=sin(TWO_PI*0.01*frameCount)*30;
  if(abs(d)<15) d=0;
  modifier.setDistance(d);
  mesh.modify(modifier);
}


void createMesh(){
  HEC_Cube creator=new HEC_Cube(300,5,5,5);
  mesh=new HE_Mesh(creator); 
}

