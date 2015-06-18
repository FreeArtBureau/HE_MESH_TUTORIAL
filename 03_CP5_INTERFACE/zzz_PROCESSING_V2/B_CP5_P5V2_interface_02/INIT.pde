/////////////////////////// INIATILISE GEOMETRY /////////////////

void createMesh() {

  HEC_Cube creator = new HEC_Cube(); // Our creator 

  //CREATOR PARMAMETERS
  creator.setEdge(60); 
  MESH = new HE_Mesh( creator );  // ADD OUR CREATOR PARAMETERS TO OUR MESH
  creator.setWidthSegments(4).setHeightSegments(4).setDepthSegments(4); // keep these small
  
  // Render our meshes
  RENDER = new WB_Render(this);
}


void createModifiers() {

  //--------------------- First Modifier
  // SIMPLE CHAMFER MODIFIER
  HEM_Wireframe strut = new HEM_Wireframe();
  //Parameters for each method can be set seperately ...
  strut.setStrutRadius(STRUTRADIUS);
  strut.setStrutFacets((int)STRUTRADIUS/20); 
  
  strut.setMaximumStrutOffset(STRUTOFFSET);
  //strut.setTaper(true);

  HEM_ChamferCorners chamfer = new HEM_ChamferCorners().setDistance(CHAMFERDIST);
  HEM_ChamferEdges edges = new HEM_ChamferEdges().setDistance(CHAMFEREDGE);
  
  // ADD OUR MODIFIERS TO THE MESH
  MESH.modify( chamfer ); 
  MESH.modify( edges );
     
  if(STRUT) {
  MESH.modify( strut );
  }
 
}

