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
  CAM = new PeasyCam(this, 150);  

  // OUR CREATOR 
  HEC_Cube creator = new HEC_Cube(); 

  //CREATOR PARMAMETERS
  creator.setEdge(70); // edge length in pixels

  MESH = new HE_Mesh(creator); // add our creator object to our mesh object
  RENDER = new WB_Render(this); // RENDER object initialise
}

/////////////////////////// DRAW ////////////////////////////
void draw() {
  background(255);
  // CAMERA
  CAM.beginHUD(); // this method disables PeasyCam for the commands between beginHUD & endHUD
  directionalLight(255, 255, 255, 1, 1, -1);
  directionalLight(127, 127, 127, -1, -1, 1);
  CAM.endHUD();

  // HEMESH
  // We draw our faces using the RENDER object
  noStroke();
  fill(0, 255, 255);
  RENDER.drawFaces( MESH ); // Draw MESH faces

  stroke(255, 0, 0);
  RENDER.drawEdges( MESH ); // Draw MESH edges
}

