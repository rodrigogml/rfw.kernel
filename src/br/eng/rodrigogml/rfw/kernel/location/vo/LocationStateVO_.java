package br.eng.rodrigogml.rfw.kernel.location.vo;

import br.eng.rodrigogml.rfw.kernel.vo.RFWVO_;

// Esta classe foi gerada utilizando o MetaObjectGenerator em 15/08/2023 15:11:36. Não edite!
public class LocationStateVO_ extends RFWVO_ {
	private static final long serialVersionUID = 3071326358745978964L;

	public static final LocationStateVO_ vo() {
		return new LocationStateVO_();
	}

	public final static String _id = "id";
	public final static String _acronym = "acronym";
	public final static String _name = "name";
	public final static String _ibgeCode = "ibgeCode";
	public final static String _lastChange = "lastChange";
	public final static String _locationCountryVO = "locationCountryVO";

	private LocationStateVO_() {
	}

	public LocationStateVO_(String basepath) {
		super(basepath);
	}

	public String acronym() {
		return getAttributePath("acronym");
	}

	public String name() {
		return getAttributePath("name");
	}

	public String ibgeCode() {
		return getAttributePath("ibgeCode");
	}

	public String lastChange() {
		return getAttributePath("lastChange");
	}

	public LocationCountryVO_ locationCountryVO() {
		return new LocationCountryVO_(getAttributePath("locationCountryVO"));
	}
}