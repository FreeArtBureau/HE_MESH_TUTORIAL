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

WB_Line L;
float TWISTANGLE;

// CAM
import peasy.*;
PeasyCam CAM;
int FRAME = 0;

//ControlP5
import controlP5.*;

ControlP5 INTERFACES;
ControlWindow CW;
int  VERTICALSTEPS, CYLINDERHEIGHT, RADIUSTOP, RADIUSBOTTOM, FACETS, 
STEPS, STRUTOFF; 
boolean STRUT;

/////////////////////////// SETUP ////////////////////////////

void setup() {
  size(800, 800, OPENGL);
  CAM = new PeasyCam(this, 400);
  VERTICALSTEPS  = 6;
  createMesh();
  createModifiers();
  controlInit();
  
}

/////////////////////////// DRAW ////////////////////////////
void draw() {
  background(255);
  CAM.beginHUD(); // this method disables PeasyCam for the commands between beginHUD & endHUD
  directionalLight(255, 255, 255, 1, 1, -1);
  directionalLight(255, 127, 127, -1, -1, 1);
  CAM.endHUD();

  createMesh();
  createModifiers();
  noStroke();
  fill(0, 0, 255);
  RENDER.drawFaces( MESH ); // DRAW MESH

}

/////////////////////////// FUNCTIONS ////////////////////////////

// SOME KEYS INTERACTION
void keyPressed() {

  if (key == 'e') {
    // Hemesh includes a method for exporting geometry
    // in stl file format wich is very handy for 3D printing ;–)
    HET_Export.saveToSTL(MESH, sketchPath("export"+ FRAME+++".stl"), 1.0);
    // obj file format for further 3D raytracing rendering ( SUNFLOW )
    HET_Export.saveToOBJ(MESH, sketchPath("export"+ FRAME+++".obj"));
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


