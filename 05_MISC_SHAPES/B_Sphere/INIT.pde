/////////////////////////// INIATILISE GEOMETRY /////////////////

void createMesh() {
  HEC_Sphere creator = new HEC_Sphere(); // Our creator 

  //CREATOR PARMAMETERS
  creator.setRadius(100);
  // Use Small numbers here to create 
  // bowl like shapes
  creator.setUFacets( FACETS );
  creator.setVFacets( FACETS ); // try 40>60 for a sphere

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
  P = new WB_Plane(0, 0, 0, 0, 0, 1);
  slice.setPlane( P );

  slice.setCap ( false );
  slice.setKeepCenter( false );
  


  // ADD OUR MODIFIERS TO THE MESH
  MESH.modify( extrude );

  if ( CATMULL ) {
    catmullClark.setBlendFactor( BLENDING );  
    MESH.subdivide( catmullClark, 2 ); // add a var here for 5 which is the n° of divisions
  }
  
  if ( SLICE ) {
  slice.setOffset( SLICE_OFFSET );
  MESH.modify( slice );  
  }
}

