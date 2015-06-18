/////////////////////////// INIATILISE GEOMETRY /////////////////

void createMesh() {

  HEC_Cube CREATOR = new HEC_Cube(); // Our creator 

  //CREATOR PARMAMETERS
  CREATOR.setEdge(60); 
  MESH = new HE_Mesh(CREATOR);  // ADD OUR CREATOR PARAMETERS TO OUR MESH
//  CREATOR.setWidthSegments(4).setHeightSegments(4).setDepthSegments(4); // keep these small

}


void createModifiers() {

  //--------------------- First Modifier
  // SIMPLE CHAMFER MODIFIER
  HEM_ChamferCorners chamfer = new HEM_ChamferCorners().setDistance(CHAMFERDIST);
  HEM_ChamferEdges edges = new HEM_ChamferEdges().setDistance(CHAMFEREDGE);

  MESH.modify( chamfer ); // ADD OUR MODIFIERS TO THE MESH
  MESH.modify( edges );
  // Render our meshes
  RENDER = new WB_Render(this);

}

