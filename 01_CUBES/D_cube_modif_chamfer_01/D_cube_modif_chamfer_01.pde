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
HEC_Cube creator; // Our creator object

// CAM
import peasy.*;
PeasyCam CAM;

/////////////////////////// SETUP ////////////////////////////

void setup() {
  size(800, 600, OPENGL);
  CAM = new PeasyCam(this, 150);  

  creator = new HEC_Cube(); // Our creator 

  //CREATOR PARMAMETERS
  creator.setEdge(60); 
  MESH = new HE_Mesh(creator);  // ADD OUR CREATOR PARAMETERS TO OUR MESH

  // SIMPLE CHAMFER MODIFIER
  HEM_ChamferCorners chamfer = new HEM_ChamferCorners().setDistance(20);
  HEM_ChamferEdges edges = new HEM_ChamferEdges().setDistance(5);

  MESH.modify( chamfer ); // ADD OUR MODIFIER TO THE MESH
  MESH.modify( edges );

  RENDER = new WB_Render(this); // RENDER MESH
}

/////////////////////////// DRAW ////////////////////////////
void draw() {
  background(255);
  //CAMERA
  CAM.beginHUD(); // this method disables PeasyCam for the commands between beginHUD & endHUD
  directionalLight(255, 255, 255, 1, 1, -1);
  directionalLight(127, 127, 127, -1, -1, 1);
  CAM.endHUD();

  //HEMESH
  noStroke();
  fill(0, 0, 255);
  RENDER.drawFaces( MESH ); // Draw MESH faces

  /*
  stroke(0);
   render.drawEdges( MESH ); Draw MESH edges
   */
}

// SOME KEYS INTERACTION
void keyPressed() {

  if (key == 'e') {
    // Hemesh includes a method for exporting geometry
    // in stl file format wich is very handy for 3D printing ;–)
    HET_Export.saveToSTL(MESH, sketchPath("export.stl"), 1.0);
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

