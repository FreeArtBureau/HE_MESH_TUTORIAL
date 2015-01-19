/////////////////////////// INIATILISE GEOMETRY /////////////////

void createMesh() {

  HEC_Cone creator = new HEC_Cone(); // Our creator 

  //CREATOR PARMAMETERS
  creator.setFacets(5); 
  creator.setHeight(70).setRadius(40); // 
  creator.setSteps(12).setTaper(1.7); // Vertical divisions & taper

  //alternatively
  //creator.setReverse(true); // reverses the cone
  //creator.setCap(false);
  MESH = new HE_Mesh(creator);  // ADD OUR CREATOR PARAMETERS TO OUR MESH
}


void createModifiers() {

  // WIREFRAME
  HEM_Wireframe strut = new HEM_Wireframe();

  //Parameters for each method can be set seperately ...
  strut.setStrutRadius( 50 );
  strut.setStrutFacets( 8 ); // no more than 15, no less than 3
  strut.setMaximumStrutOffset(4);
  strut.setTaper(true);
  // 
  //strut.setFidget(3.3);

  MESH.modify( strut );

 
  // BEND MODIFIER
   HEM_Bend bend =  new HEM_Bend().setAngleFactor(3);
   P = new WB_Plane(0, 0, 0, 0, 0, 1); 
   bend.setGroundPlane(P); 
   L = new WB_Line(0, 0, 1, 0, 0, -1);
   bend.setBendAxis(L);
   MESH.modify( bend );
   

  /*
  // STRETCH MODIFIER
  HEM_Stretch stretch = new HEM_Stretch();
  P = new WB_Plane(0, 0, 0, 0, 0, -1);
  stretch.setGroundPlane( P );

  stretch.setCompressionFactor( 15 ); // not sure what this is actually doing?
  stretch.setStretchFactor( STRETCH_FACT );
  MESH.modify( stretch );
  */
  // INTERSTING MODIFIER
  MESH.modify( new HEM_Soapfilm().setIterations(3)); 
  HET_Diagnosis.validate( MESH );

  RENDER = new WB_Render(this); // RENDER MESH
}

