/*

 We switch from different VIEWs with the following keys :
 f = show fill / faces
 s = smooth fill / faces
 e = show only contour / edges
 n = show normals
 h = show half edges
 v = show vertex normals
 
 Other stuff
 + = increase strutOffset
 - = decrease strutOffset
 w = increase TWISTANGLE
 x = decrease TWISTANGLE
 
 UP/DOWN = change archimedes shape
 
 c = png export
 m = export mesh stl
 o = reset camera position
 */


void keyReleased() {

  if (key == 'f') {
    VIEW = 1;
  }
  if (key == 's') {
    VIEW = 2;
  }
  if (key == 'e') {
    VIEW = 3;
  }
  if (key == 'n') {
    VIEW = 4;
  }
  if (key == 'h') {
    VIEW = 5;
  }
  if (key == 'v') {
    VIEW = 6;
  }
  if (key == '+') {
    STRUTOFFSET += 1;
  }

  if (key == '-') {
    STRUTOFFSET -= 1;
  }

  if (key == 'w') {
    TWISTANGLE += radians(5);
  }
  if (key == 'x') {
    TWISTANGLE -= radians(5);
  }
    if ((keyCode == UP) && (ARCHITYPE !=13)) {
    ARCHITYPE ++;
  }
  if ((keyCode == DOWN)&& (ARCHITYPE !=1)) {
    ARCHITYPE --;
  }
  
}

void keyPressed() {

  if (key == 'm') {
    // Hemesh includes a method for exporting geometry
    // in stl file format wich is very handy for 3D printing ;–)
    HET_Export.saveToSTL(MESH, sketchPath("export.stl"), 1.0);
  }

  if (key == 'c') {
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

/////////////////////////// FUNCTIONS ////////////////////////////
void renderMESH() {

  // RENDER is our RENDER class object we call and to which we add 
  // our MESH. The RENDER class has various methods for drawing to the screen
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
    // method for showing halfedges of the MESH (debugging)
    stroke(0, 255, 0);
    RENDER.drawHalfedges( 20, MESH );
    break;

  case 6: 
    // method for showing halfedges of the MESH (debugging)
    stroke(0, 255, 0);

    fill(255, 0, 0);
    RENDER.drawFaces( MESH );
    RENDER.drawVertexNormals(30, MESH );
  }
}

