/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.com.scara;

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
    static int BaseY, BaseX, LongBrazo, LongAntBr;

    String[] lines;
    int index = 0;

    public void settings() {
        size(640, 480);
    }

    public void setup() {

        pg = createGraphics(640, 480);
        stroke(255);
        strokeWeight(4);

        pi = atan(1) * 4;
        rad = pi / 180;
        grad = 180 / pi;

        BaseX = 320;      // Punto X Base (hombro)  Situamos el brazo en pantalla.
        BaseY = 240;      // punto Y Base (hombro)

        LongBrazo = 150;   //Longitud Brazo. Puedes modificar las longitudes del brazo o antebrazo.     
        LongAntBr = 150;   //Longitud AnteBrazo.

        x = (150);             // Posicion Inicial X.    Damos las coordenadas iniciales de la punta del brazo.
        y = (150) + BaseY;     // Posicion Inicial Y.    Se puede modificar los valores que están dentro del paréntesis.

        f = createFont("Arial", 16, true); // STEP 2 Create Font      
        lines = loadStrings("positions.txt");
    }

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

        LadoA = y - BaseY;
        Hipotenusa = sqrt((pow(LadoA, 2)) + (pow(x, 2)));

        Alfa = atan2(LadoA, x);

        Beta = acos(((pow(LongBrazo, 2)) - (pow(LongAntBr, 2)) + (pow(Hipotenusa, 2))) / (2 * LongBrazo * Hipotenusa));
        AngBrazo = Alfa + Beta;   // ANGULO BRAZO(en radianes).

        Gamma = acos(((pow(LongBrazo, 2)) + (pow(LongAntBr, 2)) - (pow(Hipotenusa, 2))) / (2 * LongBrazo * LongAntBr));
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

        PYa = LongBrazo * -sin(AngBrazo);
        PYb = LongAntBr * -sin(AngAntBr + AngBrazo);

        PXa = LongBrazo * cos(AngBrazo);
        PXb = LongAntBr * cos(AngAntBr + AngBrazo);

        //BRAZO (x,y)
        BrazoPY = PYa + BaseY;            //Punto de coordenada Y del Brazo.
        BrazoPX = PXa + BaseX;           //Punto de coordenada X del Brazo.

        //ANTEBRAZO (x,y)
        AntBrazoPY = PYb + PYa + BaseY;    //Punto de coordenada Y del AnteBrazo.
        AntBrazoPX = PXb + PXa + BaseX;     //Punto de coordenada X del AnteBrazo.

        stroke(0, 0, 0);
        line(BaseX, BaseY, BrazoPX, BrazoPY);
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
