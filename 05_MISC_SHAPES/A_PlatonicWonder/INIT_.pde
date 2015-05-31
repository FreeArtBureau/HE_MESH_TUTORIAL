/////////////////////////// INIATILISE GEOMETRY /////////////////

void createMesh() {

  HEC_Plato creator = new HEC_Plato();
  creator.setEdge( 64 );
  creator.setType( PLATOTYPE ); // between 1 & 5
  creator.setCenter(0, 0, 0).setZAxis(1, 1, 1).setZAngle(PI/4);
  println(creator.getName());
  
  MESH =new HE_Mesh(creator);
  RENDER = new WB_Render(this);
}


void createModifiers() {

  //Modifiers are separate objects, not unlike creators
  HEM_Wireframe strut = new HEM_Wireframe();

  //Parameters for each method can be set seperately ...
  strut.setStrutRadius(5);

  //... or all together on one line
  strut.setStrutFacets(10).setMaximumStrutOffset( STRUTOFFSET );
  
  // Finally add our modifier to our mesh
  if(STRUTOFFSET != 0) { // check we don't reach zero which will crash our program
  
  strut.setFidget(9.3);
  MESH.modify( strut );
  
   // INTERSTING MODIFIER 
   MESH.modify( new HEM_Soapfilm().setIterations( 3 ) );
  
  }
  
   HEM_Twist twist = new HEM_Twist().setAngleFactor(TWISTANGLE);
    L = new WB_Line(0, 0, 400, 0, 0, -400);
    twist.setTwistAxis(L);
    MESH.modify(twist);
}

