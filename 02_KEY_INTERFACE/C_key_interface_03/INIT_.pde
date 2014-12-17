/////////////////////////// INIATILISE GEOMETRY /////////////////

void createMesh() {

  HEC_Archimedes creator1 = new HEC_Archimedes();
  creator1.setType( ARCHITYPE ); // between 1 & 13
  creator1.setEdge( 64 );
  creator1.setCenter(0, 0, 0).setZAxis(1, 1, 1).setZAngle(PI/4);

  HEC_Dodecahedron creator2 = new HEC_Dodecahedron();
  creator2.setEdge( 64 );
  creator2.setCenter(0, 0, 0).setZAxis(1, 1, 1).setZAngle(PI/4);


  HEC_Octahedron creator3 = new HEC_Octahedron(); // Our creator 
  creator3.setEdge(70); 
  //Try these two
  creator3.setOuterRadius(-30);// radius of sphere circumscribing cube
  creator3.setMidRadius(-70);// radius of sphere tangential to edges


  // Archimedes Solids
  if (MODE == 0) {
    MESH =new HE_Mesh(creator1);
  }

  // Dodecahedron 
  if (MODE == 1) {
    MESH =new HE_Mesh(creator2);
  }

  // Octahedron 
  if (MODE == 2) {
    MESH =new HE_Mesh(creator3);
  }

  RENDER = new WB_Render(this);
}


void createModifiers() {

   if ( EXTRUDE ) {
    HEM_Extrude extrude = new HEM_Extrude().setDistance(25);
    MESH.modify( extrude ); // ADD OUR MODIFIER TO THE MESH

    HEM_Lattice lattice = new HEM_Lattice().setDepth(45);
    MESH.modify( lattice );
  }
  
  //Modifiers are separate objects, not unlike creators
  HEM_Wireframe modifier = new HEM_Wireframe();

  //Parameters for each method can be set seperately ...
  modifier.setStrutRadius(50);

  //... or all together on one line
  modifier.setStrutFacets( STRUTFACETS ).setMaximumStrutOffset( STRUTOFFSET );

  // Finally add our modifier to our mesh
  if (STRUTOFFSET != 0) { // check we don't reach zero which will crash our program
    MESH.modify(modifier);

    // INTERSTING MODIFIER 
    MESH.modify( new HEM_Soapfilm().setIterations( 3 ) );
  }

  HEM_Twist twist = new HEM_Twist().setAngleFactor(TWISTANGLE);
  L = new WB_Line(0, 0, 400, 0, 0, -400);
  twist.setTwistAxis(L);
  MESH.modify(twist);
}

