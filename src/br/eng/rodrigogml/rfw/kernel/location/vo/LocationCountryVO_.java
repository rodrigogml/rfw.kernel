package br.eng.rodrigogml.rfw.kernel.location.vo;

import br.eng.rodrigogml.rfw.kernel.vo.RFWVO_;

// Esta classe foi gerada utilizando o MetaObjectGenerator em 08/08/2023 12:34:07. Não edite!
public class LocationCountryVO_ extends RFWVO_ {
	private static final long serialVersionUID = 5918641088186601780L;

	public static final LocationCountryVO_ vo() {
		return new LocationCountryVO_();
	}

	public final static String _id = "id";
	public final static String _acronym = "acronym";
	public final static String _name = "name";
	public final static String _bacenCode = "bacenCode";
	public final static String _lastchange = "lastchange";

	private LocationCountryVO_() {
	}

	public LocationCountryVO_(String basepath) {
		super(basepath);
	}

	public String acronym() {
		return getAttributePath("acronym");
	}

	public String name() {
		return getAttributePath("name");
	}

	public String bacenCode() {
		return getAttributePath("bacenCode");
	}

	public String lastchange() {
		return getAttributePath("lastchange");
	}
}