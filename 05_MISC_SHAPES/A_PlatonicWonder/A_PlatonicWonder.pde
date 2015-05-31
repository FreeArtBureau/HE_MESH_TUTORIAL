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
int STRUTOFFSET; 
int PLATOTYPE; 

WB_Line L;
float TWISTANGLE;

HE_Mesh MESH;
WB_Render RENDER;

/////////////////////////// SETUP ////////////////////////////
void setup() {
  size(800, 600, OPENGL);
  background(255);
  smooth();
  noStroke();
  CAM = new PeasyCam(this, 300);

  VIEW = 1;
  PLATOTYPE = 1;
  STRUTOFFSET = 20;
  TWISTANGLE = radians(0);
  // Our two functions to create our mesh & modifiers  
  createMesh();
  createModifiers();
}

/////////////////////////// DRAW ////////////////////////////
void draw() {
  background(255);
  lights();
  directionalLight(255, 255, 255, 1, 1, -1);
  directionalLight(127, 127, 127, -1, -1, 1);
  fill(0, 0, 255);
  //fill(#FA890F);
  // 
  createMesh();
  createModifiers();
  renderMESH();
}


