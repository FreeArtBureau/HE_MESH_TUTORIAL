/*

We switch from different views with the following keys :
f = show fill / faces
s = smooth fill / faces
e = show only contour / edges
n = show normals
h/t = show half edges

Other stuff
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
    if (key == 't') {
    VIEW = 6;
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

