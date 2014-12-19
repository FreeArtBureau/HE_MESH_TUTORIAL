/////////////////////////// INIATILISE GEOMETRY /////////////////

void createMesh() {

  HEC_Cylinder creator=new HEC_Cylinder();

  //CREATOR PARMAMETERS
  creator.setFacets(30); 
  creator.setHeight(70).setRadius(40); // 
  creator.setSteps(12).setCap(false, false); // Vertical divisions & taper
  creator.setTaper(1.7);
  
  MESH = new HE_Mesh(creator);  // ADD OUR CREATOR PARAMETERS TO OUR MESH

}


void createModifiers() {

  // BEND MODIFIER
   // Twist Modifier
  HEM_Twist twist = new HEM_Twist().setAngleFactor( 2 );
  L = new WB_Line(1, 0, 1, -1, 0, -1);
  twist.setTwistAxis(L);
  MESH.modify(twist);
  
  // INTERSTING MODIFIER
  MESH.modify( new HEM_Soapfilm().setIterations(3) ); 
  HET_Diagnosis.validate( MESH );

  RENDER = new WB_Render(this); // RENDER MESH
}
