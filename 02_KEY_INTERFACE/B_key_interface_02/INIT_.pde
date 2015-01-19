/////////////////////////// INIATILISE GEOMETRY /////////////////

void createMesh() {

  //HEC_Dodecahedron creator = new HEC_Dodecahedron();
  HEC_Archimedes creator = new HEC_Archimedes();
  creator.setEdge( 64 );
  creator.setType( ARCHITYPE ); // between 1 & 13
  creator.setCenter(0, 0, 0).setZAxis(1, 1, 1).setZAngle(PI/4);
  
  MESH =new HE_Mesh(creator);
  RENDER = new WB_Render(this);
}


void createModifiers() {

  //Modifiers are separate objects, not unlike creators
  HEM_Wireframe modifier = new HEM_Wireframe();

  //Parameters for each method can be set seperately ...
  modifier.setStrutRadius(50);

  //... or all together on one line
  modifier.setStrutFacets(6).setMaximumStrutOffset( STRUTOFFSET );
  
  // Finally add our modifier to our mesh
  if(STRUTOFFSET != 0) { // check we don't reach zero which will crash our program
  MESH.modify(modifier);
  
   // INTERSTING MODIFIER 
   MESH.modify( new HEM_Soapfilm().setIterations( 3 ) );
  
  }
  
   HEM_Twist twist = new HEM_Twist().setAngleFactor(TWISTANGLE);
    L = new WB_Line(0, 0, 400, 0, 0, -400);
    twist.setTwistAxis(L);
    MESH.modify(twist);
}

