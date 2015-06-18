/////////////////////////// INIATILISE GEOMETRY /////////////////

void createMesh() {

  HEC_Cylinder creator=new HEC_Cylinder();

  //CREATOR PARMAMETERS
  creator.setRadius(RADIUSTOP, RADIUSBOTTOM); // upper and lower radius. If one is 0, HEC_Cone is called. 
  creator.setHeight(CYLINDERHEIGHT);
  creator.setFacets(FACETS).setSteps(STEPS);
  creator.setCap(true, false);// cap top, cap bottom?
  //Default axis of the cylinder is (0,1,0). To change this use the HEC_Creator method setZAxis(..).
  //  creator.setZAxis(0,0,0);
  MESH = new HE_Mesh(creator);  // ADD OUR CREATOR PARAMETERS TO OUR MESH 

  if ( EXTRUDE ) {
    makeSelection();
  }
}


void createModifiers() {

  //Extrude Modifier
  if ( EXTRUDE ) {
    HEM_Extrude extrude;
    extrude = new HEM_Extrude();
    extrude.setDistance(EXTRUDEDIST);
    MESH.modifySelected( extrude, SELECTION );
  }
  // Twist Modifier
  HEM_Twist twist = new HEM_Twist().setAngleFactor(TWISTANGLE);
  L = new WB_Line(0, 0, 400, 0, 0, -400);
  twist.setTwistAxis(L);
  MESH.modify(twist);

  if ( STRUT ) { 
    HEM_Wireframe strut = new HEM_Wireframe();
    //Parameters for each method can be set seperately ...
    strut.setStrutRadius(200);
    strut.setStrutFacets((int)200/20); // no more than 15, no less than 3
    //  strut.setStrutFacets(8); // no more than 15, no less than 3
    strut.setMaximumStrutOffset(STRUTOFF);
    strut.setTaper(true);
    strut.setFidget( FINESSE );
    MESH.modify( strut );
  }


  RENDER = new WB_Render(this); // RENDER MESH
}

void makeSelection() {
  //DEFINE A SELECTION
  SELECTION = new HE_Selection( MESH ); 

  //ADD FACES TO SELECTION
  Iterator<HE_Face> fItr = MESH.fItr();
  HE_Face f;
  while (fItr.hasNext ()) {
    f = fItr.next();

    if (random(30)<FACETS) 
      SELECTION.add(f);
  }
}

