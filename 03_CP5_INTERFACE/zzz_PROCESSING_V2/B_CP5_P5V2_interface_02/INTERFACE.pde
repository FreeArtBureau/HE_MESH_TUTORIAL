
////////////////// ControlP5 INTERFACE

void controlInit() {
  INTERFACES = new ControlP5(this);  
  INTERFACES.setAutoDraw(false); 

  //Create sliders 
   Slider chamfDist = INTERFACES.addSlider("CHAMFERDIST").setPosition(10, 20).setRange(1, 50).setValue(10); 
  chamfDist.getCaptionLabel().setColorBackground(color(255, 20, 30)); // changer la couler de fond pour le texte
  chamfDist.getCaptionLabel().getStyle().setPadding(4, 2, 2, 2); // changer sa taille (pour le texte)
  // shift the caption label up by 4px
  chamfDist.getCaptionLabel().getStyle().setMargin(-5, 0, 0, 0); 

  Slider chamfEdge = INTERFACES.addSlider("CHAMFEREDGE").setPosition(10, 40).setRange(1, 20).setValue(5); 
  Slider strutOff = INTERFACES.addSlider("STRUTOFFSET").setPosition(10, 60).setRange(1, 6).setValue(2); 
  Slider strutRad = INTERFACES.addSlider("STRUTRADIUS").setPosition(10, 80).setRange(40, 400).setValue(80); 

  Toggle t1 = INTERFACES.addToggle("STRUT").setPosition(10, 140).setSize(15, 15).setValue(false).setLabel("Strut ON/OFF");
  
  
}

