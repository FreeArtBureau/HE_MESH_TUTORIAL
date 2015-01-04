/*
PLEASE READ INTRO TAB
 */

/////////////////////////// GLOBALS ////////////////////////////
// LIBRARY IMPORT
import wblut.math.*;
import wblut.processing.*;
import wblut.core.*;
import wblut.hemesh.*;
import wblut.geom.*;
import processing.opengl.*;

// HEMESH CLASSES & OBJECTS
HE_Mesh MESH; // Our mesh object
WB_Render RENDER; // Our render object

// CAM
import peasy.*;
PeasyCam CAM;

/////////////////////////// SETUP ////////////////////////////

void setup() {
  size(800, 600, OPENGL);
  CAM = new PeasyCam(this, 400);  

  // OUR CREATOR
  HEC_Cube creator = new HEC_Cube(); 

  //CREATOR PARMAMETERS
  creator.setEdge(70); 
  creator.setCenter(0, 0, 0).setZAxis(1, 1, 1).setZAngle(PI/4);
  // Activate this line to see what happens
  //creator.setWidthSegments(4).setHeightSegments(4).setDepthSegments(4); // keep these small

  MESH = new HE_Mesh(creator); // add our creator object to our mesh object

  // MODIFIER : SIMPLE EXTRUSION MODIFIER 
  HEM_Extrude extrude = new HEM_Extrude().setDistance(70);
  MESH.modify( extrude ); // ADD OUR MODIFIER TO THE MESH

  RENDER = new WB_Render(this); 
}

/////////////////////////// DRAW ////////////////////////////
void draw() {
  background(255);
  // CAMERA
  CAM.beginHUD(); 
  directionalLight(255, 255, 255, 1, 1, -1);
  directionalLight(127, 127, 127, -1, -1, 1);
  CAM.endHUD();
  
  // HEMESH
  // To visualise better what is happening, I've activated the edges and disactivated the faces.
  /*
  noStroke();
  fill(0, 0, 255);
  RENDER.drawFaces( MESH ); // Draw MESH faces
  */
  
  stroke(0, 0, 255);
  strokeWeight(5);
  RENDER.drawEdges( MESH ); // Draw MESH edges
}

void keyPressed() {
  if (key == 'r') {
    setup();
  }
  if (key == 's') {
    saveFrame("screenShot.png");
  }
}

