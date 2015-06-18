
////////////////// ControlP5 INTERFACE

void controlInit() {
  INTERFACES = new ControlP5(this);  
  INTERFACES.setAutoDraw(false); 

  //Create sliders 
  Slider extrude = INTERFACES.addSlider("EXTRUDEDIST").setPosition(10, 20).setRange(1, 50).setValue(10); 
  Slider chamfEdge = INTERFACES.addSlider("CHAMFEREDGE").setPosition(10, 40).setRange(1, 20).setValue(1); 
  Slider catmullBlend = INTERFACES.addSlider("BLENDING").setPosition(10, 60).setRange(0.01, 3.00).setValue(0.50); 

  Toggle t1 = INTERFACES.addToggle("CATMULL").setPosition(10, 90)
  .setColorActive(color(255,0,0))
  .setSize(15, 15).setValue(false).setLabel("CatMull ON/OFF");
  Toggle t2 = INTERFACES.addToggle("SLICE").setPosition(10, 180).setSize(15, 15).setValue(false).setLabel("Slice ON/OFF");
  Slider oPlaneX = INTERFACES.addSlider("OPLANE_X").setPosition(10, 230).setRange(-120, 120).setValue(2);

  Slider dPlaneX = INTERFACES.addSlider("dPLANE_X").setPosition(10, 250).setRange(-12, 12).setValue(12)
 .setNumberOfTickMarks(13);  
  Slider dPlaneY = INTERFACES.addSlider("dPLANE_Y").setPosition(10, 270).setRange(-12, 12).setValue(0)
  .setNumberOfTickMarks(13); 
  Slider dPlaneZ = INTERFACES.addSlider("dPLANE_Z").setPosition(10, 290).setRange(-12, 12).setValue(0)
  .setNumberOfTickMarks(13); 


}

