package startup.softflix.com.pockemonandroid

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    //getting instance from the map
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //calling that fragment.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment

        //show the map inside fragment
        mapFragment.getMapAsync(this)

        checkPermission()//calling method on run
    }

    //adding code for permission
    var ACCESSLOCAITON=123
    //we are using fine location permisson in manifest and it is in dangrous permission so have to define it here also in code otherwise it will not work
    fun checkPermission()
    {
        //check the OS of the device, before 23 version they don't need it
        if(Build.VERSION.SDK_INT>=23)
        {
            //if user granted this permission or not
            //if permission is not granted then go to specific process
            if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
            {
                //going to specific process, will pop up to ask for permission again
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),ACCESSLOCAITON ) //if more than one permission to ask then can put comma and add as much you want
                return; //if not grants again then return , don't continue
            }
        }

        //else
        GetUserLocation()
    }

    fun GetUserLocation()
    {
        Toast.makeText(this,"User location access on ", Toast.LENGTH_SHORT).show()
        //TODO: WILL implement later

        var myLocation= MylocationListener() //making object of class that defined below

        var locationManager= getSystemService(Context.LOCATION_SERVICE) as LocationManager //system has many services like calender, location , (as LocationManger will make type of var locationmanager as LocationManager as we don't know the type)
        //with below line getting service every 3 millisecond and 3 meter(f)
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3,3f, myLocation)

        //starting thread , class defined belwo
        var mythread= myThread()
        mythread.start()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //when request code is equal to my defined code then go to -> execution
        when(requestCode){
            ACCESSLOCAITON->{

                //two possibilites that person granted or not granted and we have asked only one permission so in any only one on location
                //from grantResult we can know that granted or not
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED) //0 location is of our location and only have asked one permission, otherwise have to do with 1 index and so on
                {
                    GetUserLocation()
                }
                else//if not granted
                {
                    Toast.makeText(this, "We can't access to your locaation", Toast.LENGTH_LONG)
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    //called when map will be ready
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera

    }


    var location:Location?=null //to be accessible by all classes
    //getting user location, locationlistener is an interface
    inner class MylocationListener:LocationListener {

        constructor()
        {
            location= Location("Start")
            location!!.latitude=0.0
            location!!.longitude=0.0
        }

        //this method is called everytime user location is changed

        override fun onLocationChanged(p0: Location?) {
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            location=p0 //everytime location is updated, it will be sent to location(this)
        }

        //this method whenever status changed means whenever gps changed to on/off
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        //called when gps changed to off
        override fun onProviderEnabled(provider: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        //called when gps changed to on/enabled
        override fun onProviderDisabled(provider: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    //class for passing location that we got from above class to the Ui

    inner class myThread:Thread{
        //thread need initialization
        constructor():super()
        {

        }

        override fun run() {
            //get the location and show it to the map
            while(true)
            {
                try {
                    //always maps should be clean before running
                    mMap!!.clear()
                    //important, thread can't interact with UI , so need to use UI thread
                    runOnUiThread {
                        val sydney = LatLng(location!!.latitude, location!!.longitude)
                        mMap.addMarker(MarkerOptions().position(sydney).title("Me")
                                .snippet("Here is my location")
                                .icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.mario)))//using mario as icon of location
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,14f))

                        Thread.sleep(1000)
                    }


                }
                catch (ex:Exception){}
            }
        }
    }
}
