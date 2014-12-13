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
  creator.setWidthSegments(4).setHeightSegments(4).setDepthSegments(4); // keep these small
  
  //alternatively 
  //creator.setRadius(50);
  //creator.setInnerRadius(50);// radius of sphere inscribed in cube
  //Try these two
  //creator.setOuterRadius(25);// radius of sphere circumscribing cube
  //creator.setMidRadius(10);// radius of sphere tangential to edges
  
  // These params set the initial position & need to be initialised seperately 
  // from other params such as height / setWidthSegments / edges ...
  creator.setCenter(0, 0, 0).setZAxis(1, 1, 1).setZAngle(PI/4);
 
  MESH = new HE_Mesh(creator);
  
  //MESH.triangulate(); // this is one of many methods we can access from the HE_Mesh class
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
  // We draw our faces, vertices and edges using the RENDER object
  noStroke();
  fill(0, 0, 255);
  RENDER.drawFaces( MESH ); // Draw MESH faces
  
  fill(0, 255, 0);
  RENDER.drawVertices( 2, MESH ); // Draw MESH vertices
  
  
  stroke(255,0,0);
  RENDER.drawEdges( MESH ); // Draw MESH edges
  //RENDER.drawFaceNormals( 10, MESH );
}

void keyPressed() {
    if (key == 's') {
    saveFrame("screenShot_###.png");
    println("screen shot taken");
   }
  
}

