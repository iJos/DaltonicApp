package es.ijos.daltonicapp;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import android.R.layout;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


public class OpenCVMainActivity extends Activity implements CvCameraViewListener2 {
    private static final String  TAG              = "DaltonicApp::Activity";

    
    
    private MenuItem             menuTipoCamara = null;
    private MenuItem			 menuBlancoYNegro = null;
    private MenuItem			 menuModoReconocimiento = null;
    private MenuItem			 menuSubmenuResoluciones = null;
    private boolean              tipoCamara = true;
    private boolean				 modoGrises = false;
    private boolean				 modoReconocimiento = false; //Modo de reconocimiento colores. Preciso(true) o Rango(false). por defecto empezamos en preciso(true)
    private int					 anchoCamara = 1280; //960; <--> Nexus
    private int					 altoCamara = 720;
    
    
    //Cámara
    private CameraBridgeViewBase camara;
    

    
    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    camara.enableView();  
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public OpenCVMainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        Log.i(TAG, "called onCreate");
        
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //Intento de Fullscreen, OpenCV No quiere en galaxy nexus, sí en galaxy 4 :(
        getWindow().setFlags(
        		WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //Screen ON Permanente

        //Brillo máximo permanente
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        
        
        setContentView(R.layout.opencv_main_activity);
        
        //Cambiamos el tipo de camara actual 	JAVA <--> NATIVA
        if (tipoCamara){
        	camara = (CameraBridgeViewBase)findViewById(R.id.camara_nativa);
        }else{
            camara = (CameraBridgeViewBase)findViewById(R.id.camara_java);
        }

    	camara.setVisibility(SurfaceView.VISIBLE);
    	camara.setCvCameraViewListener(this);
    	
    	
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (camara != null)
            camara.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        if (camara != null)
            camara.disableView();
    }
    
    
    
    //Creamos el menu y las opciones
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "called onCreateOptionsMenu");
        menuTipoCamara = menu.add("Cambiar Camara Nativa/Java");
        menuBlancoYNegro = menu.add("Blanco y Negro");
        menuModoReconocimiento = menu.add("Modo Preciso / Rango de Color");
        
        SubMenu subMenu = menu.addSubMenu(4, 4, 4, "Selecciona una resolución");
        subMenu.add(1, 10, 1, "Alta Resolución (1280x720)");
        subMenu.add(1, 11, 2, "Media Resolución (960x720)");
        subMenu.add(1, 12, 3, "Baja Resolución (800x480)");
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String mensajeToast = new String();
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);

        //Boton cambia tipo de camara
        if (item == menuTipoCamara) {
            camara.setVisibility(SurfaceView.GONE);
            tipoCamara = !tipoCamara;

            if (tipoCamara) {
                camara = (CameraBridgeViewBase) findViewById(R.id.camara_nativa);
                mensajeToast = "Cámara Nativa";
            }else{
                camara = (CameraBridgeViewBase) findViewById(R.id.camara_java);
                mensajeToast = "Cámara Java";
            }

            
            camara.setVisibility(SurfaceView.VISIBLE);
            camara.setCvCameraViewListener(this);
            camara.enableView();
            Toast toast = Toast.makeText(this, mensajeToast, Toast.LENGTH_LONG);
            toast.show();
        }
        //Fin Tipo Camara
        
        
        //Boton pone blanco y negro - Grises
        if(item == menuBlancoYNegro){
        	if(modoGrises){
        		modoGrises = false;
        		Toast toast = Toast.makeText(this, "'Modo Grises' desactivado.\n'Modo Normal' habilitado." , Toast.LENGTH_LONG);
        		toast.show();
        	}else{
        		modoGrises = true;
        		Toast toast = Toast.makeText(this, "'Modo Normal' desactivado.\n'Modo Grises' habilitado." , Toast.LENGTH_LONG);
        		toast.show();
        	}
        }
        //Fin Modo Grises
        
        //Boton Modo Preciso / Modo Tonalidades
        if(item == menuModoReconocimiento){
        	if(modoReconocimiento){
        		modoReconocimiento = false;
        		Toast toast = Toast.makeText(this, "'Modo Preciso' desactivado.\n'Modo Tonalidades' habilitado." , Toast.LENGTH_LONG);
        		toast.show();
        	}else{
        		modoReconocimiento = true;
        		Toast toast = Toast.makeText(this, "'Modo Tonalidades' desactivado.\n'Modo Preciso' habilitado." , Toast.LENGTH_LONG);
        		toast.show();
        	}
        }
        
        
        //Submenu para cambiar el tamaño del HUD
        switch(item.getItemId()){
            case 10: //Id del menú, para combrobar que se ha pulsado
            	anchoCamara = 1280;
            	altoCamara = 720;
            	Toast toast = Toast.makeText(this, "Resolución del HUD máxima" , Toast.LENGTH_LONG);
        		toast.show();
                break;
            case 11:
            	anchoCamara = 960;
            	altoCamara = 720;
            	toast = Toast.makeText(this, "Resolución del HUD media" , Toast.LENGTH_LONG);
        		toast.show();
                break;
            case 12:
            	anchoCamara = 800;
            	altoCamara = 480;
            	toast = Toast.makeText(this, "Resolución del HUD mínima" , Toast.LENGTH_LONG);
        		toast.show();
                break;

        }

        return true;
    }
    
    

    

    public void onCameraViewStarted(int width, int height) {
    	
    }

    public void onCameraViewStopped() {
        
    }
    
    
    
    public Mat onCameraFrame(CvCameraViewFrame frame) {
    	
    	if(modoGrises){
    		//Modo Blanco y Negro
    		return frame.gray();
    	}else{
    		
    	
	    	// Mat, para trabajar luego en el frame los pixels
	    	Mat mat = frame.rgba();
	    	
	    	// PIXEL CENTRAL
	    	int alto = altoCamara / 2;	//camera.getHeight() / 2;
	        int ancho = anchoCamara / 2;	//camera.getWidth() / 2;
	
	    	//Recuperamos el color del pixel central
	    	double[] color = mat.get(alto, ancho);
	    	
	    	//Log en consola para ver si saca los colores
	    	//Log.i(TAG , "COLORES RGB -->"+ color[0] +";"+ color[1] +";"+ color[2] +"");
	    	
	    	//El color inverso, para pintar el crosshair y verlo siempre
	    	double[] colorInverso = { 255 - color[0], 255 - color[1], 255 - color[2], 255};
	    	
	    	//START CROSSHAIR
	
	    	//Lineas Horizontales 
	    	Core.line(mat, new Point(0, altoCamara), new Point(anchoCamara - 25, altoCamara), new Scalar(colorInverso[0], colorInverso[1], colorInverso[2]), 1, 1, 1); //Izquierda
	    	Core.line(mat, new Point(anchoCamara + 25, altoCamara), new Point(anchoCamara + anchoCamara, altoCamara), new Scalar(colorInverso[0], colorInverso[1], colorInverso[2]), 1, 1, 1); //Derecha
	    	
	    	//Lineas Verticales
	    	Core.line(mat, new Point(anchoCamara, 0), new Point(anchoCamara, altoCamara - 25), new Scalar(colorInverso[0], colorInverso[1], colorInverso[2]), 1, 1, 1); //Top
	    	Core.line(mat, new Point(anchoCamara, altoCamara + 25), new Point(anchoCamara, altoCamara + altoCamara), new Scalar(colorInverso[0], colorInverso[1], colorInverso[2]), 1, 1, 1); //Bottom
	    	
	    	//Circulo interno
	    	Core.circle(mat, new Point(ancho, alto), 3, new Scalar(colorInverso[0], colorInverso[1], colorInverso[2]), -1);
	    	
	    	//Circulo externo
	    	Core.circle(mat, new Point(ancho, alto), 50, new Scalar(colorInverso[0], colorInverso[1], colorInverso[2]), 1);
	    	//FIN CROSSHAIR
	
	    	
	    	//TEXTO
	    	//Texto generado en cada frame con el color en BGR (float)
	    	//Sí, BGR, OpenCV maneja los colores como Blue Green Red, no como Red Green Blue
	    	String texto = "RGB: " + color[0] + " " + color[1] + " " + color[2];
	    	//Core.putText(img, text, org, fontFace, fontScale, color);
	    	Core.putText(mat, texto, new Point(10, 50), 3, 1, new Scalar(255, 255, 255, 255), 2);
	    	
	    	//Texto Color Nombre
	    	String nombreColor = getColorName(color[0], color[1], color[2]);
	    	Core.putText(mat, nombreColor, new Point(ancho, 50), 3, 1, new Scalar(255, 255, 255, 255), 2);
	    	
	    	//Rectángulo coloreado del color actual
	    	//Core.rectangle(img, pt1, pt2, color, thickness);
	    	// Si thickness < 0, hace un fill del rectángulo (Lo rellena)
	    	Core.rectangle(mat, new Point( 10 , 80), new Point(anchoCamara - 10, 100), new Scalar(color[0], color[1], color[2], 255), -1); //Al pintar, usamos RGBA
	    	

	        return mat;
    	}
        
    }


    public String getColorName(double r, double g, double b){
    	
    	String nombreColor = null;
    	
    	
    	if(modoReconocimiento){ //Modo Preciso
    		
    		
	    	//Blanco
	    	if(r > 140.0 && g > 140.0 && b > 140.0){
	    		if(r > 200.0 && g > 200.0 && b > 200.0){
	    			nombreColor = "Blanco Puro";
	    		}else{
	    			nombreColor = "Blanco";
	    		}
	    	}
	    	
	    	//Negro
	    	if(r < 50.0 && g < 50.0 && b < 50.0){
	    		nombreColor = "Negro";
	    	}
	    	
	    	//Rojo
	    	if(r > 100.0 && g < 100.0 && b < 100.0){
	    		nombreColor = "Rojo";
	    	}
	    	
	    	//Verde
	    	if(r < 100.0 && g > 100.0 && b < 100.0){
	    		nombreColor = "Verde";
	    	}
	    	
	    	//Azul
	    	if(r < 100.0 && g < 100.0 && b > 100.0){
	    		nombreColor = "Azul";
	    	}
	    	
	    	//Amarillo 
	    	if(r > 180.0 && r < 230.0 && g > 200.0 && g < 230.0 && b < 30.0){
	    		nombreColor = "Amarillo";
	    	}
	    	
	    	//Cyan
	    	if(r < 10.0 && g > 200.0 && g < 230.0 && b > 230.0 && b < 240.0){
	    		nombreColor = "Cyan";
	    	}
	    	
	    	//Magenta
	    	if(r > 200.0 && r < 220.0 && g > 30.0 && g < 50.0 && b > 220.0 && b < 240.0){
	    		nombreColor = "Magenta";
	    	}
	    	
    	}else{ //Modo Rangos de Colores
	    	
	    	// Calculamos a partir del Hue, en vez del valor... Así tomamos rangos
	    	// http://en.wikipedia.org/wiki/Hue
	    	
	    	//Rojo
	    	if(r >= g && g >= b){
	    		nombreColor = "Tono Rojo";
	    	}
	    	
	    	//Amarillo
	    	if(g > r && r >= b){
	    		nombreColor = "Tono Amarillo";
	    	}
	    	
	    	//Verde
	    	if(g >= b && b > r){
	    		nombreColor = "Tono Verde";
	    	}
	    	
	    	//Cyan
	    	if(b > g && g > r){
	    		nombreColor = "Tono Cyan";
	    	}
	    	
	    	//Azul
	    	if(b > r && r >= g){
	    		nombreColor = "Tono Azul";
	    	}
	    	
	    	//Magenta
	    	if(r >= b && b > g){
	    		nombreColor = "Tono Magenta";
	    	}
	    	
	    	//Negro
	    	if(r < 10.0 && g < 10.0 && b < 10.0){
	    		nombreColor = "Tono Negro";
	    	}
	    	
	    	//Blanco
	    	if(r > 140.0 && g > 140.0 && b > 140.0){
	    		if(r > 200.0 && g > 200.0 && b > 200.0){
	    			nombreColor = "Blanco Puro";
	    		}else{
	    			nombreColor = "Tono Blanco";
	    		}
	    	}
	    	
    	}
    	
	   return nombreColor;
   }
    
 
}