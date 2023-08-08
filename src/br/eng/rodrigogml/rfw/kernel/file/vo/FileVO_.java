package br.eng.rodrigogml.rfw.kernel.file.vo;

import br.eng.rodrigogml.rfw.kernel.vo.RFWVO_;

// Esta classe foi gerada utilizando o MetaObjectGenerator em 08/08/2023 13:47:27. Não edite!
public class FileVO_ extends RFWVO_ {
	private static final long serialVersionUID = 938608718161221863L;

	public static final FileVO_ vo() {
		return new FileVO_();
	}

	public final static String _id = "id";
	public final static String _name = "name";
	public final static String _tagID = "tagID";
	public final static String _fileUUID = "fileUUID";
	public final static String _compression = "compression";
	public final static String _dateCreation = "dateCreation";
	public final static String _dateModification = "dateModification";
	public final static String _size = "size";
	public final static String _encoding = "encoding";
	public final static String _persistenceType = "persistenceType";
	public final static String _versionID = "versionID";
	public final static String _tempPath = "tempPath";
	public final static String _fileContentVO = "fileContentVO";

	private FileVO_() {
	}

	public FileVO_(String basepath) {
		super(basepath);
	}

	public String name() {
		return getAttributePath("name");
	}

	public String tagID() {
		return getAttributePath("tagID");
	}

	public String fileUUID() {
		return getAttributePath("fileUUID");
	}

	public String compression() {
		return getAttributePath("compression");
	}

	public String dateCreation() {
		return getAttributePath("dateCreation");
	}

	public String dateModification() {
		return getAttributePath("dateModification");
	}

	public String size() {
		return getAttributePath("size");
	}

	public String encoding() {
		return getAttributePath("encoding");
	}

	public String persistenceType() {
		return getAttributePath("persistenceType");
	}

	public String versionID() {
		return getAttributePath("versionID");
	}

	public String tempPath() {
		return getAttributePath("tempPath");
	}

	public FileContentVO_ fileContentVO() {
		return new FileContentVO_(getAttributePath("fileContentVO"));
	}
}