/*
PLEASE READ INTRO TAB
 */

/////////////////////////// GLOBALS ////////////////////////////
// LIBRARY
import wblut.math.*;
import wblut.processing.*;
import wblut.core.*;
import wblut.hemesh.*;
import wblut.geom.*;
import processing.opengl.*;

// HEMESH CLASSES & OBJECTS
HE_Mesh MESH; // Our mesh object
WB_Render RENDER; // Our render object

// Check these two objects
//WB_Plane P; 
WB_Line L;

// CAM
import peasy.*;
PeasyCam CAM;
int FRAME = 0;

/////////////////////////// SETUP ////////////////////////////

void setup() {
  size(600, 600, OPENGL);
  CAM = new PeasyCam(this, 200);  
  createMesh();
  createModifiers();
}

/////////////////////////// DRAW ////////////////////////////
void draw() {
  background(255);
  directionalLight(255, 130, 7, 1, 0, -1);
  directionalLight(255, 130, 7, -1, 0, 1);

  noStroke();
  fill(255);
  RENDER.drawFaces( MESH ); // DRAW MESH

  /*
   stroke(0);
   strokeWeight(0.5);
   RENDER.drawEdges( MESH );
   */
}

// SOME KEYS INTERACTION
void keyPressed() {

  if (key == 'e') {
    // Hemesh includes a method for exporting geometry
    // in stl file format wich is very handy for 3D printing ;–)
    HET_Export.saveToSTL(MESH, sketchPath("export"+ FRAME+++".stl"), 1.0);
  }

  if (key == 's') {
    saveFrame("screenShot_###.png");
    println("screen shot taken");
  }
  if (key == 'o') {
    // reset camera origin positions  - do this before
    // exporting your shape so your shape is positioned
    // on a flat plane ready for 3D printing
    CAM.reset(1000);
  }
  // Print camera position - could be helpful
  if (key == 'p') {
    float[] camPos = CAM.getPosition();
    println(camPos);
  }
}

