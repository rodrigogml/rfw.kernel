package br.eng.rodrigogml.rfw.kernel.location.vo;

import br.eng.rodrigogml.rfw.kernel.vo.RFWVO_;

// Esta classe foi gerada utilizando o MetaObjectGenerator em 15/08/2023 15:11:28. Não edite!
public class LocationAddressVO_ extends RFWVO_ {
	private static final long serialVersionUID = -2735534535038218202L;

	public static final LocationAddressVO_ vo() {
		return new LocationAddressVO_();
	}

	public final static String _id = "id";
	public final static String _cep = "cep";
	public final static String _name = "name";
	public final static String _neighborhood = "neighborhood";
	public final static String _lastChange = "lastChange";
	public final static String _locationCityVO = "locationCityVO";

	private LocationAddressVO_() {
	}

	public LocationAddressVO_(String basepath) {
		super(basepath);
	}

	public String cep() {
		return getAttributePath("cep");
	}

	public String name() {
		return getAttributePath("name");
	}

	public String neighborhood() {
		return getAttributePath("neighborhood");
	}

	public String lastChange() {
		return getAttributePath("lastChange");
	}

	public LocationCityVO_ locationCityVO() {
		return new LocationCityVO_(getAttributePath("locationCityVO"));
	}
}