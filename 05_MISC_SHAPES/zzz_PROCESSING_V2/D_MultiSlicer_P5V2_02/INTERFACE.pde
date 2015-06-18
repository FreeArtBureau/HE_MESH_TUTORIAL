
////////////////// ControlP5 INTERFACE

void controlInit() {
  INTERFACES = new ControlP5(this);  
  INTERFACES.setAutoDraw(false); 

  //Create sliders 
  Slider extrude = INTERFACES.addSlider("EXTRUDEDIST").setPosition(10, 20).setRange(0, 50).setValue(10); 
  Slider chamfEdge = INTERFACES.addSlider("CHAMFEREDGE").setPosition(10, 40).setRange(0, 20).setValue(1); 
  Slider catmullBlend = INTERFACES.addSlider("BLENDING").setPosition(10, 60).setRange(0.01, 3.00).setValue(0.50); 

  Slider type = INTERFACES.addSlider("PLATOTYPE").setPosition(10, 80).setRange(1, 5).setValue(1); 


  Toggle t1 = INTERFACES.addToggle("CAP").setPosition(10, 100).setSize(15, 15).setValue(false).setLabel("CAP");

  Toggle t2 = INTERFACES.addToggle("CATMULL").setPosition(40, 100).setSize(15, 15).setValue(false).setLabel("CatMull");
  Toggle t3 = INTERFACES.addToggle("SLICE").setPosition(10, 230).setSize(15, 15).setValue(false).setLabel("Slice");
  Toggle t4 = INTERFACES.addToggle("SLICE_CAP").setPosition(60, 230).setSize(15, 15).setValue(false).setLabel("Slice Cap");

  Toggle t5 = INTERFACES.addToggle("STRETCH").setPosition(90, 100).setSize(15, 15).setValue(false).setLabel("Stretch");


  Slider oPlaneX = INTERFACES.addSlider("OPLANE_X").setPosition(10, 270).setRange(-120, 120).setValue(2);

  Slider dPlaneX = INTERFACES.addSlider("dPLANE_X").setPosition(10, 290).setRange(-12, 12).setValue(12)
    .setNumberOfTickMarks(13);  
  Slider dPlaneY = INTERFACES.addSlider("dPLANE_Y").setPosition(10, 310).setRange(-12, 12).setValue(0)
    .setNumberOfTickMarks(13); 
  Slider dPlaneZ = INTERFACES.addSlider("dPLANE_Z").setPosition(10, 330).setRange(-12, 12).setValue(0)
    .setNumberOfTickMarks(13); 

  Slider stretchy = INTERFACES.addSlider("STRETCH_FACT").setPosition(10, 150).setRange(0.01, 5.0 ).setValue(0.5);
}

