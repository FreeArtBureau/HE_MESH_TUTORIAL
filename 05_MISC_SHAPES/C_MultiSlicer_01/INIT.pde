/////////////////////////// INIATILISE GEOMETRY /////////////////

void createMesh() {
  HEC_Cube creator = new HEC_Cube(); // Our creator 

  //CREATOR PARMAMETERS
  creator.setEdge(100);
  // Use Small numbers here to create 
  // bowl like shapes
  //  creator.setUFacets(4);
  // creator.setVFacets(4); // try 40>60 for a sphere

  MESH = new HE_Mesh( creator );  // ADD OUR CREATOR PARAMETERS TO OUR MESH

  // Render our meshes
  RENDER = new WB_Render(this);
}


void createModifiers() {
  //CATMULL
  HES_CatmullClark catmullClark = new HES_CatmullClark();

  HEM_Extrude extrude = new HEM_Extrude().setDistance( EXTRUDEDIST);
  extrude.setRelative(false);
  extrude.setChamfer( CHAMFEREDGE ); 

  // SLICE
  HEM_Slice slice = new HEM_Slice();
  P = new WB_Plane(OPLANE_X, OPLANE_X, OPLANE_X, dPLANE_X, dPLANE_Y, dPLANE_Z);
  slice.setPlane( P );

  slice.setCap ( false );
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
}

