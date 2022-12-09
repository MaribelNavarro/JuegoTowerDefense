package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Dome;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;
import java.util.Random;

/**
 * This is the Main Class of your Game.
 * Nombre del juego.- Defensa de la Torre: Castillo Real
 * @author Maribel Navarro Jaquiz
 */
public class Main extends SimpleApplication {
    
    //Constante trigger que representa los clicks del mouse
    private final static Trigger TRIGGER_CLIC = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
    
    //Se define la constante que ayuda a identificar las acciones del trigger
    private final static String MAPPING_CLIC = "clicMatar";
    
    //Aquí se declara la geometría para que el listener pueda tener acceso al objeto
    //al momento que lo mandan llamar en onAnalog
    private Geometry box01_geom;
    
    //Declaración de la velocidad inicial
    float velocidad = 4;
    //Para contar los enemigos que se han eliminado
    int numVencidos = 0;
    
    //Para identificar si se ha eliminado el enemigo
    boolean eliminado = false;
    
    //Creacion de la esfera que será el enemigo
    Sphere s = new Sphere(90,90,0.6f,true,true);
    Geometry enemigo = new Geometry("enemigo", s);
    
    Random rand = new Random(); 
    int numeroAleatorio;
   
    //Se define y hace estatico la malla que se podrá replicar
    public static Box mesh = new Box(Vector3f.ZERO, 1, 1, 1);
    

    public static void main(String[] args) {
        //Para la imagen que tendrá el videojuego
        AppSettings settings = new AppSettings(true); //Creamos el objeto para controlar las especificaciones
        settings.setTitle("Sistema Solar"); //Cambiamos el nombre de la ventana
        //Integramos una imagen personal a la pantalla de inicio
        settings.setSettingsDialogImage("Interface/defensaTorre.PNG");
        //modificar la resolucion
        settings.setResolution(1280,960);
        Main app = new Main();
        app.setSettings(settings); //Aplicamos las especificaciones a la app
        app.start();
    }
    
     private Geometry myBox(String name, Vector3f loc, ColorRGBA color){
        Geometry geom = new Geometry(name, mesh);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        geom.setMaterial(mat);
        geom.setLocalTranslation(loc);
        
        return geom;
    }
     
    /**
     * attachCenterMark crea un objeto geometry que servira de mira para apuntar 
     * diferentes objetos en el escenario ya que es una marca 2D, se debe adjuntar
     * a la interface 2D del usuario "guiNode" , este objeto es instanciado en cualquier
     * SimpleApplication
     */
    private void attachCenterMark(){
        Geometry c = this.myBox("center mark", Vector3f.ZERO, ColorRGBA.White);
        c.scale(4);
        c.setLocalTranslation(settings.getWidth()/2, settings.getHeight()/2, 0);
        guiNode.attachChild(c);//adjunta la interface 2D del usuario 
    }
    
    @Override
    public void simpleInitApp() {
        //Posicionar la camara donde está la torre
        cam.setLocation(new Vector3f(0,4f,29));
        
        //Para hacer uso del trigger y el mapping se deben registrar en el inputManager
        inputManager.addMapping(MAPPING_CLIC, TRIGGER_CLIC);
        
        //Para poder activar el mapping se debe estar escuchando para detectar el input
        inputManager.addListener(analogListener, new String[]{MAPPING_CLIC});
        
        //La mira que indica la posición del mouse es inicializada
        attachCenterMark();
       
        //Se crean los obejtos para el mundo del videojuego
        
        //Se crea la pared de la torre
        Box par = new Box(8,4f,1);
        Geometry pared = new Geometry("paredCastillo", par);
        Material matPared = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matPared.setTexture("ColorMap", assetManager.loadTexture("Textures/pared.PNG"));
        pared.setMaterial(matPared);
        
        //Se crea la puerta de la torre
        Box parP = new Box(3,2f,0);
        Geometry paredPuerta = new Geometry("puerta", parP);
        Material matParedP = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matParedP.setTexture("ColorMap", assetManager.loadTexture("Textures/puerta.PNG"));
        paredPuerta.setMaterial(matParedP);
        
        //Se crea el bloque que va arriba de la torre
        Box b = new Box(1,0.5f,1);
        Geometry bloque = new Geometry("bloqueCastillo", b);
        Material matB = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matB.setTexture("ColorMap", assetManager.loadTexture("Textures/bloque.PNG"));
        bloque.setMaterial(matB);
        
        //Se crea una torre 
        Cylinder to = new Cylinder(90,90,1.5f,10f,true);
        Geometry torre = new Geometry("torreCastillo", to);
        Material matTo = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matTo.setTexture("ColorMap", assetManager.loadTexture("Textures/pared.PNG"));
        torre.setMaterial(matTo);
        
        //Se crea el cono que será la punta de la torre
        Dome cT = new Dome(Vector3f.ZERO, 2, 32, 2f,false);
        Geometry conoTorre = new Geometry("conoTorre", cT);
        Material matcT = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matcT.setTexture("ColorMap", assetManager.loadTexture("Textures/bloque.PNG"));
        conoTorre.setMaterial(matcT);
        
        //Se crea la segunda torre
        Geometry torre2 = new Geometry("torreCastillo", to);
        torre2.setMaterial(matTo);
        
        //Se crea el cono para la punta de la segunda torre
        Geometry conoTorre2 = new Geometry("conoTorre", cT);
        conoTorre2.setMaterial(matcT);
        
        //Se crea un segundo bloque
        Geometry bloque2 = new Geometry("bloqueCastillo", b);
        bloque2.setMaterial(matB);
        //Se crea un tercer bloque
        Geometry bloque3 = new Geometry("bloqueCastillo", b);
        bloque3.setMaterial(matB);
        //Se crea el cuarto bloque
        Geometry bloque4 = new Geometry("bloqueCastillo", b);
        bloque4.setMaterial(matB);
        //Se crea el quinto bloque
        Geometry bloque5 = new Geometry("bloqueCastillo", b);
        bloque5.setMaterial(matB);
        
        //Se crea el camino del mundo
        Box c = new Box(8,0.1f,30);
        Geometry camino = new Geometry("camino", c);
        Material matC = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matC.setTexture("ColorMap", assetManager.loadTexture("Textures/stones.png"));
        camino.setMaterial(matC);
        
        //Se crea el pasto del mundo
        Box p = new Box(15,0.1f,30);
        Geometry pasto = new Geometry("pasto", p);
        Material matP = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matP.setTexture("ColorMap", assetManager.loadTexture("Textures/pasto.jpg"));
        pasto.setMaterial(matP);
        
        //Se crea un tronco grande para el pino grande
        Cylinder t = new Cylinder(90,90,1f,10f,true);
        Geometry tronco = new Geometry("tronco", t);
        Material matT = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matT.setTexture("ColorMap", assetManager.loadTexture("Textures/tronco.PNG"));
        tronco.setMaterial(matT);
        
        //Se crea la copa para el pino grande
        Dome cP = new Dome(Vector3f.ZERO, 2, 32, 4f,false);
        Geometry copaPino = new Geometry("copaPino", cP);
        Material matcP = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matcP.setTexture("ColorMap", assetManager.loadTexture("Textures/pino2.jpg"));
        copaPino.setMaterial(matcP);
        
        //Se crean varios troncos y copas para pinos grandes
        Geometry tronco1 = new Geometry("tronco", t);
        tronco1.setMaterial(matT);
        
        Geometry copaPino1 = new Geometry("copaPino", cP);
        copaPino1.setMaterial(matcP);
        
        Geometry tronco2 = new Geometry("tronco", t);
        tronco2.setMaterial(matT);
        
        Geometry copaPino2 = new Geometry("CopaPino", cP);
        copaPino2.setMaterial(matcP);
        
        //Se crea el tronco para el tronco chico
        Cylinder tC = new Cylinder(90,90,1f,6f,true);
        Geometry troncoC = new Geometry("troncoChico", tC);
        Material matTC = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matTC.setTexture("ColorMap", assetManager.loadTexture("Textures/tronco.PNG"));
        troncoC.setMaterial(matTC);
        
        //Se crea la copa para el pino chico
        Dome cPC = new Dome(Vector3f.ZERO, 2, 32, 3f,false);
        Geometry copaPinoC = new Geometry("copaPinoChico", cPC);
        Material matcPC = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matcPC.setTexture("ColorMap", assetManager.loadTexture("Textures/verde.PNG"));
        copaPinoC.setMaterial(matcPC);
        
        //Se crea otro tronco y copa para otro pino chico
        Geometry troncoC2 = new Geometry("troncoChico", tC);
        troncoC2.setMaterial(matTC);
        
        Geometry copaPinoC2 = new Geometry("copaPinoChico", cPC);
        copaPinoC2.setMaterial(matcPC);
        
        //Se crea un arbusto
        Dome a = new Dome(Vector3f.ZERO, 6, 32, 1.5f,false);
        Geometry arbusto = new Geometry("arbusto", a);
        Material matA = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matA.setTexture("ColorMap", assetManager.loadTexture("Textures/arbusto.PNG"));
        arbusto.setMaterial(matA);
        
        //Se agrega la textura al enemigo (el enemigo es una bola de fuego)
        Material matS = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matS.setTexture("ColorMap", assetManager.loadTexture("Textures/fuego.jpg"));
        enemigo.setMaterial(matS);
        
        //Se crean más arbustos
        Geometry arbusto2 = new Geometry("arbusto", a);
        arbusto2.setMaterial(matA);
        
        Geometry arbusto3 = new Geometry("arbusto", a);
        arbusto3.setMaterial(matA);
      
        //Se crea un cuaternion para rotar a 90 grados
        Quaternion roll90 = new Quaternion();
        roll90.fromAngleAxis(-FastMath.HALF_PI,Vector3f.UNIT_X);
       
        //Se mueven las copas de los pinos y se rotan los troncos
        copaPino.move(0,4f,0);
        tronco.rotate(roll90);
        
        copaPino1.move(0,4f,0);
        tronco1.rotate(roll90);
        
        copaPino2.move(0,4f,0);
        tronco2.rotate(roll90);
        
        copaPinoC.move(0,2f,0);
        troncoC.rotate(roll90);
        
        copaPinoC2.move(0,2f,0);
        troncoC2.rotate(roll90);
        
        torre.rotate(roll90);
        torre2.rotate(roll90);
        
        //Se mueve el arbusto
        arbusto.move(0,-1f,0);
        
        //Se crean los Nodos para formar los pinos
        Node pino = new Node("pino");
        pino.attachChild(tronco);
        pino.attachChild(copaPino);
        
        Node pino1 = new Node("pino1");
        pino1.attachChild(tronco1);
        pino1.attachChild(copaPino1);
        
        Node pino2 = new Node("pino2");
        pino2.attachChild(tronco2);
        pino2.attachChild(copaPino2);
        
        Node pinoC = new Node("pinoC");
        pinoC.attachChild(troncoC);
        pinoC.attachChild(copaPinoC);
        
        Node pinoC2 = new Node("pinoC2");
        pinoC2.attachChild(troncoC2);
        pinoC2.attachChild(copaPinoC2);
        
        //Se mueven todos los objetos para acomodarlos en el mundo 
        pared.move(0,4f,31);
        bloque.move(-7,8.5f,31);
        bloque2.move(-3.5f,8.5f,31);
        bloque3.move(0.5f,8.5f,31);
        bloque4.move(4,8.5f,31);
        bloque5.move(7,8.5f,31);
        torre.move(9.5f,5f,31);
        conoTorre.move(9.5f,9.7f,31);
        torre2.move(-9.6f,5f,31);
        conoTorre2.move(-9.6f,9.7f,31);
        paredPuerta.move(0,2.1f,29.9f);
        pino.move(-12,1f,-5);
        pino1.move(-12,1f,13);
        pino2.move(-12,1f,-25);
        pinoC.move(-12,1f,6);
        pinoC2.move(-10,1f,25);
        arbusto.move(-12,1f,8);
        arbusto2.move(-12,-0.1f,18);
        arbusto3.move(-10,-0.1f,-8);
        pasto.move(0,-0.1f,0);
        
        //Se modifica la posicion inicial del enemigo
        enemigo.move(0,4f,-30);
        enemigo.rotate(roll90);
        
        //Se crea el nodo para el mundo
        Node mundo = new Node("mundo");
        mundo.attachChild(pared);
        mundo.attachChild(bloque);
        mundo.attachChild(bloque2);
        mundo.attachChild(bloque3);
        mundo.attachChild(bloque4);
        mundo.attachChild(bloque5);
        mundo.attachChild(torre);
        mundo.attachChild(conoTorre);
        mundo.attachChild(torre2);
        mundo.attachChild(conoTorre2);
        mundo.attachChild(paredPuerta);
        mundo.attachChild(camino);
        mundo.attachChild(pasto);
        mundo.attachChild(pino);
        mundo.attachChild(pino2);
        mundo.attachChild(pinoC);
        mundo.attachChild(pinoC2);
        mundo.attachChild(arbusto);
        mundo.attachChild(arbusto2);
        mundo.attachChild(arbusto3);
        mundo.attachChild(pino1);
        //mundo.attachChild(enemigo);
        
        //Se agrega el mundo a la escena
        rootNode.attachChild(mundo);
    }
    
    //Agragamos el listener analógico 
    private final AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float intensity, float tpf) {
            //Se comprueba que el trigger identificado corresponda a la acción deseada
            if(name.equals(MAPPING_CLIC)){
                
                //Colision identificará el objeto al cual se le hace click
                CollisionResults results = new CollisionResults(); 
                
                //Se proyecta una línea de acuerdo a la posición de la cámara 
                //dirección donde la camara está apuntando
                Ray ray = new Ray(cam.getLocation(), cam.getDirection());
                //Se calcula si el ray que se está proyectando hace colision con el objeto.
                rootNode.collideWith(ray, results);
                
                //Si el usuario ha hecho click en algo, identificamos la geometría.
                if (results.size()>0){
                    Geometry target = results.getClosestCollision().getGeometry();
                    //Si el objeto al que se le da clic es el enemigo
                    if(target.getName().equals("enemigo")){
                    //Se identifica que el enemigo ha sido eliminado
                    eliminado = true;
                    //Aumenta en 1 el numero de enemigos eliminados
                    numVencidos ++;
                    }
                //eliminado = true;
                //Imprime en consola el objeto al que se le dio clic
                System.out.println(target.getName());
                //Imprime el numero de enemigos eliminados
                System.out.println(numVencidos);
                }
            }
        }  
    };
    
    @Override
    public void simpleUpdate(float tpf) {
        //Se crea una variable para comparar la posicion del enemigo 
        int comparacion;
        
        //Se agrega el enemigo a la escena
        rootNode.attachChild(enemigo);
        
        //Se mueve el enemigo en Z para avanzar hasta la torre
        enemigo.move(0,0,tpf*velocidad);
        //Se obtiene la posicion actual del enemigo
        Vector3f posicion = enemigo.getLocalTranslation();
        //Guarda el valor de la posicion en el eje de las Z 
        comparacion = (int) posicion.getZ();
        
        //Si un enemigo ha sido eliminado
        if(eliminado == true ){
           //Se elimina el enemigo de la escena
           rootNode.detachChild(enemigo);
           int eliminar = comparacion + 40;
           //Cambia la posicion del enemigo al origen
          
           /*
           numeroAleatorio = rand.nextInt(8);
           
           //Se escoge una posicion en x aleatoria
           //para que el enemigo aparezca en un lugar diferente
           if(numeroAleatorio == 0){
               enemigo.move(-6,0,-eliminar);
           }
           if(numeroAleatorio == 1){
               enemigo.move(-4,0,-eliminar);
           }
           if(numeroAleatorio == 2){
               enemigo.move(-2,0,-eliminar);
           }
           if(numeroAleatorio == 3){
               enemigo.move(0,0,-eliminar);
           }
           if(numeroAleatorio == 4){
               enemigo.move(2,0,-eliminar);
           }
           if(numeroAleatorio == 5){
               enemigo.move(4,0,-eliminar);
           }
           if(numeroAleatorio == 6){
               enemigo.move(6,0,-eliminar);
           }
           if(numeroAleatorio == 7){
               enemigo.move(8,0,-eliminar);
           }
           if(numeroAleatorio == 8){
               enemigo.move(1,0,-eliminar);
           }
           */
           enemigo.move(0,0,-eliminar);
           
           //Aumenta la velocidad para que el siguiente enemigo vaya más rapido
           velocidad = velocidad +1f;
           eliminado = false;
           //Se agrega el enemigo a la escena
           rootNode.attachChild(enemigo);
        }
        //Si el enemigo en el eje de las Z es igual a 29
        //El enemigo llegó a la torre
        //Se acaba el juego mostrando una imagen que idica que se ha perdido
        if(comparacion == 29){
            enemigo.setLocalTranslation(0,4f,29);
            rootNode.detachAllChildren();
            Picture pic = new Picture("HUD Picture");
            pic.setImage(assetManager, "Textures/mensajeFinal.PNG", true);
            pic.setWidth(settings.getWidth()/2);
            pic.setHeight(settings.getHeight()/2);
            pic.setPosition(settings.getWidth()/4, settings.getHeight()/4);
            guiNode.attachChild(pic);
        }
        
        //Solo cuenta correctamente si el enemigo esta bastante cerca
        if(numVencidos == 10){
            rootNode.detachAllChildren();
            System.out.println("Has Ganado el Juego");
            Picture pic = new Picture("HUD Picture");
            pic.setImage(assetManager, "Textures/mensajeGanaste.png", true);
            pic.setWidth(settings.getWidth()/2);
            pic.setHeight(settings.getHeight()/2);
            pic.setPosition(settings.getWidth()/4, settings.getHeight()/4);
            guiNode.attachChild(pic);
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}