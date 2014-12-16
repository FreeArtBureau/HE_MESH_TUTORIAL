import wblut.math.*;
import wblut.processing.*;
import wblut.core.*;
import wblut.hemesh.*;
import wblut.geom.*;

HE_Mesh mesh;
WB_Render render;

void setup() {
  size(800, 800, P3D);
  
  WB_Point3d[] points=new WB_Point3d[121];
  int index = 0;
  for (int j = 0; j < 11; j++) {
    for (int i = 0; i < 11; i++) {
      points[index]=new WB_Point3d(-200+ i * 40+(((i!=0)&&(i!=10))?random(-20, 20):0),-200+j * 40+(((j!=0)&&(j!=10))?random(-20, 20):0),sin(TWO_PI/20*i)*40+cos(TWO_PI/10*j)*40);
      index++;
    }
  }
  
  //create triangles from point grid
  WB_ExplicitTriangle[] tris=new WB_ExplicitTriangle[200];

  for(int i=0;i<10;i++){
    for(int j=0;j<10;j++){
     tris[2*(i+10*j)]=new WB_ExplicitTriangle(points[i+11*j],points[i+1+11*j],points[i+11*j+11]);
     tris[2*(i+10*j)+1]=new WB_ExplicitTriangle(points[i+1+11*j],points[i+11*j+12],points[i+11*j+11]);
    }  
  }
  
  HEC_FromTriangles creator=new HEC_FromTriangles();
  
  creator.setTriangles(tris);
  //alternatively tris can be any Collection<WB_ExplicitTriangle>
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
  stroke(255,0,0);
  render.drawBoundaryEdges(mesh);
}

