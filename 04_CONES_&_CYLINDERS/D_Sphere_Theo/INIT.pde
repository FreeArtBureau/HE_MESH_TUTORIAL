/////////////////////////// INIATILISE GEOMETRY /////////////////

void createMesh() {


  HEC_Sphere creator = new HEC_Sphere(); // Our creator 
  creator.setRadius(100);
  creator.setUFacets( FACETS );
  creator.setVFacets( FACETS ); // try 40>60 for a sphere
  
  MESH = new HE_Mesh(creator);  // ADD OUR CREATOR PARAMETERS TO OUR MESH 

  if ( EXTRUDE ) {
    makeSelection();
  }
}


void createModifiers() {

  //Extrude Modifier
  if ( EXTRUDE ) {
    
     EXTRUDEDIST = (int)map(mouseX, 0, width, 1, 50);
    
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
    strut.setFidget(7.3);
    strut.setTaper(true);
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

