
import wblut.math.*;
import wblut.processing.*;
import wblut.core.*;
import wblut.hemesh.*;
import wblut.geom.*;

HE_Mesh mesh;
WB_Render render;

void setup() {
  size(800, 800, P3D);
  
  //create base points for a single hexagon
  WB_Point3d[] basepoints =new WB_Point3d[6];
  for (int i=0;i<6;i++) {
   basepoints[i]=new WB_Point3d(0,100,0);
   if(i>0) basepoints[i].rotateAboutAxis(Math.PI/3.0*i,0,0,0,0,0,1);
  }
  
  //create polygons from shifted copies of base points
  WB_Polygon[] polygons=new WB_Polygon[7];
  double ds=Math.cos(Math.PI/6)*200;
  polygons[0]=new WB_ExplicitPolygon(basepoints,6);
  for(int i=0;i<6;i++){
    WB_Point3d[] localpoints=new WB_Point3d[6];
    for(int j=0;j<6;j++){
     localpoints[j]=basepoints[j].addAndCopy(ds,0,0);//Shift base polygon
     localpoints[j].rotateAboutAxis(Math.PI/3.0*i,0,0,0,0,0,1);//Rotate shifted polygon
    }
    polygons[i+1]=new WB_ExplicitPolygon(localpoints,6);
  }
  
  HEC_FromPolygons creator=new HEC_FromPolygons();
  
  creator.setPolygons(polygons);
  //alternatively polygons can be any Collection<WB_Polygon>
  mesh=new HE_Mesh(creator); 
  //Uncomment for a fun little combination with HEC_FromFrame
  //mesh=new HE_Mesh(new HEC_FromFrame().setFrame(mesh));
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
  stroke(255,0,0);
  render.drawBoundaryEdges(mesh);
}

