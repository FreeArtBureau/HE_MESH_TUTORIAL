
////////////////// ControlP5 INTERFACE

void controlInit() {
  INTERFACES = new ControlP5(this);  
  INTERFACES.setAutoDraw(false); 
  
  //Group
  Group g1 = INTERFACES.addGroup("g1")
    .setPosition(20,25)
      .activateEvent(true)
           //.setBackgroundHeight(100)
             .setWidth(73)
              .setBackgroundColor(color(255,0,0))
                 .setLabel("CHAMFER");
  
  
  //Create sliders 
  Slider chamfDist = INTERFACES.addSlider("CHAMFERDIST")
    .setPosition(5, 5)
    .setColorBackground(255)
      .setSize(150,20)
        .setRange(1, 50)
          .setValue(10).setGroup(g1); 
  
  Slider chamfEdge = INTERFACES.addSlider("CHAMFEREDGE")
    .setPosition(5, 30)
      .setSize(150,20)
        .setRange(1, 20)
          .setValue(5).setGroup(g1); ; 

}

