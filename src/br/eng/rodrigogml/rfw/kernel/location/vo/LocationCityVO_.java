package br.eng.rodrigogml.rfw.kernel.location.vo;

import br.eng.rodrigogml.rfw.kernel.vo.RFWVO_;

// Esta classe foi gerada utilizando o MetaObjectGenerator em 08/08/2023 12:33:40. Não edite!
public class LocationCityVO_ extends RFWVO_ {
	private static final long serialVersionUID = -9144907281519546430L;

	public static final LocationCityVO_ vo() {
		return new LocationCityVO_();
	}

	public final static String _id = "id";
	public final static String _name = "name";
	public final static String _latitude = "latitude";
	public final static String _longitude = "longitude";
	public final static String _ddd = "ddd";
	public final static String _ibgeCode = "ibgeCode";
	public final static String _lastchange = "lastchange";
	public final static String _locationStateVO = "locationStateVO";

	private LocationCityVO_() {
	}

	public LocationCityVO_(String basepath) {
		super(basepath);
	}

	public String name() {
		return getAttributePath("name");
	}

	public String latitude() {
		return getAttributePath("latitude");
	}

	public String longitude() {
		return getAttributePath("longitude");
	}

	public String ddd() {
		return getAttributePath("ddd");
	}

	public String ibgeCode() {
		return getAttributePath("ibgeCode");
	}

	public String lastchange() {
		return getAttributePath("lastchange");
	}

	public LocationStateVO_ locationStateVO() {
		return new LocationStateVO_(getAttributePath("locationStateVO"));
	}
}