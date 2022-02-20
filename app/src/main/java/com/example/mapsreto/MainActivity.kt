package com.example.mapsreto

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener{
    private lateinit var map: GoogleMap
    
    //Creamos nueva variable para mas adelante poder crear las poly personalizadas
    private val nuevaPoly = PolylineOptions()

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createFragment()
    }

    //Crearemos un fragment para mas adelante utlizarlo. Cargaremos aqui el mapa
    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    //Funcion para ejecutar el mapa. Despues llamaremos a las subsiguiente funciones que crearemos
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        //Funcion para crear marcas
        createMarker()
        //Funcion para crear polylines en la ubicacion por defecto.
        createPolylines()
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMyLocationClickListener(this)
        //Funcion creada para solicitar permisos de localización
        enabledLocationPermission()

        //Creamos un listener para poder mas adelante crear nuestros markets/polylines
        map.setOnMapLongClickListener {

            val markerOptions = MarkerOptions().position(it)
            map.addMarker(markerOptions)
            crearPolyline(it)

        }

    }

    //FUncion creada para poder crear un nuevo polyline
    private fun crearPolyline(position: LatLng) {
        //Cada vez que ponemos nuevo market se van vinculando las polys
        nuevaPoly.add(position)
        map.addPolyline(nuevaPoly)
    }


    //Funcion para crear los Polylines
   private fun createPolylines(){
       val polylineOptions = PolylineOptions()
           .add(LatLng(39.4719678854221,-0.41471779346466064))
           .add(LatLng( 39.471568279149224, -0.4144147038459778))
           .add(LatLng( 39.47179189330496, -0.414908230304718))
           .add(LatLng( 39.47179396380303, -0.4143986105918884))
           .add(LatLng( 39.47154343308754,  -0.41482508182525635))
           .add(LatLng(39.4719678854221,-0.41471779346466064))
           .width(18f)
           .color(ContextCompat.getColor(this, R.color.PolyColor))

       val polyline = map.addPolyline(polylineOptions)
        polyline.isClickable = true

        map.setOnPolylineClickListener {
            changeColor(it)
        }


   }
    private fun changeColor(it: Polyline) {
        val color: Int = (0..3).random()

        when(color){
            0 -> it.color = ContextCompat.getColor(this, R.color.PolyColor)
            1 -> it.color = ContextCompat.getColor(this, R.color.black)
            2 -> it.color = ContextCompat.getColor(this, R.color.purple_700)
            3 -> it.color = ContextCompat.getColor(this, R.color.teal_700)
        }
    }


    //Metodo para crear la marca
    private fun createMarker() {
        val coordinates = LatLng(39.47174220133245, -0.41464000940322876)
        val marker = MarkerOptions().position(coordinates).title("Por donde vive Martinico")
        map.addMarker(marker)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 18f), 4000, null)
    }

    //Funcion para ver si el permiso esta aceptado para ubicarnos  a nosotros
    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun enabledLocationPermission() {
        if (!::map.isInitialized) return
        if (isLocationPermissionGranted()) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            map.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
        }
    }
    //funcion pedir permisos
    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(this, "Dirigete a ajustes y acepta los permisos", Toast.LENGTH_SHORT)
                .show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION
            )
        }
    }

    //Metodo override para realizar la peticion de permisos
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                map.isMyLocationEnabled = true
            }else{
                Toast.makeText(this, "Para activar la localización dirigente a ajustes del dispositivo y acepta los permisos", Toast.LENGTH_SHORT).show()
            }
            else -> {}
            }
        }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!::map.isInitialized) return
        if(!!isLocationPermissionGranted()){
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            map.isMyLocationEnabled = false
            Toast.makeText(this, "Para activar la localización dirigente a ajustes del dispositivo y acepta los permisos", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "Llegando a tu ubicacion", Toast.LENGTH_SHORT).show()
        return false
    }

    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(this, "Re-Ubicacion en la posición deseada ${p0.latitude}, ${p0.longitude}", Toast.LENGTH_SHORT).show()

    }
}

