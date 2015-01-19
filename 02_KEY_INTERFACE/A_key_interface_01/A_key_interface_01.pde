/*
PLEASE READ INTRO TAB
 */

/////////////////////////// GLOBALS ////////////////////////////
import wblut.math.*;
import wblut.processing.*;
import wblut.core.*;
import wblut.hemesh.*;
import wblut.geom.*;

import processing.opengl.*;
import peasy.*;

PeasyCam CAM;
int VIEW;

HE_Mesh MESH;
WB_Render RENDER;
/////////////////////////// SETUP ////////////////////////////

void setup() {
  size(800, 600, OPENGL);
  background(255);
  smooth();
  noStroke();
  VIEW = 1;

  // Our creator and it's params.
  HEC_Dodecahedron creator = new HEC_Dodecahedron();
  creator.setEdge(64);
  creator.setCenter(0, 0, 0).setZAxis(1, 1, 1).setZAngle(PI/4);

  // Here we create a modifier and initialise it 
  HEM_Wireframe strut = new HEM_Wireframe();

  //Parameters for each method of the modifier class can be set seperately ...
  strut.setStrutRadius(50);

  // This method will affine the structures between the connections
  strut.setFidget(2.3);

  //... or all together on one line
  strut.setStrutFacets(6).setMaximumStrutOffset(20);

  // IMPORTANT
  // We first add the creator object to the mesh before modifiying it !
  MESH = new HE_Mesh(creator);

  // and then we add our modifer to our mesh using the modify method
  MESH.modify( strut );  

  RENDER = new WB_Render(this);
  CAM = new PeasyCam(this, 300);
}

/////////////////////////// DRAW ////////////////////////////
void draw() {
  background(255);
  lights();
  directionalLight(255, 255, 255, 1, 1, -1);
  directionalLight(127, 127, 127, -1, -1, 1);
  fill(0, 0, 255);

  renderMESH();
}

/////////////////////////// FUNCTIONS ////////////////////////////
void renderMESH() {

  // RENDER is our Render class object we call and to which we add 
  // our mesh. The render class has various methods for drawing to the screen
  switch(VIEW) {
  case 1:
    noStroke();
    RENDER.drawFaces( MESH );
    break;

  case 2:  
    // Nice method for smoothing !
    noStroke();
    RENDER.drawFacesSmooth( MESH );
    break;
  case 3: 
    stroke(255, 0, 0);
    strokeWeight(0.5);
    RENDER.drawEdges( MESH );
    break;

    // AND SOME MORE METHODS OF THE RENDER CLASS
  case 4: 
    // Method for showing the normals
    stroke(0, 255, 0);
    RENDER.drawFaces( MESH );
    RENDER.drawFaceNormals( 20, MESH ); // first param is the length
    break;

  case 5: 
    // method for showing halfedges of the mesh (debugging)
    stroke(0, 255, 0);
    RENDER.drawHalfedges( 20, MESH );
    break;

  case 6: 
    // method for showing halfedges of the mesh (debugging)
    stroke(0, 255, 0);
    fill(255, 0, 0);
    RENDER.drawFaces( MESH );
    RENDER.drawVertexNormals(30, MESH );
  }
}



