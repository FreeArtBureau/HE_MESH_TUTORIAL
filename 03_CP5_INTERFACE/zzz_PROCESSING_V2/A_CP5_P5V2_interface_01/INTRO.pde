/*

/////////////////////
HEMESH TUTO_01
FOR 3D PRINTING
mw_2015 
www.freeartbureau.org
/////////////////////

------------------------------
Sketch : A_cp5_P5V2_interface_01
Parent : A_cp5_interface_01
------------------------------

This sketch demonstrates how we can implement modifications
to our shapes using the ControlP5 library.
I use a simple cubic form and add two modifiers :
- Chamfer Corners
- Chamfer Edges

* Chamfer - Posh technical term for beveling ;--°

The process is thus :
-----------------------
  > Declare Mesh
    > Declare Creator
      > Set Creator parameters 
        > Add Creator to Mesh
          >  Declare Modifier
            > Set Modifier parameters
              > Add modifer to Mesh
                > Final Render in draw
                -----------------------


HEMESH LIBRARY AUTHOR
////////////////////////////
 http://www.wblut.com/
 http://hemesh.wblut.com/
////////////////////////////


CONTROLP5 LIBRARY AUTHOR
////////////////////////////
Andreas Schlegel
http://www.sojamo.de/libraries/controlP5/
////////////////////////////

*NB

  This sketch is a rework for Processing V2+
  All GUI elements are now placed within group menus
  as opposed to having a separate window.

----------------------------------------------
*/

