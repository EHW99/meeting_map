export const drawPolyline = (map, coordinates, color = '#007bff', style = 'solid') => {
    if (!map || !coordinates) return null;
    const path = coordinates.map(([lng, lat]) => new window.kakao.maps.LatLng(lat, lng));
    const polyline = new window.kakao.maps.Polyline({
      path,
      strokeWeight: 5,
      strokeColor: color,
      strokeOpacity: 0.9,
      strokeStyle: style,
    });
    polyline.setMap(map);
    return polyline;
  };
  
  export const drawTransitPlan = (map, plan) => {
    if (!map || !plan) return [];
    
    return plan.detail.map(d => {
      const path = d.coordinates.map(([lng, lat]) => new window.kakao.maps.LatLng(lat, lng));
      const polyline = new window.kakao.maps.Polyline({
        path,
        strokeWeight: 5,
        strokeColor: `#${d.routeColor || '888'}`,
        strokeOpacity: 0.9,
        strokeStyle: d.routeStyle || 'solid',
      });
      polyline.setMap(map);
      return polyline;
    });
  };
  
  export const clearPolylines = (polylines) => {
    polylines.forEach(p => p.setMap(null));
  };
  