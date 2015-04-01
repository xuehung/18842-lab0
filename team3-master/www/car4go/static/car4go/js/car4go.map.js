function initialize() {
    var zipcode = document.getElementById("zipcodeInput").value;
    
    var others = [
        ['shit1', 40.4349259, -79.9856424, 3, "To 1234 Street, Boston: 10:00am, 12/12/2015"],
        ['shit2', 40.4379258, -79.9156423, 2, "To 5678 Street, Phidaphia: 11:00am, 12/12/2015"],
        ['shit3', 40.4319255, -79.9256421, 1, "To 0123 Street, Pittsburgh: 1:00pm, 04/01/2015"]
    ];

    if (zipcode==""){
        zipcode = "15213-1000";
    }
    console.log(zipcode);
    $.ajax({
        url: "https://maps.googleapis.com/maps/api/geocode/json?key=AIzaSyA_shKiquTkC5lRp0eW3U1N6vLk9lBaszw&address="+zipcode,
        dataType:"json",
        success: function(data){
            console.log("dududu");

            _lat = data.results[0].geometry.location.lat;
            _lng = data.results[0].geometry.location.lng;
            console.log(_lat);
            console.log(_lng);
            var mapOptions = {

              center: { lat: _lat, lng: _lng},
              zoom: 13,
              zoomControl:false,
              scrollwheel: false,
              navigationControl: false,
              mapTypeControl: false,
              scaleControl: false,
              draggable: false,
              mapTypeId: google.maps.MapTypeId.ROADMAP
            };
            var map = new google.maps.Map(document.getElementById('map-canvas'),
                mapOptions);

            console.log("bibibi");
            
            setMarkers(map, others);
        },
    });
    
};
google.maps.event.addDomListener(window, 'load', initialize);


function setMarkers(map, locations) {
  // Add markers to the map

  // Marker sizes are expressed as a Size of X,Y
  // where the origin of the image (0,0) is located
  // in the top left of the image.

  // Origins, anchor positions and coordinates of the marker
  // increase in the X direction to the right and in
  // the Y direction down.
  var image = {
    url: '/static/car4go/images/classic_car_car_sedan_automobile-32.png',
    // This marker is 20 pixels wide by 32 pixels tall.
    size: new google.maps.Size(32, 32),
    // The origin for this image is 0,0.
    origin: new google.maps.Point(0,0),
    // The anchor for this image is the base of the flagpole at 0,32.
    anchor: new google.maps.Point(0, 32)
  };
  // Shapes define the clickable region of the icon.
  // The type defines an HTML &lt;area&gt; element 'poly' which
  // traces out a polygon as a series of X,Y points. The final
  // coordinate closes the poly by connecting to the first
  // coordinate.
  var shape = {
      coords: [1, 1, 1, 20, 18, 20, 18 , 1],
      type: 'poly'
  };
  for (var i = 0; i < locations.length; i++) {
    var beach = locations[i];
    var myLatLng = new google.maps.LatLng(beach[1], beach[2]);
    var marker = new MarkerWithLabel({
        position: myLatLng,
        map: map,
        icon: image,
        labelAnchor: new google.maps.Point(-18,15),
        labelClass: "MarkerWithLabel"
       
    });
    google.maps.event.addListener(marker, 'mouseover', (function(marker,i){
      return function(){
          marker.set('labelContent',locations[i][4]);
      }
    })(marker,i));
    google.maps.event.addListener(marker, 'mouseout', (function(marker,i){
       return function(){
        marker.set('labelContent', '');
      }
    })(marker,i));
  }
}