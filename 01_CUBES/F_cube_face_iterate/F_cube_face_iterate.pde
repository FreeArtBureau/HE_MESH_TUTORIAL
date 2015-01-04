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
import java.util.*;
import processing.opengl.*;

// HEMESH CLASSES & OBJECTS
HE_Mesh MESH; // Our mesh object
WB_Render RENDER; // Our render object
HE_Selection SELECTION;

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
  creator.setEdge(70); // edge length in pixels
  creator.setWidthSegments(4).setHeightSegments(4).setDepthSegments(4); // keep these small

  creator.setCenter(0, 0, 0).setZAxis(1, 1, 1).setZAngle(PI/4);

  MESH = new HE_Mesh(creator); // add our creator object to our mesh object

  //DEFINE A SELECTION
  SELECTION = new HE_Selection( MESH );  

  //ADD FACES TO SELECTION
  Iterator<HE_Face> fItr = MESH.fItr();
  HE_Face f;
  while (fItr.hasNext ()) {
    f = fItr.next();
    color c = color(random(255), random(100), random(255));
    f.setLabel(c);
    if (random(30)<10) {
      SELECTION.add(f);
    }
  }

  // MODIFIER : SIMPLE EXTRUSION MODIFIER 
  HEM_Extrude extrude = new HEM_Extrude().setDistance(15);

  // use the modifySelected method for only modifying selected faces 
  MESH.modifySelected( extrude, SELECTION ); 

  RENDER = new WB_Render(this); // RENDER object initialise
}

/////////////////////////// DRAW ////////////////////////////
void draw() {
  background(255);
  CAM.beginHUD(); // this method disables PeasyCam for the commands between beginHUD & endHUD
  directionalLight(255, 255, 255, 1, 1, -1);
  directionalLight(127, 127, 127, -1, -1, 1);
  CAM.endHUD();

  noStroke();
  // Iterate selected faces
  Iterator<HE_Face> fItr = SELECTION.fItr();
  HE_Face f;
  while (fItr.hasNext ()) {
    f = fItr.next();
    color c = f.getLabel();
    fill(c);
    RENDER.drawFace( f );
  }
  /*
  fill(255, 173); // Added a little transparency here
   RENDER.drawFaces( MESH ); // DRAW MESH FACES
   */
  stroke(0, 0, 255);
  strokeWeight(.5);
  RENDER.drawEdges( MESH ); // DRAW MESH EDGES
}

void keyPressed() {
  if (key == 'r') {
    setup();
  }
  if (key == 's') {
    saveFrame("screenShot.png");
  }
}

