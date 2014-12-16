
////////////////// ControlP5 INTERFACE

void controlInit() {
  INTERFACES = new ControlP5(this);  
  INTERFACES.setAutoDraw(false); 
  // Create a new window (doesn't work in Processing 2.0+)
  CW = INTERFACES.addControlWindow("controlP5window", 10, 10, 200, 300, 30);

  //Create sliders 

  Slider chamfDist = INTERFACES.addSlider("CHAMFERDIST").setPosition(10, 20).setRange(1, 50).setValue(10); 
  chamfDist.getCaptionLabel().setColorBackground(color(255, 20, 30)); // changer la couler de fond pour le texte
  chamfDist.getCaptionLabel().getStyle().setPadding(4, 2, 2, 2); // changer sa taille (pour le texte)
  // shift the caption label up by 4px
  chamfDist.getCaptionLabel().getStyle().setMargin(-5, 0, 0, 0); 
  
  Slider chamfEdge = INTERFACES.addSlider("CHAMFEREDGE").setPosition(10, 40).setRange(1, 20).setValue(5); 

  // Add elements to new window
  chamfDist.setWindow(CW);
  chamfEdge.setWindow(CW);


}

