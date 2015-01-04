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

// CAM
import peasy.*;
PeasyCam CAM;

/////////////////////////// SETUP ////////////////////////////

void setup() {
  size(800, 600, OPENGL);
  CAM = new PeasyCam(this, 200);  
  
  // OUR CREATOR
  HEC_Cube creator = new HEC_Cube(); 
  
  //CREATOR PARMAMETERS
  creator.setEdge(60); // edge length in pixels
  MESH = new HE_Mesh(creator); // add our creator object to our mesh object

  // MODIFIER
  HEM_Wireframe strut = new HEM_Wireframe();
  //Parameters for each method can be set seperately ...
  strut.setStrutRadius(100);

  // assigning 3 to setStrutFacets
  // and assigning values 50+ for setMaximumStrutOffset
  // gives odd geometries
  strut.setStrutFacets(3); // no more than 15, no less than 3
  strut.setMaximumStrutOffset(5);
  strut.setTaper(true);
  
  //MESH.triangulate();
  MESH.modify( strut );
  RENDER = new WB_Render(this); // RENDER object initialise
}

/////////////////////////// DRAW ////////////////////////////
void draw() {
  background(255);
  CAM.beginHUD(); // this method disables PeasyCam for the commands between beginHUD & endHUD
  directionalLight(255, 255, 255, 1, 1, -1);
  directionalLight(127, 127, 127, -1, -1, 1);
  CAM.endHUD();
  
  // We draw our faces using the RENDER object
  /*
  noStroke();
  fill(0, 0, 255);
  RENDER.drawFaces( MESH ); // DRAW MESH FACES
  */
  stroke(0, 0, 255);
  strokeWeight(.5);
  RENDER.drawEdges( MESH );

}

// SOME KEYS INTERACTION
void keyPressed() {

  if (key == 'e') {
    // Hemesh includes a method for exporting geometry
    // in stl file format wich is very handy for 3D printing ;–)
    HET_Export.saveToSTL(MESH, sketchPath("export_###.stl"), 1.0);
  }
   if (key == 's') {
    saveFrame("screenShot_###.png");
    println("screen shot taken");
   }
   
  if (key == 'o') {
    // reset camera origin positions  - do this before
    // exporting your shape so your shape is positioned
    // on a flat plane 
    CAM.reset(1000);
  }
  // Print camera position - could be helpful
  if (key == 'p') {
    float[] camPos = CAM.getPosition();
    println(camPos);
  }
}

