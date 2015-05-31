/////////////////////////// INIATILISE GEOMETRY /////////////////

void createMesh() {
  
  HEC_Plato creator = new HEC_Plato();
  creator.setEdge( 64 );
  creator.setType( PLATOTYPE ); // between 1 & 5
  creator.setCenter(0, 0, 0).setZAxis(1, 1, 1).setZAngle(PI/4);
  
  MESH =new HE_Mesh(creator);
  RENDER = new WB_Render(this);
}


void createModifiers() {
  //CATMULL
  HES_CatmullClark catmullClark = new HES_CatmullClark();
  
  //EXTRUDE
  HEM_Extrude extrude = new HEM_Extrude().setDistance( EXTRUDEDIST);
  extrude.setRelative(false);
  extrude.setChamfer( CHAMFEREDGE ); 
  
  //STRETCH
  HEM_Stretch stretching = new HEM_Stretch();
  stretching.setCompressionFactor( 0.3);
  WB_Plane PStretch = new WB_Plane(0.1, 0, 0, -0.1, 0, 0);
  stretching.setGroundPlane( PStretch );
  
  // SLICE
  HEM_Slice slice = new HEM_Slice();
  P = new WB_Plane(OPLANE_X, OPLANE_X, OPLANE_X, dPLANE_X, dPLANE_Y, dPLANE_Z);
  slice.setPlane( P );

  slice.setCap ( SLICE_CAP );
  slice.setKeepCenter( true );

  /*
  // MULTISLICE SURFACE
   HEM_MultiSliceSurface multi = new HEM_MultiSliceSurface();
   // Various planes
   WB_Plane[] planes = new WB_Plane[2];
   planes[0] = new WB_Plane(OPLANE_X, OPLANE_Y, OPLANE_Z, dPLANE_X, dPLANE_Y, dPLANE_Z);
   float offsetX, offsetY, offsetZ;
   offsetX = 5;
   offsetY = 5;
   offsetZ = 5;
   planes[1] = new WB_Plane(OPLANE_X+offsetX, OPLANE_Y+offsetY, OPLANE_Z+offsetZ, dPLANE_X+offsetX, dPLANE_Y+offsetY, dPLANE_Z+offsetZ);
   
   multi.setPlanes( planes );
   MESH.modify( multi );
   */

  // ADD OUR MODIFIERS TO THE MESH
  MESH.modify( extrude );

  if ( CATMULL ) {
    catmullClark.setBlendFactor( BLENDING );  
    MESH.subdivide( catmullClark, 2 ); // add a var here for 2 which is the n° of divisions
  }

  if ( SLICE ) {
    slice.setOffset( -30 );
    MESH.modify( slice );
  }
  
  if(STRETCH) {
   stretching.setStretchFactor(STRETCH_FACT);
  MESH.modify( stretching ); 
    
  }
}

