/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.com.scara.processing;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;

/**
 *
 * @author Joselo
 */
public class SketchPro extends PApplet {

    PFont f;
    PGraphics pg;

    static float pi, grad, rad, Xaux, Yaux, x, y, AngAntBr, AngBrazo, AntBrazoPY, AntBrazoPX;
    static int baseY, baseX, longBrazo, longAntBr;

    String[] lines;
    int index = 0;

    @Override
    public void settings() {
        size(640, 480);
    }

    @Override
    public void setup() {

        pg = createGraphics(640, 480);
        stroke(255);
        strokeWeight(4);

        pi = atan(1) * 4;
        rad = pi / 180;
        grad = 180 / pi;

        baseX = 320;      // Punto X Base (hombro)  Situamos el brazo en pantalla.
        baseY = 240;      // punto Y Base (hombro)

        longBrazo = 150;   //Longitud Brazo. Puedes modificar las longitudes del brazo o antebrazo.     
        longAntBr = 150;   //Longitud AnteBrazo.

        x = (150);             // Posicion Inicial X.    Damos las coordenadas iniciales de la punta del brazo.
        y = (150) + baseY;     // Posicion Inicial Y.    Se puede modificar los valores que están dentro del paréntesis.

        f = createFont("Arial", 16, true); // STEP 2 Create Font      
        lines = loadStrings("positions.txt");
    }

    @Override
    public void draw() {
        background(204);
        inverseK();
        dibujaBrazo();

        pg.beginDraw();

        if (index < lines.length) {
            String[] pieces = split(lines[index], '\t');
            if (pieces.length == 2) {

                x = Integer.parseInt(pieces[0]) * 2;
                y = Integer.parseInt(pieces[1]) * 2;
                pg.strokeWeight(3);
                pg.stroke(0, 128, 255);
                pg.point(AntBrazoPX, AntBrazoPY);
                image(pg, 0, 0);                
            }
            // Go to the next line for the next run through draw()
            index++;
            
        }
        
        pg.endDraw();   
                 
    }

    /*
        ----------------------------------
        *******Cinemática Inversa*********
        ----------------------------------
     */
    void inverseK() {

        float LadoA, Alfa, Beta, Gamma, Hipotenusa;

        LadoA = y - baseY;
        Hipotenusa = sqrt((pow(LadoA, 2)) + (pow(x, 2)));

        Alfa = atan2(LadoA, x);

        Beta = acos(((pow(longBrazo, 2)) - (pow(longAntBr, 2)) + (pow(Hipotenusa, 2))) / (2 * longBrazo * Hipotenusa));
        AngBrazo = Alfa + Beta;   // ANGULO BRAZO(en radianes).

        Gamma = acos(((pow(longBrazo, 2)) + (pow(longAntBr, 2)) - (pow(Hipotenusa, 2))) / (2 * longBrazo * longAntBr));
        AngAntBr = Gamma - (180 * rad);    //ANGULO ANTEBRAZO(en radianes).

        if (Float.isNaN(Beta) || Float.isNaN(Gamma)) {
            x = Xaux;  // En caso de error (fuera de rango) se vuelve a llamar a sí misma
            y = Yaux;  // y carga con los valores anteriores correctos.
            inverseK();
        }

        Xaux = x;
        Yaux = y;

    }

    void dibujaBrazo() {

        /**
         * --------------------- PUNTOS PARA DIBUJAR. ---------------------
         */
        float PYa, PYb, PXa, PXb, BrazoPY, BrazoPX;

        PYa = longBrazo * -sin(AngBrazo);
        PYb = longAntBr * -sin(AngAntBr + AngBrazo);

        PXa = longBrazo * cos(AngBrazo);
        PXb = longAntBr * cos(AngAntBr + AngBrazo);

        //BRAZO (x,y)
        BrazoPY = PYa + baseY;            //Punto de coordenada Y del Brazo.
        BrazoPX = PXa + baseX;           //Punto de coordenada X del Brazo.

        //ANTEBRAZO (x,y)
        AntBrazoPY = PYb + PYa + baseY;    //Punto de coordenada Y del AnteBrazo.
        AntBrazoPX = PXb + PXa + baseX;     //Punto de coordenada X del AnteBrazo.

        stroke(0, 0, 0);
        line(baseX, baseY, BrazoPX, BrazoPY);
        stroke(0, 0, 75);
        line(BrazoPX, BrazoPY, AntBrazoPX, AntBrazoPY);
    }

    @Override
    public void keyPressed() {

        switch (key) {
            case 'a':                
                x--;
                break;
            case 'd':
                x++;
                break;
            case 's':
                y--;
                break;
            case 'w':
                y++;
                break;
            default:
                break;
        }

    }

}
