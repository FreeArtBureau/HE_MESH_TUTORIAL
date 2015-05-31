
////////////////// ControlP5 INTERFACE

void controlInit() {
  INTERFACES = new ControlP5(this);  
  INTERFACES.setAutoDraw(false); 
  // Create a new window (doesn't work in Processing 2.0+)
  CW = INTERFACES.addControlWindow("controlP5window", 10, 10, 200, 420, 30);

  //Create sliders 
  Slider extrude = INTERFACES.addSlider("EXTRUDEDIST").setPosition(10, 20).setRange(0, 50).setValue(10); 
  extrude.getCaptionLabel().setColorBackground(color(255, 20, 30)); // changer la couler de fond pour le texte
  extrude.getCaptionLabel().getStyle().setPadding(4, 2, 2, 2); // changer sa taille (pour le texte)
  // shift the caption label up by 4px
  extrude.getCaptionLabel().getStyle().setMargin(-5, 0, 0, 0); 

  Slider chamfEdge = INTERFACES.addSlider("CHAMFEREDGE").setPosition(10, 40).setRange(0, 20).setValue(1); 
  Slider catmullBlend = INTERFACES.addSlider("BLENDING").setPosition(10, 60).setRange(0.01, 3.00).setValue(0.50); 
  
  
  Slider rad = INTERFACES.addSlider("RADIUSBOTTOM").setPosition(10, 80).setRange(1, 100).setValue(40); 
  Slider face = INTERFACES.addSlider("FACETS").setPosition(10, 100).setRange(1, 100).setValue(6); 
  Slider step = INTERFACES.addSlider("STEPS").setPosition(10, 120).setRange(1, 100).setValue(6); 
  Slider cHeight = INTERFACES.addSlider("CONEHEIGHT").setPosition(10, 140).setRange(1, 300).setValue(6); 
  Slider radTop = INTERFACES.addSlider("RADIUSBOTOP").setPosition(10, 160).setRange(1, 100).setValue(6); 

 Slider type = INTERFACES.addSlider("PLATOTYPE").setPosition(10, 180).setRange(1, 5).setValue(1); 

  
  Toggle t1 = INTERFACES.addToggle("CAP").setPosition(10, 190).setSize(15, 15).setValue(false).setLabel("CAP");

  Toggle t2 = INTERFACES.addToggle("CATMULL").setPosition(60, 190).setSize(15, 15).setValue(false).setLabel("CatMull");
  Toggle t3 = INTERFACES.addToggle("SLICE").setPosition(10, 220).setSize(15, 15).setValue(false).setLabel("Slice");
  Toggle t4 = INTERFACES.addToggle("SLICE_CAP").setPosition(60, 220).setSize(15, 15).setValue(false).setLabel("Slice Cap");

  Toggle t5 = INTERFACES.addToggle("STRETCH").setPosition(90, 190).setSize(15, 15).setValue(false).setLabel("Stretch");

  //Slider sliceOff = INTERFACES.addSlider("SLICE_OFFSET").setPosition(10, 210).setRange(-100, 100).setValue(0); 
  
  
  Slider oPlaneX = INTERFACES.addSlider("OPLANE_X").setPosition(10, 270).setRange(-120, 120).setValue(2);
//  .setNumberOfTickMarks(13); 
  //Slider oPlaneY = INTERFACES.addSlider("OPLANE_Y").setPosition(10, 250).setRange(-100, 100).setValue(10); 
  //Slider oPlaneZ = INTERFACES.addSlider("OPLANE_Z").setPosition(10, 270).setRange(-100, 100).setValue(10); 

  Slider dPlaneX = INTERFACES.addSlider("dPLANE_X").setPosition(10, 290).setRange(-12, 12).setValue(12)
 .setNumberOfTickMarks(13);  
  Slider dPlaneY = INTERFACES.addSlider("dPLANE_Y").setPosition(10, 310).setRange(-12, 12).setValue(0)
  .setNumberOfTickMarks(13); 
  Slider dPlaneZ = INTERFACES.addSlider("dPLANE_Z").setPosition(10, 330).setRange(-12, 12).setValue(0)
  .setNumberOfTickMarks(13); 

  Slider stretchy = INTERFACES.addSlider("STRETCH_FACT").setPosition(10, 350).setRange(0.01,5.0 ).setValue(0.5);

  // Add elements to new window
  extrude.setWindow(CW);
  chamfEdge.setWindow(CW);
  //sliceOff.setWindow(CW);
  rad.setWindow(CW);
  face.setWindow(CW);
  step.setWindow(CW);
  cHeight.setWindow(CW);
  radTop.setWindow(CW);
   type.setWindow(CW);
  
  catmullBlend.setWindow(CW);
  oPlaneX.setWindow(CW);
  //oPlaneY.setWindow(CW);
  //oPlaneZ.setWindow(CW);
  dPlaneX.setWindow(CW);
  dPlaneY.setWindow(CW);
  dPlaneZ.setWindow(CW);
  stretchy.setWindow(CW);
  
  t1.setWindow(CW);
  t2.setWindow(CW);
  t3.setWindow(CW);
  t4.setWindow(CW);
    t5.setWindow(CW);
}

