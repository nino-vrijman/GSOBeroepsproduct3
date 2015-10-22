package AEXServer;/**
 * Created by Kevin on 20-10-2015.
 */

import AEXClient.BannerController;
import AEXClient.IBanner;
import Shared.IFonds;
import javafx.application.Application;
import javafx.stage.Stage;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Timer;
import java.util.TimerTask;

public class AEXServerGui extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    //set port number
    private static final int portNumbner = 1099;
    //set binding name
    private static final String bindingName ="AEXServer";
    // references to registry  and mockeffectenbeurs
    private Registry registry = null;
    private MockEffectenBeurs mockEffectenBeurs = null;

    //  Timer
    Timer timer = null;

    //constructor
    public void Initialize(){
        //create mockeffectenbeurs

        try{
            mockEffectenBeurs = new MockEffectenBeurs();
            System.out.println("mockeffectenbeyrs created");
        }
        catch (RemoteException ex){
            System.out.println(ex);
        }

        //create registry at port numnber
        try{
            registry = LocateRegistry.createRegistry(1099);
        }
        catch (RemoteException ex)
        {
            System.out.println(ex);
        }

        //Week 7
        try{
            registry.rebind(bindingName, mockEffectenBeurs);
        }
        catch (RemoteException ex) {
            System.out.println(ex);
        }

        //  Timer aanmaken zodat beurskoersen constant gegenereerd worden
        timer = new Timer();

        TimerTask tt = new TimerTask()
        {
            @Override
            public void run()
            {
                mockEffectenBeurs.genereerKoersen();
            }
        };

        timer.scheduleAtFixedRate(tt, 500, 500);
        getListener();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("got dat on lock");
        Initialize();
    }
    public void getListener()
    {
        IBanner bannerController;
        try {
            registry = LocateRegistry.getRegistry("localhost", 1098);
        }
        catch (RemoteException ex)
        {
            System.out.println(ex);
        }
        if(registry != null)
        {
            try{
                bannerController = (IBanner) registry.lookup("bannerController");
                mockEffectenBeurs.addListener(bannerController, "beurs");
            }
            catch (RemoteException ex)
            {
                System.out.println(ex);
            }
            catch (NotBoundException ex)
            {
                System.out.println(ex);
            }
        }
    }
}
