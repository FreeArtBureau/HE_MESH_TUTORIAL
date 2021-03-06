
////////////////// ControlP5 INTERFACE

void controlInit() {
  INTERFACES = new ControlP5(this);  
  INTERFACES.setAutoDraw(false); 

  //Create sliders 
  Slider facets = INTERFACES.addSlider("FACETS").setPosition(10, 20).setRange(4, 100).setValue(4); 

   Slider extrude = INTERFACES.addSlider("EXTRUDEDIST").setPosition(10, 40).setRange(1, 50).setValue(10); 
  extrude.getCaptionLabel().setColorBackground(color(255, 20, 30)); // changer la couler de fond pour le texte
  extrude.getCaptionLabel().getStyle().setPadding(4, 2, 2, 2); // changer sa taille (pour le texte)
  // shift the caption label up by 4px
  extrude.getCaptionLabel().getStyle().setMargin(-5, 0, 0, 0); 



  Slider chamfEdge = INTERFACES.addSlider("CHAMFEREDGE").setPosition(10, 60).setRange(1, 20).setValue(1); 
  Slider catmullBlend = INTERFACES.addSlider("BLENDING").setPosition(10, 80).setRange(0.01, 3.00).setValue(0.50); 
  

  
  Toggle t1 = INTERFACES.addToggle("CATMULL").setPosition(10, 100).setSize(15, 15).setValue(false).setLabel("CatMull ON/OFF");
  Toggle t2 = INTERFACES.addToggle("SLICE").setPosition(10, 130).setSize(15, 15).setValue(false).setLabel("Slice ON/OFF");
  Slider sliceOff = INTERFACES.addSlider("SLICE_OFFSET").setPosition(10, 160).setRange(1, 100).setValue(10); 

}

